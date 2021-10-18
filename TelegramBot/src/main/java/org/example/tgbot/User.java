package org.example.tgbot;

import java.util.ArrayList;
import java.util.List;

public class User {
    public long id;
    public List<String> logs;

    public User(long userId) {
        id = userId;
        logs = new ArrayList<>();
    }
}

