package fr.moribus.imageonmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class SavedMap {
    ImageOnMap plugin;
    String nomImg;
    String nomJoueur = "";
    String nomMonde = "";
    int idMap;
    BufferedImage image;

    SavedMap(ImageOnMap plug, String nomJ, int id, BufferedImage img, String nomM) {
        this.plugin = plug;
        this.nomJoueur = nomJ;
        this.idMap = id;
        this.image = img;
        this.nomImg = "map" + id;
        this.nomMonde = nomM;
    }

    SavedMap(ImageOnMap plug, int id) {
        this.idMap = id;
        this.plugin = plug;
        boolean found = false;
        this.nomImg = "map" + id;
        List<String> mapdata = this.plugin.getCustomConfig().getStringList(this.nomImg);
        if(mapdata.size() >= 3 && Integer.valueOf(mapdata.get(0)) == id) {
            this.nomJoueur = mapdata.get(2);
            this.nomMonde = mapdata.get(3);
        }
        try {
            this.image = ImageIO.read(new File(this.plugin.getDataFolder() + File.separator + "Image", this.nomImg + ".png"));
            found = true;
        } catch (IOException e) {
            System.out.println("Image " + this.nomImg + ".png doesn't exists in Image directory.");
        }
        if (!found) {
            System.out.println("No map with the id " + id + " could be loaded");
        }
    }

    Boolean saveMap() {
        this.plugin.getLogger().info("Saving map " + this.idMap);
        try {
            File outputfile = new File(this.plugin.getDataFolder() + File.separator + "Image", this.nomImg + ".png");
            ImageIO.write(MapPalette.resizeImage(this.image), "png", outputfile);
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

    Boolean loadMap() {
        MapView carte = Bukkit.getMap(this.idMap);
        if (carte != null) {
            ImageRendererThread.emptyRenderers(carte);
            carte.addRenderer(new Renderer(this.image));
            return true;
        }
        return false;
    }
}