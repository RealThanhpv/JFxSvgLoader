package tpv.jfxsvg;

import javafx.scene.paint.Color;

public class SVGColor {

    /**
     * Constructor
     */
    private SVGColor() {

    }



    /**
     * svgColor converts a string to JavaFX color object
     *
     * @param color   string, lower case. E.g. white or #ffffff (6x hex)
     * @param opacity double, between 0.0 ... 1.0 (None...Full Transparent)
     * @return Color or null if string is invalid...
     */
    public static Color svgColor(String color, Double opacity) {

        color = color.toLowerCase().trim();

        if (color.equals("none")) {
            return Color.TRANSPARENT;
        }

        if (opacity == null) {
            return Color.web(color);
        }

        return Color.web(color, opacity);


    }
}