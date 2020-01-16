/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib.colors;

import java.awt.Color;

/**
 * Applies the given metric to all the colors in the palette, every time.
 * @author Richard Todd
 */
public class BruteForceSelector implements ColorSelector {

    private final Color[] palette;
    private final ColorMetric metric;
    
    public BruteForceSelector(Color[] p, ColorMetric m) {
           assert(p.length > 0);
           palette = p;
           metric = m;
    }
    
    @Override
    public ColorMetric getMetric() { return metric; }
    
    @Override
    public Color nearestTo(final Color input) {
        Color winner = palette[0];
        double smallest = Double.MAX_VALUE;
        
        for(final Color c: palette) {
            final double dist = metric.distance(input, c);
            if(dist < smallest) {
                winner = c;
                smallest = dist;
            }
        }
        
        return winner;
    }
    
}
