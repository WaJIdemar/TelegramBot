package org.example.tgbot;

import java.util.*;


public class BotLogic {
    private final Map<Long, User> users = new HashMap<>();
    private final TermsDictionary termsDictionary;
    private final ModeratingTermsDictionary moderatingTermsDictionary;
    private final StandardResponses standardResponses;
    private final StandardUserRequest standardUserRequest;
    private final ChatClient chatClient;
    private final CallbackButton callbackButton;
    private final DecisionOnTerm decisionOnTerm;
    private final Long moderatorGroupId;
    private final Long adminGroupId;

    public BotLogic(ChatClient chatClient, Long moderatorGroupId, Long adminGroupId, TermsDictionary termsDictionary,
                    ModeratingTermsDictionary moderatingTermsDictionary, StandardResponses standardResponses,
                    StandardUserRequest standardUserRequest, CallbackButton callbackButton, DecisionOnTerm decisionOnTerm) {
        this.chatClient = chatClient;
        this.moderatorGroupId = moderatorGroupId;
        this.adminGroupId = adminGroupId;
        this.termsDictionary = termsDictionary;
        this.moderatingTermsDictionary = moderatingTermsDictionary;
        this.standardResponses = standardResponses;
        this.standardUserRequest = standardUserRequest;
        this.callbackButton = callbackButton;
        this.decisionOnTerm = decisionOnTerm;

    }

    public void respondUser(Long userId, String message) {
        if (Objects.equals(userId, moderatorGroupId) || Objects.equals(userId, adminGroupId))
            return;
        message = message.toLowerCase(Locale.ROOT);
        if (Objects.equals(message, standardUserRequest.start)) {
            if (users.containsKey(userId))
                users.replace(userId, new User(userId));
            else
                users.put(userId, new User(userId));
            chatClient.sendMessage(userId, standardResponses.startMessage, new DefaultKeyboard());
            return;
        }
        var request = parseUserMessage(message);
        switch (users.get(userId).getDialogState()) {
            case WAIT_TERM -> acceptTermUser(userId, message, request); // Ждём пока пользователь введёт конкретный термин
            case WAIT_WORD_INPUT -> acceptWordInput(userId, message, request); // Ждём пока пользователь введёт слово, чтобы выдать определение или попросить написать определение
            case WAIT_DEFINITION -> acceptDefinition(userId, message, request); // Ждём опеределение от пользователя
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> acceptConfirmationDefinitionInput(userId, request);// Ждём подтверждения, что пользователь хочет написать определение
            case WAIT_RANDOM_OR_CERTAIN_TERM -> acceptRandomOrCertainTerm(userId, request); // Ждём какое определение хочет пользователь конкретно или конкретное
            case DEFAULT -> acceptDefaultState(userId, request); // Стандартное состояние
        }
    }

