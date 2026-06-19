from kafka import KafkaProducer
import time
import re

# Inițializarea producătorului Kafka conectat la localhost
producer = KafkaProducer(bootstrap_servers='localhost:9092')
topic = 'cuvinte_topic'

# Citirea fișierului text
with open('ebook.txt', 'r', encoding='utf-8') as file:
    for line in file:
        # Extragem doar cuvintele (fără semne de punctuație) folosind o expresie regulată
        words = re.findall(r'\b[a-zA-Z]+\b', line)

        for word in words:
            # Trimitem cuvântul (convertit la minuscule) în topicul Kafka
            producer.send(topic, word.lower().encode('utf-8'))

            # Adăugăm o mică întârziere pentru a simula un flux continuu (streaming)
            time.sleep(0.05)

# Ne asigurăm că toate mesajele sunt trimise
producer.flush()
print("Toate cuvintele au fost trimise către Kafka!")