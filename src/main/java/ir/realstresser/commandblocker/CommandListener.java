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
    //gonna make this async, i guess
    @Subscribe
    public void onCommand(final CommandExecuteEvent e) {
        if (e.getCommandSource() instanceof ConsoleCommandSource || e.getCommandSource().hasPermission(Main.getInstance().bypassPerm)) return;

        if (Main.getInstance().commandsToWhiteList.entrySet().stream().noneMatch(entry ->
                (entry.getKey().equalsIgnoreCase("ALL")
                        || entry.getKey().equalsIgnoreCase(((Player) e.getCommandSource()).getCurrentServer().map(server ->
                        server.getServerInfo().getName()).orElse("")))
                        && entry.getValue().contains(e.getCommand().split(" ")[0].toLowerCase()))) {
            e.setResult(CommandExecuteEvent.CommandResult.denied());
            e.getCommandSource().sendMessage(Component.text(Main.getInstance().blockedMessage).color(TextColor.color(255, 0, 0)));
        }
    }

    @Subscribe(order = PostOrder.LAST)
    @SuppressWarnings("UnstableApiUsage")
    public void onUserCommandSendEvent(final PlayerAvailableCommandsEvent e) {
        if (e.getPlayer().hasPermission(Main.getInstance().bypassPerm)) return;

        final HashSet<String> allowedCommands = new HashSet<>();

        Main.getInstance().commandsToWhiteList.entrySet().forEach(entry -> {
            if (entry.getKey().equalsIgnoreCase("ALL") || entry.getKey().equalsIgnoreCase(e.getPlayer().getCurrentServer()
                    .map(server -> server.getServerInfo().getName())
                    .orElse("")))
                allowedCommands.addAll(entry.getValue());
        });

        e.getRootNode().getChildren().removeIf(commandNode ->
                !allowedCommands.contains(commandNode.getName().toLowerCase())
        );
    }

}
