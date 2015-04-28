package com.trc202.libs.techcable.bar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.trc202.libs.techcable.Reflection.*;

/**
 * A fake dragon for 1.7 clients
 */
public class FakeDragon extends FakeEntity {
    public static final byte ENTITY_TYPE = 63;
    public static final float MAX_HEALTH = 200;

    public FakeDragon(Player player) {
        super(player);
    }

    @Override
    protected float getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    protected byte getMobTypeId() {
        return ENTITY_TYPE;
    }
}