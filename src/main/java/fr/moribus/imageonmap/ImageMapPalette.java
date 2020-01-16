package fr.moribus.imageonmap;

/*
 * ImageMapPalette
 * Copyright (c) 2020 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.Palette;
import org.bukkit.map.MapPalette;

import java.awt.Color;
import java.lang.reflect.Field;

public class ImageMapPalette implements Palette {

    private Color[] colors = new Color[0];

    public ImageMapPalette() {
        try {
            Field colorsField = MapPalette.class.getDeclaredField("colors");
            colorsField.setAccessible(true);
            colors = (Color[]) colorsField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPaletteIndex(int rgb) throws ImageWriteException {
        if (rgb == 0x00000000) {
            return 0;
        }
        byte index = MapPalette.matchColor(new Color(rgb));
        return index >= 0 ? index : index + 256;
    }

    @Override
    public int getEntry(int index) {
        if (index == 0) {
            return 0x00000000;
        }
        try {
            return colors[index].getRGB();
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public int length() {
        return colors.length;
    }
}
