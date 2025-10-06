package io.smcode.levelspvp.commands;

import io.smcode.levelspvp.commands.arguments.CreateGameArgument;
import io.smcode.levelspvp.commands.arguments.JoinGameArgument;
import io.smcode.levelspvp.commands.arguments.SetTeamLocationArgument;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelsCommand implements TabExecutor {
    private static final Map<String, ArgumentExecutor> arguments = new HashMap<>();
    private static final String helpFormat = "<blue>/levelspvp %s <gray> - %s";

    private final Messages messages;

    public LevelsCommand(GameManager manager, Messages messages) {
        this.messages = messages;
        arguments.put("create", new CreateGameArgument(messages, manager));
        arguments.put("spawn", new SetTeamLocationArgument(manager, messages));
        arguments.put("join", new JoinGameArgument(manager, messages));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(messages.getMessage(Message.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        final String argument = args[0].toLowerCase();
        final ArgumentExecutor argumentExecutor = arguments.get(argument);

        if (argumentExecutor == null) {
            sendHelp(player);
            return true;
        }

        argumentExecutor.execute(player, args);

        return true;
    }

    private void sendHelp(Player player) {
        for (ArgumentExecutor argument : arguments.values()) {
            player.sendRichMessage(helpFormat.formatted(argument.getUsage(), argument.getDescription()));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            final List<String> validArguments = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], arguments.keySet(), validArguments);
            return validArguments;
        }
        return List.of();
    }
}
