package fr.moribus.imageonmap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

public class TacheTraitementMap extends BukkitRunnable {
    int i;
    Player player;
    ImageRendererThread imageRendering;
    PlayerInventory inv;
    ItemStack map;
    ImageOnMap plugin;
    boolean resized;

    TacheTraitementMap(Player p, String url, ImageOnMap plug, boolean resize) {
        this.i = 0;
        this.player = p;
        this.imageRendering = new ImageRendererThread(url, resize);
        this.imageRendering.start();
        this.inv = this.player.getInventory();
        this.plugin = plug;
        this.resized = resize;
    }

    public void run() {
        if (!this.imageRendering.getStatus()) {
            this.i += 1;
            if (this.imageRendering.isErroring() || this.i > 42) {
                this.player.sendMessage("TIMEOUT: the render took too much time");
                cancel();
            }
        } else {
            cancel();
            int nbImage = this.imageRendering.getImg().length;
            if ((this.plugin.getConfig().getInt("Limit-map-by-server") != 0) && (nbImage + ImgUtility.getMapCount(this.plugin) > this.plugin.getConfig().getInt("Limit-map-by-server"))) {
                this.player.sendMessage("ERROR: cannot render " + nbImage + " picture(s): the limit of maps per server would be exceeded.");
                return;
            }
            if ((this.plugin.getConfig().getInt("Limit-map-by-player") != 0) && (nbImage + ImgUtility.getNombreDeMapsParJoueur(this.plugin, this.player.getName()) > this.plugin.getConfig().getInt("Limit-map-by-player"))) {
                this.player.sendMessage(ChatColor.RED + "ERROR: cannot render " + nbImage + " picture(s): the limit of maps allowed for you (per player) would be exceeded.");
                return;
            }
            ArrayList<ItemStack> restant = new ArrayList();
            for (int i = 0; i < nbImage; i++) {
                MapView map;
                if ((nbImage == 1) && (this.player.getItemInHand().getType() == Material.FILLED_MAP)) {
                    map = ((MapMeta) this.player.getItemInHand().getItemMeta()).getMapView();
                } else {
                    map = Bukkit.createMap(Bukkit.getServer().getWorlds().get(0));
                }
                ImageRendererThread.emptyRenderers(map);
                map.addRenderer(new Renderer(this.imageRendering.getImg()[i]));
                this.map = new ItemStack(Material.FILLED_MAP);
                MapMeta meta = (MapMeta) this.map.getItemMeta();
                meta.setMapView(map);
                if (nbImage > 1) {
                    List<String> lore = meta.getLore();
                    if(lore == null) {
                        lore = new ArrayList<String>();
                    }
                    lore.add(this.imageRendering.getNumeroMap().get(i));
                    meta.setLore(lore);
                }
                this.map.setItemMeta(meta);
                if ((nbImage == 1) && (this.player.getItemInHand().getType() == Material.FILLED_MAP)) {
                    this.player.setItemInHand(this.map);
                } else {
                    ImgUtility.addMap(this.map, this.inv, restant);
                }
                SavedMap svg = new SavedMap(this.plugin, this.player.getName(), map.getId(), this.imageRendering.getImg()[i],  map.getWorld().getName());
                svg.saveMap();
                this.player.sendMap(map);
            }
            if (!restant.isEmpty()) {
                this.player.sendMessage(restant.size() + " maps can't be place in your inventory. Please make free space in your inventory and run " + ChatColor.GOLD + "/maptool getrest");
            }
            this.plugin.setRemainingMaps(this.player.getName(), restant);
            this.player.sendMessage("Render finished");
        }
    }
}