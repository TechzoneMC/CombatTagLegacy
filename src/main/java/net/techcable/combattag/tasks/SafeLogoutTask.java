package net.techcable.combattag.tasks;

import lombok.*;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.config.DisplayMode;
import net.techcable.techutils.ui.ActionBar;
import net.techcable.techutils.ui.BossBar;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class SafeLogoutTask extends BukkitRunnable {

    public SafeLogoutTask(CombatPlayer player) {
        this.player = player;
        logoutTime = (CombatTag.getInstance().getSettings().getSafeLogoutTime() * 1000);
        startTime = System.currentTimeMillis();
        displayMode = CombatTag.getInstance().getSettings().getSafeLogoutDisplayMode();
    }

    private final CombatPlayer player;

    @Getter
    private final DisplayMode displayMode;
    private int logoutTime;
    private long startTime;


    private long lastTick = startTime;

    public static final String KICK_MSG = ChatColor.GREEN + "You have safely logged out";
    @Override
    public void run() {
        if (isDone()) {
            player.getEntity().kickPlayer(KICK_MSG);
            cancel();
        } else {
            int timeRemaining = (int) (lastTick - startTime) / 1000;
            player.setSafeLogoutTimeRemaining(timeRemaining);
            switch (displayMode) {
                case ACTION_BAR:
                    displayActionBarCountdown("Time till SafeLogout:\n", timeRemaining, logoutTime, player);
                    break;
                case BOSS_BAR:
                    displayBossBarContdown("Time till SafeLogout", timeRemaining, logoutTime, player);
                    break;
                case CHAT:
                    player.getEntity().sendMessage("Time till SafeLogout " + timeRemaining);
                    break;
            }
        }
        lastTick = System.currentTimeMillis();
    }

    public boolean isDone() {
        return lastTick - startTime >= logoutTime;
    }

    private static void displayBossBarContdown(String prefix, int timeRemaining, int maxTime, CombatPlayer player) {
        if (timeRemaining < 0) return;
        BossBar bar = BossBar.getBossBar(player.getEntity());
        bar.setMessage(prefix);
        bar.setPercentage((int) (Math.min(timeRemaining, maxTime) / ((double) maxTime)) * 100);
    }

    private static void displayActionBarCountdown(String prefix, int timeRemaining, int maxTime, CombatPlayer player) {
        if (timeRemaining < 0) return;
        StringBuilder text = new StringBuilder();
        text.append(prefix);
        text.append(" ");
        int fullDisplay = 60;
        int timeIncompleted = (int) (fullDisplay * (Math.min(timeRemaining, maxTime) / ((double) maxTime))); //Thanks to Mr. BlobMan for the casting denominators to double fix
        int timeCompleted = fullDisplay - timeIncompleted;
        text.append(ChatColor.RED);
        for (int i = 0; i < timeIncompleted; i++) {
            text.append(':');
        }
        text.append(ChatColor.GREEN);
        for (int i = 0; i < timeCompleted; i++) {
            text.append(':');
        }
        text.append(ChatColor.RESET);
        text.append(' ');
        text.append(timeRemaining);
        text.append("s");
        ActionBar bar = new ActionBar(text.toString());
        if (player != null && player.getEntity().isOnline()) bar.sendTo(player.getEntity());
    }

}