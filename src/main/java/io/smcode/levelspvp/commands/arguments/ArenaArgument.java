package io.smcode.levelspvp.commands.arguments;

import io.smcode.levelspvp.commands.ArgumentExecutor;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.Game;
import io.smcode.levelspvp.game.GameManager;
import io.smcode.levelspvp.game.GameTeam;
import io.smcode.levelspvp.setup.PointsManager;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class ArenaArgument implements ArgumentExecutor {
    private final GameManager gameManager;
    private final Messages messages;
    private final PointsManager pointsManager;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(messages.getPrefix().append(Component.text("Usage: /levelspvp arena <game> <team>", NamedTextColor.RED)));
            return;
        }

        final Optional<Game> optionalGame = this.gameManager.getGame(args[1]);

        if (optionalGame.isEmpty()) {
            player.sendMessage(messages.getMessage(Message.ENTITY_NOT_FOUND,
                    Placeholder.unparsed("name", args[1]), Placeholder.unparsed("entity", "Game")));
            return;
        }

        final Game game = optionalGame.get();

        try {
            final GameTeam team = GameTeam.valueOf(args[2].toUpperCase());
            pointsManager.giveTool(player, game, team, PointsManager.PointType.ARENA);
        } catch (IllegalArgumentException e) {
            player.sendRichMessage("<red><team> is not a valid team", Placeholder.unparsed("team", args[2]));
        }
    }

    @Override
    public String getUsage() {
        return "arena <game> <team>";
    }

    @Override
    public String getDescription() {
        return "Set arena for a game.";
    }
}
