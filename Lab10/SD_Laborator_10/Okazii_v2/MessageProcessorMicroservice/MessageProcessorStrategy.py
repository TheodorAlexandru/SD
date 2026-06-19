from abc import ABC, abstractmethod


class MessageProcessorStrategy(ABC):
    @abstractmethod
    def process_messages(self, bids_list):
        pass

class EnglishMessageStrategy(MessageProcessorStrategy):
    def process_messages(self, bids_list):
        unique_bids = {}
        for bid in bids_list:
            identity = bid['identity']
            if identity not in unique_bids or bid['amount'] > unique_bids[identity]['amount']:
                unique_bids[identity] = bid

        sorted_bids = sorted(unique_bids.values(), key=lambda item: item['amount'], reverse=True)
        return sorted_bids


class CandleMessageStrategy(MessageProcessorStrategy):
    def process_messages(self, bids_list):
        unique_bids = {}
        for bid in bids_list:
            if bid.get('is_valid_time', True):
                identity = bid['identity']
                unique_bids[identity] = bid
        return list(unique_bids.values())