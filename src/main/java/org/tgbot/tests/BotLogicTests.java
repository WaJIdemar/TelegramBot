package org.tgbot.tests;

import org.tgbot.DecisionOnTerm;
import org.tgbot.DialogState;
import org.tgbot.StandardResponses;
import org.tgbot.StandardUserRequest;
import org.tgbot.buttons.CallbackButton;
import org.tgbot.databases.elements.ModeratorTermDefinition;
import org.tgbot.databases.elements.TermDefinition;
import org.tgbot.keyboards.*;
import org.tgbot.logic.BotLogic;
import org.tgbot.tests.model.TestChatClient;
import org.tgbot.tests.model.TestDatabaseUsers;
import org.tgbot.tests.model.TestModeratingTermsDictionary;
import org.tgbot.tests.model.TestTermsDictionary;

import static org.junit.jupiter.api.Assertions.*;

public class BotLogicTests {

    private final TestChatClient testChatClient;
    private final TestDatabaseUsers testDatabaseUsers;
    private final TestTermsDictionary testTermsDictionary;
    private final TestModeratingTermsDictionary testModeratingTermsDictionary;

    private final StandardResponses standardResponses;

    private final Long userId;

    private final Long moderatorGroupId;

    private final DecisionOnTerm decisionOnTerm;

    private final BotLogic botLogic;

    private BotLogicTests() {
        testChatClient = new TestChatClient();
        testDatabaseUsers = new TestDatabaseUsers();
        testTermsDictionary = new TestTermsDictionary();
        testModeratingTermsDictionary = new TestModeratingTermsDictionary();
        standardResponses = new StandardResponses();
        userId = 123L;
        moderatorGroupId = 12L;
        decisionOnTerm = new DecisionOnTerm();
        botLogic = new BotLogic(testChatClient, moderatorGroupId, 11L, 13L, testTermsDictionary,
                testModeratingTermsDictionary, standardResponses,
                new StandardUserRequest(), new CallbackButton(), new DecisionOnTerm(), testDatabaseUsers);
    }

    private void clean() {

        testTermsDictionary.clear();
        testDatabaseUsers.clear();
        testModeratingTermsDictionary.clear();
        testChatClient.clear();
    }

