package com.trc202.tasks;

import java.util.UUID;

import com.trc202.CombatTag.ActionBar;
import com.trc202.CombatTag.CombatTag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {
    private final CombatTag plugin;
    private final int maxTagTime;
    public static final String BAR_CHAR = "■";
    public ActionBarTask(CombatTag plugin) {
        this.plugin = plugin;
        this.maxTagTime = plugin.settings.getTagDuration();
        runTaskTimerAsynchronously(plugin, 3, 10); //Every 10th of a second
    }
    
    @Override
    public void run() { //Based of MCMMO    
        for (UUID playerId : plugin.listTagged()) {
            StringBuilder text = new StringBuilder("Time left in combat: ");
            double tagPercentage = (plugin.getRemainingTagTime(playerId) / maxTagTime) * 100.0D;
            int fullDisplay = 20;
            int coloredDisplay = (int) Math.ceil(fullDisplay * (tagPercentage / 100.0D));
            int grayDisplay = fullDisplay - coloredDisplay;
            text.append(ChatColor.GREEN);
            for (int i = 0; i < coloredDisplay; i++) {
                text.append(BAR_CHAR);
            }
            text.append(ChatColor.GRAY);
            for (int i = 0; i < grayDisplay; i++) {
                text.append(BAR_CHAR);
            }
            ActionBar bar = new ActionBar(text.toString());
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) bar.sendTo(player);
        }
    }
}