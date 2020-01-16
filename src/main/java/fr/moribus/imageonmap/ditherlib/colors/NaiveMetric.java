/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib.colors;

import java.awt.Color;

/**
 * This ColorMetric just treats the RGB values as
 * a Euclidean space.
 * @author RichardTodd
 */
public final class NaiveMetric implements ColorMetric {

    @Override
    public double distance(final Color a, final Color b) {
        final double rdiff = a.getRed()-b.getRed();
        final double gdiff = a.getGreen()-b.getGreen();
        final double bdiff = a.getBlue()-b.getBlue();
         
        // removed the sqrt, as it shouldn't matter for distance comparisons...
        return rdiff*rdiff + gdiff*gdiff + bdiff*bdiff;
    }
    
}
