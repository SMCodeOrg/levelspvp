package io.smcode.levelspvp.game;

import io.smcode.levelspvp.LevelsPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameManager {
    private final Set<Game> games = new HashSet<>();
    private final LevelsPlugin plugin;

    public GameManager(LevelsPlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<Game> getGame(String name) {
        return games.stream().filter(game -> game.getName().equals(name)).findFirst();
    }

    public void joinGame(Game game, Player player) {
        game.join(player);
    }

    public void leaveGame(Player player) {
        for (Game game : games) {
            game.leave(player);
        }
    }

    public Game createNewGame(Location lobby, String name, GameSettings settings) {
        final Game game =  new Game(name, settings);
        game.getLocations().setLobby(lobby);
        this.games.add(game);
        saveGameToConfig(game);
        return game;
    }

    public Game createNewGame(Location lobby, String name) {
        return createNewGame(lobby, name, GameSettings.DEFAULT_SETTINGS);
    }

    public void saveGames() {
        for (Game game : games) {
            saveGameToConfig(game);
        }
    }

    private void saveGameToConfig(Game game) {
        plugin.getConfig().set("games." + game.getId(), game);
        plugin.saveConfig();
    }

    public void loadGames() {
        final ConfigurationSection gameSection = plugin.getConfig().getConfigurationSection("games");

        if (gameSection == null)
            return;

        for (String gameId : gameSection.getKeys(false)) {
            final Game game = (Game) plugin.getConfig().get("games." + gameId);
            this.games.add(game);
        }

        plugin.getLogger().info("Loaded %d games.".formatted(games.size()));
    }
}
