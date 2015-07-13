package net.techcable.combattag.config;

import net.techcable.techutils.config.ConfigSerializer;

import org.bukkit.configuration.InvalidConfigurationException;

public enum DisplayMode {
    CHAT,
    ACTION_BAR,
    BOSS_BAR,
    NONE;

    public static class Serializer implements ConfigSerializer<DisplayMode> {

        @Override
        public Object serialize(DisplayMode mode) {
            switch (mode) {
                case CHAT:
                    return "chat";
                case ACTION_BAR:
                    return "actionBar";
                case BOSS_BAR:
                    return "bossBar";
                case NONE:
                    return "none";
                default:
                    throw new IllegalArgumentException("Invalid display mode " + mode);
            }
        }

        @Override
        public boolean canHandle(Class<?> type) {
            return DisplayMode.class.equals(type);
        }

        @Override
        public DisplayMode deserialize(Object yaml, Class<? extends DisplayMode> type) throws InvalidConfigurationException {
            if (!(yaml instanceof String)) throw new InvalidConfigurationException("Display modes must be strings");
            String s = (String) yaml;
            if (s.equalsIgnoreCase("chat")) return CHAT;
            else if (s.equalsIgnoreCase("actionBar")) return ACTION_BAR;
            else if (s.equalsIgnoreCase("bossBar")) return BOSS_BAR;
            else if (s.equalsIgnoreCase("none")) return NONE;
            else throw new InvalidConfigurationException(s + " is not a valid display mode");
        }
    }
}
