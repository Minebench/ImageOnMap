/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import fr.moribus.imageonmap.ditherlib.colors.ColorSelector;

/**
 * The Stucki Dithering algorithm.
 *    
 * @author Richard Todd
 */
public class Stucki extends ErrDiffusionDither {

    public Stucki(ColorSelector cs) {
        super(cs, 
              new double[][] { { 8, 4 }, { 2, 4, 8, 4, 2 }, { 1, 2, 4, 2, 1 } }, 
              42, 
              2);
    }
    
    @Override public String toString() { return "Stucki"; }        
    
}
