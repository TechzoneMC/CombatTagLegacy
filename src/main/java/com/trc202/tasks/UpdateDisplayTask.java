package com.trc202.tasks;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableList;
import com.trc202.libs.techcable.ActionBar;
import com.trc202.CombatTag.CombatTag;

import com.trc202.libs.techcable.bar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateDisplayTask extends BukkitRunnable {
    private final CombatTag plugin;
    private final int maxTagTime;
    public static final char BAR_CHAR = ':';
    public static final int DEFAULT_ITERATIONS_TILL_REMOVAL = 8;
    public UpdateDisplayTask(CombatTag plugin) {
        this.plugin = plugin;
        this.maxTagTime = plugin.settings.getTagDuration();
    }

    private Map<Player, Integer> iterationsTillRemoval = new WeakHashMap<Player, Integer>();
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            int timeRemaining = (int) plugin.getRemainingTagTime(playerId) / 1000; //Time remaining in seconds
            String displayMode = CombatTag.getPlugin(CombatTag.class).settings.getTagDisplayMode();
            if (displayMode.equalsIgnoreCase("actionBar")) {
                displayActionBarCountdown("CombatTag:", timeRemaining, maxTagTime, player);
            } else if (displayMode.equalsIgnoreCase("mobBar")) {
                BossBar bar = BossBar.getBossBar(player);
                if (iterationsTillRemoval.containsKey(player)) {
                    int tillRemoval = iterationsTillRemoval.get(player);
                    if (tillRemoval == 0) {
                        BossBar.getBossBar(player).stopShowing();
                        iterationsTillRemoval.remove(player);
                    } else {
                        iterationsTillRemoval.put(player, tillRemoval - 1);
                    }
                } else {
                    if (!bar.isShown() && timeRemaining <= 0) continue;
                    int percent = (int) (timeRemaining/((double)maxTagTime)*100.0D);
                    if (timeRemaining <= 0) {
                        bar.setMessage(ChatColor.GREEN + "You are no longer in combat");
                        iterationsTillRemoval.put(player, DEFAULT_ITERATIONS_TILL_REMOVAL);
                    } else {
                        bar.setMessage(ChatColor.YELLOW + "Time Left In Combat: " + ChatColor.WHITE + timeRemaining + "s");
                    }
                    bar.setPercentage(percent);
                }
            }
        }
    }


    private static void displayActionBarCountdown(String prefix, int timeRemaining, int maxTime, Player player) {
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