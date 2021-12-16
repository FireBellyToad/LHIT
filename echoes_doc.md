# Validatore scripts Echoes

In fase di startup dell'engine e come parte di un tool separato (ValidEcho.java) vengono validati tutti gli script json degli Echi. Lanciando il tool separato viene lanciata la validazione per ogni script presente nella cartella scripts. L'azione si ferma sul primo errore trovato partendo dall'inizio alla fine di ogni file, i quali a loro volta vengono validati in ordine alfabetico. 

## Documentazione comandi

* textBoxKey: Chiave di una textbox per questo step
* renderOnlyMapLayer: In questo step renderizzerà solo i tile del map_layer indicato
* move: oggetto composto da due campi
    * direction: (necessario) Direzione in cui l'echoactor si muove in questo step.
    * speed: (necessario) Velocità in cui l'echoactor si muove in questo step.
* goTo: oggetto composto da più campi. Solo *step* e uno degli altri campi è necessario.
    * step: (necessario) Step dello script in cui saltare.
    * times: Ripete il goTo per un certo numero di volte.
    * untilAtLeastOneKillableAlive: Ripete il goTo fino a che c'è una GameInstance di tipo Killable che non sia morta nella stanza.
    * untilAtLeastOnePOIExaminable: Ripete il goTo fino a che c'è una POIInstance di un certo tipo non esaminata nella stanza.
    * untilAtLeastPlayerDamageIsLessThan: Ripete il goTo fino a che il Personaggio ha un numero di Danni minore o uguale ad un valore.
* spawn: oggetto composto da più campi. genera una GameInstance in questo step
    * identifier: (necessario) instance da generare.
    * x: posizione X dove verrà generata. Di default è quella dell'Echoactor.
    * y: posizione Y dove verrà generata. Di default è quella dell'Echoactor.
    * relative: se true, le coordinate qui sopra sono aggiunte algebricamente alla posizone dell'Echoactor
* splashToShow: splash da mostrare fino alla fine dello step.