# Тестовое задание для Slotegrator 



## Описание

API тесты реализованы с помощью Rest Assured + JUnit 5\
Все тест кейсы можно запускать в любом порядке.

Метод checkPlayersListIsEmpty не работатает из-за ошибки в методе delete
на вход ожидается числовое значение, но id пользователей приходят в виде строки
из-за чего при передаче значения в запрос возвращается 404 ошибка "Not Found"

## Как запустить

Для запуска тестов можно либо запустить их из IDE нажав правым кликом на папке java -> Run 'All Tests', либо на конкретный package - \
JUnit тесты в папке test/java/testCases\

Так-же можно запустить тесты из командной строки с помощью команды\
```mvn test -Dtest=TestClass#MethodName```
