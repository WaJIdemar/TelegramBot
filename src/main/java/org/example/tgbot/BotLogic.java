package org.example.tgbot;

import org.example.tgbot.buttons.CallbackButton;
import org.example.tgbot.databases.*;
import org.example.tgbot.databases.elements.ModeratorTermDefinition;
import org.example.tgbot.databases.elements.TermDefinition;
import org.example.tgbot.databases.elements.User;
import org.example.tgbot.keyboards.*;

import java.util.*;

public class BotLogic {
    private final DatabaseUsers users;
    private final TermsDictionary termsDictionary;
    private final ModeratingTermsDictionary moderatingTermsDictionaryRepo;
    private final StandardResponses standardResponses;
    private final StandardUserRequest standardUserRequest;
    private final ChatClient chatClient;
    private final CallbackButton callbackButton;
    private final DecisionOnTerm decisionOnTerm;
    private final Long moderatorGroupId;
    private final Long adminGroupId;
    private final Long channelId;

    public BotLogic(ChatClient chatClient, Long moderatorGroupId,  Long adminGroupId, Long channelId, TermsDictionary termsDictionary,
                    ModeratingTermsDictionary moderatingTermsDictionaryRepo, StandardResponses standardResponses,
                    StandardUserRequest standardUserRequest, CallbackButton callbackButton, DecisionOnTerm decisionOnTerm,
                    DatabaseUsers users) {
        this.chatClient = chatClient;
        this.moderatorGroupId = moderatorGroupId;
        this.adminGroupId = adminGroupId;
        this.termsDictionary = termsDictionary;
        this.moderatingTermsDictionaryRepo = moderatingTermsDictionaryRepo;
        this.standardResponses = standardResponses;
        this.standardUserRequest = standardUserRequest;
        this.callbackButton = callbackButton;
        this.decisionOnTerm = decisionOnTerm;
        this.channelId = channelId;
        this.users = users;
    }

    public void respondUser(Long userId, String message) {
        if (Objects.equals(userId, moderatorGroupId) || Objects.equals(userId, adminGroupId) || Objects.equals(userId, channelId))
            return;
        message = message.toLowerCase(Locale.ROOT);
        if (Objects.equals(message, standardUserRequest.start)) {
            users.put(new User(userId));
            chatClient.sendMessage(userId, standardResponses.startMessage, new DefaultKeyboard());
            return;
        }
        var request = parseUserMessage(message);
        User user = users.getUserOrCreate(userId);
        switch (user.getDialogState()) {
            case WAIT_TERM -> acceptUserTerm(user, message, request); // Ждём пока пользователь введёт конкретный термин
            case WAIT_WORD_INPUT -> acceptWordInput(user, message, request); // Ждём пока пользователь введёт слово, чтобы выдать определение или попросить написать определение
            case WAIT_DEFINITION -> acceptDefinition(user, message, request); // Ждём опеределение от пользователя
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> acceptConfirmationDefinitionInput(user, request);// Ждём подтверждения, что пользователь хочет написать определение
            case WAIT_RANDOM_OR_CERTAIN_TERM -> acceptRandomOrCertainTerm(user, request); // Ждём какое определение хочет пользователь конкретно или конкретное
            case DEFAULT -> acceptDefaultState(user, request); // Стандартное состояние
        }
        users.put(user);
    }

