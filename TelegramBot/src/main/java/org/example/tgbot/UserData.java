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
                case UNKNOWCOMMAND -> massageToUser = StandardResponsesToUser.unknowComand;
            }
        }
        return massageToUser;
    }

    private static Requests parsingUserMessage(String massage) {
        var words = Arrays.stream(massage.split(" ")).toList();
        if (words.contains(StandartUserRequest.help))
            return Requests.HELP;
        if (words.contains(StandartUserRequest.getting))
            return Requests.GREETING;
        if (words.contains(StandartUserRequest.getData))
            return Requests.GETDATA;
        if (words.contains(StandartUserRequest.outData))
            return Requests.OUTDATA;
        return Requests.UNKNOWCOMMAND;
    }
}
