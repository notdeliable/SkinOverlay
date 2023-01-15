package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.SpongeMinecraftUtils;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.List;
import java.util.UUID;

public class PlayerObjectSponge implements PlayerObject {

    private final User user;

    public PlayerObjectSponge(User user) {
        this.user = user;
    }

    @Override
    public ServerPlayer getPlayer() {
        return user.player().orElseThrow();
    }

    @Override
    public UUID playerUUID() {
        return user.uniqueId();
    }

    @Override
    public String playerName() {
        return user.name();
    }

    @Override
    public void sendMessage(String input) {
        SpongeMinecraftUtils.msg(getPlayer(), input);
    }

    @Override
    public void sendMessage(List<String> input) {
        SpongeMinecraftUtils.msg(getPlayer(), input);
    }

    @Override
    public void sendMessage(String... input) {
        SpongeMinecraftUtils.msg(getPlayer(), input);
    }

    @Override
    public void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        SpongeMinecraftUtils.msg(getPlayer(), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        SpongeMinecraftUtils.msg(getPlayer(), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        SpongeMinecraftUtils.msg(getPlayer(), input, placeholders, ignoreCase);
    }

    @Override
    public boolean isOnline() {
        return user.isOnline();
    }
}
