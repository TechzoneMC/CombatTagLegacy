package com.trc202.CombatTag;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import static com.trc202.CombatTag.Reflection.*;

/**
 * A 1.8 ActionBar
 * 
 * Works on protocol hack and real 1.8
 */
public class ActionBar {
    public ActionBar(String text) {
        this.text = text;
    }
    
    private final String text;
    
    public String getText() {
        return text;
    }
    
    public void sendTo(Player player) {
        ActionBarHandler handler = getActionBarHandler();
        if (handler == null) return;
        handler.sendTo(player, this);
    }
    
    private static ActionBarHandler getActionBarHandler() {
        if (SpigotActionBarHandler.isSupported()) {
            return new SpigotActionBarHandler();
        } else if (NMSActionBarHandler.isSupported()) {
            return new NMSActionBarHandler();
        } else {
            return null;
        }
    }
    
    private static interface ActionBarHandler {
        public void sendTo(Player p, ActionBar bar);
    }
    
    private static class SpigotActionBarHandler implements ActionBarHandler {
        private SpigotActionBarHandler() {
            assert isSupported() : "Spigot action bar is unsupported!";
        }
        
        public void sendTo(Player p, ActionBar bar) {
            if (getProtocolVersion(p) < 16) return;
            Object baseComponent = serializeOld(bar.getText()); //According to wiki.vg only old style formatting is accepted for ActionBars
            Object packet = callConstructor(makeConstructor(getNmsClass("PacketPlayOutChat"), getNmsClass("IChatBaseComponent"), int.class), baseComponent, 2);
            sendPacket(p, packet);
        }
        
        private static int getProtocolVersion(Player player) {
            Object handle = getHandle(player);
            Field playerConnectionField = makeField(handle.getClass(), "playerConnection");
            Object connection = getField(playerConnectionField, handle);
            Field networkManagerField = makeField(connection.getClass(), "networkManager");
            Object networkManager = getField(networkManagerField, connection);
            Method getVersion = makeMethod(networkManager.getClass(), "getVersion");
            assert getVersion() != null : "Not Protocol Hack";
            int version = callMethod(getVersion, networkManager);
            return version;
        }
        
        public static boolean isSupported() {
            return Reflection.getClass("org.spigotmc.ProtocolData") != null;
        }
    }
    
    private static class NMSActionBarHandler implements ActionBarHandler {
        
        private NMSActionBarHandler() {
            assert !SpigotActionBarHandler.isSupported() : "Spigot action bar is supported";
            assert NMSActionBarHandler.isSupported() : "NMS Action bar isn't supported";
        }
        
        public void sendTo(Player p, ActionBar bar) {
            Object baseComponent = serializeOld(bar.getText()); //According to wiki.vg only old style formatting is accepted for ActionBars
            Object packet = callConstructor(makeConstructor(getNmsClass("PacketPlayOutChat"), getNmsClass("IChatBaseComponent"), byte.class), baseComponent, (byte) 2);
            sendPacket(p, packet);
        }
        
        public static boolean isSupported() {
            return makeConstructor(getNmsClass("PacketPlayOutChat"), getNmsClass("IChatBaseComponent"), byte.class) != null;
        }
    }
    
    //Utils
    
    private static void sendPacket(Player player, Object packet) {
        Object handle = getHandle(player);
        Field playerConnectionField = makeField(handle.getClass(), "playerConnection");
        Object connection = getField(playerConnectionField, handle);
        callMethod(makeMethod(getNmsClass("PlayerConnection"), "sendPacket", getNmsClass("Packet")), connection, packet);
    }

    private static Object serializeOld(String text) { //Serialize to raw text (needed for ActionBars)
        Class<?> chatComponentTextClass = getNmsClass("ChatComponentText");
        return callConstructor(makeConstructor(chatComponentTextClass, String.class), text);
    }

    private static Object serialize(String text) { //Serialize to IChatBaseComponent 
        Class<?> craftChatMessageClass = getCbClass("util.CraftChatMessage");
        Object baseComponentArray = callMethod(makeMethod(craftChatMessageClass, "fromString", String.class), null, text);
        assert baseComponentArray.getClass().isArray();
        Object first = null;
        Method addSibling = makeMethod(getNmsClass("IChatBaseComponent"), "addSibling", getNmsClass("IChatBaseComponent"));
        for (int i = 0; i < Array.getLength(baseComponentArray); i++) {
            Object baseComponent = Array.get(baseComponentArray, i);
            if (first == null) {
                first = baseComponent;
            } else {
                first = callMethod(addSibling, first, baseComponent);
            }
        }
        return first;
    }
}