/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib.colors;

import java.awt.Color;

/**
 * There's more than one way to decide which colors
 * are closest to each other.  This interface represents
 * a metric from (Color,Color) to distance.  Ideally,
 * any implementing classes should make sure that the result
 * is an actual metric space in the mathematical sense 
 * (triangle inequality, etc).
 * @author Richard Todd
 */
@FunctionalInterface
public interface ColorMetric {
    /** 
     * Compute the distance between two Colors by some method.
     * @param a The first Color
     * @param b The second Color
     * @return The distance between a and b, as a double.
     */
     double distance(Color a, Color b);
}
