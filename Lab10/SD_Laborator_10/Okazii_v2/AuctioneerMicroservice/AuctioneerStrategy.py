from abc import ABC, abstractmethod
from random import randint


class AuctioneerStrategy(ABC):
    @abstractmethod
    def manage_auction(self, auctioneer_instance):
        pass

class EnglishAuctioneerStrategy(AuctioneerStrategy):
    def manage_auction(self, auctioneer_instance):
        print("Licitatie Engleza: astept oferte crescatoare...")
        auctioneer_instance.receive_bids()

class CandlesAuctioneerStrategy(AuctioneerStrategy):
    def manage_auction(self, auctioneer_instance):
        duration = randint(15,30)
        print(f"Licitatie Candle: se va opri dupa {duration} secunde")
        auctioneer_instance.receive_bids_with_timer(duration)