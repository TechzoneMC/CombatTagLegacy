package net.techcable.combattag.listeners;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.config.CombatTagConfig;
import net.techcable.combattag.events.CombatTagEvent;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Blocks certain actions in combat
 */
public class ConfigListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCombatTag(CombatTagEvent event) {
        if (event.getPlayer().getEntity().hasPermission("combattag.ignore")) {
            event.setCancelled(true);
        }
        if (event.isBecauseOfAttack()) {
            if (event.getCause() instanceof Player) {
                Player attacker = (Player) event.getCause();
                if (attacker.getGameMode().equals(GameMode.CREATIVE) && getSettings().isBlockCreativeTagging()) {
                    attacker.sendMessage("You can't combat tag players while in creative");
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (event.isBecauseOfDefend()) {
            if (getSettings().isOnlyTagDamager()) {
                event.setCancelled(true);
            }
        }
        if (event.getPlayer().getEntity().isFlying() && getSettings().isBlockFly()) {
            event.getPlayer().getEntity().sendMessage("You can't fly while in combat!");
            event.getPlayer().getEntity().setFlying(false);
        }
        for (String disallowedWorld : getSettings().getDisallowedWorlds()) {
            if (disallowedWorld.equals(event.getPlayer().getEntity().getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (player.isTagged()) {
            event.getPlayer().sendMessage("You can't fly while in combat");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && getSettings().isBlockEnderPearl() && player.isTagged()) {
            player.getEntity().sendMessage("You can't enderpearl in combat");
            event.setCancelled(true);
            return;
        }
        if (getSettings().isBlockTeleport() && player.isTagged()) {
            player.getEntity().sendMessage("You can't teleport in combat");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (!getSettings().isBlockEditWhileTagged()) {
            event.getPlayer().sendMessage("You can't place blocks in combat");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (!getSettings().isBlockEditWhileTagged()) {
            event.getPlayer().sendMessage("You can't break blocks in combat");
            event.setCancelled(true);
        }
    }


    public CombatTagConfig getSettings() {
        return CombatTag.getInstance().getSettings();
    }


    //See below for evil dark magic

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (player.isTagged()) {
            String command = event.getMessage();
            for (String disabledCommand : CombatTag.getInstance().getSettings().getDisallowedCommands()) {
                if (disabledCommand.equalsIgnoreCase("all") && !command.equalsIgnoreCase("/ct") && !command.equalsIgnoreCase("/combattag")) {
                    player.getEntity().sendMessage(ChatColor.RED + "[CombatTag] All commands are disabled while in combat");
                    event.setCancelled(true);
                    return;
                }
                if (command.indexOf(" ") == disabledCommand.length()) {
                    if (command.substring(0, command.indexOf(" ")).equalsIgnoreCase(disabledCommand)) {
                        if (player.getPlugin().getSettings().isDebugMode()) {
                            CombatTag.getInstance().getLogger().info("[CombatTag] Combat Tag has blocked the command: " + disabledCommand + " .");
                        }
                        player.getEntity().sendMessage(ChatColor.RED + "[CombatTag] This command is disabled while in combat");
                        event.setCancelled(true);
                        return;
                    }
                } else if (disabledCommand.indexOf(" ") > 0) {
                    if (command.toLowerCase().startsWith(disabledCommand.toLowerCase())) {
                        if (player.getPlugin().getSettings().isDebugMode()) {
                            CombatTag.getInstance().getLogger().info("[CombatTag] Combat Tag has blocked the command: " + disabledCommand + " .");
                        }
                        player.getEntity().sendMessage(ChatColor.RED + "[CombatTag] This command is disabled while in combat");
                        event.setCancelled(true);
                        return;
                    }
                } else if (!command.contains(" ") && command.equalsIgnoreCase(disabledCommand)) {
                    if (player.getPlugin().getSettings().isDebugMode()) {
                        CombatTag.getInstance().getLogger().info("[CombatTag] Combat Tag has blocked the command: " + disabledCommand + " .");
                    }
                    player.getEntity().sendMessage(ChatColor.RED + "[CombatTag] This command is disabled while in combat");
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
