package com.trc202.libs.techcable.intercept;

import com.trc202.libs.techcable.Reflection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

import static com.trc202.libs.techcable.Reflection.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketInterceptor {
    private final Plugin plugin;

    private static final Class<?> playerClass = getNmsClass("EntityPlayer");
    private static final Class<?> connectionClass = getNmsClass("PlayerConnection");
    private static final Class<?> networkManagerClass = getNmsClass("NetworkManager");

    static {
        for
    }

    private static final Field connectionField = makeField(playerClass, "playerConnection");
    private static final Field networkManagerField = makeField(connectionClassm,);

    public void inject(Player player) {
        Object handle = Reflection.getHandle(player);
        Object connection
    }
}
