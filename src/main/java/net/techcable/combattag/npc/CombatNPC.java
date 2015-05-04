package net.techcable.combattag.npc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.techcable.combattag.CombatTag;
import net.techcable.npclib.NPC;
import net.techcable.techutils.inventory.InventoryUtils;
import net.techcable.techutils.inventory.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
        }.runTaskTimer(CombatTag.getInstance(), 0, 0);
    }

    public void tick() {} //Does nothing. At least for now

    public Player getEntity() {
        return (Player) getNpc().getEntity();
    }
}
