package io.smcode.skywars.game;

import io.smcode.skywars.SkyWarsPlugin;
import io.smcode.skywars.events.PlayerAttemptJoinGameEvent;
import io.smcode.skywars.events.PlayerJoinGameEvent;
import io.smcode.skywars.events.PlayerLeaveGameEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
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

    Game(String name, GameSettings settings) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.settings = settings;
        this.state = GameState.IN_LOBBY;
        this.countdown = new CountdownTimer(SkyWarsPlugin.getInstance(), 60, this);
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

    public static Game deserialize(Map<String, Object> data) {
        final Game game = new Game(
                (String) data.get("Name"),
                (GameSettings) data.get("Game-Settings")
        );

        game.setId(UUID.fromString((String) data.get("Id")));
        game.setLocations((GameLocations) data.get("Locations"));

        return game;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "Id", getId().toString(),
                "Name", getName(),
                "Game-Settings", getSettings(),
                "Locations", getLocations()
        );
    }
}
