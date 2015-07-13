package net.techcable.combattag.listeners;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.config.DisplayMode;
import net.techcable.combattag.events.CombatTagEvent;
import net.techcable.combattag.tasks.SafeLogoutTask;
import net.techcable.techutils.ui.BossBar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

public class SafeLogoutListener implements Listener {

    public DisplayMode getDisplayMode() {
        return CombatTag.getInstance().getSettings().getSafeLogoutDisplayMode();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        final CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        SafeLogoutTask task = player.getLogoutTask();
        if (task == null) return;
        boolean isMoveOutOfBlock = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
        cancelWithMsg(player, "You moved while trying to logout");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        final CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        SafeLogoutTask task = player.getLogoutTask();
        if (task == null) return;
        cancelWithMsg(player, "You " + (event.getCause().equals(TeleportCause.ENDER_PEARL) ? "ender pearled" : "teleported") + " while trying to logout");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(CombatTagEvent event) {
        final CombatPlayer player = event.getPlayer();
        SafeLogoutTask task = player.getLogoutTask();
        if (!event.getPlayer().getId().equals(player.getId())) return;
        cancelWithMsg(player, "You got tagged while trying to logout");
    }

    private void cancelWithMsg(final CombatPlayer player, String msg) {
        switch (getDisplayMode()) {
            case BOSS_BAR:
                final BossBar bar = BossBar.getBossBar(player.getEntity());
                bar.setMessage(msg);
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
            case ACTION_BAR:
                player.sendActionBar(msg);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        player.sendActionBar("");
                    }
                }.runTaskLater(CombatTag.getInstance(), 20);
                break;
            case CHAT:
                player.getEntity().sendMessage(msg);
                player.cancelSafeLogout();
                break;
        }
    }
}
