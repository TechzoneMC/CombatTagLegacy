package net.techcable.combattag;

import lombok.Getter;
import lombok.Setter;
import net.techcable.combattag.libs.ActionBar;
import net.techcable.combattag.tasks.SafeLogoutTask;
import net.techcable.techutils.entity.TechPlayer;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.UUID;

public class CombatPlayer extends TechPlayer {
    private volatile long whenTagExpires = 0;

    public CombatTag getPlugin() {
        return (CombatTag) super.getPlugin();
    }

    public CombatPlayer(UUID id,  CombatTag plugin) {
        super(id, plugin);
    }

    public static CombatPlayer getPlayer(Player player) {
        return CombatTag.getPlugin(CombatTag.class).getPlayer(player);
    }

    public boolean isTagged() {
        return System.currentTimeMillis() - whenTagExpires > (getPlugin().getSettings().getTagDuration() * 1000);
    }

    public void tag() {
        tag(getPlugin().getSettings().getTagDuration() * 1000);
    }

    public void tag(long time) {
        whenTagExpires = Math.max(whenTagExpires, System.currentTimeMillis() + time);
    }

    public void sendActionBar(String s) {
        ActionBar bar = new ActionBar(s);
        bar.sendTo(getEntity());
    }

    @Getter
    private SafeLogoutTask logoutTask;

    public void cancelSafeLogout() {
        if (logoutTask == null) return;
        logoutTask.cancel();
        logoutTask = null;
    }

    public void untag() {
        whenTagExpires = System.currentTimeMillis();
    }

    public long getRemainingTagTime() {
        return System.currentTimeMillis() - whenTagExpires;
    }
    
    @Getter
    @Setter
    private volatile int safeLogoutTimeRemaining; 
}
