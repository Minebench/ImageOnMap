package fr.moribus.imageonmap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
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
                if (stack.getType() == Material.FILLED_MAP) {
                    MapView map = ((MapMeta) stack.getItemMeta()).getMapView();
                    for (Player p : this.plugin.getServer().getOnlinePlayers())
                        p.sendMap(map);
                }
            }
    }
}