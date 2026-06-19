FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul Auctioneer
WORKDIR /message_processor
COPY . MessageProcessor.py $WORKDIR

# comanda de executie este: python <nume_fisier>.py
CMD ["python", "MessageProcessor.py"]