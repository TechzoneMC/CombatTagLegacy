package com.trc202.tasks;

import java.util.UUID;

import com.trc202.libs.techcable.ActionBar;
import com.trc202.CombatTag.CombatTag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {
    private final CombatTag plugin;
    private final int maxTagTime;
    public static final char BAR_CHAR = ':';
    public ActionBarTask(CombatTag plugin) {
        this.plugin = plugin;
        this.maxTagTime = plugin.settings.getTagDuration();
    }
    
    @Override
    public void run() { //Based of MCMMO    
        for (UUID playerId : plugin.listTagged()) {
            Player player = Bukkit.getPlayer(playerId);
            int timeRemaining = (int) plugin.getRemainingTagTime(playerId) / 1000; //Time remaining in seconds
            displayActionBarCoutdown("CombatTag:", timeRemaining, maxTagTime, player);
        }
    }

    private static void displayActionBarCoutdown(String prefix, int timeRemaining, int maxTime, Player player) {
        if (timeRemaining < 0) return;
        StringBuilder text = new StringBuilder();
        text.append(prefix);
        text.append(" ");
        int fullDisplay = 60;
        int timeIncompleted = (int) (fullDisplay * (Math.min(timeRemaining, maxTime) / ((double)maxTime))); //Thanks to Mr. BlobMan for the casting denominators to double fix
        int timeCompleted = fullDisplay - timeIncompleted;
        text.append(ChatColor.RED);
        for (int i = 0; i < timeIncompleted; i++) {
            text.append(BAR_CHAR);
        }
        text.append(ChatColor.GREEN);
        for (int i = 0; i < timeCompleted; i++) {
            text.append(BAR_CHAR);
        }
        text.append(ChatColor.RESET);
        text.append(' ');
        text.append(timeRemaining);
        text.append("s");
        ActionBar bar = new ActionBar(text.toString());
        if (player != null && player.isOnline()) bar.sendTo(player);
    }
}