# OnToury

Alexa-Skill für ein Reisequiz, das man allein oder mit mehreren Personen spielen kann.

Lass dich von uns als Beta-Tester einladen und *starte OnToury* in deinem persönlichen Assistenten!

## Status
[![Travis-CI Status](https://travis-ci.org/sweIhm-ws2018-19/skillproject-fr-33.svg?branch=master)](https://travis-ci.org/sweIhm-ws2018-19/skillproject-fr-33)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=edu.hm.cs.2018%3Areisequiz&metric=alert_status)](https://sonarcloud.io/dashboard?id=edu.hm.cs.2018%3Areisequiz)

## Design

Der Skill beruht auf einem recht einfachen Zustandsautomaten.
Es gibt nur einen großen Intent Handler, der anhand des Zustands entscheidet wie und ob die verschiedenen Eingaben behandelt werden.
Die Spiellogik selbst ist auschließlich in den Methoden des `QuizGame`-Objekts implementiert.

Der komplette Spielzustand wird als JSON im Sessionstate des Skills hinterlegt, es gibt keine weitere Persistenzschicht.
 
![Fluss-Diagramm](https://github.com/sweIhm-ws2018-19/skillproject-fr-33/wiki/FlussdiagrammSprint3.png)

## Erfahre mehr über uns auf [unserer Webseite](https://sweihm-ws2018-19.github.io/skillproject-fr-33/)
