import socket
import sys
import time
from datetime import date
import requests
import json

# Facem rost de simboluri
symbols_url = 'https://finnhub.io/api/v1/stock/symbol?exchange=US&token=brmr2kfrh5rcss140jmg'
symbols = requests.get(symbols_url)
serialize_symbols = json.loads(symbols.text)

# Cream serverul TCP in care vom trimite simbolurile si stirile
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = ('127.0.0.1', 9999)
sock.bind(server_address)

sock.listen(1)

while True:
    conn, client_address = sock.accept()
    print(f"\n[+] Spark s-a conectat cu succes de la: {client_address}")
    try:
        data_curenta = date.today()
        for symbol in serialize_symbols:
            news_url = f"https://finnhub.io/api/v1/company-news?symbol={symbol['symbol']}&from={data_curenta}&to={data_curenta}&token=brmr2kfrh5rcss140jmg"
            news = requests.get(news_url)
            if news.status_code == 200:
                serialize_news = json.loads(news.text)
                for element_news in serialize_news:
                    message = json.dumps(element_news) + '\n'
                    conn.sendall(message.encode('utf-8'))
                    time.sleep(3)
            time.sleep(1)
    finally:
        conn.close()