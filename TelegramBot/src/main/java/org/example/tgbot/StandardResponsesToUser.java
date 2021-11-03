package org.example.tgbot;

public class StandardResponsesToUser {
    public String startMessage = "Привет я чат-бот, чтобы узнать о моих возможностьях напиши \"Помощь\"";
    public String helpMessage = """
            Я могу:
            Выдать вам ролевой термин по запросу "Дай определение"
            Ну и здороваться могу, да. Просто напиши "Привет\"""";
    public String gettingMessage = "Привет";
    public String outTerm = "Рандомное или конкретное?";
    public String waitTerm = "Напиши термин";
    public String userAgree = "Вы имели ввиду: ";
    public String termNotFound = "У нас нет похожих слов в словаре. Желаете написать его определение?";
    public String writeDefinition = "Желаете написать его определение?";
    public String unknownCommand = "Извини, я тебя не понимаю(((";
}
