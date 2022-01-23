package svgloader;

import javafx.scene.paint.Color;

public class SVGColor {
    /**
    * Constructor
    */
    private SVGColor( ) {
    	
    }

    /**
    svgColor converts a string to JavaFX color object
    @param color   string, lower case. E.g. white or #ffffff (6x hex)
    @return Color or null if string is invalid...
    */
    public static Color svgColor(String color) {
		return svgColor(color, opacity);
    }
    /**
    svgColor converts a string to JavaFX color object
    @param color   string, lower case. E.g. white or #ffffff (6x hex)
    @param opacity double, between 0.0 ... 1.0 (None...Full Transparent)
    @return Color or null if string is invalid...
    */
    public static Color svgColor(String color, double value) {

    	
    	try {
                color = color.toLowerCase().trim();
                
                if(color.equals("none"))                  
                   return Color.TRANSPARENT;

                return Color.web(color, value);			
            }
            catch (Exception e){} 

        
    	return null;		
		   	
    }
    private static double opacity = 1.0;
}