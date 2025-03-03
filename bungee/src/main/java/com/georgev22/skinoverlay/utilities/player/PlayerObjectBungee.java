package com.georgev22.skinoverlay.utilities.player;

import java.util.List;
import java.util.UUID;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.skinoverlay.SkinOverlay;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerObjectBungee extends PlayerObject {
    private final ProxiedPlayer proxiedPlayer;

    public PlayerObjectBungee(final ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }

    @Override
    public ProxiedPlayer player() {
        return this.proxiedPlayer;
    }

    @Override
    public Audience audience() {
        return SkinOverlay.getInstance().getSkinOverlay().adventure().player(this.proxiedPlayer.getUniqueId());
    }

    @Override
    public UUID playerUUID() {
        return this.proxiedPlayer.getUniqueId();
    }

    @Override
    public String playerName() {
        return this.proxiedPlayer.getName();
    }

    @Override
    public void sendMessage(String input) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input);
    }

    @Override
    public void sendMessage(List<String> input) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input);
    }

    @Override
    public void sendMessage(String... input) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input);
    }

    @Override
    public void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BungeeMinecraftUtils.msg(proxiedPlayer, input, placeholders, ignoreCase);
    }

    @Override
    public boolean isOnline() {
        return proxiedPlayer.isConnected();
    }

    @Override
    public boolean permission(String permission) {
        return isOnline() && player().hasPermission(permission);
    }
}
