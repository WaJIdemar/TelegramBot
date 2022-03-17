package org.example.tgbot;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class BotLogicTests {

    private final TestChatClient testChatClient = new TestChatClient();
    private final BotLogic botLogic = new BotLogic(testChatClient, 12L, 11L, 13L,
            new TermsDictionaryRepo(System.getenv("MONGO_URI")), new ModeratingTermsDictionaryRepo(System.getenv("MONGO_URI")), new StandardResponses(),
            new StandardUserRequest(), new CallbackButton(), new DecisionOnTerm(),
            new UsersRepository(System.getenv("MONGO_URI")));

    @org.junit.jupiter.api.Test
    void testBotLogicStart() {
        botLogic.respondUser(1233L, "/start");
        assertEquals("Привет, я чат-бот! Чтобы узнать что я умею, напишите \"Помощь\"", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
        }

    @org.junit.jupiter.api.Test
    void testBotLogicHello() {
        botLogic.respondUser(1233L, "/start");
        botLogic.respondUser(1233L, "привет");
        assertEquals("Привет!", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicHelp() {
        botLogic.respondUser(1233L, "/start");
        botLogic.respondUser(1233L, "помощь");
        assertEquals("""
            Я умею:
            -Дать вам определение для рандомного или конкретного ролевого термина. Для этого напишите "Дай определение"
            -Ну и здороваться могу, да. Просто напишите "Привет\"""", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicRandomTerm() {
        botLogic.respondUser(1233L, "/start");
        botLogic.respondUser(1233L, "дай определение");
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
        botLogic.respondUser(1233L, "рандомное");
        assertTrue(new TermsDictionary().containsTermOnDictionary(testChatClient.currentMes.split(" - ")[0].toLowerCase(Locale.ROOT)));
        assertEquals(1233L, testChatClient.currentChatId);
        botLogic.respondUser(1233L, "назад");
        assertEquals("Ок(", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);

    }

    @org.junit.jupiter.api.Test
    void testBotLogicCertainTermCancel() {
        botLogic.respondUser(1233L, "/start");
        botLogic.respondUser(1233L, "дай определение");
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
        botLogic.respondUser(1233L, "конкретное");
        assertEquals("Определение какого термина хотите узнать?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
        botLogic.respondUser(1233L, "отменить");
        assertEquals("Ок(", testChatClient.prevMes);
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCertainTerm() {
        botLogic.respondUser(1233L, "/start");
        botLogic.respondUser(1233L, "дай определение");
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
        botLogic.respondUser(1233L, "конкретное");
        assertEquals("Определение какого термина хотите узнать?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
        botLogic.respondUser(1233L, "лор");
        assertEquals(new TermsDictionary().getCertainDefinition("лор").term,
                testChatClient.prevMes.split(" - ")[0].toLowerCase(Locale.ROOT));
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
        assertEquals(1233L, testChatClient.currentChatId);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicSimilarTerm(){
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "дай определение");
        botLogic.respondUser(123L, "конкретное");
        botLogic.respondUser(123L, "рол");
        assertEquals("Возможно вы имели ввиду что-то из этого: лор ?\n" +
                "Выберите и напишите слово из списка, если подходящих среди них не оказалось - нажмите \"Отменить\"",
                testChatClient.currentMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicUnknownCommand(){
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "fsjkbvbx,ukdsf");
        assertEquals("Извините, я вас не понимаю(", testChatClient.currentMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicUnknownTerm(){
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "дай определение");
        botLogic.respondUser(123L, "конкретное");
        botLogic.respondUser(123L, "qwerty");
        assertEquals("У меня нет похожих слов в словаре( Но я ещё учусь! Хотите добавить в мой словарь определение для термина qwerty?",
                testChatClient.currentMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCancelToAddNewTerm()
    {
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "дай определение");
        botLogic.respondUser(123L, "конкретное");
        botLogic.respondUser(123L, "qwerty");
        botLogic.respondUser(123L, "нет");
        assertEquals("Ок(",testChatClient.prevMes);
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicAddToNewTerm(){
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "дай определение");
        botLogic.respondUser(123L, "конкретное");
        botLogic.respondUser(123L, "qwerty");
        botLogic.respondUser(123L, "да");
        assertEquals("Опишите мне, что такое qwerty?", testChatClient.currentMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCancelToAddDefinition(){
        botLogic.respondUser(123L, "/start");
        botLogic.respondUser(123L, "дай определение");
        botLogic.respondUser(123L, "конкретное");
        botLogic.respondUser(123L, "qwerty");
        botLogic.respondUser(123L, "да");
        botLogic.respondUser(123L, "отменить");
        assertEquals("Ок(", testChatClient.prevMes);
        assertEquals("Рандомное или конкретное определение?", testChatClient.currentMes);
    }
}
