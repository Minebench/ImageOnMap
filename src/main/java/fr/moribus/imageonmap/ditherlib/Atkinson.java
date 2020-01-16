/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import fr.moribus.imageonmap.ditherlib.colors.ColorSelector;

/**
 * The Atkinson Error-diffusion algorithm
 * @author Richard Todd
 */
public class Atkinson extends ErrDiffusionDither {
    
    public Atkinson(ColorSelector cs) {
        super(cs, 
              new double[][] { { 1, 1 }, { 1, 1, 1 }, { 0, 1 } }, 
              8, 
              1);
    }
    
    @Override public String toString() { return "Atkinson Dither"; }        
}
