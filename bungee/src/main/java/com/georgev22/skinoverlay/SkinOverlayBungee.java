package com.georgev22.skinoverlay;

import co.aikar.commands.BungeeCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.listeners.bungee.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bungee.PlayerListeners;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
public class SkinOverlayBungee extends Plugin implements SkinOverlayImpl {

    private int tick = 0;

    private boolean enabled = false;

    @Override
    public void onLoad() {
        try {
            new LibraryLoader(this.getClass(), this.getDataFolder()).loadAll();
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
    }

    @Override
    public void onEnable() {
        getProxy().getScheduler().schedule(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++), 0, 50L, TimeUnit.MILLISECONDS);
        SkinOverlay.getInstance().setCommandManager(new BungeeCommandManager(this));
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler.SkinHandler_());
        SkinOverlay.getInstance().onEnable();
        BungeeMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        getProxy().registerChannel("skinoverlay:bungee");
        enabled = true;
    }

    @Override
    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        enabled = false;
    }

    @Override
    public boolean isBungee() {
        return true;
    }

    @Override
    public Description description() {
        return new Description(getDescription().getName(), getDescription().getVersion(), getDescription().getMain(), Collections.singletonList(getDescription().getAuthor()));
    }

    @Override
    public boolean setEnable(boolean enable) {
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        try {
            Utils.saveResource(resource, replace, getDataFolder(), this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOnlineMode() {
        return getProxy().getConfig().isOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        List<PlayerObject> playerObjects = new ArrayList<>();
        getProxy().getPlayers().forEach(proxiedPlayer -> playerObjects.add(new PlayerObjectBungee(proxiedPlayer)));
        return playerObjects;
    }
}
