package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PlayerListeners implements Listener {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onConnect(ServerConnectedEvent serverConnectedEvent) {
        if (!serverConnectedEvent.getPlayer().isConnected())
            return;
        final PlayerObject playerObject = new PlayerObject.PlayerObjectWrapper(serverConnectedEvent.getPlayer().getUniqueId(), this.skinOverlay.isBungee()).getPlayerObject();
        final UserData userData = UserData.getUser(playerObject);
        try {
            userData.load(new Utils.Callback<>() {
                public Boolean onSuccess() {
                    UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                    SchedulerManager.getScheduler().runTask(PlayerListeners.this.skinOverlay.getClass(), () -> {
                        try {
                            userData.setDefaultSkinProperty(PlayerListeners.this.skinOverlay.getSkinHandler().getGameProfile(playerObject).getProperties().get("textures").iterator().next());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        BungeeMinecraftUtils.printMsg((userData.getSkinProperty().getName() + " " + userData.getSkinProperty().getValue() + " " + userData.getSkinProperty().getSignature()));
                        if (userData.getSkinName().equals("default")) {
                            return;
                        }
                        Utilities.updateSkin(playerObject, true, false);
                    });
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        PlayerObject playerObject = new PlayerObject.PlayerObjectWrapper(playerDisconnectEvent.getPlayer().getUniqueId(), this.skinOverlay.isBungee()).getPlayerObject();
        final UserData userData = UserData.getUser(playerObject);
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
}

