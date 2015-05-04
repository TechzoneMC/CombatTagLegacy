package net.techcable.combattag.libs.bar;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static net.techcable.techutils.Reflection.*;

public class PacketPlayOutEntityDestroy extends Packet {
    private static final Class<?> handleType = getNmsClass("PacketPlayOutEntityDestroy");
    private static final Field toDestroy = handleType.getDeclaredFields()[0];
    private static final Constructor constructor = makeConstructor(handleType);

    @Getter
    private final Object handle;

    public PacketPlayOutEntityDestroy(int... idsToDestroy) {
        this.handle = callConstructor(constructor);
        setField(toDestroy, handle, idsToDestroy);
    }
}
