package org.example.tgbot;

import java.util.*;

public class UserData {
    public static Map<Long, User> logs = new HashMap<Long, User>();

    public static String massageToUser(long id, String massage) {
        massage = massage.toLowerCase(Locale.ROOT);
        String massageToUser = "";
        if (logs.get(id) == null) {
            logs.put(id, new User(id));
            logs.get(id).log.add(massage);
            massageToUser = StandardResponsesToUser.startMassage;
        } else if (Objects.equals(logs.get(id).log.get(logs.get(id).log.size() - 1), "запомни")) {
            logs.get(id).AddData(massage);
            logs.get(id).log.add(massage);
            massageToUser = "Запомнил";
        } else {
            logs.get(id).log.add(massage);
            var result = parsingUserMessage(massage);
            switch (result) {
                case HELP -> massageToUser = StandardResponsesToUser.helpMassage;
                case GREETING -> massageToUser = StandardResponsesToUser.gettingMassage;
                case GETDATA -> massageToUser = StandardResponsesToUser.gettingGetData;
                case OUTDATA -> massageToUser = logs.get(id).Data;
                case OUTTERM -> massageToUser = Dictionary.dictionaryForRandom[
                        new Random().nextInt(Dictionary.countOfTerms)];
                case UNKNOWCOMMAND -> massageToUser = StandardResponsesToUser.unknowComand;
            }
        }
        return massageToUser;
    }

    private static Requests parsingUserMessage(String massage) {
        if (Objects.equals(massage, StandartUserRequest.help))
            return Requests.HELP;
        if (Objects.equals(massage, StandartUserRequest.getting))
            return Requests.GREETING;
        if (Objects.equals(massage, StandartUserRequest.getData))
            return Requests.GETDATA;
        if (Objects.equals(massage, StandartUserRequest.outData))
            return Requests.OUTDATA;
        if (Objects.equals(massage, StandartUserRequest.outTerm))
            return Requests.OUTTERM;
        return Requests.UNKNOWCOMMAND;
    }
}
