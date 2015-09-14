package net.techcable.combattag.events;

import net.techcable.combattag.CombatPlayer;

import lombok.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class CombatLogEvent extends Event implements Cancellable {

    private final CombatPlayer logger;

    @Setter
    private boolean cancelled;

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
