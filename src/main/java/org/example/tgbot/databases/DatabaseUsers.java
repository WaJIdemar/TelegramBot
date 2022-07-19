package org.example.tgbot.databases;

import org.example.tgbot.databases.elements.User;

public interface DatabaseUsers {
    User getUserOrCreate(Long chatId);

    void put(User user);
}
