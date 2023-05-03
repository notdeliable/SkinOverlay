package com.georgev22.skinoverlay.handler;

import com.georgev22.library.utilities.EntityManager.Entity;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;

public class Skin extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

    private SProperty property;
    private SkinOptions skinOptions = new SkinOptions("default");

    public Skin(SProperty sProperty) {
        this.property = sProperty;
    }

    public Skin(SProperty sProperty, String skinName) {
        this.property = sProperty;
        this.skinOptions = new SkinOptions(skinName);
    }

    public Skin(SProperty sProperty, SkinOptions skinOptions) {
        this.property = sProperty;
        this.skinOptions = skinOptions;
    }

    public @Nullable SProperty skinProperty() {
        return getCustomData("property") != null ? getCustomData("property") : property;
    }

    public SkinOptions skinOptions() {
        return getCustomData("skinOptions") != null ? getCustomData("skinOptions") : skinOptions;
    }

    public void setSkinOptions(SkinOptions skinOptions) {
        addCustomData("skinOptions", this.skinOptions = skinOptions);
    }

    public void setProperty(SProperty property) {
        addCustomData("property", this.property = property);
    }

    public @Nullable String skinURL() {
        return new JsonParser().parse(new String(Base64.getDecoder().decode(property.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
    }

    @Override
    public String toString() {
        return "Skin{" +
                "property=" + property +
                ", skinOptions=" + skinOptions +
                '}';
    }
}
