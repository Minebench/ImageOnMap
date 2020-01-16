/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import fr.moribus.imageonmap.ditherlib.colors.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Performs no actual dithering... just replaces each 
 * pixel with the nearest match, ignoring any error 
 * accumulation that a "real" dither would do.
 * @author Richard Todd
 */
public final class NaiveDither implements Ditherer {

    private final ColorSelector selector;
    
    public NaiveDither(ColorSelector cs) {
        selector = cs;
    }
    
    @Override
    public BufferedImage dither(BufferedImage input) {
        BufferedImage output = new BufferedImage(input.getHeight(), input.getWidth(), input.getType());

        for(int y=0 ; y < input.getHeight(); y++ ) {
            for(int x = 0; x < input.getWidth(); x++ ) {
                 output.setRGB(x, y, selector.nearestTo(new Color(input.getRGB(x, y), true)).getRGB());
            }
        }
        
        return output;
    }

    @Override public String toString() { return "No Dither"; }    
}
