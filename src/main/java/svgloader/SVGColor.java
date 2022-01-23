package svgloader;

import javafx.scene.paint.Color;

public class SVGColor {
    /**
    * Constructor
    */
    public SVGColor( ) {
    	
    }
    /**
    setOpacity sets generally the converted color to this Opacity setting
    @param opacity double, the opacity grad between 0.0 ... 1.0 (None...Full Transparent)
    */
    public void setOpacity(double value) {
    	value = value > 1? 1:(value < 0? 0:value);
    	opacity = value;
    }
    /*
    getOpacity returns the Opacity setting
    @return double the setting opacity grad between 0.0 ... 1.0 (None...Full Transparent)
    */
    public double getOpacity( ) {
		return opacity;
    	
    }
    /**
    svgColor converts a string to JavaFX color object
    @param color   string, lower case. E.g. white or #ffffff (6x hex)
    @return Color or null if string is invalid...
    */
    public Color svgColor(String color) {
		return svgColor(color, opacity);
    }
    /**
    svgColor converts a string to JavaFX color object
    @param color   string, lower case. E.g. white or #ffffff (6x hex)
    @param opacity double, between 0.0 ... 1.0 (None...Full Transparent)
    @return Color or null if string is invalid...
    */
    public Color svgColor(String color, double value) {

    	
    	try {
                color = color.toLowerCase().trim();
                
                if(color.equals("none"))                  
                   return Color.TRANSPARENT;

                return Color.web(color, value);			
            }
            catch (Exception e){} 

        
    	return null;		
		   	
    }
    private double opacity = 1.0;
}