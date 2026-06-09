package com.anticheat.checks;

import com.anticheat.AntiCheatPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFallCheck {

    private final AntiCheatPlugin plugin;

    public NoFallCheck(AntiCheatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean check(Player player, EntityDamageEvent event) {
        if (!plugin.getConfig().getBoolean("checks.nofall.enabled", true)) return false;
        if (player.hasPermission("anticheat.bypass")) return false;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return false;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            // If damage is 0 from fall = NoFall
            return event.getDamage() == 0;
        }
        return false;
    }
}
