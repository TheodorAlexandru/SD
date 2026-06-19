#!/usr/bin/env python3
import sqlite3

# 1. Ne conectăm la copia bazei de date
conn = sqlite3.connect('/home/theo/Documents/SD/Lab12/ex3_tema_acasa/places.sqlite')
cursor = conn.cursor()

# 2. Executăm o comandă SQL pentru a extrage URL-ul și frecvența vizitelor
# Alegem doar paginile care au fost vizitate măcar o dată
cursor.execute("SELECT url, visit_count FROM moz_places WHERE visit_count > 0")

# 3. Deschidem un fișier text nou pentru a salva rezultatele
with open('/home/theo/Documents/SD/Lab12/ex3_tema_acasa/istoric.txt', 'w', encoding='utf-8') as f:
    for rand in cursor.fetchall():
        url = rand[0]
        frecventa = rand[1]

        # Salvăm în formatul așteptat de Mapper-ul tău: URL|frecventa
        f.write('%s|%s\n' % (url, frecventa))

# 4. Închidem conexiunea cu baza de date
conn.close()
print("Extragerea s-a terminat cu succes! S-a creat fișierul istoric.txt")