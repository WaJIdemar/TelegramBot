package org.example.tgbot;

import java.util.*;

public class UserData {
    private final Map<Long, User> logs = new HashMap<Long, User>();
    private final Dictionary dictionary = new Dictionary();
    private final StandardResponsesToUser standardResponsesToUser = new StandardResponsesToUser();
    private final StandardUserRequest standardUserRequest = new StandardUserRequest();

    public  String massageToUser(long id, String message) {
        message = message.toLowerCase(Locale.ROOT);
        String massageToUser = "";
        if (logs.get(id) == null) {
            logs.put(id, new User(id));
            logs.get(id).log.add(message);
            massageToUser = standardResponsesToUser.startMassage;
        } else if (Objects.equals(logs.get(id).log.get(logs.get(id).log.size() - 1), "запомни")) {
            logs.get(id).AddData(message);
            logs.get(id).log.add(message);
            massageToUser = "Запомнил";
        } else {
            logs.get(id).log.add(message);
            var result = parsingUserMessage(message);
            switch (result) {
                case HELP -> massageToUser = standardResponsesToUser.helpMassage;
                case GREETING -> massageToUser = standardResponsesToUser.gettingMassage;
                case GETDATA -> massageToUser = standardResponsesToUser.gettingGetData;
                case OUTDATA -> massageToUser = logs.get(id).Data;
                case OUTTERM -> massageToUser = dictionary.getTerm();
                case UNKNOWCOMMAND -> massageToUser = standardResponsesToUser.unknownCommand;
            }
        }
        return massageToUser;
    }

    private  Requests parsingUserMessage(String massage) {
        if (Objects.equals(massage, standardUserRequest.help))
            return Requests.HELP;
        if (Objects.equals(massage, standardUserRequest.getting))
            return Requests.GREETING;
        if (Objects.equals(massage, standardUserRequest.getData))
            return Requests.GETDATA;
        if (Objects.equals(massage, standardUserRequest.outData))
            return Requests.OUTDATA;
        if (Objects.equals(massage, standardUserRequest.outTerm))
            return Requests.OUTTERM;
        return Requests.UNKNOWCOMMAND;
    }
}
