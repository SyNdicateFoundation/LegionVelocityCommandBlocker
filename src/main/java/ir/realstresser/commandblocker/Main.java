//TODO: make this shit use .toml instead
package ir.realstresser.commandblocker;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import ir.realstresser.commandblocker.configuration.ConfigManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Plugin(id = "command_blocker", name = "LegionCommandBlocker", version = "1.0", description = "Command blocker with configuration.")
@SuppressWarnings("unused") public class Main {
    @Getter private static Main instance;
    @Getter private static ProxyServer serverInstance;
    @Getter private static ConfigManager configManager;
    public String blockedMessage;
    public Map<String, HashMap<String, Object>> commandsToWhiteList;
    public Logger logger;

    @Inject public Main(final ProxyServer server, final Logger logger) {
        instance = this;
        serverInstance = server;
        this.logger = logger;
        commandsToWhiteList = new HashMap<>();
    }
    @SneakyThrows
    @Subscribe public void onInit(final ProxyInitializeEvent e) {
        configManager = new ConfigManager();
        blockedMessage = configManager.getString("blockmessage", "Sorry this command is blocked.");
        serverInstance.getEventManager().register(this, new CommandListener());
    }
}
