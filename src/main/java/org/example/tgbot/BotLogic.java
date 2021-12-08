package org.example.tgbot;

import java.util.*;


public class BotLogic {
    private final Map<Long, User> users = new HashMap<>();
    private final TermsDictionary termsDictionary;
    private final ModeratingTermsDictionary moderatingTermsDictionary;
    private final StandardResponsesToUser standardResponsesToUser;
    private final StandardUserRequest standardUserRequest;
    private final ChatClient chatClient;
    private final CallbackButton callbackButton;
    private final DecisionOnTerm decisionOnTerm;
    private final Long moderatorGroupId;
    private final Long adminGroupId;

    public BotLogic(ChatClient chatClient, Long moderatorGroupId, Long adminGroupId, TermsDictionary termsDictionary,
                    ModeratingTermsDictionary moderatingTermsDictionary,StandardResponsesToUser standardResponsesToUser,
                    StandardUserRequest standardUserRequest, CallbackButton callbackButton, DecisionOnTerm decisionOnTerm) {
        this.chatClient = chatClient;
        this.moderatorGroupId = moderatorGroupId;
        this.adminGroupId = adminGroupId;
        this.termsDictionary = termsDictionary;
        this.moderatingTermsDictionary = moderatingTermsDictionary;
        this.standardResponsesToUser = standardResponsesToUser;
        this.standardUserRequest = standardUserRequest;
        this.callbackButton = callbackButton;
        this.decisionOnTerm = decisionOnTerm;

    }

    public void respondUser(Long id, String message) {
        if (Objects.equals(id, moderatorGroupId) || Objects.equals(id, adminGroupId))
            return;
        message = message.toLowerCase(Locale.ROOT);
        if (Objects.equals(message, standardUserRequest.start)) {
            if (users.containsKey(id))
                users.replace(id, new User(id));
            else
                users.put(id, new User(id));
            chatClient.sendMessage(id, standardResponsesToUser.startMessage, new DefaultKeyboard());
            return;
        }
        var request = parseUserMessage(message);
        switch (users.get(id).getDialogState()) {
            case WAIT_TERM -> acceptTermUser(id, message, request); // Ждём пока пользователь введёт конкретный термин
            case WAIT_WORD_INPUT -> acceptWordInput(id, message, request); // Ждём пока пользователь введёт слово, чтобы выдать определение или попросить написать определение
            case WAIT_DEFINITION -> acceptDefinition(id, message, request); // Ждём опеределение от пользователя
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> acceptConfirmationDefinitionInput(id, request);// Ждём подтверждения, что пользователь хочет написать определение
            case WAIT_RANDOM_OR_CERTAIN_TERM -> acceptRandomOrCertainTerm(id, request); // Ждём какое определение хочет пользователь конкретно или конкретное
            case DEFAULT -> acceptDefaultState(id, request); // Стандартное состояние
        }
    }

