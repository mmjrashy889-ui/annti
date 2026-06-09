package com.anticheat.checks;

import com.anticheat.AntiCheatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReachCheck {

    private final AntiCheatPlugin plugin;

    public ReachCheck(AntiCheatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean check(Player attacker, EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("checks.reach.enabled", true)) return false;
        if (attacker.hasPermission("anticheat.bypass")) return false;

        double maxDist = plugin.getConfig().getDouble("checks.reach.max_distance", 3.2);
        double dist = attacker.getLocation().distance(event.getEntity().getLocation());

        return dist > maxDist;
    }
}
