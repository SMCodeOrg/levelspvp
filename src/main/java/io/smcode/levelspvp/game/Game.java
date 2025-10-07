package io.smcode.levelspvp.game;

import io.smcode.levelspvp.LevelsPlugin;
import io.smcode.levelspvp.events.PlayerAttemptJoinGameEvent;
import io.smcode.levelspvp.events.PlayerJoinGameEvent;
import io.smcode.levelspvp.events.PlayerLeaveGameEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class Game implements ConfigurationSerializable {
    @Setter
    private UUID id;
    private final String name;
    private final GameSettings settings;
    private final Set<GamePlayer> players = new HashSet<>();
    private final Map<GameTeam, Set<GamePlayer>> teams = new HashMap<>();
    @Setter
    private GameLocations locations = new GameLocations();
    @Setter
    private GameState state;
    private final CountdownTimer countdown;
    @Getter
    @Setter
    private Map<GameTeam, BoundingBox> arenas = new HashMap<>();

    Game(String name, GameSettings settings) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.settings = settings;
        this.state = GameState.IN_LOBBY;
        this.countdown = new CountdownTimer(LevelsPlugin.getInstance(), 60, this);
    }

    void join(Player player) {
        final PlayerAttemptJoinGameEvent joinEvent = new PlayerAttemptJoinGameEvent(player, this);
        Bukkit.getPluginManager().callEvent(joinEvent);

        if (!joinEvent.isCancelled()) {
            this.players.add(new GamePlayer(player));
            Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(player, this));

            if (this.players.size() >= this.settings.getMinPlayers() && !this.countdown.isRunning()) {
                this.countdown.start();
            }
        }
    }

    void leave(Player player) {
        this.players.removeIf(gp -> gp.getPlayer().getUniqueId().equals(player.getUniqueId()));

        Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(player, this));
    }

    public void broadcast(Component message) {
        for (GamePlayer gp : getPlayers()) {
            final Player player;

            if ((player = gp.getPlayer()).isOnline())
                player.sendMessage(message);
        }
    }

    public boolean hasPlayer(Player player) {
        return getPlayers().stream()
                .map(GamePlayer::getPlayer)
                .anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()));
    }

    public void setArena(GameTeam team, BoundingBox arena) {
        arenas.put(team, arena);
    }

    public static Game deserialize(Map<String, Object> data) {
        final Game game = new Game(
                (String) data.get("Name"),
                (GameSettings) data.get("Game-Settings")
        );
        final Map<String, BoundingBox> localArenas = data.get("Arenas") == null ? new HashMap<>() : (Map<String, BoundingBox>) data.get("Arenas");

        for (String teamString : localArenas.keySet()) {
            final GameTeam team = GameTeam.valueOf(teamString);
            game.setArena(team, localArenas.get(teamString));
        }

        game.setId(UUID.fromString((String) data.get("Id")));
        game.setLocations((GameLocations) data.get("Locations"));

        return game;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, BoundingBox> localArenas = new HashMap<>();

        for (GameTeam team : getArenas().keySet()) {
            localArenas.put(team.name(), getArenas().get(team));
        }

        return Map.of(
                "Id", getId().toString(),
                "Name", getName(),
                "Arenas", localArenas,
                "Game-Settings", getSettings(),
                "Locations", getLocations()
        );
    }
}
