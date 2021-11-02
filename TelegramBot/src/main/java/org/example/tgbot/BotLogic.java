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
        } else {
            var result = parsingUserMessage(message);
            switch (result) {
                case HELP -> messageToUser = standardResponsesToUser.helpMessage;
                case GREETING -> messageToUser = standardResponsesToUser.gettingMessage;
                case OUT_TERM -> messageToUser = termsDictionary.getRandomTerm();
                case UNKNOWN_COMMAND -> messageToUser = standardResponsesToUser.unknownCommand;
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
        return Requests.UNKNOWN_COMMAND;
    }
}
