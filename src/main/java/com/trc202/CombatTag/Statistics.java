package com.trc202.CombatTag;

import com.trc202.libs.Metrics;
import com.trc202.libs.Metrics.Graph;
import com.trc202.libs.Metrics.Plotter;

import java.io.IOException;
import java.util.function.Supplier;

public class Statistics {
    public Statistics(CombatTag plugin) {
        this.plugin = plugin;
    }

    private final CombatTag plugin;
    private Metrics metrics;

    /**
     * Activate metrics
     *
     * @param instaKill this supplier indicates the punishment used
     * @return true if successful
     */
    public boolean activate(final Supplier<Boolean> instaKill) {
        try {
            if (metrics == null) {
                metrics = new Metrics(plugin);
            }
            Graph punishment = metrics.createGraph("Punishment used on Combat Tag");
            punishment.addPlotter(new Plotter("Instakill") {
                @Override
                public int getValue() {
                    if (instaKill.get()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            punishment.addPlotter(new Plotter("NPC") {
                @Override
                public int getValue() {
                    if (!instaKill.get()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            metrics.start();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
