package net.techcable.combattag.config;

import net.techcable.techutils.config.ConfigSerializer;

import org.bukkit.configuration.InvalidConfigurationException;

public enum Punishment {
    INSTAKILL,
    NPC;

    public static class Serializer implements ConfigSerializer<Punishment> {

        @Override
        public Object serialize(Punishment punishment) {
            switch (punishment) {
                case INSTAKILL:
                    return "instakill";
                case NPC:
                    return "npc";
                default:
                    throw new IllegalArgumentException("Invalid punishment");
            }
        }

        @Override
        public Punishment deserialize(Object o, Class<? extends Punishment> aClass) throws InvalidConfigurationException {
            if (!(o instanceof String)) throw new InvalidConfigurationException("Punishment must be a string");
            String s = (String) o;
            if (s.equalsIgnoreCase("instakill")) {
                return INSTAKILL;
            } else if (s.equalsIgnoreCase("npc")) {
                return NPC;
            } else throw new InvalidConfigurationException(o + " is not a valid punishment");
        }

        @Override
        public boolean canHandle(Class<?> aClass) {
            return Punishment.class.equals(aClass);
        }
    }
}
