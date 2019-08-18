package fr.moribus.imageonmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class SavedMap {
    private File file;
    private ImageOnMap plugin;
    private String nomImg;
    private String nomJoueur = "";
    private String nomMonde = "";
    private int idMap;
    private BufferedImage image = null;

    SavedMap(ImageOnMap plug, String nomJ, int id, BufferedImage img, String nomM) {
        this.plugin = plug;
        this.nomJoueur = nomJ;
        this.idMap = id;
        this.image = img;
        this.nomImg = "map" + id;
        this.nomMonde = nomM;
        this.file = new File(this.plugin.getDataFolder() + File.separator + "Image", this.nomImg + ".png");
    }

    SavedMap(ImageOnMap plug, int id) {
        this.idMap = id;
        this.plugin = plug;
        this.nomImg = "map" + id;
        List<String> mapdata = this.plugin.getCustomConfig().getStringList(this.nomImg);
        if(mapdata.size() >= 3 && Integer.valueOf(mapdata.get(0)) == id) {
            this.nomJoueur = mapdata.get(2);
            this.nomMonde = mapdata.get(3);
        }
        this.file = new File(this.plugin.getDataFolder() + File.separator + "Image", this.nomImg + ".png");
        if (!file.exists()) {
            System.out.println("Image " + this.nomImg + ".png doesn't exists in Image directory.");
        }
    }

    boolean saveMap() {
        this.plugin.getLogger().info("Saving map " + this.idMap);
        try {
            File outputfile = new File(this.plugin.getDataFolder() + File.separator + "Image", this.nomImg + ".png");
            if (image != null) {
                ImageIO.write(MapPalette.resizeImage(this.image), "png", outputfile);
            } else if (!outputfile.exists()) {
                System.out.println("Could not save image " + this.nomImg + ".png as we don't have any image information.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        ArrayList<String> liste = new ArrayList<String>();
        liste.add(String.valueOf(this.idMap));
        liste.add(this.nomImg);
        liste.add(this.nomJoueur);
        liste.add(this.nomMonde);
        this.plugin.getCustomConfig().set(this.nomImg, liste);
        this.plugin.saveCustomConfig();
        return true;
    }

    boolean loadMap() {
        MapView carte = Bukkit.getMap(this.idMap);
        if (carte != null) {
            ImageRendererThread.emptyRenderers(carte);
            carte.addRenderer(new Renderer(this.idMap, this.file));
            return true;
        }
        return false;
    }
}