    private void acceptUserTerm(User user, String message, UserIntent userIntent) {
        if (userIntent == UserIntent.CANCEL) {
            user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            chatClient.sendMessage(user.getUserId(), standardResponses.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
        } else {
            if (termsDictionary.containsTermOnDictionary(message)) {
                TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
                String text = termDefinition.createToString();
                chatClient.sendMessage(user.getUserId(), text, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            } else {
                user.setUserTerm(message);
                ArrayList<String> similarTerms = termsDictionary.searchSimilarTermsOnDictionary(message);
                user.setUserSimilarWordsTermsDictionary(similarTerms);

                if (Objects.equals(similarTerms, new ArrayList<String>())) {
                    if (!user.banned) {
                        chatClient.sendMessage(user.getUserId(), standardResponses.termNotFound.formatted(message), new YesOrNoKeyboard());
                        user.setDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                    } else {
                        user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                        chatClient.sendMessage(user.getUserId(), standardResponses.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                        chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                    }
                } else {
                    user.setUserSimilarWordsTermsDictionary(similarTerms);
                    chatClient.sendMessage(user.getUserId(), standardResponses.userAgree.formatted(String.join(", ", similarTerms)),
                            new CancelKeyboard());
                    user.setDialogState(DialogState.WAIT_WORD_INPUT);
                }
            }
        }
    }

    private void acceptWordInput(User user, String message, UserIntent userIntent) {
        if (userIntent == UserIntent.CANCEL) {
            if (!user.banned) {
                chatClient.sendMessage(user.getUserId(), standardResponses.writeDefinition.formatted(user.getUserTerm()), new YesOrNoKeyboard());
                user.setDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
            } else {
                user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                chatClient.sendMessage(user.getUserId(), standardResponses.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            }

        } else if (user.getUserSimilarWordsTermsDictionary().contains(message)) {
            TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
            String text = termDefinition.createToString();
            chatClient.sendMessage(user.getUserId(), text, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(user.getUserId(), standardResponses.invalidInputWaitWord, new CancelKeyboard());
        }
    }

    private void acceptDefinition(User user, String message, UserIntent userIntent) {
        if (userIntent == UserIntent.CANCEL)
            chatClient.sendMessage(user.getUserId(), standardResponses.cancel, new RandomOrCertainTermKeyboard());
        else {
            chatClient.sendMessage(user.getUserId(), standardResponses.definitionSentForConsideration, new RandomOrCertainTermKeyboard());
            Long index = moderatingTermsDictionaryRepo.addNewTerm(user.getUserTerm(), message, user.getUserId());
            chatClient.sendMessage(moderatorGroupId,
                    standardResponses.messageForModerator.formatted(user.getUserId(), user.getUserTerm(), message, index),
            new AcceptingKeyboard(index));
        }
        chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
        user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
    }


    private void acceptConfirmationDefinitionInput(User user, UserIntent userIntent) {
        if (userIntent == UserIntent.YES) {
            chatClient.sendMessage(user.getUserId(), standardResponses.waitDefinition.formatted(user.getUserTerm()), new CancelKeyboard());
            user.setDialogState(DialogState.WAIT_DEFINITION);
        } else if (userIntent == UserIntent.NO) {
            chatClient.sendMessage(user.getUserId(), standardResponses.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(user.getUserId(), standardResponses.invalidInputYesOrNo, new YesOrNoKeyboard());
        }
    }

    private void acceptRandomOrCertainTerm(User user, UserIntent userIntent) {
        switch (userIntent) {
            case RANDOM_TERM -> {
                TermDefinition termDefinition = termsDictionary.getRandomTerm();
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(user.getUserId(), text, new RandomOrCertainTermKeyboard());
                user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            case CERTAIN_TERM -> {
                chatClient.sendMessage(user.getUserId(), standardResponses.waitTerm, new CancelKeyboard());
                user.setDialogState(DialogState.WAIT_TERM);
            }
            case BACK -> {
                chatClient.sendMessage(user.getUserId(), standardResponses.cancel, new DefaultKeyboard());
                user.setDialogState(DialogState.DEFAULT);
            }
            default -> {
                chatClient.sendMessage(user.getUserId(), standardResponses.invalidInputRandomOrCertain,
                        new RandomOrCertainTermKeyboard());
                user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
        }
    }

    private void acceptDefaultState(User user, UserIntent userIntent) {
        switch (userIntent) {
            case HELP -> chatClient.sendMessage(user.getUserId(), standardResponses.helpMessage, new DefaultKeyboard());
            case GREETING -> chatClient.sendMessage(user.getUserId(), standardResponses.gettingMessage, new DefaultKeyboard());
            case OUT_TERM -> {
                chatClient.sendMessage(user.getUserId(), standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                user.setDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            default -> {
                chatClient.sendMessage(user.getUserId(), standardResponses.unknownCommand, new DefaultKeyboard());
                user.setDialogState(DialogState.DEFAULT);
            }
        }
    }

    public void processingCallBack(Long chatId, Integer messageId, String text, String data) {
        String button = data.split("_")[0];
        Long keyModeratorTermsDictionary = Long.parseLong(data.split("_")[1]);
        ModeratorTermDefinition moderatorTermDefinition = moderatingTermsDictionaryRepo.getModeratorTermDefinition(keyModeratorTermsDictionary);
        TermDefinition termDefinition = moderatorTermDefinition.getTermDefinition();
        if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.accept)) {
            termsDictionary.addNewTerm(termDefinition);
            chatClient.sendMessage(moderatorTermDefinition.getUserId(),
                    standardResponses.acceptTerm.formatted(termDefinition.createToString()));
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.accept);
        } else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.ban)) {
            User user = users.getUserOrCreate(moderatorTermDefinition.getUserId());
            user.banned = true;
            users.put(user);
            chatClient.sendMessage(moderatorTermDefinition.getUserId(),
                    standardResponses.banUser.formatted(termDefinition.createToString()));
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.ban);
        }
        else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.reject)){
            chatClient.sendMessage(moderatorTermDefinition.getUserId(),
                    standardResponses.rejectTerm.formatted(termDefinition.createToString()));
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.reject);
        }

    }

    private UserIntent parseUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return UserIntent.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return UserIntent.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return UserIntent.OUT_TERM;
        if (Objects.equals(message, standardUserRequest.outRandomTerm)
                || Objects.equals(message, standardUserRequest.outRandomTerm2))
            return UserIntent.RANDOM_TERM;
        if (Objects.equals(message, standardUserRequest.outCertainTerm)
                || Objects.equals(message, standardUserRequest.outCertainTerm2))
            return UserIntent.CERTAIN_TERM;
        if (Objects.equals(message, standardUserRequest.cancel))
            return UserIntent.CANCEL;
        if (Objects.equals(message, standardUserRequest.yes))
            return UserIntent.YES;
        if (Objects.equals(message, standardUserRequest.no))
            return UserIntent.NO;
        if (Objects.equals(message, standardUserRequest.back))
            return UserIntent.BACK;
        return UserIntent.UNKNOWN_COMMAND;
    }
}
