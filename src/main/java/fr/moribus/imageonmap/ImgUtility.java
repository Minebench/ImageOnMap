package fr.moribus.imageonmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
            plugin.getConfig().set("Limit-map-by-server", Integer.valueOf(0));
        }
        if (plugin.getConfig().get("Limit-map-by-player") == null) {
            plugin.getConfig().set("Limit-map-by-player", Integer.valueOf(0));
        }
        if (plugin.getConfig().get("collect-data") == null) {
            plugin.getConfig().set("collect-data", Boolean.valueOf(true));
        }
        if (plugin.getConfig().get("import-maps") == null) {
            plugin.getConfig().set("import-maps", Boolean.valueOf(true));
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
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (((String) plugin.getCustomConfig().getStringList(s).get(2)).contentEquals(pseudo))) {
                nombre++;
            }
        }
        return nombre;
    }

    static boolean estDansFichier(ImageOnMap plugin, short id) {
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (Short.parseShort((String) plugin.getCustomConfig().getStringList(s).get(0)) == id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean estDansFichier(ImageOnMap plugin, short id, String pseudo) {
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (Short.parseShort((String) plugin.getCustomConfig().getStringList(s).get(0)) == id) && (((String) plugin.getCustomConfig().getStringList(s).get(2)).contentEquals(pseudo))) {
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
                ArrayList<String> liste = new ArrayList();
                liste.add(String.valueOf(plugin.getConfig().getStringList(s).get(0)));
                liste.add((String) plugin.getConfig().getStringList(s).get(1));
                liste.add((String) plugin.getConfig().getStringList(s).get(2));
                plugin.getCustomConfig().set((String) plugin.getConfig().getStringList(s).get(1), liste);
                plugin.getConfig().set(s, null);
                i++;
            }
        }
        plugin.getLogger().info("Importing finished. " + i + "maps were imported");
        plugin.getConfig().set("import-maps", Boolean.valueOf(false));
        plugin.saveConfig();
        plugin.saveCustomConfig();
        return true;
    }

    static MapView getMap(ImageOnMap plugin, short id) {
        if (!estDansFichier(plugin, id)) {
            return null;
        }
        MapView map = Bukkit.getMap(id);
        if (map == null) {
            plugin.getLogger().warning("Map#" + id + " exists in maps.yml but not in the world folder !");
            return null;
        }
        return map;
    }

    static boolean removeMap(ImageOnMap plugin, short id) {
        if(plugin.getCustomConfig().contains("map" + id)) {
            MapView carte = Bukkit.getMap(id);

            if (carte == null)
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

    static ArrayList<String> getListMapByPlayer(ImageOnMap plugin, String pseudo) {
        ArrayList<String> listeMap = new ArrayList();
        Set<String> cle = plugin.getCustomConfig().getKeys(false);
        for (String s : cle) {
            if ((plugin.getCustomConfig().getStringList(s).size() >= 3) && (pseudo.equalsIgnoreCase((String) plugin.getCustomConfig().getStringList(s).get(2)))) {
                listeMap.add((String) plugin.getCustomConfig().getStringList(s).get(0));
            }
        }
        return listeMap;
    }

    static void addMap(ItemStack map, Inventory inv, ArrayList<ItemStack> restant) {
        HashMap<Integer, ItemStack> reste = inv.addItem(new ItemStack[]{map});
        if (!reste.isEmpty()) {
            restant.add((ItemStack) reste.get(Integer.valueOf(0)));
        }
    }
}