from abc import ABC, abstractmethod
from random import randint

class BidderStrategy(ABC):
    @abstractmethod
    def compute_bid(self, current_data):
        pass

class EnglishBidderStrategy(BidderStrategy):
    def compute_bid(self, current_data):
        last_bid = current_data.get('last_bid', 1000)
        return last_bid + randint(100,500)

class CandleBidderStrategy(BidderStrategy):
    def compute_bid(self, current_data):
        last_bid = current_data.get('last_bid', 1000)
        return last_bid + randint(500, 1000)