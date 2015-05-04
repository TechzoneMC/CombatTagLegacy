package net.techcable.combattag.libs.bar;

import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static net.techcable.techutils.Reflection.*;

/**
* Created by Nicholas Schlabach on 4/16/2015.
*/
public class PacketPlayOutEntityTeleport extends Packet {
    private static final Class<?> teleportClass = getNmsClass("PacketPlayOutEntityTeleport");
    static {
        Field[] fields = teleportClass.getDeclaredFields();
        teleportEntityIdField = fields[0];
        teleportXField = fields[1];
        teleportYField = fields[2];
        teleportZField = fields[3];
        teleportYawField = fields[4];
        teleportPitchField = fields[5];
        teleportOnGroundField = fields[6];
    }

    public PacketPlayOutEntityTeleport(int entityId, Location l, boolean onGround) {
        Object handle = callConstructor(constructor);
        this.handle = handle;
        setField(teleportEntityIdField, handle, entityId);
        setField(teleportXField, handle, net.techcable.combattag.libs.bar.BossBar.toFixedPoint(l.getX()));
        setField(teleportYField, handle, net.techcable.combattag.libs.bar.BossBar.toFixedPoint(l.getY()));
        setField(teleportZField, handle, net.techcable.combattag.libs.bar.BossBar.toFixedPoint(l.getZ()));
        setField(teleportYawField, handle, net.techcable.combattag.libs.bar.BossBar.toByteAngle(l.getYaw()));
        setField(teleportPitchField, handle, net.techcable.combattag.libs.bar.BossBar.toByteAngle(l.getPitch()));
        if (teleportOnGroundField != null) {
            setField(teleportOnGroundField, handle, onGround);
        }
    }

    private static final Constructor constructor = makeConstructor(teleportClass);
    private static Field teleportEntityIdField;
    private static Field teleportXField;
    private static Field teleportYField;
    private static Field teleportZField;
    private static Field teleportYawField;
    private static Field teleportPitchField;
    private static Field teleportOnGroundField;

    private final Object handle;
    public Object getHandle() {
        return handle;
    }
}
