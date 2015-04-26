package fr.moribus.ImageOnMap;

import java.awt.Image;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer implements Runnable {
    public MapCanvas canvas;
    boolean estRendu;
    Image touhou;
    private Thread TRendu;

    public Renderer(Image img) {
        this.estRendu = false;
        this.touhou = img;
    }

    public void render(MapView v, MapCanvas mc, Player p) {
        this.canvas = mc;
        if (!this.estRendu) {
            this.TRendu = new Thread(this);
            this.TRendu.start();
            this.estRendu = true;
        }
    }

    public void run() {
        this.canvas.drawImage(0, 0, this.touhou);
    }
}