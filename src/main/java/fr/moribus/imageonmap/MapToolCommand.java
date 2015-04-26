package fr.moribus.ImageOnMap;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class MapToolCommand
        implements CommandExecutor {
    short id;
    ImageOnMap plugin;
    MapView map;
    Player player;
    Inventory inv;

    MapToolCommand(ImageOnMap p) {
        this.plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
        if (!ImgUtility.verifyIdentity(sender)) {
            return false;
        }
        String nomCmd = arg2;
        this.player = ((Player) sender);
        this.inv = this.player.getInventory();
        if (arg3.length < 1) {
            this.player.sendMessage("Map tools usage:\n/" +
                    ChatColor.GOLD + nomCmd + ChatColor.RESET + " get [id]: get the map corresponding to this id" +
                    "\n/" + ChatColor.GOLD + nomCmd + ChatColor.RESET + " delete [id]: remove the map corresponding to this id" +
                    "\n/" + ChatColor.GOLD + nomCmd + ChatColor.RESET + " list: show all ids of maps in your possession");
            return true;
        }
        if (arg3[0].equalsIgnoreCase("get")) {
            try {
                this.id = Short.parseShort(arg3[1]);
            } catch (NumberFormatException err) {
                this.player.sendMessage("you must enter a number !");
                return true;
            }
            this.map = ImgUtility.getMap(this.plugin, this.id);
            if (this.map == null) {
                if (this.player.isOp()) {
                    this.player.sendMessage(ChatColor.RED + "Can't retrieve the map ! Check if map" + this.id + " exists in your maps.yml or if the dat file in the world folder exists");
                } else {
                    this.player.sendMessage(ChatColor.RED + "ERROR: This map doesn't exists");
                }
                return true;
            }
            if (this.inv.firstEmpty() == -1) {
                this.player.sendMessage("Your inventory is full, you can't take the map !");
                return true;
            }
            this.inv.addItem(new ItemStack[]{new ItemStack(Material.MAP, 1, this.map.getId())});
            this.player.sendMap(this.map);
            this.player.sendMessage("Map " + ChatColor.ITALIC + this.id + ChatColor.RESET + " was added to your inventory.");

            return true;
        }
        if (arg3[0].equalsIgnoreCase("delete")) {
            if (!this.player.hasPermission("imageonmap.usermmap")) {
                this.player.sendMessage("You are not allowed to delete map !");
                return true;
            }
            if (arg3.length <= 1) {
                if (this.player.getItemInHand().getType() == Material.MAP) {
                    this.id = this.player.getItemInHand().getDurability();
                } else {
                    this.player.sendMessage(ChatColor.RED + "You must hold a map or enter an id");
                }
            } else {
                try {
                    this.id = Short.parseShort(arg3[1]);
                } catch (NumberFormatException err) {
                    this.player.sendMessage("you must enter a number !");
                    return true;
                }
            }
            if (ImgUtility.removeMap(this.plugin, this.id)) {
                this.player.sendMessage("Map#" + this.id + " was deleted");
                return true;
            }
            this.player.sendMessage(ChatColor.RED + "Can't delete Map#" + this.id + ": check the server log");
            return true;
        }
        if (arg3[0].equalsIgnoreCase("list")) {
            String msg = "";
            int compteur = 0;

            ArrayList<String> liste = ImgUtility.getListMapByPlayer(this.plugin, this.player.getName());
            for (; compteur < liste.size(); compteur++) {
                msg = msg + (String) liste.get(compteur) + " ";
            }
            this.player.sendMessage(msg +
                    "\nYou have rendered " + ChatColor.DARK_PURPLE + (compteur + 1) + ChatColor.RESET + " pictures");
        } else if (arg3[0].equalsIgnoreCase("getrest")) {
            if (this.plugin.getRemainingMaps(this.player.getName()) == null) {
                this.player.sendMessage("All maps have already be placed in your inventory");
                return true;
            }
            ArrayList<ItemStack> reste = this.plugin.getRemainingMaps(this.player.getName());
            ArrayList<ItemStack> restant = new ArrayList<ItemStack>();
            for (int i = 0; i < reste.size(); i++) {
                ImgUtility.addMap((ItemStack) reste.get(i), this.inv, restant);
            }
            if (restant.isEmpty()) {
                this.plugin.removeRemaingMaps(this.player.getName());
                this.player.sendMessage("All maps have been placed in your inventory");
            } else {
                this.plugin.setRemainingMaps(this.player.getName(), restant);
                this.player.sendMessage(restant.size() + " maps can't be placed in your inventory. Please run " + ChatColor.GOLD + "/maptool getrest again");
            }
        }
        return true;
    }
}