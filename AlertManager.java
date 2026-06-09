package com.anticheat.checks;

import com.anticheat.AntiCheatPlugin;
import com.anticheat.managers.PlayerDataManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class FlyCheck {

    private final AntiCheatPlugin plugin;
    private final PlayerDataManager dataManager;

    public FlyCheck(AntiCheatPlugin plugin, PlayerDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    public boolean check(Player player, PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("checks.fly.enabled", true)) return false;
        if (player.hasPermission("anticheat.bypass")) return false;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return false;
        if (player.isFlying() || player.getAllowFlight()) return false;
        if (player.isInsideVehicle()) return false;
        if (player.hasPotionEffect(PotionEffectType.LEVITATION)) return false;

        double fromY = event.getFrom().getY();
        double toY = event.getTo().getY();
        double deltaY = toY - fromY;

        // Check if player is going up without jumping
        if (deltaY > 0.42 && !isOnGround(player) && !player.isOnGround()) {
            // Exempt if near a block
            if (!isNearBlock(player)) {
                return true;
            }
        }

        // Check sustained flight (staying in air too long without falling)
        long now = System.currentTimeMillis();
        long lastMove = dataManager.getLastMoveTime(player.getUniqueId());
        double lastY = dataManager.getLastY(player.getUniqueId());

        if (!player.isOnGround() && Math.abs(deltaY) < 0.01 && toY > 1) {
            long airTime = now - lastMove;
            if (airTime > 800 && !isNearBlock(player)) {
                return true;
            }
        }

        dataManager.setLastMoveTime(player.getUniqueId(), now);
        dataManager.setLastY(player.getUniqueId(), toY);
        return false;
    }

    private boolean isOnGround(Player player) {
        return player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid();
    }

    private boolean isNearBlock(Player player) {
        return player.getLocation().add(0, 1, 0).getBlock().getType().isSolid()
                || player.getLocation().getBlock().getType().isSolid();
    }
}
