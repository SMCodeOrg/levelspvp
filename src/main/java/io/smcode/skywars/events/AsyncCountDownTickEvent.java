package io.smcode.skywars.events;

import io.smcode.skywars.game.CountdownTimer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncCountDownTickEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final CountdownTimer timer;

    public AsyncCountDownTickEvent(CountdownTimer timer) {
        super(true);
        this.timer = timer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
