package net.techcable.combattag;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import net.techcable.combattag.npc.CombatNPC;
import net.techcable.combattag.npc.NPCManager;
import net.techcable.npclib.NPC;
import net.techcable.techutils.entity.UUIDUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Executes the /ct command
 *
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
            onTagCheck((Player)commandSender);
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
            Set<String> disabled = Sets.newHashSet(CombatTag.getInstance().getSettings().getDisabledCommands());
            String subSubCommand = args[1];
            if (!args[2].startsWith("/")) {
                commandSender.sendMessage("Please specifiy a vaild command");
                commandSender.sendMessage("Did you prefix it with a '/' ?");
                return true;
            }
            if (subSubCommand.equalsIgnoreCase("add")) {
                disabled.add(args[2]);
            } else if (subSubCommand.equalsIgnoreCase("remove")) {
                disabled.remove(args[2]);
            } else {
                commandSender.sendMessage("Unknown sub-sub command");
                return true;
            }
            CombatTag.getInstance().getSettings().setDisabledCommands(disabled.toArray(new String[disabled.size()]));
            return true;
        } else {
            commandSender.sendMessage("Unknown sub command");
            return true;
        }
    }

    public void onTagCheck(Player player) {
        CombatPlayer combatPlayer = CombatPlayer.getPlayer(player);
        if (combatPlayer.isTagged()) {
            player.sendMessage(plugin.getSettings().getCommandMessageTagged().replace("[time]", Integer.toString((int) (combatPlayer.getRemainingTagTime() / 1000))));
        } else {
            player.sendMessage(plugin.getSettings().getCommandMessageNotTagged());
        }
    }

    public void onWipe() {
        for (CombatNPC npc : plugin.getNpcManager().listNpcs()) {
            plugin.getNpcManager().despawnNpc(npc);
        }
    }
}