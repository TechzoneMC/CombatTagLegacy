package net.techcable.combattag.npc;

import java.util.UUID;

import net.techcable.npclib.HumanNPC;

import lombok.*;

@RequiredArgsConstructor
public enum NPCType {
    COMBAT_LOG_NPC(CombatLogNPC.class) {
        @Override
        public long getIdMask() {
            return CombatLogNPC.ID_MASK;
        }

        @Override
        public CombatNPC createNPC(NPCManager manager, HumanNPC npc, UUID playerId) {
            return new CombatLogNPC(manager, playerId, npc);
        }
    },
    SAFELOGOUT_NPC(SafeLogoutNPC.class) {
        @Override
        public long getIdMask() {
            return SafeLogoutNPC.SAFE_LOGOUT_MASK;
        }

        @Override
        public CombatNPC createNPC(NPCManager manager, HumanNPC npc, UUID playerId) {
            return new SafeLogoutNPC(manager, playerId, npc);
        }
    };
    private final Class<? extends CombatNPC> type;

    public abstract long getIdMask();

    public UUID mask(UUID id) {
        return new UUID(id.getLeastSignificantBits(), id.getMostSignificantBits() ^ getIdMask());
    }

    public UUID unmask(UUID id) {
        return mask(id);
    }

    public boolean isCopyDataToPlayerOnDeath() {
        return true;
    }

    public abstract CombatNPC createNPC(NPCManager manager, HumanNPC npc, UUID playerId);
}
