# Proiect Energy System Etapa 2

## Despre

Proiectare Orientata pe Obiecte, Seriile CA, CD
2020-2021

Student: Burcea Camelia, 324CA

## Rulare teste

Clasa Test#main
  * ruleaza solutia pe testele din checker/, comparand rezultatele cu cele de referinta
  * ruleaza checkstyle

Detalii despre teste: checker/README

Biblioteci necesare pentru implementare:
* Jackson Core 
* Jackson Databind 
* Jackson Annotations

## Implementare

	 Clasa Main va parcurge fisierul de intrare JSON si va crea obiectul de tip Input pe care il va da ca parametru metodei Simulatuon din clasa GameSimulation, metoda ce contine toata logica jocului si care intoarce fisierul de output ce trebuie scris in fisierul JSON de iesire. 
	 
### Entitati

   Structura proiectului este urmatoarea:
-> checker: Clasele predefinite pentru rularea checker-ului

-> entities:
	-> EnergyType: clasa data in schelet pentru a prelucra tipul de energie folosit de fiecare producator;
	
-> files (contine clasele auxiliare jocului, in afara claselor ce implementeaza participantii la joc):
	-> Contract: folosita pentru crearea si manipularea contractelor;
	-> Costs: clasa auxiliara pentru actualizarea costurilor distribuitorilor; permite citirea informatiilor noi despre distribuitori din fiecare runda;
	-> MonthlyUpdates: clasa auxiliara pentru citirea actualizarilor de cost pentru distribuitori, consumatori si producatori;
	-> DistributorChanges: folosita pentru a actualiza modificarile pentru fiecare distribuitor (actualizeaza costul pentru infrastructura);
	-> ProducerChanges: folosita pentru a actualiza modificarile pentru fiecare producator;
	
-> input:
	-> Input: clasa auxiliara pentru parsarea fisierului JSON aferent fiecarui test;
	-> InitialData: clasa auxiliara ce retine listele initiale de consumatori si distributori;
	 
-output:
	-> ConsumerOutput: contine datele despre utilizator ce vor fi afisate la iesire;
	-> ContractOutput: contine datele despre contract ce vor fi afisate la iesire;
	-> DistributorOutput: contine datele despre distribuitor ce vor fi afisate la iesire;
	-> ProducerOutput: contine datele despre producator ce vor fi afisate la iesire;
	-> Entity: interfata utilizata la implementarea folosind Factory;
	-> Output: folosita pentru afisarea in fisier a rezultatelor jocului;
	-> OutputFactory: clasa ce implementeaza design pattern-ul Factory;
	-> MonthlyStat: folosita pentru a afisa in output distribuitorii pe care ii au producatorii in fiecare luna;
	
-> players (contine jucatorii):
	-> Consumer: implementeaza logica unui consumator;
	-> Distributor: logica unui distribuitor;
	-> Producer: logica unui producator;
	-> Observer: interfata folosita pentru implementarea design pattern-ului Observer;
	-> GamePlayers: clasa abstracta ce contine campurile comune ale jucatorilor, mostenita de toti jucatorii;
	
-> strategies:
	-> EnergyChoiceStrategyType: clasa predefinita in scheletul temei, folosita pentru a manipula strategiile de alegere a producatorilor de catre distribuitori;

-> GameSimulation: clasa in care este implementata toata logica jocului, inclusiv runda initiala;

-> Main : se citeste fisierul de input si se apeleaza GameSimulation, la final se scriu rezultatele in fisierul de output;

-> Test: ruleaza fiecare fisier de test.


### Flow

	Flow-ul simularii este urmatorul:
   Runda initiala:
- se sorteaza producatorii in functie de id;
- se creaza obiectele de output, respectiv obiectele ProducerOutput aferente producatorilor;
- fiecare distribuitor isi alege producatorii, isi calculeaza costul de productie si il actualizeaza, isi calculeaza pretul contractului;
- fiecare consumator primeste venitul lunar, isi alege distribuitorul, apoi plateste contractul;
- fiecare distribuitor isi plateste costurile lunare.

    Pentru fiecare luna (runda):
    La inceputul lunii:
- se adauga noii distribuitori si consumatori la listele existente;
- pentru fiecare distribuitor se "seteaza" drept ne-notificat;
- se fac actualizarile aferente fiecarei luni pentru distribuitori;
- fiecare distribuitor isi calculeaza pretul contractului si il actualizeaza, apoi scoate din lista de consumatori pe cei care au iesit din joc;
- consumatorii primesc venitul lunar, apoi, daca e nevoie, isi aleg un nou distribuitor si isi platesc rata lunara;
- in caz ca un consumator iese din joc, este scos din lista de consumatori si din contractele distribuitorului;
- distribuitorii isi platesc costurile lunare;
  
  La mijlocul lunii:
- producatorii isi actualizeaza costurile (acestea sunt actualizate si pentru obiectele OutputProducers);
- daca un producator a avut actualizari ale costurilor, isi notifica distribuitorii (seteaza distribuitorii drept notificati);
  
  La finalul lunii:
- se scot din joc distribuitorii si consumatorii care au dat faliment;
- se sorteaza distribuitorii in ordinea id-urilor, iar apoi distribuitorii notificati de producatori isi actualizeaza datele (implementarea Observer);
- la final, se creaza listele cu id-urile distribuitorilor pentru fiecare producator (obiectele MonthlyStats). 
	
	La terminarea rundelor, se creaza obiectele (ConsumerOutput si DistributorOutput) care vor fi afisate in fisierele JSON de output.
	
### Elemente de design OOP

 - Mostenirea
	Am folosit mostenirea pentru jucatori: fiecare clasa ce implementeaza un jucator (Consumer, Distributor, Producer) mosteneste clasa GamePlayers, intrucat campurile din GamePlayers erau campuri comune tuturor jucatorilor.
	
 -  Incapsularea
 	Am folosit incapsularea atunci cand am implementat logica jocului, prin crearea unei clase GameSimulation ce contine o metoda care are ca parametru (datele din) fisierul de input si returneaza fisierul output. Aceasta metoda este apelata in clasa Main. Astfel, incapsularea este realizata prin separarea datelor de functii, datele regasindu-se in clasa Main, iar functiile, in GameSimulation.


### Design patterns

	Am folosit design pattern-urile Factory si Observer.

 - Observer 
	Design pattern-ul Observer este implementat cu ajutorul consumatorilor si distribuitorilor. Fiecare producator are rolul subiectului, iar distribuitorii au rolul observatorilor. Atunci cand datele unui producator se modifica, acesta isi notifica distribuitorii, pentru ca ulterior distribuitorii notificati sa isi actualizeze datele si sa aleaga alti producatori.
	
 - Factory
	 Design pattern-ul Factory a fost implementat cu ajutorul interfetei Entity si al clasei OutputFactory. In clasa din urma, in cadrul metodei createEntiry va fi creata si returnata o instanta a uneia din clasele ConsumerOutput si DistributorOutput, in functie de numele instantei, dat ca parametru al metodei.
		

### Dificultati intalnite, limitari, probleme

Dificultatile intalnite au fost in rezolvarea problemelor de checkstyle. Cateva din problemele cu care m-am confruntat au fost erorile legate de "numere magice", pe care nu le-am rezolvat, si eroarea legata de specificatorii de acces ai campului "label" din clasa EnergyChoiceStrategyType, intrucat aceasta clasa era predefinita si nu stiam daca ar trebui sa rezolv eroarea sau sa las scheletul temei nemodificat.


### GitHub: 
<https://github.com/cameliaburcea/Proiect_Etapa2.git>

