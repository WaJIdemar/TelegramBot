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
        } else if (users.get(id).getDialogState() == DialogState.WAIT_TERM) {
            if (message.equals("/отмена")) {
                users.get(id).changeDialogState(DialogState.DEFAULT);
                messageToUser = "ок(";
            } else {
                if (termsDictionary.containsTermOnDictionary(message)) {
                    messageToUser = termsDictionary.getCertainDefinition(message);
                    users.get(id).changeDialogState(DialogState.DEFAULT);
                } else {
                    String similarTerm = termsDictionary.searchSimilarTermOnDictionary(message);
                    if ("".equals(similarTerm)) {
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
        } else if (users.get(id).getDialogState() == DialogState.WAIT_DEFINITION) {
            if (message.equals("/отмена"))
                messageToUser = "Ок(";
            else
               messageToUser = "Ваше определение отправлено на рассмотрение";
            users.get(id).changeDialogState(DialogState.DEFAULT);
        } else if (users.get(id).getDialogState() == DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT) {
            if (message.equals("да")) {
                messageToUser = "Введите определение";
                buttonsMenu = allButtonsMenu.getCancel();
                users.get(id).changeDialogState(DialogState.WAIT_DEFINITION);
            } else {
                messageToUser = "Ок(";
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
        } else if (users.get(id).getDialogState() == DialogState.WAIT_CONFIRMATION_TERM_INPUT){
            if (message.equals("да")){
                messageToUser = termsDictionary.getCertainDefinition(users.get(id).getUserTerm());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
            else {
                messageToUser = standardResponsesToUser.writeDefinition;
                buttonsMenu = allButtonsMenu.getYesOrNo();
                users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
            }
        }
        else {
            var result = parsingUserMessage(message);
            switch (result) {
                case HELP -> messageToUser = standardResponsesToUser.helpMessage;
                case GREETING -> messageToUser = standardResponsesToUser.gettingMessage;
                case OUT_TERM -> {
                    messageToUser = standardResponsesToUser.outTerm;
                    buttonsMenu = allButtonsMenu.getRandomOrCertainTerm();
                }
                case RANDOM_TERM -> messageToUser = termsDictionary.getRandomTerm();
                case CERTAIN_TERM -> {
                    messageToUser = standardResponsesToUser.waitTerm;
                    users.get(id).changeDialogState(DialogState.WAIT_TERM);
                    buttonsMenu = allButtonsMenu.getCancel();
                }
                case UNKNOWN_COMMAND -> {
                    messageToUser = standardResponsesToUser.unknownCommand;
                    buttonsMenu = allButtonsMenu.getDefaultButtonsMenu();
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
        return Requests.UNKNOWN_COMMAND;
    }
}
