import json
from datetime import datetime
from time import sleep

from kafka import KafkaConsumer, KafkaProducer

from MessageProcessorStrategy import EnglishMessageStrategy, CandleMessageStrategy


class MessageProcessor:
    def __init__(self, bids_topic, notify_message_processor_topic, processed_bids_topic, auction_type_topic):
        super().__init__()
        self.bids_topic = bids_topic
        self.notify_message_processor_topic = notify_message_processor_topic
        self.processed_bids_topic = processed_bids_topic
        self.auction_type_topic = auction_type_topic

        self.strategy = None

        self.auction_type_consumer = KafkaConsumer(
            self.auction_type_topic,
            bootstrap_servers='localhost:9092',
            auto_offset_reset="latest",
            value_deserializer=lambda value: json.loads(value.decode("utf-8"))
        )

        # consumatorul notificarii de la Auctioneer cum ca s-a terminat licitatia
        self.notify_message_processor_consumer = KafkaConsumer(
            self.notify_message_processor_topic,
            bootstrap_servers='localhost:9092',
            auto_offset_reset="earliest"  # mesajele se preiau de la cel mai vechi la cel mai recent
        )

        # consumatorul pentru ofertele de la licitatie
        self.bids_consumer = KafkaConsumer(
            self.bids_topic,
            bootstrap_servers='localhost:9092',
            auto_offset_reset="earliest",
            consumer_timeout_ms=1000
        )

        # producatorul pentru mesajele procesate
        self.processed_bids_producer = KafkaProducer(
            bootstrap_servers='localhost:9092'
        )

    def select_auction_strategy(self):
        print("Astept tipul licitatiei...")
        for msg in self.auction_type_consumer:
            tip = msg.value.get('tip')
            if tip == 'engleza':
                self.strategy = EnglishMessageStrategy()
            elif tip == 'candle':
                self.strategy = CandleMessageStrategy()

            if self.strategy:
                self.auction_type_consumer.close()
                break

    def get_and_process_messages(self):
        # se asteapta notificarea de la Auctioneer pentru incheierea licitatiei
        print("Astept notificare de la toate entitatile Auctioneer pentru incheierea licitatiei...")
        auction_end_message = next(self.notify_message_processor_consumer)

        # a ajuns prima notificare, se asteapta si celelalte notificari timp de maxim 15 secunde
        self.notify_message_processor_consumer.config["consumer_timeout_ms"] = 15_000
        for auction_end_message in self.notify_message_processor_consumer:
            pass
        self.notify_message_processor_consumer.close()

        if str(auction_end_message.value, encoding="utf-8") == "incheiat":
            # se preiau toate ofertele din topicul bids_topic si se proceseaza
            print("Licitatie incheiata. Procesez mesajele cu oferte...")

            parsed_bids = []
            for msg in self.bids_consumer:
                identity = None
                bid_amount = None

                for header in msg.headers:
                    if header[0] == "identity":
                        identity = str(header[1], encoding="utf-8")
                    elif header[0] == "amount":
                        bid_amount = int.from_bytes(header[1], 'big')

                if identity and bid_amount is not None:
                    parsed_bids.append({
                        'identity': identity,
                        'amount': bid_amount,
                        'timestamp': msg.timestamp,
                        'original_msg': msg
                    })


            self.bids_consumer.close()

            sorted_bids = self.strategy.process_messages(parsed_bids)

            self.finish_processing(sorted_bids)

    def finish_processing(self, sorted_bids):
        print("Procesarea s-a incheiat! Trimit urmatoarele oferte:")
        for bid in sorted_bids:
            identity = bid['identity']
            bid_amount = bid['amount']
            timestamp = bid['timestamp']

            original_msg = bid['original_msg']

            print("[{}] {} a licitat {}.".format(datetime.fromtimestamp(timestamp / 1000), identity, bid_amount))

            # se stocheaza mesajele ordonate dupa timestamp si fara duplicate intr-un topic separat
            self.processed_bids_producer.send(topic=self.processed_bids_topic, value=original_msg.value, headers=original_msg.headers)

        self.processed_bids_producer.flush()
        self.processed_bids_producer.close()

    def run(self):
        self.select_auction_strategy()

        self.get_and_process_messages()




if __name__ == '__main__':
    message_processor = MessageProcessor(
        bids_topic="topic_oferte",
        notify_message_processor_topic="topic_notificare_procesor_mesaje",
        processed_bids_topic="topic_oferte_procesate",
        auction_type_topic="topic_tip_licitatie"
    )
    message_processor.run()
