package io.smcode.levelspvp.events;

import io.smcode.levelspvp.game.Game;

public class GameStartedEvent extends GameEvent {
    public GameStartedEvent(Game game) {
        super(game);
    }
}
