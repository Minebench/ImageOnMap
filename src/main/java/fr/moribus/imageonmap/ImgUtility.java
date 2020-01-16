package fr.moribus.imageonmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class ImgUtility {

    static boolean verifyIdentity(CommandSender sender) {
        if ((sender instanceof Player)) {
            return true;
        }
        if ((sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command can't be run by the console!");
            return false;
        }
        if ((sender instanceof BlockCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command can't be run by a command block!");
            return false;
        }
        sender.sendMessage(ChatColor.RED + "This command can't be run by that sender!");
        return false;
    }

    static boolean creeRepImg(ImageOnMap plugin) {
        File dossier = new File(plugin.getDataFolder().getPath() + File.separator + "Image");
        if (!dossier.exists()) {
            return dossier.mkdirs();
        }
        return true;
    }

    static void creeSectionConfig(ImageOnMap plugin) {
        if (plugin.getConfig().get("Limit-map-by-server") == null) {
            plugin.getConfig().set("Limit-map-by-server", 0);
        }
        if (plugin.getConfig().get("Limit-map-by-player") == null) {
            plugin.getConfig().set("Limit-map-by-player", 0);
        }
        if (plugin.getConfig().get("collect-data") == null) {
            plugin.getConfig().set("collect-data", true);
        }
        if (plugin.getConfig().get("import-maps") == null) {
            plugin.getConfig().set("import-maps", true);
        }
        if (plugin.getConfig().get("image-dithering.type") == null) {
            plugin.getConfig().set("image-dithering.type", "none");
        }
        if (plugin.getConfig().get("image-dithering.metric") == null) {
            plugin.getConfig().set("image-dithering.metric", "rgblumin");
        }
        plugin.saveConfig();
    }

    static int getMapCount(ImageOnMap plugin) {
        int nombre = 0;
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if (plugin.getCustomConfig().getStringList(s).size() >= 3) {
                nombre++;
            }
        }
        return nombre;
    }

    static int getNombreDeMapsParJoueur(ImageOnMap plugin, String pseudo) {
        int nombre = 0;
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && ((plugin.getCustomConfig().getStringList(s).get(2)).contentEquals(pseudo))) {
                nombre++;
            }
        }
        return nombre;
    }

    static boolean estDansFichier(ImageOnMap plugin, int id) {
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (Integer.parseInt(plugin.getCustomConfig().getStringList(s).get(0)) == id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean estDansFichier(ImageOnMap plugin, int id, String pseudo) {
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (Integer.parseInt(plugin.getCustomConfig().getStringList(s).get(0)) == id) && ((plugin.getCustomConfig().getStringList(s).get(2)).contentEquals(pseudo))) {
                return true;
            }
        }
        return false;
    }

    static boolean importerConfig(ImageOnMap plugin) {
        Set<String> cle = plugin.getConfig().getKeys(false);

        plugin.getLogger().info("Start importing maps config to maps.yml...");
        int i = 0;
        for (String s : cle) {
            if (plugin.getConfig().getStringList(s).size() >= 3) {
                List<String> liste = new ArrayList<>();
                liste.add(String.valueOf(plugin.getConfig().getStringList(s).get(0)));
                liste.add(plugin.getConfig().getStringList(s).get(1));
                liste.add(plugin.getConfig().getStringList(s).get(2));
                plugin.getCustomConfig().set(plugin.getConfig().getStringList(s).get(1), liste);
                plugin.getConfig().set(s, null);
                i++;
            }
        }
        plugin.getLogger().info("Importing finished. " + i + "maps were imported");
        plugin.getConfig().set("import-maps", false);
        plugin.saveConfig();
        plugin.saveCustomConfig();
        return true;
    }

    static MapView getMap(ImageOnMap plugin, int id) {
        MapView map = Bukkit.getMap(id);
        if (map == null && estDansFichier(plugin, id)) {
            plugin.getLogger().warning("Map#" + id + " exists in maps.yml but not in the world folder !");
            return null;
        }
        return map;
    }

    static boolean removeMap(ImageOnMap plugin, int id) {
        if(plugin.getCustomConfig().contains("map" + id)) {
            MapView carte = Bukkit.getMap(id);

            if (carte != null)
                ImageRendererThread.emptyRenderers(carte);

            plugin.getCustomConfig().set("map" + id, null);
            plugin.saveCustomConfig();
            plugin.saveConfig();
            File map = new File(plugin.getDataFolder().getPath() + File.separator + "Image", "map" + id + ".png");
            boolean isDeleted = map.delete();
            if (isDeleted)
                return true;
            plugin.getLogger().warning("Picture map" + id + ".png cannot be deleted !");
            return false;
        }
        plugin.getLogger().info("No map with id" + id + " was found");
        return false;
    }

    static List<String> getListMapByPlayer(ImageOnMap plugin, String pseudo) {
        List<String> listeMap = new ArrayList<>();
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (pseudo.equalsIgnoreCase(plugin.getCustomConfig().getStringList(s).get(2)))) {
                listeMap.add(plugin.getCustomConfig().getStringList(s).get(0));
            }
        }
        return listeMap;
    }

    static void addMap(ItemStack map, Inventory inv, List<ItemStack> restant) {
        Map<Integer, ItemStack> reste = inv.addItem(map);
        if (!reste.isEmpty()) {
            restant.add(reste.get(0));
        }
    }
}