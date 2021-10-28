package org.example.tgbot;

public class ResponseToUser {
    private final String messageToUser;
    private final ButtonsMenuStatus buttonsMenuStatus;

    public ResponseToUser(String messageToUser, ButtonsMenuStatus buttonsMenuStatus){
        this.messageToUser = messageToUser;
        this.buttonsMenuStatus = buttonsMenuStatus;
    }

    public String getMessageToUser(){
        return messageToUser;
    }

    public ButtonsMenuStatus getButtonsMenuStatus(){
        return buttonsMenuStatus;
    }
}
