package net.techcable.combattag.npc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCLib;
import net.techcable.npclib.NPCRegistry;

import lombok.*;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class NPCManager {
    @Getter
    private final CombatTag plugin;

    public void onDespawn(CombatNPC npc) {
        npcs.remove(npc.getNpcId());
    }

    private final Map<UUID, CombatNPC> npcs = new HashMap<>();

    public CombatNPC spawnNpc(CombatPlayer player, NPCType type) {
        UUID npcId = type.mask(player.getId());
        if (getNpc(npcId) == null)
            throw new IllegalStateException("Already spawned npc for " + player.getName() + " of type " + type.name());
        HumanNPC npc = getRegistry().createHumanNPC(npcId, player.getName());
        CombatNPC combatNPC = type.createNPC(this, npc, player.getId());
        combatNPC.onSpawn(npc);
        npcs.put(npcId, combatNPC);
        return combatNPC;
    }

    public CombatNPC getNpc(UUID npcId) {
        return npcs.get(npcId);
    }

    public Collection<CombatNPC> listNpcs() {
        return Collections2.transform(getRegistry().listNpcs(), new Function<NPC, CombatNPC>() {

            public CombatNPC apply(NPC input) {
                return npcs.get(input.getUUID());
            }
        });
    }

    private NPCRegistry getRegistry() {
        return NPCLib.getNPCRegistry("CombatTag", CombatTag.getInstance());
    }

    public boolean isNPC(Player entity) {
        return asNPC(entity) != null;
    }

    public CombatNPC asNPC(Player npcEntity) {
        if (! getRegistry().isNPC(npcEntity)) return null;
        UUID npcId = npcEntity.getUniqueId();
        return getNpc(npcId);
    }
}
