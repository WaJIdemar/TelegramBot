package org.example.tgbot;

import java.util.ArrayList;

public class User {
    private final Long id;
    private DialogState dialogState;
    private ArrayList<String> similarWordsTermsDictionary;
    private String userTerm;

    public User(Long userId) {
        id = userId;
        dialogState = DialogState.DEFAULT;
    }

    public void changeDialogState(DialogState newDialogState) {
        dialogState = newDialogState;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public Long getUserId() {
        return id;
    }

    public void changeUserSimilarWordsTermsDictionary(ArrayList<String> terms) {
        similarWordsTermsDictionary = terms;
    }

    public ArrayList<String> getUserSimilarWordsTermsDictionary() {
        return similarWordsTermsDictionary;
    }

    public String getUserTerm() {
        return userTerm;
    }

    public void changeUserTerm(String userTerm){
        this.userTerm = userTerm;
    }
}

