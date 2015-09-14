package net.techcable.combattag.listeners;

import net.techcable.combattag.events.CombatLogEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InstakillListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatLog(CombatLogEvent event) {
        event.getLogger().getEntity().damage(1000);
    }
}
