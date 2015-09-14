package com.trc202.CombatTagApi;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.npc.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CombatTagApi {
    private final CombatTag plugin;

    public CombatTagApi(com.trc202.CombatTag.CombatTag plugin) {
        this.plugin = (CombatTag) plugin;
    }

    private static CombatTagApi instance;

    public static CombatTagApi getInstance() {
        if (instance == null) {
            instance = new CombatTagApi(CombatTag.getInstance());
        }
        return instance;
    }

    /**
     * Checks to see if the player is in combat. The combat time can be
     * configured by the server owner If the player has died while in combat the
     * player is no longer considered in combat and as such will return false
     *
     * @return true if player is in combat
     */
    public boolean isInCombat(Player playerEntity) {
        CombatPlayer player = CombatPlayer.getPlayer(playerEntity);
        return player.isTagged();
    }

    /**
     * Checks to see if the player is in combat. The combat time can be
     * configured by the server owner If the player has died while in combat the
     * player is no longer considered in combat and as such will return false
     *
     * @return true if player is online and in combat
     */
    @SuppressWarnings("deprecation")
    public boolean isInCombat(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return isInCombat(player);
        }
        return false;
    }

    /**
     * Returns the time before the tag is over -1 if the tag has expired -2 if
     * the player is not in combat
     */
    public long getRemainingTagTime(Player player) {
        CombatPlayer combatPlayer = plugin.getPlayer(player);
        if (combatPlayer.isTagged()) {
            return combatPlayer.getRemainingTagTime();
        } else {
            return -1L;
        }
    }

    /**
     * Returns the time before the tag is over -1 if the tag has expired -2 if
     * the player is not in combat
     */
    @SuppressWarnings("deprecation")
    public long getRemainingTagTime(String name) {
        if (Bukkit.getPlayerExact(name) != null) {
            Player player = Bukkit.getPlayerExact(name);
            if (isInCombat(player)) {
                return getRemainingTagTime(player);
            } else {
                return -1L;
            }
        }
        return -2L;
    }

    /**
     * Returns if the entity is an NPC
     *
     * @return true if the player is an NPC
     */
    public boolean isNPC(Entity entity) {
        NPCManager npcManager = plugin.getNpcManager();
        return npcManager != null && entity instanceof Player && npcManager.isNPC(((Player) entity));
    }

    /**
     * Tags player
     *
     * @return true if the action is successful, false if not
     */
    public boolean tagPlayer(Player player) {
        CombatPlayer combatPlayer = plugin.getPlayer(player);
        combatPlayer.tag();
        return true;
    }

    /**
     * Untags player
     */
    public void untagPlayer(Player player) {
        CombatPlayer combatPlayer = plugin.getPlayer(player);
        combatPlayer.untag();
    }

    /**
     * Returns the value of a configuration option with the specified name
     *
     * @return String value of option
     */
    public String getConfigOption(String configKey) {
        throw new UnsupportedOperationException("No access to internals permitted");
    }
}
