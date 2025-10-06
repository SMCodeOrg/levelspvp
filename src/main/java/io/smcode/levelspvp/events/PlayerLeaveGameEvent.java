package io.smcode.levelspvp.events;

import io.smcode.levelspvp.game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveGameEvent extends GameEvent {
    public PlayerLeaveGameEvent(@NotNull Player player, Game game) {
        super(player, game);
    }
}
