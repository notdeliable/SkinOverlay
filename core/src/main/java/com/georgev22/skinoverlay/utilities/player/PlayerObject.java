package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public abstract class PlayerObject {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public PlayerObject playerObject() {
        return this;
    }

    public abstract Object player();

    public abstract UUID playerUUID();

    public abstract String playerName();

    public boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    public abstract void sendMessage(String input);

    public abstract void sendMessage(List<String> input);

    public abstract void sendMessage(String... input);

    public abstract void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract boolean isOnline();

    public GameProfile gameProfile() {
        try {
            return SkinOverlay.getInstance().getSkinHandler().getGameProfile(playerObject());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<ObjectMap.Pair<String, UUID>> inform = Lists.newArrayList(
            ObjectMap.Pair.create("GeorgeV22", UUID.fromString("a4f5cd7f-362f-4044-931e-7128b4e6bad9"))
    );

    public void developerInform() {
        final UUID uuid = playerUUID();
        final String name = playerName();

        final ObjectMap.Pair<String, UUID> pair = ObjectMap.Pair.create(name, uuid);

        boolean found = false;

        for (ObjectMap.Pair<String, UUID> loop : this.inform) {
            if (loop.key().equals(pair.key())) {
                found = true;
                break;
            }
            if (loop.value().equals(pair.value())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            if (!isOnline() && player() == null) {
                return;
            }

            sendMessage(Lists.newArrayList(

                    "",

                    "",

                    "&7Hey &f%player%&7, details are listed below.",

                    "&7Version: &c%version%",

                    "&7Java Version: &c%javaversion%",

                    "&7Server Version: &c%serverversion%",

                    "&7Name: &c%name%",

                    "&7Author: &c%author%",

                    "&7Main package: &c%package%",

                    "&7Main path: &c%main%",

                    "&7Experimental Features: &c" + OptionsUtil.EXPERIMENTAL_FEATURES.getBooleanValue(),

                    ""

            ), new HashObjectMap<String, String>()
                    .append("%player%", playerName())
                    .append("%version%", skinOverlay.getDescription().version())
                    .append("%package%", skinOverlay.getClass().getPackage().getName())
                    .append("%name%", skinOverlay.getDescription().name())
                    .append("%author%", String.join(", ", skinOverlay.getDescription().authors()))
                    .append("%main%", skinOverlay.getDescription().main())
                    .append("%javaversion%", System.getProperty("java.version"))
                    .append("%serverversion%", skinOverlay.getSkinOverlay().serverVersion()), false);
        }, 20L * 10L);
    }

    public void playerJoin() {
        if (OptionsUtil.PROXY.getBooleanValue() & !skinOverlay.type().isProxy()) {
            return;
        }
        UserData userData = UserData.getUser(playerObject());

        try {
            userData.load(new Utils.Callback<>() {

                public Boolean onSuccess() {
                    UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                    SchedulerManager.getScheduler().runTask(SkinOverlay.getInstance().getClass(), () -> {
                        userData.user().append("playerObject", Optional.of(playerObject()));
                        try {
                            userData.setDefaultSkinProperty(gameProfile().getProperties().get("textures").stream().filter(property -> property.getName().equalsIgnoreCase("textures")).findFirst().orElse(skinOverlay.getSkinHandler().getSkin(playerObject())));
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            skinOverlay.getLogger().log(Level.SEVERE, "Something went wrong:", e);
                        }
                        if (!(skinOverlay.type().equals(SkinOverlayImpl.Type.VELOCITY) | skinOverlay.type().equals(SkinOverlayImpl.Type.BUNGEE))) {
                            updateSkin();
                        }
                    });
                    UserData.getLoadedUsers().append(userData, userData.user());
                    return true;
                }

                @Contract(pure = true)
                public @NotNull Boolean onFailure() {
                    return false;
                }

                public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                    throwable.printStackTrace();
                    return onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerQuit() {
        if (OptionsUtil.PROXY.getBooleanValue() & !skinOverlay.type().isProxy()) {
            return;
        }
        UserData userData = UserData.getUser(playerObject());
        userData.save(true, new Utils.Callback<>() {

            public Boolean onSuccess() {
                UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                return true;
            }

            @Contract(pure = true)
            public @NotNull Boolean onFailure() {
                return false;
            }

            public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
                return this.onFailure();
            }
        });
    }

    public void updateSkin() {
        UserData userData = UserData.getUser(playerObject());
        if (!isOnline())
            return;
        if (userData.getSkinName().equals("default")) {
            return;
        }
        Utilities.updateSkin(playerObject(), true, false);
    }


    @Override
    public String toString() {
        return "PlayerObject{\n" +
                "playerName: " + playerName() + "\n" +
                "playerUUID: " + playerUUID() + "\n" +
                "isBedrock: " + isBedrock() + "\n" +
                "isOnline: " + isOnline() + "\n" +
                "gameProfile: " + gameProfile().toString() + "\n" +
                "}";
    }
}
