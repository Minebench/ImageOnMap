/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import fr.moribus.imageonmap.ditherlib.colors.ColorSelector;

/**
 * The Jarvice, Judice, and Ninke Dithering algorithm.
 *    
 * @author Richard Todd
 */
public class JarvisJudiceNinke extends ErrDiffusionDither {

    public JarvisJudiceNinke(ColorSelector cs) {
        super(cs, 
              new double[][] { { 7, 5 }, { 3, 5, 7, 5, 3 }, { 1, 3, 5, 3, 1 } }, 
              48, 
              2);
    }
    
    @Override public String toString() { return "Jarvis Judice Ninke"; }        
    
}
