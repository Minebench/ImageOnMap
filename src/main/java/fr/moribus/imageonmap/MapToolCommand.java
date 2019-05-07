package fr.moribus.imageonmap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class MapToolCommand implements CommandExecutor {
    private ImageOnMap plugin;

    MapToolCommand(ImageOnMap p) {
        this.plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
        if (!ImgUtility.verifyIdentity(sender)) {
            return false;
        }
        String nomCmd = arg2;
        Player player = ((Player) sender);
        Inventory inv = player.getInventory();
        if (arg3.length < 1) {
            player.sendMessage("Map tools usage:\n/" +
                    ChatColor.GOLD + nomCmd + ChatColor.RESET + " get [id]: get the map corresponding to this id" +
                    "\n/" + ChatColor.GOLD + nomCmd + ChatColor.RESET + " delete [id]: remove the map corresponding to this id" +
                    "\n/" + ChatColor.GOLD + nomCmd + ChatColor.RESET + " list: show all ids of maps in your possession");
            return true;
        }
        int id;
        if (arg3[0].equalsIgnoreCase("get")) {
            try {
                id = Integer.parseInt(arg3[1]);
            } catch (NumberFormatException err) {
                player.sendMessage("you must enter a number !");
                return true;
            }
            MapView map = ImgUtility.getMap(this.plugin, id);
            if (map == null) {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.RED + "Can't retrieve the map ! Check if dat file map" + id + " in the world folder exists");
                } else {
                    player.sendMessage(ChatColor.RED + "ERROR: This map doesn't exists");
                }
                return true;
            }
            if (inv.firstEmpty() == -1) {
                player.sendMessage("Your inventory is full, you can't take the map!");
                return true;
            }
            ItemStack item = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta) item.getItemMeta();
            meta.setMapView(map);
            item.setItemMeta(meta);
            inv.addItem(item);
            player.sendMap(map);
            player.sendMessage("Map " + ChatColor.ITALIC + id + ChatColor.RESET + " was added to your inventory.");

            return true;
        }
        if (arg3[0].equalsIgnoreCase("delete")) {
            if (!player.hasPermission("imageonmap.usermmap")) {
                player.sendMessage("You are not allowed to delete map !");
                return true;
            }
            if (arg3.length <= 1) {
                if (player.getItemInHand().getType() == Material.FILLED_MAP) {
                    id = ((MapMeta) player.getItemInHand().getItemMeta()).getMapId();
                } else {
                    player.sendMessage(ChatColor.RED + "You must hold a map or enter an id");
                    return true;
                }
            } else {
                try {
                    id = Integer.parseInt(arg3[1]);
                } catch (NumberFormatException err) {
                    player.sendMessage("you must enter a number !");
                    return true;
                }
            }
            if (ImgUtility.removeMap(this.plugin, id)) {
                player.sendMessage("Map#" + id + " was deleted");
                return true;
            }
            player.sendMessage(ChatColor.RED + "Can't delete Map#" + id + ": check the server log");
            return true;
        }
        if (arg3[0].equalsIgnoreCase("list")) {
            List<String> liste = ImgUtility.getListMapByPlayer(this.plugin, player.getName());
            player.sendMessage(String.join(" ", liste) +
                    "\nYou have rendered " + ChatColor.DARK_PURPLE + liste.size() + ChatColor.RESET + " pictures");
        } else if (arg3[0].equalsIgnoreCase("getrest")) {
            if (this.plugin.getRemainingMaps(player.getName()) == null) {
                player.sendMessage("All maps have already be placed in your inventory");
                return true;
            }
            List<ItemStack> reste = this.plugin.getRemainingMaps(player.getName());
            List<ItemStack> restant = new ArrayList<>();
            for (int i = 0; i < reste.size(); i++) {
                ImgUtility.addMap(reste.get(i), inv, restant);
            }
            if (restant.isEmpty()) {
                this.plugin.removeRemaingMaps(player.getName());
                player.sendMessage("All maps have been placed in your inventory");
            } else {
                this.plugin.setRemainingMaps(player.getName(), restant);
                player.sendMessage(restant.size() + " maps can't be placed in your inventory. Please run " + ChatColor.GOLD + "/maptool getrest again");
            }
        }
        return true;
    }
}