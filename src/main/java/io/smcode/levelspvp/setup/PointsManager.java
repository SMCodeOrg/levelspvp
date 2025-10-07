package io.smcode.levelspvp.setup;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.smcode.levelspvp.LevelsPlugin;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.Game;
import io.smcode.levelspvp.game.GameTeam;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;

import java.util.*;

@RequiredArgsConstructor
public class PointsManager implements Listener {
    private static final NamespacedKey key;
    private static final NamespacedKey keyId;
    private final Map<UUID, List<Location>> positions = new HashMap<>();
    private final Map<UUID, PointSession> sessions = new HashMap<>();
    private final Messages messages;

    static {
        key = new NamespacedKey(LevelsPlugin.getInstance(), "point_tool");
        keyId = new NamespacedKey(LevelsPlugin.getInstance(), "point_tool_id");
    }

    @SuppressWarnings("UnstableApiUsage")
    public void giveTool(Player player, Game game, GameTeam team, PointType type) {
        final ItemStack item = ItemStack.of(Material.BLAZE_ROD);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Point Tool", NamedTextColor.YELLOW));
        item.lore(List.of(
                Component.text(" "),
                Component.text("Click a block to mark position", NamedTextColor.GRAY),
                Component.text(" ")
        ));

        final UUID id = UUID.randomUUID();

        item.editPersistentDataContainer(pdc -> {
            pdc.set(key, PersistentDataType.STRING, type.name());
            pdc.set(keyId, PersistentDataType.STRING, id.toString());
        });

        player.getInventory().addItem(item);
        sessions.put(player.getUniqueId(), new PointSession(team, game));
        positions.put(id, new ArrayList<>());
    }

    public List<Location> addPoint(ItemStack item, Location location) {
        final UUID id = UUID.fromString(Objects.requireNonNull(item.getPersistentDataContainer().get(keyId, PersistentDataType.STRING)));
        final List<Location> points = positions.getOrDefault(id, new ArrayList<>());

        if (points.size() < 2) {
            points.add(location);
            positions.put(id, points);
        }

        return points;
    }

    public PointType getPointType(ItemStack item) {
        return PointType.valueOf(item.getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }

    public boolean isPointTool(ItemStack item) {
        return item.getPersistentDataContainer().has(key) && item.getType() == Material.BLAZE_ROD;
    }

    public Optional<PointSession> getSessionGame(Player player) {
        return Optional.ofNullable(sessions.get(player.getUniqueId()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();
        final Block block;

        if ((block = event.getClickedBlock()) == null || item == null || !isPointTool(item)) return;

        event.setCancelled(true);

        final PointType type = getPointType(item);

        if (type == PointType.ARENA) {
            final List<Location> points;

            if ((points = addPoint(item, block.getLocation())).size() == 2) {
                final Location loc1 = points.get(0);
                final Location loc2 = points.get(1);
                final BoundingBox arena = new BoundingBox(
                        Math.min(loc1.getBlockX(), loc2.getBlockX()),
                        loc1.getY(),
                        Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
                        Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1,
                        loc2.getY(),
                        Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1
                );
                final Optional<PointSession> optionalPointSession = getSessionGame(player);

                optionalPointSession.ifPresent(session -> {
                    final Game game = session.game();
                    final GameTeam team = session.team();
                    game.setArena(team, arena);
                    player.sendMessage(messages.getMessage(Message.ARENA_CREATED));
                    player.getInventory().remove(item);
                    sessions.remove(player.getUniqueId());
                });
            }
        }
    }

    public enum PointType {
        ARENA,
        LAYER_DOOR;
    }

    public record PointSession(GameTeam team, Game game) {

    }
}
