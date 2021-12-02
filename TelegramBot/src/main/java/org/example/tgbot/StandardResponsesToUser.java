package org.example.tgbot;

public class StandardResponsesToUser {
    public String startMessage = "Привет, я чат-бот! Чтобы узнать что я умею, напишите \"Помощь\"";
    public String helpMessage = """
            Я умею:
            -Дать вам определение для рандомного или конкретного ролевого термина. Для этого напишите "Дай определение"
            -Ну и здороваться могу, да. Просто напишите "Привет\"""";
    public String gettingMessage = "Привет!";
    public String outTerm = "Рандомное или конкретное определение?";
    public String waitTerm = "Определение какого термина хотите узнать?";
    public String userAgree = """
            Возможно вы имели ввиду что-то из этого: %s ?
            Выберите и напишите слово из списка, если подходящих среди них не оказалось - нажмите "Отменить\"""";
    public String invalidInputWaitWord = "Напишите одно из предложеных слов, либо нажмите кнопку \"Отменить\"";
    public String invalidInputYesOrNo = "Напишите \"Да\" или \"Нет\" или воспользуйтесь кнопками";
    public String termNotFound = "У меня нет похожих слов в словаре( Но я ещё учусь! Хотите добавить в мой словарь определение для термина %s?";
    public String writeDefinition = "Хотите добавить в мой словарь новое определение %s?";
    public String cancel = "Ок(";
    public String waitDefinition = "Опишите мне, что такое %s?";
    public String invalidInputRandomOrCertain = "Напишите \"Конкретный термин\" или \"Рандомный термин\", или \"Назад\", " +
            "или воспользуйтесь кнопками.";
    public String definitionSentForConsideration = "Спасибо! Ваше определение отправлено на рассмотрение моим разработчиками";
    public String notFondTermsAndUserBanned = "У меня нет похожих слов в словаре( И вы не можете добавить это слово в словарь, так как " +
            "были забанены(";
    public String unknownCommand = "Извините, я вас не понимаю(";
}
