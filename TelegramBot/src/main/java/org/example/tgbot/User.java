package org.example.tgbot;

public class User {
    private final Long  id;
    private DialogState dialogState;
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

    public void changeUserTerm(String term) {
        userTerm = term;
    }

    public String getUserTerm() {
        return userTerm;
    }
}

