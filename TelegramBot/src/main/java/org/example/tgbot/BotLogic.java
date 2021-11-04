package org.example.tgbot;

import java.util.*;

public class BotLogic {
    private final Map<Long, User> users = new HashMap<>();
    private final TermsDictionary termsDictionary = new TermsDictionary();
    private final StandardResponsesToUser standardResponsesToUser = new StandardResponsesToUser();
    private final StandardUserRequest standardUserRequest = new StandardUserRequest();
    private final ButtonsMenu allButtonsMenu = new ButtonsMenu();

    public ResponseToUser responseUser(long id, String message) {
        message = message.toLowerCase(Locale.ROOT);
        String messageToUser = "";
        List<List<String>> buttonsMenu = allButtonsMenu.getDefaultButtonsMenu();
        if (!users.containsKey(id)) {
            users.put(id, new User(id));
            messageToUser = standardResponsesToUser.startMessage;
            return new ResponseToUser(messageToUser, buttonsMenu);
        }
        var request = parsingUserMessage(message);
        switch (users.get(id).getDialogState()) {
            case WAIT_TERM -> {
                if (request == Requests.CANCEL) {
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                    messageToUser = standardResponsesToUser.cancel;
                } else {
                    if (termsDictionary.containsTermOnDictionary(message)) {
                        messageToUser = termsDictionary.getCertainDefinition(message);
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    } else {
                        String similarTerm = termsDictionary.searchSimilarTermOnDictionary(message);
                        if (Objects.equals(similarTerm, "")) {
                            messageToUser = standardResponsesToUser.termNotFound;
                            users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                        } else {
                            messageToUser = standardResponsesToUser.userAgree + similarTerm;
                            users.get(id).changeUserTerm(similarTerm);
                            users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_TERM_INPUT);
                        }
                        buttonsMenu = allButtonsMenu.getYesOrNo();
                    }
                }
            }
            case WAIT_DEFINITION -> {
                if (request == Requests.CANCEL)
                    messageToUser = standardResponsesToUser.cancel;
                else
                    messageToUser = standardResponsesToUser.definitionSentForConsideration;
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> {
                if (request == Requests.CONFIRMATION) {
                    messageToUser = standardResponsesToUser.waitDefinition;
                    buttonsMenu = allButtonsMenu.getCancel();
                    users.get(id).changeDialogState(DialogState.WAIT_DEFINITION);
                } else {
                    messageToUser = standardResponsesToUser.cancel;
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                }
            }
            case WAIT_CONFIRMATION_TERM_INPUT -> {
                if (request == Requests.CONFIRMATION) {
                    messageToUser = termsDictionary.getCertainDefinition(users.get(id).getUserTerm());
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                } else {
                    messageToUser = standardResponsesToUser.writeDefinition;
                    buttonsMenu = allButtonsMenu.getYesOrNo();
                    users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                }
            }
            case WAIT_RANDOM_OR_CERTAIN_TERM -> {
                switch (request) {
                    case RANDOM_TERM -> {
                        messageToUser = termsDictionary.getRandomTerm();
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                    case CERTAIN_TERM -> {
                        messageToUser = standardResponsesToUser.waitTerm;
                        users.get(id).changeDialogState(DialogState.WAIT_TERM);
                        buttonsMenu = allButtonsMenu.getCancel();
                    }
                    case CANCEL -> {
                        messageToUser = standardResponsesToUser.cancel;
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                        buttonsMenu = allButtonsMenu.getDefaultButtonsMenu();
                    }
                    default -> {
                        messageToUser = standardResponsesToUser.unknownCommand;
                        buttonsMenu = allButtonsMenu.getDefaultButtonsMenu();
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                }
            }
            case DEFAULT -> {
                switch (request) {
                    case HELP -> messageToUser = standardResponsesToUser.helpMessage;
                    case GREETING -> messageToUser = standardResponsesToUser.gettingMessage;
                    case OUT_TERM -> {
                        messageToUser = standardResponsesToUser.outTerm;
                        buttonsMenu = allButtonsMenu.getRandomOrCertainTerm();
                        users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
                    }
                    default -> {
                        messageToUser = standardResponsesToUser.unknownCommand;
                        buttonsMenu = allButtonsMenu.getDefaultButtonsMenu();
                        users.get(id).changeDialogState(DialogState.DEFAULT);
                    }
                }
            }
        }
        return new ResponseToUser(messageToUser, buttonsMenu);
    }

    private Requests parsingUserMessage(String message) {
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
