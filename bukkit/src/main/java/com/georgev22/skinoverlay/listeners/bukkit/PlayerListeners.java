package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectConnectionEvent;
import com.georgev22.skinoverlay.handler.Skin;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static com.georgev22.skinoverlay.utilities.Utilities.decrypt;

public class PlayerListeners implements Listener, PluginMessageListener {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        skinOverlay.getEventManager().callEvent(
                new PlayerObjectConnectionEvent(
                        skinOverlay.getPlayer(playerJoinEvent.getPlayer().getUniqueId()).orElseThrow(),
                        PlayerObjectConnectionEvent.ConnectionType.CONNECT,
                        true
                )
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        skinOverlay.getEventManager().callEvent(
                new PlayerObjectConnectionEvent(
                        skinOverlay.getPlayer(playerQuitEvent.getPlayer().getUniqueId()).orElseThrow(),
                        PlayerObjectConnectionEvent.ConnectionType.DISCONNECT,
                        true
                )
        );
    }

    @SneakyThrows
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equalsIgnoreCase("skinoverlay:bungee")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        UUID uuid = UUID.fromString(Objects.requireNonNull(decrypt(in.readUTF())));
        Skin skin = (Skin) Utils.deserializeObjectFromString(Objects.requireNonNull(decrypt(in.readUTF())));
        boolean b = Boolean.parseBoolean(decrypt(in.readUTF()));
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        if (b) {
            skinOverlay.getSkinHandler().setSkin(skin, playerObject);
        } else {
            if (subChannel.equalsIgnoreCase("change")) {
                if (!skin.skinOptions().getSkinName().contains("custom")) {
                    skinOverlay.getSkinHandler().setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), skin.skinOptions().getSkinName() + ".png")), skin, playerObject);
                } else {
                    URL url = new URL(skin.skinOptions().getUrl());
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    try (InputStream stream = url.openStream()) {
                        byte[] buffer = new byte[4096];

                        while (true) {
                            int bytesRead = stream.read(buffer);
                            if (bytesRead < 0) {
                                break;
                            }
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                    skinOverlay.getSkinHandler().setSkin(() -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())), skin, playerObject);
                }
            } else if (subChannel.equalsIgnoreCase("reset")) {
                skinOverlay.getSkinHandler().setSkin(() -> null, skin, playerObject);
            }
        }
    }

}

