package org.example.tgbot;

public class TelegramChatClient implements ChatClient {

    private final TelegramBot telegramBot;

    public TelegramChatClient(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public Long getModeratorGroupId() {
        return Long.parseLong(telegramBot.moderatorGroupId);
    }

    @Override
    public void sendMessage(Long chatId, String text, Keyboard keyboard) {
        telegramBot.sendMessage(chatId, text, keyboard);
    }

    @Override
    public void sendMessageModeratorGroup(TermDefinition termDefinition) {
        telegramBot.sendMessageModeratorGroup(termDefinition);
    }
}
