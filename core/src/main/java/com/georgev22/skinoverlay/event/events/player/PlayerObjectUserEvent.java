package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.utilities.player.User;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents a user player object event.
 */
public class PlayerObjectUserEvent extends UserEvent implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    /**
     * The player object associated with this event.
     */
    private final PlayerObject playerObject;
    private boolean cancelled = false;

    /**
     * Constructs a {@code PlayerObjectUserEvent} with the specified user, player object, and asynchronous status.
     * <p>
     *
     * @param playerObject the player object associated with this event
     * @param user         the user associated with this event
     * @param async        whether this event should be run asynchronously
     */
    public PlayerObjectUserEvent(@NotNull PlayerObject playerObject, @NotNull User user, boolean async) {
        super(user, async);
        this.playerObject = playerObject;
    }

    /**
     * Returns the player object associated with this event.
     *
     * @return the player object associated with this event
     */
    public PlayerObject getPlayerObject() {
        return playerObject;
    }

    /**
     * Cancels the event.
     *
     * @return {@code true} if the event was successfully cancelled, {@code false} otherwise
     */
    @Override
    public boolean cancel() {
        return cancelled = true;
    }

    /**
     * Returns whether the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}