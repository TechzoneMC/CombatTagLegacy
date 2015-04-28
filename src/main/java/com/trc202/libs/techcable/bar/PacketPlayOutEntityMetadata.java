package com.trc202.libs.techcable.bar;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.trc202.libs.techcable.Reflection.*;

public class PacketPlayOutEntityMetadata extends Packet {
    private static final Class<?> handleType = getNmsClass("PacketPlayOutEntityMetadata");
    static {
        for (int i = 0; i < handleType.getDeclaredFields().length; i++) {
            Field field = handleType.getDeclaredFields()[i];
            switch (i) {
                case 0 :
                    entityIdField = field;
                    break;
                case 1 :
                    dataWatcherValuesField = field;
                    break;
                default :
                    throw new RuntimeException("Unknown field index " + i);
            }
        }
    }
    private static Field entityIdField;
    private static Field dataWatcherValuesField;
    private static final Constructor constructor = makeConstructor(handleType);

    @Getter
    private final Object handle;
    public PacketPlayOutEntityMetadata(int entityId, WrappedDataWatcher watcher) {
        this.handle = callConstructor(constructor);
        setField(entityIdField, handle, entityId);
        setField(dataWatcherValuesField, handle, watcher.toHandleList());
    }
}
