package org.example.tgbot;

import java.util.*;

public class BotLogic {
    private final Map<Long, User> users = new HashMap<>();
    private final TermsDictionary termsDictionary = new TermsDictionary();
    private final StandardResponsesToUser standardResponsesToUser = new StandardResponsesToUser();
    private final StandardUserRequest standardUserRequest = new StandardUserRequest();
    private final ChatClient chatClient;

    public BotLogic(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void respondUser(Long id, String message) {
        message = message.toLowerCase(Locale.ROOT);
        if (!users.containsKey(id)) {
            users.put(id, new User(id));
            chatClient.sendMessage(id, standardResponsesToUser.startMessage, new DefaultKeyboard());
            return;
        }
        var request = parsUserMessage(message);
        switch (users.get(id).getDialogState()) {
            case WAIT_TERM -> { // Ждём пока пользователь введёт конкретный термин
                if (request == Requests.CANCEL) {
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                    chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                } else {
                    if (termsDictionary.containsTermOnDictionary(message)) {
                        DictionItem dictionItem = termsDictionary.getCertainDefinition(message);
                        String text = dictionItem.term.substring(0, 1).toUpperCase() + dictionItem.term.substring(1)
                                + " - " + dictionItem.definition;
                        chatClient.sendMessage(id, text, new DefaultKeyboard());
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    } else {
                        String similarTerm = termsDictionary.searchSimilarTermOnDictionary(message);
                        users.get(id).changeUserTerm(message);
                        if (Objects.equals(similarTerm, "")) {
                            chatClient.sendMessage(id, standardResponsesToUser.termNotFound.formatted(message), new YesOrNoKeyBoard());
                            users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                        } else {
                            chatClient.sendMessage(id, standardResponsesToUser.userAgree.formatted(similarTerm), new CancelKeyBoard());
                            users.get(id).changeDialogState(DialogState.WAIT_WORD_INPUT);
                        }
                    }
                }
            }
            case WAIT_WORD_INPUT -> { // Ждём пока пользователь введёт слово, чтобы выдать определение или попросить написать определение
                if (request == Requests.CANCEL) {
                    chatClient.sendMessage(id, standardResponsesToUser.writeDefinition.formatted(users.get(id).getUserTerm()), new YesOrNoKeyBoard());
                    users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                } else {
                    DictionItem dictionItem = termsDictionary.getCertainDefinition(message);
                    String text = dictionItem.term.substring(0, 1).toUpperCase() + dictionItem.term.substring(1)
                            + " - " + dictionItem.definition;
                    chatClient.sendMessage(id, text, new DefaultKeyboard());
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                }
            }
            case WAIT_DEFINITION -> { // Ждём опеределение от пользователя
                if (request == Requests.CANCEL)
                    chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                else
                    chatClient.sendMessage(id, standardResponsesToUser.definitionSentForConsideration, new DefaultKeyboard());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> { // Ждём подтверждения, что пользователь хочет написать определение
                if (request == Requests.CONFIRMATION) {
                    chatClient.sendMessage(id, standardResponsesToUser.waitDefinition.formatted(users.get(id).getUserTerm()), new CancelKeyBoard());
                    users.get(id).changeDialogState(DialogState.WAIT_DEFINITION);
                } else {
                    chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                }
            }
            case WAIT_RANDOM_OR_CERTAIN_TERM -> { // Ждём какое определение хочет пользователь конкретно или конкретное
                switch (request) {
                    case RANDOM_TERM -> {
                        DictionItem dictionItem = termsDictionary.getRandomTerm();
                        String text = dictionItem.term.substring(0, 1).toUpperCase() + dictionItem.term.substring(1)
                                + " - " + dictionItem.definition;
                        chatClient.sendMessage(id, text, new DefaultKeyboard());
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                    case CERTAIN_TERM -> {
                        chatClient.sendMessage(id, standardResponsesToUser.waitTerm, new CancelKeyBoard());
                        users.get(id).changeDialogState(DialogState.WAIT_TERM);
                    }
                    case CANCEL -> {
                        chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                    default -> {
                        chatClient.sendMessage(id, standardResponsesToUser.unknownCommand, new DefaultKeyboard());
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                }
            }
            case DEFAULT -> { // Стандартное состояние
                switch (request) {
                    case HELP -> chatClient.sendMessage(id, standardResponsesToUser.helpMessage, new DefaultKeyboard());
                    case GREETING -> chatClient.sendMessage(id, standardResponsesToUser.gettingMessage, new DefaultKeyboard());
                    case OUT_TERM -> {
                        chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyBoard());
                        users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                    }
                    default -> {
                        chatClient.sendMessage(id, standardResponsesToUser.unknownCommand, new DefaultKeyboard());
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                }
            }
        }
    }

    private Requests parsUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return Requests.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return Requests.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return Requests.OUT_TERM;
        if (Objects.equals(message, standardUserRequest.outRandomTerm))
            return Requests.RANDOM_TERM;
        if (Objects.equals(message, standardUserRequest.outCertainTerm))
            return Requests.CERTAIN_TERM;
        if (Objects.equals(message, standardUserRequest.cancel))
            return Requests.CANCEL;
        if (Objects.equals(message, standardUserRequest.yes))
            return Requests.CONFIRMATION;
        return Requests.UNKNOWN_COMMAND;
    }
}
