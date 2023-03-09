package org.tgbot.databases;

import org.tgbot.databases.elements.User;

public interface DatabaseUsers {
    User getUserOrCreate(Long chatId);

    void put(User user);
}
