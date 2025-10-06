package io.smcode.levelspvp.commands.arguments;

import io.smcode.levelspvp.commands.ArgumentExecutor;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.Game;
import io.smcode.levelspvp.game.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class CreateGameArgument implements ArgumentExecutor {
    private final Messages messages;
    private final GameManager manager;


    public CreateGameArgument(Messages messages, GameManager manager) {
        this.messages = messages;
        this.manager = manager;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messages.getPrefix().append(Component.text("Usage: /levelspvp create <name>", NamedTextColor.RED)));
            return;
        }

        final Game newGame = manager.createNewGame(player.getLocation(), args[1]);
        player.sendMessage(messages.getMessage(Message.GAME_CREATED, Placeholder.unparsed("gameid", newGame.getId().toString())));
    }

    @Override
    public String getUsage() {
        return "create <name>";
    }

    @Override
    public String getDescription() {
        return "Create a new game.";
    }
}
