package fr.moribus.ImageOnMap;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Poster {
    BufferedImage src;
    BufferedImage[] imgDecoupe;
    HashMap<Integer, String> numeroMap;
    int nbPartie;
    int nbColonne;

    Poster(BufferedImage img) {
        this.src = img;
        this.numeroMap = new HashMap();
        decoupeImg();
    }

    public BufferedImage[] getPoster() {
        return this.imgDecoupe;
    }

    public int getNbColonne() {
        return this.nbColonne;
    }

    private void decoupeImg() {
        int x = 0;
        int y = 0;
        int index = 0;
        int resteX = this.src.getWidth() % 128;
        int resteY = this.src.getHeight() % 128;
        int ligne = 1;
        if (this.src.getWidth() / 128 <= 0) {
            ligne = 1;
        } else {
            if (this.src.getWidth() % 128 != 0) {
                ligne = this.src.getWidth() / 128 + 1;
            } else {
                ligne = this.src.getWidth() / 128;
            }
        }
        int colonne = 1;
        if (this.src.getHeight() <= 0) {
            colonne = 1;
        } else {
            if (this.src.getHeight() % 128 != 0) {
                colonne = this.src.getHeight() / 128 + 1;
            } else {
                colonne = this.src.getHeight() / 128;
            }
        }
        this.nbColonne = colonne;
        this.nbPartie = (ligne * colonne);
        this.imgDecoupe = new BufferedImage[this.nbPartie];
        for (int lig = 0; lig < ligne; lig++) {
            y = 0;
            if ((lig == ligne - 1) && (resteX != 0)) {
                for (int col = 0; col < colonne; col++) {
                    if ((col == colonne - 1) && (resteY != 0)) {
                        this.imgDecoupe[index] = this.src.getSubimage(x, y, resteX, resteY);
                    } else {
                        this.imgDecoupe[index] = this.src.getSubimage(x, y, resteX, 128);
                        y += 128;
                    }
                    this.numeroMap.put(index, "column " + (lig + 1) + ", row " + (col + 1));
                    index++;
                }
            } else {
                for (int col = 0; col < colonne; col++) {
                    if ((col == colonne - 1) && (resteY != 0)) {
                        this.imgDecoupe[index] = this.src.getSubimage(x, y, 128, resteY);
                    } else {
                        this.imgDecoupe[index] = this.src.getSubimage(x, y, 128, 128);
                        y += 128;
                    }
                    this.numeroMap.put(index, "column " + (lig + 1) + ", row " + (col + 1));
                    index++;
                }
                x += 128;
            }
        }
    }
}