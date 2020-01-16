/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import fr.moribus.imageonmap.ditherlib.colors.ColorSelector;
import java.awt.Color;

/**
 * A class to do the work of a typical error diffusion ditherer...
 * @author richa
 */
public class ErrDiffusionDither implements Ditherer {
    private final ColorSelector selector;
    private final  double[][] matrix;
    private final  double denominator;
    private final  int xoffs;  // how many pixels does the matrix reach back?

    public ErrDiffusionDither(final ColorSelector cs, final double[][] m, final double den, final int xo) {
        selector = cs;
        matrix = m;
        denominator = den;
        xoffs = xo;
    }
    
    /**
     * clip a value so it stays in the 0 to 255 range
     * @param in input value to be clipped
     * @return the clipped value
     */
    private int clip(double in) {
        int out = (int) Math.round(in);
        if(out < 0.0) out = 0;
        else if (out > 255) out = 255;
        return out;
    }


    // rotate the error matrices to keep from
    // allocating new ones.
    private void rotateErrors(double[][] m) {
        // store off old current row and zero it out
        double[] temp = m[0];
        Arrays.fill(temp, 0.0);
        
        // rotate the other rows up
        for (int i = 0; i < m.length-1; i++) {
            m[i] = m[i+1];
        }
        
        // save the newly-zeroed row to the end to start filling
        m[m.length-1] = temp;
    }    
    
    @Override
    public BufferedImage dither(BufferedImage input) {
        double[][] error = new double[matrix.length][input.getWidth()*3];

        final BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        for(int y=0 ; y < input.getHeight(); y++ ) {
            for(int x = 0; x < input.getWidth(); x++ ) {
                final int x3 = x*3;
                final Color orig = new Color(input.getRGB(x, y), true);
                final Color adapted = new Color(clip(orig.getRed()+error[0][x3]),
                                                clip(orig.getGreen()+error[0][x3+1]),
                                                clip(orig.getBlue()+error[0][x3+2]),
                                                orig.getAlpha());
                final Color nearest = selector.nearestTo(adapted);
                output.setRGB(x, y, nearest.getRGB());
                
                // calculate the error
                double rdiff = adapted.getRed() - nearest.getRed();
                double gdiff = adapted.getGreen() - nearest.getGreen();
                double bdiff = adapted.getBlue() - nearest.getBlue();
                
                // propagate the error
                
                // First row...
                for (int ex = x3+3, mIdx = 0; 
                    (mIdx < matrix[0].length) && (ex < error[0].length); 
                    ex+=3, mIdx++) {
                    error[0][ex]   += rdiff * matrix[0][mIdx] / denominator;
                    error[0][ex+1] += gdiff * matrix[0][mIdx] / denominator;
                    error[0][ex+2] += bdiff * matrix[0][mIdx] / denominator;
                }
                
                // Remaining rows ... calculate initial mIdx...
                // x = 0    xoffs = 2   so  initm = 2, initx = 0
                // x = 1    xoffs = 2   so  initm = 1, initx = 0
                // x = 2    xoffs = 2   so  initm = 0  initx = 0
                // x = 3    xoffs = 2   so  initm = 0  initx3 = 3
                final int initMatrixIndex = Math.max(0, xoffs - x);
                final int initX3 = Math.max(0, x - xoffs) * 3;
                for (int row = 1; row < matrix.length; row++) {
                    for (int ex = initX3, mIdx = initMatrixIndex; 
                          (mIdx < matrix[row].length) && (ex < error[row].length) ; 
                            ex+=3, mIdx++) {
                        error[row][ex]   += rdiff * matrix[row][mIdx] / denominator;
                        error[row][ex+1] += gdiff * matrix[row][mIdx] / denominator;
                        error[row][ex+2] += bdiff * matrix[row][mIdx] / denominator;                        
                    }
                }

            }
            
            rotateErrors(error);
        }
        
        return output;
    }
    
    @Override public String toString() { return "Error Diffusion Dither"; }
}
