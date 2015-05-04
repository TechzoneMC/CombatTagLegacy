package net.techcable.combattag.listeners;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.DisplayMode;
import net.techcable.combattag.events.CombatTagEvent;
import net.techcable.combattag.libs.bar.BossBar;
import net.techcable.combattag.tasks.SafeLogoutTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SafeLogoutListener implements Listener {

    public DisplayMode getDisplayMode() {
        return DisplayMode.parse(CombatTag.getInstance().getSettings().getSafeLogoutDisplayMode(), DisplayMode.ACTION_BAR);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        SafeLogoutTask task = player.getLogoutTask();
        if (task == null) return;
        boolean isMoveOutOfBlock = event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ();
        if (isMoveOutOfBlock) {
            switch (getDisplayMode()) {
                case BOSS_BAR:
                    final BossBar bar = BossBar.getBossBar(player.getEntity());
                    bar.setMessage("You moved while trying to logout");
                    bar.setPercentage(100);
                    player.cancelSafeLogout();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (bar.isShown()) {
                                bar.stopShowing();
                            }
                        }
                    }.runTaskLater(CombatTag.getInstance(), 20);
                    break;
                case ACTION_BAR :
                    player.sendActionBar("You moved while trying to logout");
                    player.cancelSafeLogout();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendActionBar("");
                        }
                    }.runTaskLater(CombatTag.getInstance(), 20);
                    break;
                case CHAT :
                    player.getEntity().sendMessage("You moved while trying to logout");
                    player.cancelSafeLogout();
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(CombatTagEvent event) {
        final CombatPlayer player = event.getPlayer();
        SafeLogoutTask task = player.getLogoutTask();
        if (!event.getPlayer().getId().equals(player.getId())) return;
        switch (getDisplayMode()) {
            case BOSS_BAR:
                final BossBar bar = BossBar.getBossBar(player.getEntity());
                bar.setMessage("You got tagged while trying to logout");
                bar.setPercentage(100);
                player.cancelSafeLogout();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (bar.isShown()) {
                            bar.stopShowing();
                        }
                    }
                }.runTaskLater(CombatTag.getInstance(), 20);
                break;
            case ACTION_BAR :
                player.sendActionBar("You got tagged while trying to logout");
                player.cancelSafeLogout();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendActionBar("");
                    }
                }.runTaskLater(CombatTag.getInstance(), 20);
                break;
            case CHAT :
                player.getEntity().sendMessage("You got tagged while trying to logout");
                player.cancelSafeLogout();
                break;
        }
    }
}
