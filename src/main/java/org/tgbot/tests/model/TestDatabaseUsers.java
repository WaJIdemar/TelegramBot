package org.tgbot.tests.model;

import org.tgbot.databases.DatabaseUsers;
import org.tgbot.databases.elements.User;

import java.util.HashMap;
import java.util.Map;

public class TestDatabaseUsers implements DatabaseUsers {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getUserOrCreate(Long chatId) {
        User user = users.get(chatId);
        return user == null ? new User(chatId) : user;
    }

    @Override
    public void put(User user) {
        if (!users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
        } else {
            users.replace(user.getUserId(), user);
        }
    }

    public void clear() {
        users.clear();
    }

    public boolean userContains(Long chatId) {
        return users.containsKey(chatId);
    }
}
