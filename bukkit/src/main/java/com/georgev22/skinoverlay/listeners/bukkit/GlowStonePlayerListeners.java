package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.PlayerSkinPartOptionsChangedEvent;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import net.glowstone.events.player.PlayerClientOptionsChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GlowStonePlayerListeners implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClientSettingsChange(PlayerClientOptionsChangeEvent event) {
        if (OptionsUtil.EXPERIMENTAL_FEATURES.getBooleanValue()) {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            skinOverlay.getEventManager().callEvent(new PlayerSkinPartOptionsChangedEvent(
                    skinOverlay.getPlayer(event.getPlayer().getUniqueId()).orElseThrow(),
                    event.hasSkinPartsChanged(),
                    true)
            );
        }
    }

}
