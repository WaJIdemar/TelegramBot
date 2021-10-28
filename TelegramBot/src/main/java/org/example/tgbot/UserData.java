package org.example.tgbot;

import java.util.*;

public class UserData {
    private final Map<Long, User> users = new HashMap<>();
    private final DictionaryTerms dictionaryTerms = new DictionaryTerms();
    private final StandardResponsesToUser standardResponsesToUser = new StandardResponsesToUser();
    private final StandardUserRequest standardUserRequest = new StandardUserRequest();
    private ButtonsMenuStatus buttonsMenuStatus = ButtonsMenuStatus.STARTMENU;

    public String messageToUser(long id, String message) {
        message = message.toLowerCase(Locale.ROOT);
        String messageToUser = "";
        if (users.get(id) == null) {
            users.put(id, new User(id));
            users.get(id).logs.add(message);
            messageToUser = standardResponsesToUser.startMessage;
            changeButtonsMenuStatus(Requests.GREETING);
        } else {
            users.get(id).logs.add(message);
            var result = parsingUserMessage(message);
            switch (result) {
                case HELP -> messageToUser = standardResponsesToUser.helpMessage;
                case GREETING -> messageToUser = standardResponsesToUser.gettingMessage;
                case OUTTERM -> messageToUser = dictionaryTerms.getRandomTerm();
                case UNKNOWCOMMAND -> messageToUser = standardResponsesToUser.unknownCommand;
            }
        }
        return messageToUser;
    }

    private Requests parsingUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return Requests.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return Requests.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return Requests.OUTTERM;
        return Requests.UNKNOWCOMMAND;
    }

    private void changeButtonsMenuStatus(Requests requests){
        switch (requests){
            case HELP, GREETING, OUTTERM, UNKNOWCOMMAND -> buttonsMenuStatus = ButtonsMenuStatus.STARTMENU;
        }
    }

    public ButtonsMenuStatus getButtonsMenuStatus(){
        return buttonsMenuStatus;
    }
}
