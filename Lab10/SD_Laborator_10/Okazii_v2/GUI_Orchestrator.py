from kafka import KafkaProducer
import json


class GUI_Orchestrator:
    def __init__(self):
        self.producer = KafkaProducer(
            bootstrap_servers='localhost:9092',
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
        self.topic = 'topic_tip_licitatie'

    def run(self):
        print("=== Meniu Configurare Licitație ===")
        print("1. Engleză")
        print("2. Lumânare (Candle)")
        #print("3. Olandeză (Dutch)")
        #print("4. Suedeză (Swedish)")

        alegere = input("Selectează tipul licitației (1/2): ")

        mapping = {
            '1': 'engleza',
            '2': 'candle'
            #'3': 'olandeza',
            #'4': 'suedeza'
        }

        tip_licitatie = mapping.get(alegere)

        if tip_licitatie:
            # Trimitem configurația către toate microserviciile
            mesaj = {'tip': tip_licitatie}
            self.producer.send(self.topic, value=mesaj)
            self.producer.flush()
            print(f"Am trimis configurația: {tip_licitatie} pe topicul {self.topic}.")
        else:
            print("Alegere invalidă!")


if __name__ == '__main__':
    gui = GUI_Orchestrator()
    gui.run()