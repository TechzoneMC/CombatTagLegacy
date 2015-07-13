package net.techcable.combattag.npc;

import lombok.*;

import java.util.UUID;

import net.techcable.combattag.CombatTag;
import net.techcable.npclib.NPC;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class CombatNPC {

    private final UUID playerId;
    private final NPC npc;

    public CombatNPC(UUID playerId, NPC npc) {
        this.playerId = playerId;
        this.npc = npc;
        new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(CombatTag.getInstance(), 0, 1);
    }

    public void tick() {
    } //Does nothing. At least for now

    public Player getEntity() {
        return (Player) getNpc().getEntity();
    }

    public static final long ID_MASK = 0xda97ca67e780116dL;
    public static UUID mask(UUID id) {
        return new UUID(id.getMostSignificantBits(), id.getLeastSignificantBits() ^ ID_MASK);
    }

    public static UUID unmask(UUID id) {
        return mask(id);
    }

    public UUID getNpcId() {
        return mask(getPlayerId());
    }
}
