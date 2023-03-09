package org.tgbot.databases.elements;

public class ModeratorTermDefinition {
    private TermDefinition termDefinition;
    private Long userId;
    private Long moderatorTermId;

    public ModeratorTermDefinition(){
    }

    public ModeratorTermDefinition(TermDefinition termDefinition, Long id, Long userId){
        moderatorTermId = id;
        this.termDefinition = termDefinition;
        this.userId = userId;
    }

    public Long getModeratorTermId(){
        return moderatorTermId;
    }

    public TermDefinition getTermDefinition(){
        return termDefinition;
    }

    public Long getUserId(){
        return userId;
    }
}
