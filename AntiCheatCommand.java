package com.anticheat.checks;

import com.anticheat.AntiCheatPlugin;
import com.anticheat.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class AimAssistCheck {

    private final AntiCheatPlugin plugin;
    private final PlayerDataManager dataManager;

    public AimAssistCheck(AntiCheatPlugin plugin, PlayerDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    public boolean check(Player player, PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("checks.aimassist.enabled", true)) return false;
        if (player.hasPermission("anticheat.bypass")) return false;

        float currentYaw = event.getTo().getYaw();
        dataManager.addYawHistory(player.getUniqueId(), currentYaw);

        List<Float> history = dataManager.getYawHistory(player.getUniqueId());
        if (history.size() < 10) return false;

        // Detect snapping: sudden large yaw changes that are too perfect
        double totalChange = 0;
        int snapCount = 0;

        for (int i = 1; i < history.size(); i++) {
            float diff = Math.abs(history.get(i) - history.get(i - 1));
            if (diff > 180) diff = 360 - diff;
            totalChange += diff;
            // Large sudden snap = possible aim assist
            if (diff > 30 && diff < 90) snapCount++;
        }

        // Too many snaps in short period
        return snapCount >= 4;
    }
}
