package io.smcode.levelspvp.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Messages {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Component notFound = Component.text("<not found>");

    private final FileConfiguration config;
    private Component prefix = Component.empty();

    public Messages(File file) {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.config.options().setHeader(List.of(
                "This configuration file supports MiniMessage formatting for strings.",
                "Learn more about MiniMessages at:",
                "https://docs.advntr.dev/minimessage/format.html#minimessage-format",
                " ",
                "You can verify and preview your MiniMessage at:",
                "https://webui.advntr.dev/"
        ));

        final String prefix = this.config.getString("Prefix");

        if (prefix != null)
            this.prefix = mm.deserialize(prefix.trim()).appendSpace();

        try {
            this.config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Component getMessage(Message message) {
        return getMessage(message, TagResolver.empty());
    }

    public Component getMessage(Message message, TagResolver... placeholders) {
        final String messageString =  this.config.getString(message.getPath());

        if (messageString == null)
            return notFound;

        return prefix.append(mm.deserialize(messageString, TagResolver.resolver(placeholders)));
    }

    public Component getPrefix() {
        return prefix;
    }
}