    @org.junit.jupiter.api.Test
    void testBotLogicStart() {

        clean();

        botLogic.respondUser(userId, "/start");

        assertEquals(standardResponses.startMessage, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertTrue(testDatabaseUsers.userContains(userId));
        assertEquals(DialogState.DEFAULT, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertInstanceOf(DefaultKeyboard.class, testChatClient.currentKeyboard);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicHelp() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "помощь");

        assertEquals(standardResponses.helpMessage, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertTrue(testDatabaseUsers.userContains(userId));
        assertEquals(DialogState.DEFAULT, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertInstanceOf(DefaultKeyboard.class, testChatClient.currentKeyboard);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicGetTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");

        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.outTerm, testChatClient.currentMes);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicGetRandomTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "рандомное");

        assertEquals(userId, testChatClient.currentChatId);
        TermDefinition termDefinition = testTermsDictionary.getRandomTerm();
        assertEquals(termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                + " - " + termDefinition.definition, testChatClient.currentMes);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCertainTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");

        assertEquals(standardResponses.waitTerm, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(CancelKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.WAIT_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCertainTermCancel() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "отменить");

        assertEquals(standardResponses.outTerm, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicGetCertainTerm() {

        clean();

        TermDefinition termDefinition = new TermDefinition("лор", "бла-бла-бла");
        testTermsDictionary.addNewTerm(termDefinition);

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "лор");

        assertEquals(termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                + " - " + termDefinition.definition, testChatClient.previousMes);
        assertEquals(standardResponses.outTerm, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicSimilarTerm() {

        clean();

        testTermsDictionary.addNewTerm(new TermDefinition("лор", "бла-бла-бла"));

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "рол");

        assertEquals(standardResponses.userAgree.formatted("лор"), testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(CancelKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.WAIT_WORD_INPUT, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicUnknownCommand() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "fsjkbvbx,ukdsf");

        assertEquals(standardResponses.unknownCommand, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(DefaultKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.DEFAULT, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicUnknownTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");

        assertEquals(standardResponses.termNotFound.formatted("qwerty"), testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertInstanceOf(YesOrNoKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicCancelToAddNewTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "нет");

        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.outTerm, testChatClient.currentMes);
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
        assertTrue(testTermsDictionary.isEmpty());
        assertTrue(testModeratingTermsDictionary.isEmpty());
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicAddToNewTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");

        assertEquals(standardResponses.waitDefinition.formatted("qwerty"), testChatClient.currentMes);
        assertInstanceOf(CancelKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(DialogState.WAIT_DEFINITION, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertTrue(testTermsDictionary.isEmpty());
        assertTrue(testModeratingTermsDictionary.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicAddToNewTermDefinition() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "qwerty1");

        assertEquals(standardResponses.definitionSentForConsideration, testChatClient.previousMes);
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
        assertTrue(testTermsDictionary.isEmpty());
        assertFalse(testModeratingTermsDictionary.isEmpty());

        ModeratorTermDefinition moderatorTermDefinition = testModeratingTermsDictionary.getModeratorTermDefinition(0L);
        TermDefinition termDefinition = moderatorTermDefinition.getTermDefinition();

        assertEquals(userId, moderatorTermDefinition.getUserId());
        assertEquals(0L, moderatorTermDefinition.getModeratorTermId());
        assertEquals("qwerty", termDefinition.term);
        assertEquals("qwerty1", termDefinition.definition);
        assertInstanceOf(AcceptingKeyboard.class, testChatClient.currentCallbackKeyboard);
        assertEquals(standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0),
                testChatClient.currentMesCallback);

    }

    @org.junit.jupiter.api.Test
    void testBotLogicCancelToAddDefinition() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "отменить");

        assertEquals(standardResponses.outTerm, testChatClient.currentMes);
        assertInstanceOf(RandomOrCertainTermKeyboard.class, testChatClient.currentKeyboard);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM, testDatabaseUsers.getUserOrCreate(userId).getDialogState());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicAcceptedTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "qwerty1");
        botLogic.processingCallBack(moderatorGroupId, 123,
                standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0), "accept_0");

        assertEquals(moderatorGroupId, testChatClient.currentChatIdCallback);
        assertEquals(standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0) +
                "\n______\n" + decisionOnTerm.accept, testChatClient.currentMes);

        TermDefinition termDefinition = testTermsDictionary.getCertainDefinition("qwerty");

        assertEquals("qwerty", termDefinition.term);
        assertEquals("qwerty1", termDefinition.definition);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.acceptTerm.formatted("Qwerty - qwerty1"), testChatClient.previousMes);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicRejectTerm() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "qwerty1");
        botLogic.processingCallBack(moderatorGroupId, 123,
                standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0), "reject_0");

        assertEquals(moderatorGroupId, testChatClient.currentChatIdCallback);
        assertEquals(standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0) +
                "\n______\n" + decisionOnTerm.reject, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.rejectTerm.formatted("Qwerty - qwerty1"), testChatClient.previousMes);
        assertTrue(testTermsDictionary.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void testBotLogicBanUser() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "qwerty1");
        botLogic.processingCallBack(moderatorGroupId, 123,
                standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0), "ban_0");

        assertEquals(moderatorGroupId, testChatClient.currentChatIdCallback);
        assertEquals(standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0) +
                "\n______\n" + decisionOnTerm.ban, testChatClient.currentMes);
        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.banUser.formatted("Qwerty - qwerty1"), testChatClient.previousMes);
        assertTrue(testTermsDictionary.isEmpty());
        assertTrue(testDatabaseUsers.getUserOrCreate(userId).banned);
    }

    @org.junit.jupiter.api.Test
    void testBotLogicAddTermBanUser() {

        clean();

        botLogic.respondUser(userId, "/start");
        botLogic.respondUser(userId, "дай определение");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");
        botLogic.respondUser(userId, "да");
        botLogic.respondUser(userId, "qwerty1");
        botLogic.processingCallBack(moderatorGroupId, 123,
                standardResponses.messageForModerator.formatted(userId, "qwerty", "qwerty1", 0), "ban_0");
        botLogic.respondUser(userId, "конкретное");
        botLogic.respondUser(userId, "qwerty");

        assertEquals(userId, testChatClient.currentChatId);
        assertEquals(standardResponses.notFondTermsAndUserBanned, testChatClient.previousMes);
        assertTrue(testDatabaseUsers.getUserOrCreate(userId).banned);
    }
}
