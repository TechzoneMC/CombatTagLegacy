package net.techcable.combattag.libs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.techcable.techutils.Reflection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static net.techcable.techutils.Reflection.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NMSUtils {

    public static final Field ENTITY_PLAYER_INVULNERABLE_TICKS_FIELD = makeField(getNmsClass("EntityPlayer"), "invulnerableTicks");
    public static final Method ENTITY_LIVING_SET_HEALTH_METHOD = makeMethod(getNmsClass("EntityLiving"), "setHealth", float.class);

    public static void setInvulnerableTicks(Entity bukkitEntity, int invulnerableTicks) {
        Object entity = Reflection.getHandle(bukkitEntity);
        if (getNmsClass("EntityPlayer").isInstance(entity)) {
            setField(ENTITY_PLAYER_INVULNERABLE_TICKS_FIELD, entity, invulnerableTicks);
        }
    }

    /*
     * EntityLiving.setHealth() calls die() if the entity is a player.
     * EntityPlayer.die() tries to close any open inventories, causing the offline player to throw an exception.
     */
    public static void setHealth(LivingEntity bukkitEntity, double health) {
        Object entity = Reflection.getHandle(bukkitEntity);
        Reflection.callMethod(ENTITY_LIVING_SET_HEALTH_METHOD, entity, (float)health);
    }

}
