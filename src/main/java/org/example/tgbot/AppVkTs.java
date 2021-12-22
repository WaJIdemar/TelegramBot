package org.example.tgbot;

public class AppVkTs {
    private String ts;

    public AppVkTs(String ts) {
        this.ts = ts;
    }

    public AppVkTs() {
    }

    public String getTs() {
        return ts;
    }

    public void changeTs(String ts){
        this.ts = ts;
    }
}
