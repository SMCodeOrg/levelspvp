package io.smcode.skywars.listeners;

import io.smcode.skywars.config.Message;
import io.smcode.skywars.config.Messages;
import io.smcode.skywars.events.AsyncCountDownTickEvent;
import io.smcode.skywars.events.PlayerAttemptJoinGameEvent;
import io.smcode.skywars.events.PlayerJoinGameEvent;
import io.smcode.skywars.events.PlayerLeaveGameEvent;
import io.smcode.skywars.game.*;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class GameListener implements Listener {
    private final Messages messages;
    private final GameManager manager;

    @EventHandler
    public void onAttemptJoinGame(PlayerAttemptJoinGameEvent event) {
        final Game game = event.getGame();
        final Player player = event.getPlayer();

        if (game.getState() != GameState.IN_LOBBY) {
            event.setCancelled(true);
            player.sendMessage(messages.getMessage(Message.GAME_NOT_JOINABLE));
            return;
        }

        if (game.getPlayers().size() >= game.getSettings().getMaxPlayers()) {
            event.setCancelled(true);
            player.sendMessage(messages.getMessage(Message.GAME_FULL));
            return;
        }

        if (game.hasPlayer(player)) {
            event.setCancelled(true);
            player.sendMessage(messages.getMessage(Message.GAME_NOT_JOINABLE));
            return;
        }

        player.teleport(game.getLocations().getLobby());
    }

    @EventHandler
    public void onGameJoin(PlayerJoinGameEvent event) {
        final Game game = event.getGame();
        final Player player = event.getPlayer();

        game.broadcast(messages.getMessage(Message.GAME_JOINED,
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("players", String.valueOf(game.getPlayers().size())),
                Placeholder.unparsed("maxplayers", String.valueOf(game.getSettings().getMaxPlayers()))));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.leaveGame(event.getPlayer());
    }

    @EventHandler
    public void onGameLeave(PlayerLeaveGameEvent event) {
        final Game game = event.getGame();
        final Player player = event.getPlayer();

        game.broadcast(messages.getMessage(Message.GAME_JOINED,
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("players", String.valueOf(game.getPlayers().size())),
                Placeholder.unparsed("maxplayers", String.valueOf(game.getSettings().getMaxPlayers()))));
    }

    @EventHandler
    public void onCountDownTick(AsyncCountDownTickEvent event) {
        final int secondsLeft = event.getTimer().getSeconds();
        final Game game = event.getTimer().getGame();

        if (secondsLeft == 0) {
            int i = 0;

            for (GamePlayer player : game.getPlayers()) {
                final GameTeam team = GameTeam.values()[i % GameTeam.values().length];
                final Location teamSpawn = game.getLocations().getSpawns().get(team);
                final Set<GamePlayer> currentPlayersInTeam = game.getTeams().getOrDefault(team, new HashSet<>());
                currentPlayersInTeam.add(player);
                game.getTeams().put(team, currentPlayersInTeam);
                player.getPlayer().teleport(teamSpawn);

                i++;
            }
        }

        if (secondsLeft % 30 == 0) {
            game.broadcast(messages.getMessage(Message.COUNTDOWN_TIME, Placeholder.unparsed("seconds", String.valueOf(secondsLeft))));
        }
    }
}
