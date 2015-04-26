package fr.moribus.ImageOnMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class SendMapOnFrameEvent implements Listener {
    ImageOnMap plugin;

    SendMapOnFrameEvent(ImageOnMap plug) {
        this.plugin = plug;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity e : event.getChunk().getEntities())
            if (e instanceof ItemFrame) {
                ItemStack stack = ((ItemFrame) e).getItem();
                if (stack.getType() == Material.MAP) {
                    MapView map = Bukkit.getMap(stack.getDurability());
                    for (Player p : this.plugin.getServer().getOnlinePlayers())
                        p.sendMap(map);
                }
            }
    }
}