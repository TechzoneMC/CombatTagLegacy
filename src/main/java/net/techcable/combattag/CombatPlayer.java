package net.techcable.combattag;

import lombok.*;

import java.util.UUID;

import net.techcable.combattag.tasks.SafeLogoutTask;
import net.techcable.techutils.entity.TechPlayer;
import net.techcable.techutils.ui.ActionBar;

import org.bukkit.entity.Player;

public class CombatPlayer extends TechPlayer {

    private volatile long whenTagExpires = -1;

    public CombatTag getPlugin() {
        return (CombatTag) super.getPlugin();
    }

    public CombatPlayer(UUID id, CombatTag plugin) {
        super(id, plugin);
    }

    public static CombatPlayer getPlayer(Player player) {
        return CombatTag.getPlugin(CombatTag.class).getPlayer(player);
    }

    public boolean isTagged() {
        if (whenTagExpires <= 0) return false;
        long timeRemaining = whenTagExpires - System.currentTimeMillis();
        return timeRemaining > 0;
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
    @Setter
    private SafeLogoutTask logoutTask;

    public void cancelSafeLogout() {
        if (logoutTask == null) return;
        logoutTask.cancel();
        logoutTask = null;
    }

    public void untag() {
        whenTagExpires = -1;
    }

    public long getRemainingTagTime() {
        return whenTagExpires - System.currentTimeMillis();
    }

    @Getter
    @Setter
    private volatile int safeLogoutTimeRemaining;
}
