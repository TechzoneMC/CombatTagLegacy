package net.techcable.combattag.npc;

import lombok.*;

import java.util.UUID;

import net.techcable.combattag.CombatTag;
import net.techcable.npclib.HumanNPC;

import net.techcable.npclib.utils.uuid.UUIDUtils;
import net.techcable.techutils.inventory.InventoryUtils;
import net.techcable.techutils.inventory.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class CombatNPC {
    private final NPCManager npcManager;
    private final UUID playerId;
    private final HumanNPC npc;

    public CombatNPC(NPCManager npcManager, UUID playerId, HumanNPC npc) {
        this.npcManager = npcManager;
        this.playerId = playerId;
        this.npc = npc;
        new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(npcManager.getPlugin(), 0, 1);
    }

    public void onSpawn(HumanNPC npc) {
        npc.setProtected(false);
        npc.setSkin(playerId);
        String playerName = UUIDUtils.getName(playerId);
        npc.setName(playerName);
        boolean showInTab = npcManager.getPlugin().getSettings().isShowNpcInTab();
        npc.setShowInTabList(showInTab);
    }

    public void tick() {
    } //Does nothing. At least for now

    public abstract NPCType getType();

    public Player getEntity() {
        return getNpc().getEntity();
    }

    public UUID getNpcId() {
        return npc.getUUID();
    }

    public void copyFrom(PlayerData playerData) {
        PlayerData npcData = InventoryUtils.getData(getEntity());
        InventoryUtils.copy(playerData, npcData);
    }

    public void copyTo(PlayerData playerData) {
        PlayerData npcData = InventoryUtils.getData(getEntity());
        InventoryUtils.copy(npcData, playerData);
    }

    public void despawn() {
        getNpc().despawn();
        npcManager.onDespawn(this);
    }
}
