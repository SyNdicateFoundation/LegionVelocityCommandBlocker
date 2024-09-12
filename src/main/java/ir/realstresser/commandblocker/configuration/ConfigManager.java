package ir.realstresser.commandblocker.configuration;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

@Getter public class ConfigManager {
    private Map<String, Object> configValues;
    private final File configPath;
    private final File configFile;
    public ConfigManager() {
        this.configPath = new File(getPluginDirectory().toString(), "LegionCommandBlocker");
        this.configFile = new File(configPath, "config.yml");
        try {
            if(!configPath.exists()) configPath.mkdirs();
            if (!configFile.exists()) copyConfig();
        }catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }
    public void init() {
        try {
            final FileInputStream inputStream = new FileInputStream(configPath);
            configValues = new Yaml().load(inputStream);
            inputStream.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getString(String path, String defaultValue) {
        return (String) getOrDefault(path, defaultValue);
    }

    @SuppressWarnings("unchecked") public Map<String, Object> getSection(String path) {
        return (Map<String, Object>) getOrDefault(path, Map.of());
    }

    @SuppressWarnings("unchecked") private Object getOrDefault(String path, Object defaultValue) {
        String[] sections = path.split("\\.");
        Map<String, Object> currentSection = configValues;
        for (int i = 0; i < sections.length - 1; i++) {
            currentSection = (Map<String, Object>) currentSection.get(sections[i]);
            if (currentSection == null) {
                return defaultValue;
            }
        }
        return currentSection.getOrDefault(sections[sections.length - 1], defaultValue);
    }
    public void copyConfig(){
        try {
            final InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml"));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            final BufferedWriter writer = new BufferedWriter(new FileWriter(configPath));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();
            in.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private Path getPluginDirectory() {
        return Paths.get("plugins");
    }
}
