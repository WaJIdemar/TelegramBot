package org.tgbot.databases.elements;

import org.tgbot.DialogState;

import java.util.ArrayList;

public class User {
    private Long userId;
    private DialogState dialogState;
    private ArrayList<String> similarWordsTermsDictionary;
    private String userTerm;
    public Boolean banned;

    public User() {
    }


    public User(Long userId) {
        this.userId = userId;
        dialogState = DialogState.DEFAULT;
        banned = false;
    }

    public void setDialogState(DialogState newDialogState) {
        dialogState = newDialogState;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserSimilarWordsTermsDictionary(ArrayList<String> terms) {
        similarWordsTermsDictionary = terms;
    }

    public ArrayList<String> getUserSimilarWordsTermsDictionary() {
        return similarWordsTermsDictionary;
    }

    public String getUserTerm() {
        return userTerm;
    }

    public void setUserTerm(String userTerm) {
        this.userTerm = userTerm;
    }
}

