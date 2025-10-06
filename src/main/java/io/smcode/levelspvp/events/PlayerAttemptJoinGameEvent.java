package io.smcode.levelspvp.events;

import io.smcode.levelspvp.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerAttemptJoinGameEvent extends GameEvent implements Cancellable {
    private boolean cancelled = false;

    public PlayerAttemptJoinGameEvent(@NotNull Player player, Game game) {
        super(player, game);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
