package net.techcable.combattag;

//Featherboard

import lombok.*;

import be.maximvdw.featherboard.api.PlaceholderAPI;
import be.maximvdw.featherboard.api.PlaceholderAPI.PlaceholderRequestEvent;
import be.maximvdw.featherboard.api.PlaceholderAPI.PlaceholderRequestEventHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaximvdwHelper {
    public static void registerPlaceholders() {
        try {
            Utils.debug("Attempting to register featherboard placeholders");
            PlaceholderAPI.registerPlaceholder("combattime", new PlaceholderRequestEventHandler() {

                @Override
                public String onPlaceholderRequest(PlaceholderRequestEvent event) {
                    CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
                    return Integer.toString((int) (player.getRemainingTagTime() / 1000));
                }
            });
            PlaceholderAPI.registerPlaceholder("safelogout", new PlaceholderRequestEventHandler() {

                @Override
                public String onPlaceholderRequest(PlaceholderRequestEvent event) {
                    CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
                    int safeLogoutTimeRemaining = player.getSafeLogoutTimeRemaining();
                    return Integer.toString(safeLogoutTimeRemaining);
                }
            });
            Utils.info("Enabling featherboard support");
        } catch (NoClassDefFoundError ex) {}
    }
}