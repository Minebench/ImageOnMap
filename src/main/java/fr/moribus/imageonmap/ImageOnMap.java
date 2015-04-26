package fr.moribus.ImageOnMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ImageOnMap extends JavaPlugin {
    int test = 0;
    File dossier;
    boolean dossierCree;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private HashMap<String, ArrayList<ItemStack>> cache = new HashMap();

    public void onLoad() {
    }

    public void onEnable() {
        this.dossierCree = ImgUtility.creeRepImg(this);

        ImgUtility.creeSectionConfig(this);
        if (this.getConfig().get("map_path") == null) {
            this.getConfig().set("map_path", this.getServer().getWorlds().get(0).getName());
        } else if (this.getConfig().get("map_path") != this.getServer().getWorlds().get(0).getName()) {
            this.getConfig().set("map_path", this.getServer().getWorlds().get(0).getName());
        }
        if (this.getConfig().getBoolean("import-maps")) {
            ImgUtility.importerConfig(this);
        }
        if (this.dossierCree) {
            this.getCommand("tomap").setExecutor(new ImageRenderCommand(this));
            this.getCommand("maptool").setExecutor(new MapToolCommand(this));

            this.saveDefaultConfig();
            this.loadMaps();
        } else {
            this.getLogger().info("An isErroring occured ! Unable to create Image folder. Plugin will NOT work !");
            this.setEnabled(false);
        }
    }

    public void loadMaps() {
        Set<String> cle = getCustomConfig().getKeys(false);
        int nbMap = 0;
        int nbErr = 0;
        for (String s : cle) {
            if (this.getCustomConfig().getStringList(s).size() >= 3) {
                SavedMap map = new SavedMap(this, Short.valueOf(this.getCustomConfig().getStringList(s).get(0)));
                if (map.loadMap()) {
                    nbMap++;
                } else {
                    nbErr++;
                }
            }
        }
        this.getLogger().info(nbMap + " maps were loaded");
        if (nbErr != 0) {
            this.getLogger().info(nbErr + " maps couldn't be loaded");
        }
    }

    public void reloadCustomConfig() {
        if (this.customConfigFile == null) {
            this.customConfigFile = new File(this.getDataFolder(), "map.yml");
        }
        this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);


        InputStream defConfigStream = this.getResource("map.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.customConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig() {
        if (this.customConfig == null) {
            reloadCustomConfig();
        }
        return this.customConfig;
    }

    public void saveCustomConfig() {
        if ((this.customConfig == null) || (this.customConfigFile == null)) {
            return;
        }
        try {
            this.getCustomConfig().save(this.customConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
        }
    }

    public ArrayList<ItemStack> getRemainingMaps(String j) {
        return (ArrayList) this.cache.get(j);
    }

    public void setRemainingMaps(String j, ArrayList<ItemStack> remaining) {
        this.cache.put(j, remaining);
    }

    public void removeRemaingMaps(String j) {
        this.cache.remove(j);
    }
}