package net.techcable.combattag;

import net.techcable.combattag.tasks.SafeLogoutTask;

import lombok.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SafeLogoutExecutor implements CommandExecutor {
    private final CombatTag plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (! s.equals("logout")) return false;
        if (! plugin.getSettings().isSafeLogoutEnabled()) {
            commandSender.sendMessage("Safe logout is not enabled");
            return true;
        }
        if (! (commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can safely logout");
            return true;
        }
        CombatPlayer player = plugin.getPlayer((Player) commandSender);
        if (player.getLogoutTask() != null) {
            commandSender.sendMessage("Safe logout already in progress");
            return true;
        }
        if (player.isTagged()) {
            commandSender.sendMessage("Cant safe logout while tagged");
            return true;
        }
        SafeLogoutTask task = new SafeLogoutTask(player);
        player.setLogoutTask(task);
        commandSender.sendMessage("Starting safe logout");
        return true;
    }
}
