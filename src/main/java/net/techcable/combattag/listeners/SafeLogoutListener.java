package net.techcable.combattag.listeners;

import lombok.RequiredArgsConstructor;
import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.config.DisplayMode;
import net.techcable.combattag.events.CombatTagEvent;
import net.techcable.combattag.npc.CombatNPC;
import net.techcable.combattag.npc.NPCManager;
import net.techcable.combattag.npc.NPCType;
import net.techcable.combattag.tasks.SafeLogoutTask;
import net.techcable.techutils.inventory.InventoryUtils;
import net.techcable.techutils.inventory.PlayerData;
import net.techcable.techutils.ui.BossBar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class SafeLogoutListener implements Listener {
    private final CombatTag plugin;

    public DisplayMode getDisplayMode() {
        return CombatTag.getInstance().getSettings().getSafeLogoutDisplayMode();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        final CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        boolean isMoveOutOfBlock = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
        if (!isMoveOutOfBlock) return;
        tryCancelWithMsg(player, "You moved while trying to logout");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        final CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        tryCancelWithMsg(player, "You " + (event.getCause().equals(TeleportCause.ENDER_PEARL) ? "ender pearled" : "teleported") + " while trying to logout");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(CombatTagEvent event) {
        final CombatPlayer player = event.getPlayer();
        tryCancelWithMsg(player, "You got tagged while trying to logout");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerKickEvent event) {
        String reason = event.getReason();
        if (SafeLogoutTask.KICK_MSG.equals(reason)) return; // They were safe logged out
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        onLogout(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        onLogout(player);
    }

    public void onLogout(CombatPlayer player) {
        if (player.isTagged()) return; // Let the punishment system handle it
        NPCManager npcManager = plugin.getNpcManager();
        CombatNPC npc = npcManager.spawnNpc(player, NPCType.SAFELOGOUT_NPC);
        PlayerData playerData = InventoryUtils.getData(player.getEntity());
        npc.copyFrom(playerData);
    }

    private void tryCancelWithMsg(final CombatPlayer player, String msg) {
        SafeLogoutTask task =  player.getLogoutTask();
        if (task == null) return;
        switch (getDisplayMode()) {
            case BOSS_BAR:
                final BossBar bar = BossBar.getBossBar(player.getEntity());
                bar.setMessage(msg);
                bar.setPercentage(100);
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
                break;
        }
        player.cancelSafeLogout();
    }
}
