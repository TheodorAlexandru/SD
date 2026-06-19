import json
import time

from kafka import KafkaConsumer, KafkaProducer

from AuctioneerStrategy import AuctioneerStrategy, EnglishAuctioneerStrategy, \
    CandlesAuctioneerStrategy


class Auctioneer:
    def __init__(self, bids_topic, notify_message_processor_topic, auction_type_topic):
        super().__init__()
        self.bids_topic = bids_topic
        self.notify_processor_topic = notify_message_processor_topic
        self.auction_type_topic = auction_type_topic

        self.strategy = None

        self.auction_type_consumer = KafkaConsumer(
            self.auction_type_topic,
            bootstrap_servers='localhost:9092',
            auto_offset_reset="latest",
            value_deserializer=lambda value: json.loads(value.decode("utf-8"))
        )

        # consumatorul pentru ofertele de la licitatie
        self.bids_consumer = KafkaConsumer(
            self.bids_topic,
            bootstrap_servers='localhost:9092',
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            group_id="auctioneers",
            consumer_timeout_ms=15_000  # timeout de 15 secunde
        )

        # producatorul pentru notificarea procesorului de mesaje
        self.notify_processor_producer = KafkaProducer(
            bootstrap_servers='localhost:9092'
        )

    def select_auction_strategy(self):
        print("Astept tipul licitatiei...")
        for msg in self.auction_type_consumer:
            tip = msg.value.get('tip')
            if tip == 'engleza':
                self.strategy = EnglishAuctioneerStrategy()
            elif tip == 'candle':
                self.strategy = CandlesAuctioneerStrategy()

            if self.strategy:
                print(f"Tipul licitatiei este --{tip}--")
                self.auction_type_consumer.close()
                break

    def receive_bids(self):
        # se preiau toate ofertele din topicul bids_topic
        print("Astept oferte pentru licitatie...")
        for msg in self.bids_consumer:
            for header in msg.headers:
                if header[0] == "identity":
                    identity = str(header[1], encoding="utf-8")
                elif header[0] == "amount":
                    bid_amount = int.from_bytes(header[1], 'big')

            print("{} a licitat {}".format(identity, bid_amount))

        # bids_consumer genereaza exceptia StopIteration atunci cand se atinge timeout-ul de 10 secunde
        # => licitatia se incheie dupa ce timp de 15 secunde nu s-a primit nicio oferta
        self.finish_auction()

    def finish_auction(self):
        print("Licitatia s-a incheiat!")
        self.bids_consumer.close()

        # se notifica MessageProcessor ca poate incepe procesarea mesajelor
        auction_finished_message = bytearray("incheiat", encoding="utf-8")
        self.notify_processor_producer.send(topic=self.notify_processor_topic, value=auction_finished_message)
        self.notify_processor_producer.flush()
        self.notify_processor_producer.close()

    def receive_bids_with_timer(self, duration):
        # Metoda noua pentru Licitatia Candle
        end_time = time.time() + duration

        while time.time() < end_time:
            # Folosim poll pentru a nu bloca bucla while in asteptarea mesajelor.
            # Verifica dupa oferte noi timp de 1 secunda (1000 ms), apoi evalueaza iar conditia de timp.
            records = self.bids_consumer.poll(timeout_ms=1000)

            for topic_partition, messages in records.items():
                for msg in messages:
                    identity = None
                    bid_amount = None
                    for header in msg.headers:
                        if header[0] == "identity":
                            identity = str(header[1], encoding="utf-8")
                        elif header[0] == "amount":
                            bid_amount = int.from_bytes(header[1], 'big')

                    if identity and bid_amount is not None:
                        print("{} a licitat {}".format(identity, bid_amount))

        print("Timpul a expirat!")
        self.finish_auction()

    def run(self):
        self.select_auction_strategy()

        self.strategy.manage_auction(self)


if __name__ == '__main__':
    auctioneer = Auctioneer(
        bids_topic="topic_oferte",
        notify_message_processor_topic="topic_notificare_procesor_mesaje",
        auction_type_topic="topic_tip_licitatie"
    )
    auctioneer.run()
