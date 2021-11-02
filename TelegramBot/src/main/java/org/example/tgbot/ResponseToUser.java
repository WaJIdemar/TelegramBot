package org.example.tgbot;

import java.util.List;

public class ResponseToUser {
    private final String messageToUser;
    private final List<List<String>> buttonsMenu;

    public ResponseToUser(String messageToUser, List<List<String>> buttonsMenu){
        this.messageToUser = messageToUser;
        this.buttonsMenu = buttonsMenu;
    }

    public String getMessageToUser(){
        return messageToUser;
    }

    public List<List<String>> getButtonsMenuStatus(){
        return buttonsMenu;
    }
}
