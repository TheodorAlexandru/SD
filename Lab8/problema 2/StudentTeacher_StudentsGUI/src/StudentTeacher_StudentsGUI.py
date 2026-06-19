from tkinter import *
from tkinter import ttk
import threading
import socket

HOST = "localhost"


def resolve_question(question_text, target_port, destination_text):
    # creare socket TCP
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # incercare de conectare catre microserviciul Teacher
    try:
        sock.connect((HOST, target_port))

        # transmitere intrebare - se deleaga intrebarea catre microserviciu
        sock.send(bytes(destination_text + "|" +question_text + "\n", "utf-8"))

        # primire raspuns -> microserviciul Teacher foloseste coregrafia de microservicii pentru a trimite raspunsul inapoi
        response_text = str(sock.recv(1024), "utf-8")

    except ConnectionError:
        # in cazul unei erori de conexiune, se afiseaza un mesaj
        response_text = f"Eroare de conectare la microserviciul Student pe portul {target_port}!\n"

    # se adauga raspunsul primit in caseta text din interfata grafica
    response_widget.insert(END, response_text + "\n")



def ask_question():
    # preluare text intrebare de pe interfata grafica si port
    question_text = question.get()
    question.delete(0, END)

    port_text = port.get()
    destination_text = destination.get()

    try:
        target_port = int(port_text)
    except ValueError:
        response_widget.insert(END, "Eroare: portul trebuie sa fie un numar incepand cu 1701!\n")
        return

    # pornire thread separat pentru tratarea intrebarii respective
    # astfel, nu se blocheaza interfata grafica!
    threading.Thread(target=resolve_question, args=(question_text, target_port, destination_text)).start()


if __name__ == '__main__':
    # elementul radacina al interfetei grafice
    root = Tk()
    root.title("Interactiune student-profesor/studenti")

    # la redimensionarea ferestrei, cadrele se extind pentru a prelua spatiul ramas
    root.columnconfigure(0, weight=1)
    root.rowconfigure(0, weight=1)

    # cadrul care incapsuleaza intregul continut
    content = ttk.Frame(root)

    # caseta text care afiseaza raspunsurile la intrebari
    response_widget = Text(content, height=10, width=50)

    # eticheta text din partea dreapta
    destination_label = ttk.Label(content, text="Destinatar (IP sau ALL):")

    # caseta de introducere text cu care se preia caror utilizatori se va trimite mesajul
    destination = ttk.Entry(content, width=50)

    # Setam "ALL" ca valoare implicita
    destination.insert(0, "ALL")

    # eticheta text din partea dreapta
    port_label = ttk.Label(content, text="Portul studentului care intreaba (1701...):")

    # caseta de introducere text cu care se preia portul de la utilizator
    port = ttk.Entry(content, width=50)

    # eticheta text din partea dreapta
    question_label = ttk.Label(content, text="Studentul intreaba:")

    # caseta de introducere text cu care se preia intrebarea de la utilizator
    question = ttk.Entry(content, width=50)

    # butoanele din dreapta-jos
    ask = ttk.Button(content, text="Intreaba", command=ask_question)  # la apasare, se apeleaza functia ask_question
    exitbtn = ttk.Button(content, text="Iesi", command=root.destroy)  # la apasare, se iese din aplicatie

    # plasarea elementelor in layout-ul de tip grid
    content.grid(column=0, row=0)
    response_widget.grid(column=0, row=0, columnspan=3, rowspan=7)
    destination_label.grid(column=3, row=0, columnspan=2)
    destination.grid(column=3, row=1, columnspan=2)
    port_label.grid(column=3, row=2, columnspan=2)
    port.grid(column=3, row=3, columnspan=2)
    question_label.grid(column=3, row=4, columnspan=2)
    question.grid(column=3, row=5, columnspan=2)
    ask.grid(column=3, row=6)
    exitbtn.grid(column=4, row=6)

    # bucla principala a interfetei grafice care asteapta evenimente de la utilizator
    root.mainloop()
