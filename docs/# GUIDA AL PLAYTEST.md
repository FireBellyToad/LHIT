# GUIDA AL PLAYTEST

# FASE 0: Preparazione

Prendi un foglio di carta, un foglio Excel o un file txt sul computer per scrivere:

* La versione che stai giocando (online, per windows o jar)
* Che bug hai trovato
* Cosa stavi facendo nel gioco quando il bug è emerso
* Data e ora del bug
* Informazioni aggiuntive (file erros.log)

Puoi anche registrare la partita con i tasto Windows + G se hai Windows 10 o più (maggiori info qui -> https://it.itopvpn.com/recorder-tips/come-registrare-schermo-windows-10-22#:~:text=semplice%20e%20veloce.-,Come%20Registrare%20lo%20Schermo%20Windows%2010%20Senza%20Utilizzare%20gli%20Strumenti%20di%20Terze%20Parti,-Come%20Registrare%20Video ). 

# FASE 1: Gioco libero

In questa fase gioca liberamente, come se fossi un normale giocatore.

Lo scopo di questa fase è trovare problemi macroscopici:

* Bug gravi che interrompono la partita (crash) o non permettono di avanzare nel gioco (softlock)
* Bug gravi di grafica corrotta o comportamenti evidentemente sbagliati delle entità di gioco
* Bug gravi sul salvataggio della partita
    

# FASE 2: Test attivo

In questa fase gioca in modo da fare tutto quello che NON faresti in una normale giocata:

* strisciare lungo i muri
* lasciarti uccidere volontariamente dai nemici
* toccare i nemici senza ferirli
* uscire e rientrare dalla partita continuamente
* ecc. ecc.

Lo scopo è trovare tutte le cose minori, che nell'insieme vanno a minare l'esperienza di gioco rendendola grossolana e poco pulita.

# OH NO! IL GIOCO CRASHA SENZA MOTIVO AL PUNTO XYZ !

Può capitare, purtroppo. Dipende da tanti fattori e a volte è imprevedibile. Per aiutarmi a capire cosa è successo, puoi lanciare il gioco da terminale, in modo da poter vedere i log Java.
Per farlo basta seguire i seguenti passi:

1. Assicurati di aver estratto tutti i file nella stessa cartella.
2. Apri la cartella dove è contenuto l'exe (o il bat)
3. Clicca nella barra dove c'è il percorso della cartella che stai visualizzando e scrivi "cmd". Premi invio.
4. Dovrebbe aprirsi una finestra di terminale che punta alla cartella dell'exe.
5. Scrivi "start LHIT.exe" o "start LHIT_windowed.bat"
6. Dovrebbe partire il gioco normalmente, ma nella finestra di terminale aperta dovresti vedere dei log mentre giochi

Quando il crash avviene, puoi mandarmi uno screenshot o fare copia incolla del testo del terminale.

Per fare uno screenshot puoi aprire lo "Strumento di cattura" di Windows e cliccare su "Nuovo elemento di cattura" dal menu "File" o dall'icona con la forbice.
In alternativa puoi usare il tasto STAMP della tastiera, aprire Paint, fare "CTRL + V" e mandarmi l'immagine salvata.

# BUG NOTI

- [Versione Browser] i tasti freccia muovo anche l'intera pagina


Grazie!!

Jacopo "Faust" Buttiglieri