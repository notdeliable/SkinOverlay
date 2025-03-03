package com.georgev22.skinoverlay.utilities;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.configmanager.CFG;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.georgev22.library.utilities.Utils.placeHolder;
import static com.georgev22.skinoverlay.SkinOverlay.getInstance;

public enum MessagesUtil {
    NO_PERMISSION("Messages.No Permission", "&c&l(!)&c You do not have the correct permissions to do this!"),
    ONLY_PLAYER_COMMAND("Messages.Only Player Command", "&c&l(!)&c Only players can run this command!"),
    OFFLINE_PLAYER("Messages.Offline Player", "&c&l(!)&c Player %player% is offline!"),
    DONE("Messages.Overlay Applied", "&a&l(!)&a Overlay %url% applied!"),
    RESET("Messages.Overlay Reset", "&a&l(!)&a Default skin applied(%player%)!!"),
    INSUFFICIENT_ARGUMENTS("Messages.Insufficient arguments", "&c&l(!)&c Insufficient arguments (%command%)"),
    COMMANDS_DESCRIPTIONS_SKINOVERLAY_HELP("Commands.Descriptions.SkinOverlay.help", "Shows the help page"),
    COMMANDS_DESCRIPTIONS_SKINOVERLAY_OVERLAY("Commands.Descriptions.SkinOverlay.overlay", "Wear a specific overlay from the plugin files"),
    COMMANDS_DESCRIPTIONS_SKINOVERLAY_CLEAR("Commands.Descriptions.SkinOverlay.clear", "Removes the skin overlay"),
    COMMANDS_DESCRIPTIONS_SKINOVERLAY_URL("Commands.Descriptions.SkinOverlay.url", "Wear a specific overlay from a URL"),
    COMMANDS_DESCRIPTIONS_SKINOVERLAY_RELOAD("Commands.Descriptions.SkinOverlay.reload", "Reload the plugin configuration files (some settings need server restart)"),
    ;
    private String[] messages;
    private final String path;

    private static CFG messagesCFG;
    private static Locale locale;

    MessagesUtil(String path, String... messages) {
        this.messages = messages;
        this.path = path;
    }

    private boolean isMultiLined() {
        return this.messages.length > 1;
    }

    public static void repairPaths(Locale locale) throws Exception {
        if (messagesCFG == null | !Objects.equals(MessagesUtil.locale, locale)) {
            MessagesUtil.locale = locale;
            messagesCFG = new CFG("messages_" + locale.getStringLocale(), getInstance().getDataFolder(), true, true, getInstance().getLogger(), getInstance().getClass());
        }
        boolean changed = false;
        for (MessagesUtil enumMessage : MessagesUtil.values()) {
            if (messagesCFG.getFileConfiguration().contains(enumMessage.getPath())) {
                MessagesUtil.setPathToMessage(messagesCFG, enumMessage);
                continue;
            }
            MessagesUtil.setMessageToPath(messagesCFG, enumMessage);
            if (changed) continue;
            changed = true;
        }
        if (changed) {
            messagesCFG.saveFile();
        }
    }

    private static void setMessageToPath(CFG cfg, @NotNull MessagesUtil enumMessage) {
        if (enumMessage.isMultiLined()) {
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages());
        } else {
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages()[0]);
        }
    }

    private static void setPathToMessage(@NotNull CFG cfg, @NotNull MessagesUtil enumMessage) {
        if (Utils.isList(cfg.getFileConfiguration(), enumMessage.getPath())) {
            enumMessage.setMessages(cfg.getFileConfiguration().getStringList(enumMessage.getPath()).toArray(new String[0]));
        } else {
            enumMessage.setMessages(cfg.getFileConfiguration().getString(enumMessage.getPath()));
        }
    }

    public String getPath() {
        return this.path;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public void setMessages(String messages) {
        this.messages[0] = messages;
    }

    public void msg(@NotNull CommandIssuer issuer) {
        this.msg(issuer, new HashObjectMap<>(), false);
    }

    public void msg(CommandIssuer issuer, Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            for (String message : messages) {
                issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(placeHolder(message, map, ignoreCase))));
            }
        } else {
            issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(placeHolder(this.getMessages()[0], map, ignoreCase))));
        }
    }

    public void msgConsole() {
        msgConsole(new HashMap<>(), false);
    }

    public void msgConsole(Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            switch (getInstance().type()) {
                case BUKKIT ->
                        BukkitMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case BUNGEE ->
                        BungeeMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case VELOCITY ->
                        VelocityMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
            }
        } else {
            switch (getInstance().type()) {
                case BUKKIT -> BukkitMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case BUNGEE -> BungeeMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case VELOCITY ->
                        VelocityMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
            }
        }
    }

    public void msgAll() {
        if (this.isMultiLined()) {
            getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(this.getMessages()));
        } else {
            getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(this.getMessages()[0]));
        }
    }

    public void msgAll(Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(placeHolder(this.getMessages(), map, ignoreCase)));
        } else {
            getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(placeHolder(this.getMessages()[0], map, ignoreCase)));
        }
    }

    public static CFG getMessagesCFG() {
        return MessagesUtil.messagesCFG;
    }

}

