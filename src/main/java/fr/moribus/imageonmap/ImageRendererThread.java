package fr.moribus.imageonmap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import javax.imageio.ImageIO;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRendererThread extends Thread {
    boolean error = false;
    private String URL;
    private BufferedImage imgSrc;
    private BufferedImage[] img;
    private Poster poster;
    private boolean estPrete = false;
    private boolean resized;

    ImageRendererThread(String u, boolean r) {
        this.URL = u;
        this.resized = r;
    }

    static void emptyRenderers(MapView map) {
        for(MapRenderer mp : map.getRenderers())
            map.removeRenderer(mp);
    }

    public boolean isErroring() {
        return this.error;
    }

    public BufferedImage[] getImg() {
        if (this.estPrete) {
            return this.img;
        }
        return null;
    }

    public HashMap<Integer, String> getNumeroMap() {
        return this.poster.numeroMap;
    }

    public Boolean getStatus() {
        return Boolean.valueOf(this.estPrete);
    }

    public void run() {
        try {
            this.imgSrc = ImageIO.read(URI.create(this.URL).toURL().openStream());
            if (this.resized) {
                this.img = new BufferedImage[1];
                Image i = this.imgSrc.getScaledInstance(128, 128, 4);
                BufferedImage imgScaled = new BufferedImage(128, 128, 2);
                imgScaled.getGraphics().drawImage(i, 0, 0, null);
                this.img[0] = imgScaled;
            } else {
                int width = this.imgSrc.getWidth();
                int height = this.imgSrc.getHeight();

                int tmpW = 0;
                int tmpH = 0;
                int i = 1;
                while (tmpW < width) {
                    tmpW = i * 128;
                    i++;
                }
                i = 0;
                while (tmpH < height) {
                    tmpH = i * 128;
                    i++;
                }
                BufferedImage canvas = new BufferedImage(tmpW, tmpH, 2);

                Graphics2D graph = canvas.createGraphics();

                int centerX = 0;
                int centerY = 0;
                centerX = (tmpW - this.imgSrc.getWidth()) / 2;
                centerY = (tmpH - this.imgSrc.getHeight()) / 2;

                graph.translate(centerX, centerY);

                graph.drawImage(this.imgSrc, null, null);

                this.poster = new Poster(canvas);
                this.img = this.poster.getPoster();
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.error = true;
        }
        this.estPrete = true;
    }
}