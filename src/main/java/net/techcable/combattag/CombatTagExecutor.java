package net.techcable.combattag;

import lombok.*;

import java.util.Set;

import net.techcable.combattag.npc.CombatNPC;
import net.techcable.npclib.utils.uuid.UUIDUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

/**
 * Executes the /ct command
 * <p/>
 * Supported sub commands:
 * /ct force [player]
 * /ct wipe
 */
@RequiredArgsConstructor
public class CombatTagExecutor implements CommandExecutor {

    private final CombatTag plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("ct") && !label.equalsIgnoreCase("combattag")) return false;
        if (args.length == 0) { //Ct
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("You must be a player to execute this command");
                return true;
            }
            onTagCheck((Player) commandSender);
            return true;
        }
        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("wipe")) {
            if (!commandSender.hasPermission("combattag.wipe")) {
                commandSender.sendMessage("You don't have permission to despawn all npcs");
                return true;
            }
            onWipe();
            return true;
        } else if (subCommand.equalsIgnoreCase("command")) {
            if (args.length < 3) {
                commandSender.sendMessage("Insufficent arguments");
                return true;
            }
            String subSubCommand = args[1];
            if (!args[2].startsWith("/")) {
                commandSender.sendMessage("Please specifiy a vaild command");
                commandSender.sendMessage("Did you prefix it with a '/' ?");
                return true;
            }
            if (subSubCommand.equalsIgnoreCase("add")) {
                CombatTag.getInstance().getSettings().getDisallowedCommands().add(args[2]);
            } else if (subSubCommand.equalsIgnoreCase("remove")) {
                CombatTag.getInstance().getSettings().getDisallowedCommands().remove(args[2]);
            } else {
                commandSender.sendMessage("Unknown sub-sub command");
                return true;
            }
            return true;
        } else if (subCommand.equalsIgnoreCase("force")) {
            CombatPlayer player;
            if (args.length > 1) {
                Player rawPlayer = UUIDUtils.getPlayerExact(args[1]);
                if (rawPlayer == null) {
                    commandSender.sendMessage("Target a player");
                    return true;
                }
                player = CombatPlayer.getPlayer(rawPlayer);
            } else if (commandSender instanceof Player) {
                player = CombatPlayer.getPlayer((Player)commandSender);
            } else {
                commandSender.sendMessage("Please Specify a player");
                return true;
            }
            if (player.isTagged()) {
                commandSender.sendMessage("Untagging " + player.getName());
                player.untag();
            } else {
                int time;
                try {
                    time = args.length > 2 ? Integer.parseInt(args[3]) : 35;
                } catch (NumberFormatException ex) {
                    commandSender.sendMessage(args[3] + " is not a valid time");
                    return true;
                }
                commandSender.sendMessage("Tagging " + player.getName() + " for " + time + " seconds");
                player.tag(time * 1000);
            }
            return true;
        } else {
            commandSender.sendMessage("Unknown sub command");
            return true;
        }
    }

    public void onTagCheck(Player player) {
        CombatPlayer combatPlayer = CombatPlayer.getPlayer(player);
        if (combatPlayer.isTagged()) {
            player.sendMessage(plugin.getMessages().getCommandMessageTagged((int)combatPlayer.getRemainingTagTime() / 1000));
        } else {
            player.sendMessage(plugin.getMessages().getCommandMessageWhenNotTagged());
        }
    }

    public void onWipe() {
        for (CombatNPC npc : plugin.getNpcManager().listNpcs()) {
            npc.despawn();
        }
    }
}