import os
import sys
from PyQt5.QtWidgets import QWidget, QApplication
from PyQt5.uic import loadUi
from mq_communication import RabbitMq
from PyQt5 import QtCore

def debug_trace(ui=None):
    from pdb import set_trace
    QtCore.pyqtRemoveInputHook()
    set_trace()


class RestaurantApp(QWidget):
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

    def __init__(self):
        super(RestaurantApp, self).__init__()

        ui_path = os.path.join(self.ROOT_DIR, 'restaurant.ui')
        loadUi(ui_path, self)

        # butoane
        self.send_btn.clicked.connect(self.send_order)

        # conector MQ
        self.rabbit_mq = RabbitMq(self)

        # creez un chelner random (sau hardcodat)
        self.waiter_id = "Waiter123ABC"
        self.waiter_label.setText(self.waiter_id)

    def set_response(self, response):
        """
        Afișez rezultatul primit din Kotlin
        Format: RESULT~chefId~orderId~mesaj
        """
        try:
            _, chefId, orderId, message = response.split("~")
            self.result_area.setText(
                f"Chef: {chefId}\nOrder ID: {orderId}\n\n{message}"
            )
        except:
            self.result_area.setText("Wrong message format:\n" + response)

    def send_request(self, request):
        self.rabbit_mq.send_message(message=request)
        self.rabbit_mq.receive_message()

    def send_order(self):
        # citesc meniul din combo box
        menu_item = self.menu_box.currentText()
        request = f"ORDER:{self.waiter_id}:{menu_item}"
        self.send_request(request)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = RestaurantApp()
    window.show()
    sys.exit(app.exec_())
