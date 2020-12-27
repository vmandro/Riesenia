Z domácich úloh som si vybral počasie.

V projekte som použil retrofit. Aplikácia funguje tak, že na začiatku sa skúsi pripojiť
k api a získať json ktorý sa následne rozparsuje a vyplnia sa jednotlivé políčka. GET request
na api sa robí každých 30 min, avšak dá sa aktualizovať aj tlačidlom zatočenej šípky(request
sa dá ale opakovať najviac 60x za deň). Aplikácia zobrazuje rôzne údaje o aktuálnom počasí.
Možno ešte dodám, že pod kľúčom weather v jsone sa môže nachádzať viacero údajov, keďže je
to list a teda spravil som to tak, že obrázok sa berie len z 1. prvku v zozname ale
popis sa berie zo všetkých a spojí sa do jedného stringu ktorý sa vypíše.
