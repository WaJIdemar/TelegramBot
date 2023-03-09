package org.tgbot.databases.elements;

public class TermDefinition {
    public String term;
    public String definition;

    public TermDefinition() {
    }

    public TermDefinition(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String createToString(){
        return term.substring(0, 1).toUpperCase() + term.substring(1)
                + " - " + definition;
    }
}
