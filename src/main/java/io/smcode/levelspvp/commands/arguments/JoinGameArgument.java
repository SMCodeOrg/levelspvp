package io.smcode.levelspvp.commands.arguments;

import io.smcode.levelspvp.commands.ArgumentExecutor;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.Game;
import io.smcode.levelspvp.game.GameManager;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class JoinGameArgument implements ArgumentExecutor {
    private final GameManager manager;
    private final Messages messages;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messages.getPrefix().append(Component.text("Usage: /levelspvp join <game>", NamedTextColor.RED)));
            return;
        }

        final Optional<Game> optionalGame = this.manager.getGame(args[1]);

        if (optionalGame.isEmpty()) {
            player.sendMessage(messages.getMessage(Message.ENTITY_NOT_FOUND,
                    Placeholder.unparsed("name", args[1]), Placeholder.unparsed("entity", "Game")));
            return;
        }

        final Game game = optionalGame.get();
        this.manager.joinGame(game, player);
    }

    @Override
    public String getUsage() {
        return "join <game>";
    }

    @Override
    public String getDescription() {
        return "Join a game of LevelsPVP";
    }
}
