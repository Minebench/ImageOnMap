/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.moribus.imageonmap.ditherlib.colors;

import java.awt.Color;

/**
 * This package has more than one ColorSelector, and for projects
 * that don't want to have to be savvy about which selector to use, this 
 * factory is provided.  You give it your palette, and it decides what
 * kind of matcher is warranted.
 * @author Richard Todd
 */
public class ColorSelectionFactory {
    /**
     * Generates an appropriate ColorSelector for the given palette and metric.
     * @param palette The colors to be considered for selection.
     * @param metric The metric to be used to determine the color differences.
     * @return 
     */
    public static ColorSelector getInstance(Color[] palette, ColorMetric metric) {
         return new BruteForceSelector(palette, metric);
    } 
}
