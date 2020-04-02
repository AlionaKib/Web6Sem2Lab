# Web6Sem2Lab
Project structure:
scr-->
  Control-->
    connector.java //взаимодействие с базой данных
    
  Exceptions-->
    IncorrectLogin.java
    NoElementInBase.java
    SameIndex.java
    WrongAveragePoint.java
    
  Model-->
    Client-->
      Student.java
      StudentClient.java //клиент из 1 лабораторной работы
      StudentList.java
      Teacher.java //пользователь во 2 лабораторной работе
    Remoute-->
      StudentService  //интерфейс для 1 лабораторной работы
    Server-->
      StudentServiceImpl.java  //сервер для 1 лабораторной работы
  
  ojdbc6.jar //библиотека для работы с бд oracle
  OracleJDBCExample.java //регистрация библиотеки и тестовое соединение с бд
  Test.java //MAIN CLASS для 2 лр!!!!!!!!!!
  
Запуск OracleJDBCExample.java из консоли в 2 команды:
javac OracleJDBCExample.java
java -cp c:\...\scr\ojdbc6.java; c:\...\scr\ OracleJDBCExample
