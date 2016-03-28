Cvičenie 4
==========

**Riešenie odovzdávajte podľa
[pokynov na konci tohoto zadania](#technické-detaily-riešenia)
do stredy 20.3. 23:59:59.**

Súbory potrebné pre toto cvičenie si môžete stiahnuť ako jeden zip
[`pu04.zip`](https://github.com/FMFI-UK-1-AIN-412/lpi/archive/pu04.zip).

toCnf (2b)
----------

Do tried na reprezentáciu formúl z cvičenia 2 doimplementujte metódu
`toCnf()`, ktorá vráti ekvisplniteľnú formulu v konjunktívnej normálnej forme
(viď [Reprezentácia CNF](#reprezentácia-cnf)). Použite algoritmus Cejtinovej
transformácie.

Na prednáške sme opísali fungovanie Cejtinovej transformácie na celej vstupnej
formule. Na naprogramovanie je ale najjednoduchšie v každej z našich
formulových tried implementovať ako virtuálnu metódu, ktorá sa rekurzívne
zavolá na priame podformuly, spojí ich CNF a pridá k nim klauzuly opisujúce
vzťah svojej novej výrokovej premennej a výrokových premenných zastupujúcich
priame podformuly.

Premenná jednoducho vráti (zoznam obsahujúci) jednu klauzulu, v ktorej je jeden
(nenegovaný) `Literal` so správnym menom.

Negácia zavolá transformáciu na svoju priamu podformulu, dostane jej CNF (zoznam
klauzúl) a pridá k nemu klauzuly ekvivalentné s (<var>x</var> ↔︎ ¬<var>y</var>),
kde <var>y</var> je nová výroková premenná zastupujúca negáciu a <var>x</var>
výroková premenná zastupujúca jej priamu podformulu. Týmito klauzulami sú,
samozrejme, (¬<var>x</var> ∨ ¬<var>y</var>) a (<var>x</var> ∨ <var>y</var>).

Konjunkcia zavolá transformáciu na jednotlivé konjunkty, dostane niekoľko CNF
(zoznamov klauzúl) a vyrobí z nich jednu tak, že jednoducho dá všetky klauzuly do
jedného zoznamu a pridá k nemu klauzuly ekvivalentné s (<var>y</var> ↔︎
(<var>x</var><sub>1</sub> ∧ … ∧ <var>x</var><sub><var>n</var></sub>)),
kde <var>y</var> je nová výroková premenná zastupujúca konjunkciu
a <var>x</var><sub>1</sub> až <var>x</var><sub><var>n</var></sub> sú výrokové
premenné zastupujúce jej priame podformuly. Týchto klauzúl je <var>n</var> + 1.
Z nich <var>n</var> má po dva literály a jedna má <var>n</var> + 1 literálov.

Pre ďalšie triedy formúl je postup podobný.

Nesmieme pritom zabudnúť, že Cejtinova transformácia formuly musí obsahovať
aj **jednoprvkovú klauzulu** s výrokovou premennou, ktorá zastupuje túto
formulu. Táto klauzula je vo výslednej CNF iba jedna, s premennou, ktorá
zastupuje celú transformovanú formulu. Jednoprvkové klauzuly s premennými
zastupujúcimi podformuly sa vo výsledku nesmú objaviť.

## Nové výrokové premenné

Pri transformácii každej (neatomickej) formuly potrebujete vytvoriť novú
výrokovú premennú s doteraz nepoužitým menom. Môžete na to použiť triednu
(statickú) metódu `Variable.newName()`.

Neatomická formula musí poznať mená výrokových premenných, ktoré zastupujú jej
priame podformuly. To sa dá zabezpečiť viacerými spôsobmi.

## Reprezentácia CNF

Triedy, ktoré sme vyrobili v [cvičení 2](../pu02/), nie sú z viacerých dôvodov
veľmi vhodné na reprezentáciu formúl v CNF:
- kedykoľvek by sme očakávali formulu v CNF tvare,  museli by sme vždy
  kontrolovať, či naozaj má správny tvar;
- je trošku neefektívna ( `Negation(Variable("x"))`) a ťažkopádnejšia
  na použite (musíme zisťovať akého typu je podformula v `Disjunction` atď.);
- chceme pridať niekoľko metód, ktoré majú zmysel len pre CNF formulu.

Najpriamočiarejší spôsob, ako sa týmto problémom vyhnúť, je reprezentovať CNF
formulu jednoducho ako pole (resp. zoznam) klauzúl, pričom každá klauzula je pole
literálov. Literál by mohol byť reprezentovaný ako dvojica: meno
a boolovský flag hovoriaci, či je negovaný.
Operácie s takto reprezentovanými CNF formulami by ale potom nemohli byť
implementované ako ich metódy.

Obidve výhody dosiahneme tak,
že vytvoríme triedy `Cnf` a `Clause`, ktoré oddedíme od poľa (resp. zoznamu)
a pridáme im navyše potrebné metódy.
Na reprezentáciu literálov vytvoríme triedu `Literal`.
Ďalšou výhodou takéhoto prístupu je aj to, že vieme písať kód,
ktorý sa oveľa ľahšie číta:
namiesto hromady hranatých zátvoriek vidíme, či vytvárame klauzulu
alebo celú CNF formulu.

V súbore [`java/Cnf.java`](java/Cnf.java) nájdete hotové definície tried `Literal`,
`Clause` a `Cnf`, ktoré máte použiť na reprezentáciu literálov, klauzúl a CNF
formúl. Vaše metódy `toCnf` teda majú vždy vracať inštanciu triedy `Cnf`.

## Technické detaily riešenia

Riešenie odovzdajte do vetvy `pu04` v adresári `prakticke/pu04`.  Odovzdávajte
(modifikujte) súbor (knižnicu) [`java/Formula.java`](java/Formula.java). Program
[`java/CnfTest.java`](java/CnfTest.java) musí korektne zbehnúť s vašou knižnicou.

Súbor [`java/Formula.java`](java/Formula.java) obsahuje vzorové riešenie časti predchádzajúceho
cvičenia [`pu02`](../pu02). Vašou úlohou je doimplementovať metódu `toCnf` pre
triedu `Formula`. V každej jej podtriede implementujte rovnakú alebo vhodne
zvolenú pomocnú metódu.

Odovzdávanie riešení v iných jazykoch konzultujte s cvičiacimi.

## Bonus (1b)

Metóda `Variable.newName()` vytvára mená, ktoré určite nie sú použité v našich
testovacích formulách, ale nezaručuje, že vytvorené meno bude za každých
okolností nové. Upravte ju (a prípadné ďalšie metódy a konštruktory) tak, aby
boli vytvorené mená určite nové.
