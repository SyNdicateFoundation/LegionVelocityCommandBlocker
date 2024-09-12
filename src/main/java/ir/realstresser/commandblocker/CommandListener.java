package ir.realstresser.commandblocker;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashSet;
import java.util.Map;

@SuppressWarnings({"unused"})
public class CommandListener {

    @Subscribe
    public void onCommand(final CommandExecuteEvent e) {
        if (e.getCommandSource() instanceof ConsoleCommandSource || e.getCommandSource().hasPermission("blocker.bypass"))
            return;

        Player player = (Player) e.getCommandSource();
        String currentServer = player.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("");

        for (String commandName : Main.getConfigManager().getSection("commands").keySet()) {
            Map<String, Object> commandSection = (Map<String, Object>) Main.getConfigManager().getSection("commands").get(commandName);

            String bypass = (String) commandSection.getOrDefault("bypass", "default.bypass");
            String server = (String) commandSection.getOrDefault("server", "default.server");

            if (server.equalsIgnoreCase("ALL") || server.equalsIgnoreCase(currentServer)) {
                if (e.getCommand().matches(bypass))
                    return;
            }
        }

        e.setResult(CommandExecuteEvent.CommandResult.denied());
        e.getCommandSource().sendMessage(Component.text(Main.getInstance().blockedMessage).color(TextColor.color(255, 0, 0)));
    }

    @Subscribe(order = PostOrder.LAST)
    @SuppressWarnings("UnstableApiUsage")
    public void onUserCommandSendEvent(final PlayerAvailableCommandsEvent e) {
        if (e.getPlayer().hasPermission("blocker.bypass")) return;

        String currentServer = e.getPlayer().getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("");

        final HashSet<String> allowedCommands = new HashSet<>();

        for (String commandName : Main.getConfigManager().getSection("commands").keySet()) {
            Map<String, Object> commandSection = (Map<String, Object>) Main.getConfigManager().getSection("commands").get(commandName);
            String bypass = (String) commandSection.getOrDefault("bypass", "default.bypass");
            String server = (String) commandSection.getOrDefault("server", "default.server");

            if (server.equalsIgnoreCase("ALL") || server.equalsIgnoreCase(currentServer))
                allowedCommands.add(commandName);
        }
        e.getRootNode().getChildren().removeIf(commandNode ->
                !allowedCommands.contains(commandNode.getName().toLowerCase())
        );
    }
}
