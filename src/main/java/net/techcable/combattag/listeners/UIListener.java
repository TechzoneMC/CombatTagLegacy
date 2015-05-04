package net.techcable.combattag.listeners;

import com.trc202.settings.Settings;
import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.DisplayMode;
import net.techcable.combattag.Utils;
import net.techcable.combattag.events.CombatTagEvent;
import net.techcable.combattag.libs.bar.BossBar;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Displays data on your combat tag
 */
public class UIListener {

    @EventHandler
    public void onCombatTag(final CombatTagEvent event) {
        DisplayMode mode = DisplayMode.parse(CombatTag.getInstance().getSettings().getTagDisplayMode(), DisplayMode.ACTION_BAR);
        switch (mode) {
            case ACTION_BAR :
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getPlayer().isTagged()) {
                            displayActionBarCountdown("Combat Time: ", (int)(event.getPlayer().getRemainingTagTime() / 1000), CombatTag.getInstance().getSettings().getTagDuration(), event.getPlayer());
                        } else {
                            event.getPlayer().sendActionBar("You are no longer in combat");
                        }
                    }
                }.runTaskTimer(CombatTag.getInstance(), 10, 10);
                break;
            case BOSS_BAR :
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getPlayer().isTagged()) {
                            displayBossBarContdown(ChatColor.RED + "Combat Time: ", (int) (event.getPlayer().getRemainingTagTime() / 1000), CombatTag.getInstance().getSettings().getTagDuration(), event.getPlayer());
                        } else {
                            BossBar bar = BossBar.getBossBar(event.getPlayer().getEntity());
                            bar.setPercentage(100);
                            bar.setMessage(ChatColor.GREEN + "You are no longer in combat");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (event.getPlayer().isTagged()) return; //They have been tagged again
                                    BossBar bar = BossBar.getBossBar(event.getPlayer().getEntity());
                                    bar.stopShowing();
                                }
                            }.runTaskLater(CombatTag.getInstance(), 600); //30 seconds
                        }
                    }
                }.runTaskTimer(CombatTag.getInstance(), 10, 10);
                break;
            case CHAT :
                Settings config = CombatTag.getInstance().getSettings();
                if (event.isBecauseOfAttack()) {
                    String msg = config.getTagMessageDamager().replace("[player]", event.getCause().getName());
                    event.getPlayer().getEntity().sendMessage(msg);
                } else if (event.isBecauseOfDefend()) {
                    String msg = config.getTagMessageDamaged().replace("[player]", event.getCause().getName());
                    event.getPlayer().getEntity().sendMessage(msg);
                }
                break;
        }
    }

    private static void displayBossBarContdown(String prefix, int timeRemaining, int maxTime, CombatPlayer player) {
        if (timeRemaining < 0) return;
        BossBar bar = BossBar.getBossBar(player.getEntity());
        bar.setMessage(prefix);
        bar.setPercentage((int)(Math.min(timeRemaining, maxTime) / ((double)maxTime)) * 100);
    }

    private static void displayActionBarCountdown(String prefix, int timeRemaining, int maxTime, CombatPlayer player) {
        if (timeRemaining < 0) return;
        StringBuilder text = new StringBuilder();
        text.append(prefix);
        text.append(" ");
        int fullDisplay = 60;
        int timeIncompleted = (int) (fullDisplay * (Math.min(timeRemaining, maxTime) / ((double)maxTime))); //Thanks to Mr. BlobMan for the casting denominators to double fix
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
        player.sendActionBar(text.toString());
    }
}
