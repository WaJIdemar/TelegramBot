package org.example.tgbot;

public interface ChatClient {
    Long getModeratorGroupId();
    
    void sendMessage(Long chatId, String text, Keyboard keyboard);

    void sendMessageModeratorGroup(TermDefinition termDefinition);
}
