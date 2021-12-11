package org.example.tgbot;

public class ModeratorTermDefinition {
    public TermDefinition termDefinition;
    public Long userId;
    public ModeratorTermDefinition(TermDefinition termDefinition, Long userId){
        this.termDefinition = termDefinition;
        this.userId = userId;
    }
}
