package net.techcable.combattag;

public enum DisplayMode {
    ACTION_BAR,
    BOSS_BAR,
    CHAT,
    NONE;

    public static DisplayMode parse(String s, DisplayMode def) {
        switch (s) {
            case "actionBar" :
                return ACTION_BAR;
            case "bossBar" :
                return BOSS_BAR;
            case "chat" :
                return CHAT;
            case "none" :
                return NONE;
            default :
                return def;
        }
    }
}