    private void acceptTermUser(Long userId, String message, UserItent userItent) {
        if (userItent == UserItent.CANCEL) {
            users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            chatClient.sendMessage(userId, standardResponses.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
        } else {
            if (termsDictionary.containsTermOnDictionary(message)) {
                TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(userId, text, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            } else {
                users.get(userId).changeUserTerm(message);
                ArrayList<String> similarTerms = termsDictionary.searchSimilarTermsOnDictionary(message);
                users.get(userId).changeUserSimilarWordsTermsDictionary(similarTerms);

                if (Objects.equals(similarTerms, new ArrayList<String>())) {
                    if (!users.get(userId).banned) {
                        chatClient.sendMessage(userId, standardResponses.termNotFound.formatted(message), new YesOrNoKeyboard());
                        users.get(userId).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                    } else {
                        users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                        chatClient.sendMessage(userId, standardResponses.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                        chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                    }
                } else {
                    users.get(userId).changeUserSimilarWordsTermsDictionary(similarTerms);
                    chatClient.sendMessage(userId, standardResponses.userAgree.formatted(String.join(", ", similarTerms)),
                            new CancelKeyboard());
                    users.get(userId).changeDialogState(DialogState.WAIT_WORD_INPUT);
                }
            }
        }
    }

    private void acceptWordInput(Long userId, String message, UserItent userItent) {
        if (userItent == UserItent.CANCEL) {
            if (!users.get(userId).banned) {
                chatClient.sendMessage(userId, standardResponses.writeDefinition.formatted(users.get(userId).getUserTerm()), new YesOrNoKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
            } else {
                users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                chatClient.sendMessage(userId, standardResponses.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            }

        } else if (users.get(userId).getUserSimilarWordsTermsDictionary().contains(message)) {
            TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
            String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                    + " - " + termDefinition.definition;
            chatClient.sendMessage(userId, text, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(userId, standardResponses.invalidInputWaitWord, new CancelKeyboard());
        }
    }

    private void acceptDefinition(Long userId, String message, UserItent userItent) {
        if (userItent == UserItent.CANCEL)
            chatClient.sendMessage(userId, standardResponses.cancel, new RandomOrCertainTermKeyboard());
        else {
            chatClient.sendMessage(userId, standardResponses.definitionSentForConsideration, new RandomOrCertainTermKeyboard());
            Integer index = moderatingTermsDictionary.addNewTerm(users.get(userId).getUserTerm(), message, userId);
            chatClient.sendMessage(moderatorGroupId,
                    standardResponses.messageForModerator.formatted(users.get(userId).getUserTerm(), message, index),
            new AcceptingKeyboard(index));
        }
        chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
        users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
    }


    private void acceptConfirmationDefinitionInput(Long userId, UserItent userItent) {
        if (userItent == UserItent.YES) {
            chatClient.sendMessage(userId, standardResponses.waitDefinition.formatted(users.get(userId).getUserTerm()), new CancelKeyboard());
            users.get(userId).changeDialogState(DialogState.WAIT_DEFINITION);
        } else if (userItent == UserItent.NO) {
            chatClient.sendMessage(userId, standardResponses.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
            users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(userId, standardResponses.invalidInputYesOrNo, new YesOrNoKeyboard());
        }
    }

    private void acceptRandomOrCertainTerm(Long userId, UserItent userItent) {
        switch (userItent) {
            case RANDOM_TERM -> {
                TermDefinition termDefinition = termsDictionary.getRandomTerm();
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(userId, text, new RandomOrCertainTermKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            case CERTAIN_TERM -> {
                chatClient.sendMessage(userId, standardResponses.waitTerm, new CancelKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_TERM);
            }
            case BACK -> {
                chatClient.sendMessage(userId, standardResponses.cancel, new DefaultKeyboard());
                users.get(userId).changeDialogState(DialogState.DEFAULT);
            }
            default -> {
                chatClient.sendMessage(userId, standardResponses.invalidInputRandomOrCertain,
                        new RandomOrCertainTermKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
        }
    }

    private void acceptDefaultState(Long userId, UserItent userItent) {
        switch (userItent) {
            case HELP -> chatClient.sendMessage(userId, standardResponses.helpMessage, new DefaultKeyboard());
            case GREETING -> chatClient.sendMessage(userId, standardResponses.gettingMessage, new DefaultKeyboard());
            case OUT_TERM -> {
                chatClient.sendMessage(userId, standardResponses.outTerm, new RandomOrCertainTermKeyboard());
                users.get(userId).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            default -> {
                chatClient.sendMessage(userId, standardResponses.unknownCommand, new DefaultKeyboard());
                users.get(userId).changeDialogState(DialogState.DEFAULT);
            }
        }
    }

    public void processingCallBack(Long chatId, Integer messageId, String text, String data) {
        String button = data.split("_")[0];
        Integer keyModeratorTermsDictionary = Integer.parseInt(data.split("_")[1]);
        if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.accept)) {
            termsDictionary.addNewTerm(moderatingTermsDictionary.getDefinition(keyModeratorTermsDictionary));
            chatClient.sendMessage(moderatingTermsDictionary.get(keyModeratorTermsDictionary).userId, standardResponses.acceptTerm);
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.accept);
        } else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.ban)) {
            users.get(Long.parseLong(data.split("_")[1])).banned = true;
            chatClient.sendMessage(moderatingTermsDictionary.get(keyModeratorTermsDictionary).userId, standardResponses.banUser);
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.ban);
        }
        else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.reject)){
            chatClient.sendMessage(moderatingTermsDictionary.get(keyModeratorTermsDictionary).userId, standardResponses.rejectTerm);
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.reject);
        }

    }

    private UserItent parseUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return UserItent.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return UserItent.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return UserItent.OUT_TERM;
        if (Objects.equals(message, standardUserRequest.outRandomTerm)
                || Objects.equals(message, standardUserRequest.outRandomTerm2))
            return UserItent.RANDOM_TERM;
        if (Objects.equals(message, standardUserRequest.outCertainTerm)
                || Objects.equals(message, standardUserRequest.outCertainTerm2))
            return UserItent.CERTAIN_TERM;
        if (Objects.equals(message, standardUserRequest.cancel))
            return UserItent.CANCEL;
        if (Objects.equals(message, standardUserRequest.yes))
            return UserItent.YES;
        if (Objects.equals(message, standardUserRequest.no))
            return UserItent.NO;
        if (Objects.equals(message, standardUserRequest.back))
            return UserItent.BACK;
        return UserItent.UNKNOWN_COMMAND;
    }
}