    private void acceptTermUser(Long id, String message, RequestFromUser requestFromUser) {
        if (requestFromUser == RequestFromUser.CANCEL) {
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
        } else {
            if (termsDictionary.containsTermOnDictionary(message)) {
                TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            } else {
                users.get(id).changeUserTerm(message);
                ArrayList<String> similarTerms = termsDictionary.searchSimilarTermsOnDictionary(message);
                users.get(id).changeUserSimilarWordsTermsDictionary(similarTerms);

                if (Objects.equals(similarTerms, new ArrayList<String>())) {
                    if (!users.get(id).banned) {
                        chatClient.sendMessage(id, standardResponsesToUser.termNotFound.formatted(message), new YesOrNoKeyboard());
                        users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                    } else {
                        users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                        chatClient.sendMessage(id, standardResponsesToUser.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                        chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
                    }
                } else {
                    users.get(id).changeUserSimilarWordsTermsDictionary(similarTerms);
                    chatClient.sendMessage(id, standardResponsesToUser.userAgree.formatted(String.join(", ", similarTerms)),
                            new CancelKeyboard());
                    users.get(id).changeDialogState(DialogState.WAIT_WORD_INPUT);
                }
            }
        }
    }

    private void acceptWordInput(Long id, String message, RequestFromUser requestFromUser) {
        if (requestFromUser == RequestFromUser.CANCEL) {
            if (!users.get(id).banned) {
                chatClient.sendMessage(id, standardResponsesToUser.writeDefinition.formatted(users.get(id).getUserTerm()), new YesOrNoKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
            } else {
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                chatClient.sendMessage(id, standardResponsesToUser.notFondTermsAndUserBanned, new RandomOrCertainTermKeyboard());
                chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
            }

        } else if (users.get(id).getUserSimilarWordsTermsDictionary().contains(message)) {
            TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
            String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                    + " - " + termDefinition.definition;
            chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(id, standardResponsesToUser.invalidInputWaitWord, new CancelKeyboard());
        }
    }

    private void acceptDefinition(Long id, String message, RequestFromUser requestFromUser) {
        if (requestFromUser == RequestFromUser.CANCEL)
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
        else {
            chatClient.sendMessage(id, standardResponsesToUser.definitionSentForConsideration, new RandomOrCertainTermKeyboard());
            Integer index = moderatingTermsDictionary.addNewTerm(users.get(id).getUserTerm(), message);
            chatClient.sendMessage(moderatorGroupId,
                    standardResponsesToUser.messageForModerator.formatted(users.get(id).getUserTerm(), message, index),
            new AcceptingKeyboard(index, id));
        }
        chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
        users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
    }


    private void acceptConfirmationDefinitionInput(Long id, RequestFromUser requestFromUser) {
        if (requestFromUser == RequestFromUser.YES) {
            chatClient.sendMessage(id, standardResponsesToUser.waitDefinition.formatted(users.get(id).getUserTerm()), new CancelKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_DEFINITION);
        } else if (requestFromUser == RequestFromUser.NO) {
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(id, standardResponsesToUser.invalidInputYesOrNo, new YesOrNoKeyboard());
        }
    }

    private void acceptRandomOrCertainTerm(Long id, RequestFromUser requestFromUser) {
        switch (requestFromUser) {
            case RANDOM_TERM -> {
                TermDefinition termDefinition = termsDictionary.getRandomTerm();
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            case CERTAIN_TERM -> {
                chatClient.sendMessage(id, standardResponsesToUser.waitTerm, new CancelKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_TERM);
            }
            case BACK -> {
                chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
            default -> {
                chatClient.sendMessage(id, standardResponsesToUser.invalidInputRandomOrCertain,
                        new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
        }
    }

    private void acceptDefaultState(Long id, RequestFromUser requestFromUser) {
        switch (requestFromUser) {
            case HELP -> chatClient.sendMessage(id, standardResponsesToUser.helpMessage, new DefaultKeyboard());
            case GREETING -> chatClient.sendMessage(id, standardResponsesToUser.gettingMessage, new DefaultKeyboard());
            case OUT_TERM -> {
                chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            default -> {
                chatClient.sendMessage(id, standardResponsesToUser.unknownCommand, new DefaultKeyboard());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
        }
    }

    public void processingCallBack(Long chatId, Integer messageId, String text, String data) {
        String button = data.split("_")[0];
        if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.accept)) {
            termsDictionary.addNewTerm(moderatingTermsDictionary.getDefinition(Integer.parseInt(data.split("_")[1])));
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.accept);
        } else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.ban)) {
            users.get(Long.parseLong(data.split("_")[1])).banned = true;
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.ban);
        }
        else if (Objects.equals(button.toLowerCase(Locale.ROOT), callbackButton.reject)){
            chatClient.editMessage(chatId, messageId, text + "\n______\n" + decisionOnTerm.reject);
        }

    }

    private RequestFromUser parseUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return RequestFromUser.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return RequestFromUser.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return RequestFromUser.OUT_TERM;
        if (Objects.equals(message, standardUserRequest.outRandomTerm)
                || Objects.equals(message, standardUserRequest.outRandomTerm2))
            return RequestFromUser.RANDOM_TERM;
        if (Objects.equals(message, standardUserRequest.outCertainTerm)
                || Objects.equals(message, standardUserRequest.outCertainTerm2))
            return RequestFromUser.CERTAIN_TERM;
        if (Objects.equals(message, standardUserRequest.cancel))
            return RequestFromUser.CANCEL;
        if (Objects.equals(message, standardUserRequest.yes))
            return RequestFromUser.YES;
        if (Objects.equals(message, standardUserRequest.no))
            return RequestFromUser.NO;
        if (Objects.equals(message, standardUserRequest.back))
            return RequestFromUser.BACK;
        return RequestFromUser.UNKNOWN_COMMAND;
    }
}
