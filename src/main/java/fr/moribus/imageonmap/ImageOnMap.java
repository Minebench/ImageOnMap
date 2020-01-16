package fr.moribus.imageonmap;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import fr.moribus.imageonmap.ditherlib.Atkinson;
import fr.moribus.imageonmap.ditherlib.Ditherer;
import fr.moribus.imageonmap.ditherlib.FloydSteinberg;
import fr.moribus.imageonmap.ditherlib.JarvisJudiceNinke;
import fr.moribus.imageonmap.ditherlib.NaiveDither;
import fr.moribus.imageonmap.ditherlib.Sierra24A;
import fr.moribus.imageonmap.ditherlib.Sierra3;
import fr.moribus.imageonmap.ditherlib.Stucki;
import fr.moribus.imageonmap.ditherlib.colors.ColorMetric;
import fr.moribus.imageonmap.ditherlib.colors.ColorSelectionFactory;
import fr.moribus.imageonmap.ditherlib.colors.ColorSelector;
import fr.moribus.imageonmap.ditherlib.colors.NaiveMetric;
import fr.moribus.imageonmap.ditherlib.colors.RGBLumosityMetric;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;

public final class ImageOnMap extends JavaPlugin {
    public static Ditherer DITHERER = null;
    int test = 0;
    File dossier;
    boolean dossierCree;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private HashMap<String, List<ItemStack>> cache = new HashMap<>();

    public void onEnable() {
        this.dossierCree = ImgUtility.creeRepImg(this);

        loadConfig();
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0])) {
                loadConfig();
                sender.sendMessage(ChatColor.YELLOW + "Config reloaded!");
                return true;
            }
        }
        return false;
    }

    private void loadConfig() {
        reloadConfig();
        ImgUtility.creeSectionConfig(this);
        try {
            Field colorsField = MapPalette.class.getDeclaredField("colors");
            colorsField.setAccessible(true);
            Color[] colors = (Color[]) colorsField.get(null);
            ColorMetric cm;
            switch (this.getConfig().getString("image-dithering.metric")) {
                case "rgblumin":    cm = new RGBLumosityMetric(); break;
                case "bukkit":      cm = (c1, c2) -> {
                    // From private MapPalette#getDistance method
                    double rmean = (c1.getRed() + c2.getRed()) / 2.0D;
                    double r = c1.getRed() - c2.getRed();
                    double g = c1.getGreen() - c2.getGreen();
                    double b = c1.getBlue() - c2.getBlue();
                    double weightR = 2.0D + rmean / 256.0D;
                    double weightG = 4.0D;
                    double weightB = 2.0D - (255 - rmean) / 256.0D;
                    return weightR * r * r + weightG * g * g + weightB * b * b;
                }; break;
                default:            cm = new NaiveMetric(); break;
            }

            ColorSelector selector = ColorSelectionFactory.getInstance(colors, cm);
            switch (this.getConfig().getString("image-dithering.type").toLowerCase()) {
                case "atkinson":        DITHERER = new Atkinson(selector); break;
                case "floyd steinberg": DITHERER = new FloydSteinberg(selector); break;
                case "jarvis":          DITHERER = new JarvisJudiceNinke(selector); break;
                case "stucki":          DITHERER = new Stucki(selector); break;
                case "sierra 3":        DITHERER = new Sierra3(selector); break;
                case "sierra 2-4a":     DITHERER = new Sierra24A(selector); break;
                default:                DITHERER = new NaiveDither(selector); break;
            }
            getLogger().info("Using " + DITHERER.toString() + " as image ditherer");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void loadMaps() {
        int nbMap = 0;
        int nbErr = 0;
        File dir = new File(getDataFolder(),"Image");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(child.getName().startsWith("map")) {
                    try {
                        String idStr = child.getName().substring(3, child.getName().lastIndexOf('.'));
                        short id = Short.parseShort(idStr);
                        SavedMap map = new SavedMap(this, id);
                        if(map.loadMap()) {
                            nbMap++;
                        } else {
                            nbErr++;
                        }
                    } catch (NumberFormatException e) {
                        getLogger().warning(child.getName().substring(3) + " is not a valid map id? (File " + child.getName() + ")");
                        nbErr++;
                    } catch (IndexOutOfBoundsException e) {
                        getLogger().warning(child.getName() + " is not a valid map image?");
                        nbErr++;
                    }
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
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
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

    public List<ItemStack> getRemainingMaps(String j) {
        return this.cache.get(j);
    }

    public void setRemainingMaps(String j, List<ItemStack> remaining) {
        this.cache.put(j, remaining);
    }

    public void removeRemaingMaps(String j) {
        this.cache.remove(j);
    }
}