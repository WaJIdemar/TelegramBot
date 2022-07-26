package org.example.tgbot;

public class StandardResponses {
    public final String startMessage = "Привет, я чат-бот! Чтобы узнать что я умею, напишите \"Помощь\"";
    public final String helpMessage = """
            Я умею:
            -Дать вам определение для рандомного или конкретного ролевого термина. Для этого напишите "Дай определение"
            -Ну и здороваться могу, да. Просто напишите "Привет\"""";
    public final String gettingMessage = "Привет!";
    public final String outTerm = "Рандомное или конкретное определение?";
    public final String waitTerm = "Определение какого термина хотите узнать?";
    public final String userAgree = """
            Возможно вы имели ввиду что-то из этого: %s ?
            Выберите и напишите слово из списка, если подходящих среди них не оказалось - нажмите "Отменить\"""";
    public final String invalidInputWaitWord = "Напишите одно из предложеных слов, либо нажмите кнопку \"Отменить\"";
    public final String invalidInputYesOrNo = "Напишите \"Да\" или \"Нет\" или воспользуйтесь кнопками";
    public final String termNotFound = "У меня нет похожих слов в словаре( Но я ещё учусь! Хотите добавить в мой словарь определение для термина %s?";
    public final String writeDefinition = "Хотите добавить в мой словарь новое определение %s?";
    public final String cancel = "Ок(";
    public final String waitDefinition = "Опишите мне, что такое %s?";
    public final String invalidInputRandomOrCertain = "Напишите \"Конкретный термин\" или \"Рандомный термин\", или \"Назад\", " +
            "или воспользуйтесь кнопками.";
    public final String definitionSentForConsideration = "Спасибо! Ваше определение отправлено на рассмотрение моим разработчиками";
    public final String notFondTermsAndUserBanned = "У меня нет похожих слов в словаре( И вы не можете добавить это слово в словарь, так как " +
            "были забанены(";
    public final String unknownCommand = "Извините, я вас не понимаю(";
    public final String messageForModerator = """
            ID: %s
            Одобрите пожалуйста, если достойно
            ня (^_^)6
            ______
            %s - %s
            #%d
            """;
    public final String acceptTerm = """
            Поздравляю, Ваш термин:
            %s.
            Был одобрен)""";
    public final String banUser = """
            Вы были забанены за некоректное определение:
            %s
            (((""";
    public final String rejectTerm = """
            Ваш термин:
            %s.
            Был отклонён(""";
}
