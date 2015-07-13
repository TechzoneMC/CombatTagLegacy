package net.techcable.combattag;

import java.io.IOException;

import net.techcable.combattag.Metrics.Graph;
import net.techcable.combattag.Metrics.Plotter;

import com.google.common.base.Supplier;

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
     *
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
