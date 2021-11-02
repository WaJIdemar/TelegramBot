package org.example.tgbot;

public class User {
    private final long id;
    private DialogState dialogState;

    public User(long userId) {
        id = userId;
        dialogState = DialogState.DEFAULT;
    }

    public void changeDialogState(DialogState newDialogState) {
        dialogState = newDialogState;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public long getUserId() {
        return id;
    }
}

