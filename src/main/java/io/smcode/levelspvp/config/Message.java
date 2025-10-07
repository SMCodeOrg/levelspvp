package io.smcode.levelspvp.config;

public enum Message {
    GAME_FULL("Game-Full"),
    GAME_NOT_JOINABLE("Game-Not-Joinable"),
    GAME_JOINED("Game-Join-Message"),
    GAME_LEFT("Game-Leave-Message"),
    COMMAND_ONLY_FOR_PLAYERS("Command-Only-For-Players"),
    GAME_CREATED("Game-Created"),
    ENTITY_NOT_FOUND("Entity-Not-Found"),
    ENTITY_UPDATED("Entity-Updated"),
    ENTITY_DUPLICATE("Entity-Duplicate"),
    CONFIG_RELOAD("Config-Reload"),
    NEXT_LAYER_OPEN("Next-Layer-Open"),
    ARENA_CREATED("Arena-Created"),
    COUNTDOWN_TIME("Countdown-Timer");

    private final String path;

    Message(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
