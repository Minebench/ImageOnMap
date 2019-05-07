package fr.moribus.imageonmap;

import java.awt.Image;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer implements Runnable {
    private MapCanvas canvas;
    private boolean estRendu;
    private Image touhou;

    public Renderer(Image img) {
        this.estRendu = false;
        this.touhou = img;
    }

    public void render(MapView v, MapCanvas mc, Player p) {
        this.canvas = mc;
        if (!this.estRendu) {
            new Thread(this).start();
            this.estRendu = true;
        }
    }

    public void run() {
        this.canvas.drawImage(0, 0, this.touhou);
    }
}