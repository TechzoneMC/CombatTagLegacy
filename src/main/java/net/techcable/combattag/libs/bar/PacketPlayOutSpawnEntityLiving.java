package net.techcable.combattag.libs.bar;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static net.techcable.combattag.libs.bar.BossBar.*;
import static net.techcable.techutils.Reflection.*;

public class PacketPlayOutSpawnEntityLiving extends Packet {
    private static final Class<?> handleType = getNmsClass("PacketPlayOutSpawnEntityLiving");
    static {
        Field[] fields = handleType.getDeclaredFields();
        entityIdField = fields[0];
        entityTypeField = fields[1];
        entityXField = fields[2];
        entityYField = fields[3];
        entityZField = fields[4];
        entityVelocityXField = fields[5];
        entityVelocityYField = fields[6];
        entityVelocityZField = fields[7];
        entityYawField = fields[8];
        entityPitchField = fields[9];
        entityHeadPitchField = fields[10];
        entityDataWatcherField = fields[11];
    }
    private static Field entityIdField;
    private static Field entityTypeField;
    private static Field entityXField;
    private static Field entityYField;
    private static Field entityZField;
    private static Field entityVelocityXField;
    private static Field entityVelocityYField;
    private static Field entityVelocityZField;
    private static Field entityYawField;
    private static Field entityPitchField;
    private static Field entityHeadPitchField;
    private static Field entityDataWatcherField;
    private static final Constructor constructor = makeConstructor(handleType);

    @Getter
    private final Object handle;
    public PacketPlayOutSpawnEntityLiving(int entityId, byte entityType, Location location, Vector bukkitVelocity, WrappedDataWatcher watcher) {
        this.handle = callConstructor(constructor);
        setField(entityIdField, handle, entityId);
        setField(entityTypeField, handle, (int)entityType);
        setField(entityXField, handle, toFixedPoint(location.getX()));
        setField(entityYField, handle, toFixedPoint(location.getY()));
        setField(entityZField, handle, toFixedPoint(location.getZ()));
        int[] velocity = doVelocityMagic(bukkitVelocity.getX(), bukkitVelocity.getY(), bukkitVelocity.getZ());
        setField(entityVelocityXField, handle, velocity[0]);
        setField(entityVelocityYField, handle, velocity[1]);
        setField(entityVelocityZField, handle, velocity[2]);
        setField(entityYawField, handle, toByteAngle(location.getYaw()));
        setField(entityPitchField, handle, toByteAngle(location.getPitch()));
        setField(entityHeadPitchField, handle, toByteAngle(location.getPitch())); //Meh
        setField(entityDataWatcherField, handle, watcher.getHandle());
    }
}
