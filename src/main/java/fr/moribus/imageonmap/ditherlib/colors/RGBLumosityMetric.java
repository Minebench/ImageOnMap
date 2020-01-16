/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib.colors;

import java.awt.Color;

/**
 * An RGB metric that uses lumosity, as found on http://bisqwit.iki.fi/story/howto/dither/jy/.
 * @author Richard Todd
 */
public class RGBLumosityMetric implements ColorMetric {

    @Override
    public double distance(final Color a, final Color b) {
        // Compare the difference of two RGB values, weigh by CCIR 601 luminosity:
        final double luma1 = (a.getRed() * 299 + a.getGreen() * 587 + a.getBlue() * 114) / 1000.0;
        final double luma2 = (b.getRed() * 299 + b.getGreen() * 587 + b.getBlue() * 114) / 1000.0;
        final double lumadiff = (luma1 - luma2) / 256.0 ;
        final double diffR = (a.getRed() - b.getRed()) / 256.0 ;
        final double diffG = (a.getGreen() - b.getGreen()) / 256.0 ;
        final double diffB = (a.getBlue() - b.getBlue()) / 256.0 ;
        return (diffR * diffR * 0.299 + diffG * diffG * 0.587 + diffB * diffB * 0.114) * 0.75
                + lumadiff * lumadiff;
    }
    
}
