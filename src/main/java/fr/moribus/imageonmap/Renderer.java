package fr.moribus.imageonmap;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import javax.imageio.ImageIO;

public class Renderer extends MapRenderer implements Runnable {
    private MapCanvas canvas;
    private boolean estRendu = false;
    private File file = null;
    private final int id;
    private Image touhou = null;

    public Renderer(int id, Image img) {
        this.id = id;
        this.touhou = img;
    }

    public Renderer(int id, File file) {
        this.id = id;
        this.file = file;
    }

    public void render(MapView v, MapCanvas mc, Player p) {
        this.canvas = mc;
        if (!this.estRendu) {
            new Thread(this).start();
            this.estRendu = true;
        }
    }

    public void run() {
        if (touhou == null && file != null && file.exists()) {
            try {
                touhou = ImageIO.read(file);
            } catch (IOException e) {
                System.out.println("Unable to read Image " + file.getName() + " for map " + id + ".");
                e.printStackTrace();
                file = null;
            }
        }
        if (touhou != null) {
            this.canvas.drawImage(0, 0, this.touhou);
        } else {
            this.canvas.drawText(0, 0, new MinecraftFont(), "Unable to load\nimage " + id);
        }
    }
}