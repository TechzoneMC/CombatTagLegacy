package net.techcable.combattag.libs.bar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class FakeWither extends FakeEntity {
    public static final int MAX_HEALTH = 300;
    public static final int MOB_ID = 64;

    public FakeWither(Player player) {
        super(player);
    }

    @Override
    protected float getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    protected byte getMobTypeId() {
        return MOB_ID;
    }

    /*
     * Doesn't have intended effect
    private boolean lastSentInvisible = false;
    private boolean invisible = false;
    public void updateInvisibility() {
        if (lastSentInvisible == invisible) return; //If the visibility state on the client is what it should be don't send the packet
        if (invisible) {
            sendEffectPacket(fakeEntityId, new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true), true);
        } else {
            sendRemoveEffectPacket(fakeEntityId, PotionEffectType.INVISIBILITY);
        }
        lastSentInvisible = invisible;
    }

    private static final Class<?> effectPacket = getNmsClass("PacketPlayOutEntityEffect");
    private static final Constructor effectPacketConstructor = makeConstructor(effectPacket);
    private static final Field effectPacketEntityIdField = effectPacket.getDeclaredFields()[0];
    private static final Field effectPacketEffectIdField = effectPacket.getDeclaredFields()[1];
    private static final Field effectPacketAmplifierField = effectPacket.getDeclaredFields()[2];
    private static final Field effectPacketDurationField = effectPacket.getDeclaredFields()[3];
    private static final Field effectPacketHideParticlesField = effectPacket.getDeclaredFields().length > 4 ? effectPacket.getDeclaredFields()[4] : null;
    private void sendEffectPacket(final int entityId, final PotionEffect effect, final boolean hideParticles) {
        Packet packet = new Packet() {
            private Object handle = null;
            @Override
            protected Object getHandle() {
                if (handle == null) {
                    handle = callConstructor(effectPacketConstructor);
                    setField(effectPacketEntityIdField, handle, entityId);
                    setField(effectPacketEffectIdField, handle, (byte)effect.getType().getId());
                    setField(effectPacketAmplifierField, handle, (byte)effect.getAmplifier());
                    setField(effectPacketDurationField, handle, effect.getDuration());
                    if (effectPacketHideParticlesField != null) {
                        setField(effectPacketHideParticlesField, handle, (byte)(hideParticles ? 1 : 0));
                    } //Protocol Hack AKA were screwed for hideParticles :(
                }
                return handle;
            }
        };
        packet.sendPacket(player);
    }
    private static final Constructor removeConstructor = makeConstructor(getNmsClass("PacketPlayOutRemoveEntityEffect"));
    private static final Field removePacketEntityIdField = getNmsClass("PacketPlayOutRemoveEntityEffect").getDeclaredFields()[0];
    private static final Field removePacketEffectIdField = getNmsClass("PacketPlayOutRemoveEntityEffect").getDeclaredFields()[1];
    private void sendRemoveEffectPacket(final int entityId, final PotionEffectType type) {
        Packet packet = new Packet() {
            private Object handle = null;
            @Override
            protected Object getHandle() {
                if (handle == null) {
                    handle = callConstructor(removeConstructor);
                    setField(removePacketEntityIdField, handle, entityId);
                    setField(removePacketEffectIdField, handle, type.getId());//I really don't wanna deal with the PotionEffectType -> MobEffectList conversion
                }
                return handle;
            }
        };
        packet.sendPacket(player);
    }
    */
}
