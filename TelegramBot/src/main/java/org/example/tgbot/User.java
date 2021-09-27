package org.example.tgbot;

import java.util.ArrayList;
import java.util.List;

public class User {
    public long id;
    public List<String> log;
    public String Data;

    public User(long userId) {
        id = userId;
        log = new ArrayList<>();
    }
    public void AddData(String OData)
    {
        Data = OData;
    }
}

