package net.techcable.combattag.config;

import net.techcable.combattag.Utils;
import net.techcable.techutils.config.AnnotationConfig;
import net.techcable.techutils.config.Setting;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

public class CombatTagMessages extends AnnotationConfig {

    @Setting("attackerTagMessage")
    private String attackerTagMessage = "You have hit {player}. Type /ct to check your remaining tag time.";

    public String getAttackerTagMessage(LivingEntity defender) {
        return color(attackerTagMessage.replace("{player}", Utils.getName(defender)));
    }

    @Setting("defenderTagMessage")
    private String defenderTagMessage = "You have been hit by {player}. Type /ct to check your remaining tag time.";

    public String getDefenderTagMessage(LivingEntity attacker) {
        return color(defenderTagMessage.replace("{player}", Utils.getName(attacker)));
    }

    //
    // Commands
    //

    @Setting("command.whenTagged")
    private String commandMessageTagged = "You are in combat for {time} seconds.";

    public String getCommandMessageTagged(int time) {
        return color(commandMessageTagged.replace("{time}", Integer.toString(time)));
    }

    @Setting("command.whenNotTagged")
    private String commandMessageWhenNotTagged = "You are not currently in combat.";

    public String getCommandMessageWhenNotTagged() {
        return color(commandMessageWhenNotTagged);
    }

    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
