/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE-ditherlib file in the
 * repository for more information.
 */
package fr.moribus.imageonmap.ditherlib;

import java.awt.image.BufferedImage;

/**
 * As there are many dithering algorithms, the Ditherer interface
 * provides a common way to speak to them.
 * @author Richard Todd
 */
public interface Ditherer {
      BufferedImage dither(BufferedImage input);
}
