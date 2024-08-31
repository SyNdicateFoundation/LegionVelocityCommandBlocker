//TODO: make this shit use .toml instead
package ir.realstresser.commandblocker;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Plugin(id = "command_blocker", name = "LegionCommandBlocker", version = "1.0", description = "Command blocker with configuration.")
@SuppressWarnings("unused") public class Main {
    @Getter private static Main instance;
    @Getter private static ProxyServer serverInstance;
    private final File legionFolder;
    private final File configFile;
    public String blockedMessage;
    public Map<String, HashSet<String>> commandsToWhiteList;
    public String bypassPerm;
    public Logger logger;

    @Inject public Main(final ProxyServer server, final Logger logger) {
        instance = this;
        serverInstance = server;
        this.logger = logger;
        commandsToWhiteList = new HashMap<>();
        this.legionFolder = new File(getPluginDirectory().toString(), "LegionCommandBlocker");
        this.configFile = new File(legionFolder, "config.txt");
    }
    @SneakyThrows
    @Subscribe public void onInit(final ProxyInitializeEvent e) {
        handleConfig();

        logger.info("registered: ");

        Main.getInstance().commandsToWhiteList.forEach((key, value) -> value.forEach(v -> {
            logger.info(key + ": ");
            logger.info(v + " ");
        }));

        serverInstance.getEventManager().register(this, new CommandListener());
    }

    private void handleConfig() throws Exception{
        if (!legionFolder.exists()) {
            legionFolder.mkdirs();
            try (final BufferedWriter bf = new BufferedWriter(new FileWriter(configFile))) {
                bf.write("# usage: SERVERNAME:COMMAND");
                bf.newLine();
                bf.write("BLOCKMESSAGE:Sorry, this command is blocked on this server, try later.");
                bf.newLine();
                bf.write("BYPASS:blocker.bypass");
                bf.newLine();
                bf.write("# BEGIN OF COMMAND LIST");
                bf.newLine();
                bf.write("ALL:tpa, spawn");
            }
        }

        try (final BufferedReader bf = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.trim().startsWith("#")) continue;

                final String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;

                final String key = parts[0].trim();
                String val = parts[1].trim();

                switch (key) {
                    case "BLOCKMESSAGE":
                        blockedMessage = val;
                        break;
                    case "BYPASS":
                        bypassPerm = val;
                        break;
                    default:
                        final String[] commands = val.split(",");
                        Arrays.stream(commands).forEach(v -> commandsToWhiteList.computeIfAbsent(key, k -> new HashSet<>()).add(v.trim()));
                        break;
                }
            }
        }
}


    private Path getPluginDirectory() {
        return Paths.get("plugins");
    }
}
