package org.example.tgbot;

public class StandardResponsesToUser {
    public String startMessage = "Привет я чат-бот, чтобы узнать о моих возможностьях напиши \"Помощь\"";
    public String helpMessage = """
            Я могу:
            Выдать вам рандомный или конкретный ролевой термин по запросу "Дай определение"
            Ну и здороваться могу, да. Просто напиши "Привет\"""";
    public String gettingMessage = "Привет";
    public String outTerm = "Рандомное или конкретное?";
    public String waitTerm = "Напиште термин";
    public String userAgree = """
            Выозможно вы имели ввиду что-то из этого: %s
            Если да, то напишите это слово, если нет - нажмите "/Отмена\"""";
    public String termNotFound = "У нас нет похожих слов в словаре. Желаете написать определение %s?";
    public String writeDefinition = "Желаете написать определение %s?";
    public String cancel = "Ок(";
    public String waitDefinition = "Введите определение %s";
    public String definitionSentForConsideration = "Ваше определение отправлено на рассмотрение";
    public String unknownCommand = "Извини, я тебя не понимаю(((";
}
