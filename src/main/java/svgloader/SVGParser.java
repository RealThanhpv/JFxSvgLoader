package svgloader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;



public abstract class SVGParser {
	/**
	Constructor
	@param svgName String
	@exception Java generic Exception if suffix is neither .svg, nor .svgz or the content does
	not contain any block starting with <svg and ending with </svg>
	*/
     
	public SVGParser(String svgName) throws Exception {
	    

            byte[] buf = null;

             
            try {
                FileInputStream inFile = new FileInputStream(svgName);                
                buf = new byte[inFile.available()];
                inFile.read(buf);

                inFile.close();	
                dir = svgName.replace(svgName.substring(svgName.lastIndexOf('/')+1), "");
                
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
	
            byte[] first = Arrays.copyOfRange(buf, 0, 100);
            String encd = new String(first); 
            encd = getString(encd, "encoding");  
            if(encd.isEmpty())
                encd ="UTF-8";
          
            SVG = new String(buf, encd)         
                                .replaceAll("[\\n]+"," ")
//                                .replaceAll(" {2,}", " ")
                                .replaceAll("\\<\\?xml.+\\?\\>"," ")
                                .replaceAll("\\<\\?metadata.+\\?\\>"," ")
                                .replaceAll("<!--[\\s\\S]*?-->", "")
                                .replaceAll("<!DOCTYPE[^>]*>", "")
//                                .replaceAll("xmlns[^\\s]*\""," ")
                                ;
            
       

//            System.out.println(SVG);
			 			
			  
	}
	

        public abstract List<Node> createSVG(String xml, String cas);
        public abstract List<Node> buildObjectList(List<String> list, String cas);
        /**
	Shaping a geometrical form
	@param sh JFX-Shape (Rectangle, Ellipse, Circle, etc.)
	@param xml String, the parsing string (e.g. <circle .. style="...."/>)
        @param cas String, cascading attribute string applied to the shape
	*/
	public void shape(SVGPath sh, String xml, String cas) { 
		String attr; 
                char sc = xml.charAt(1); 
                if(sc == 'p'  && xml.charAt(2) == 'a') {                   
                    attr = getAttributeString(xml, "path")  + cas;//                  
                    setPath(sh, attr);    
                   
		}
                else if(sc == 'r') {
                  
                    attr = getAttributeString(xml, "rect") + cas;                    
                    setRect(sh, attr);
               
		}
                else if(sc == 'c') {
                    attr = getAttributeString(xml, "circle") + cas;
                    setCircle(sh, attr);
                                    
		}
                else if(sc == 'e') {
                    attr = getAttributeString(xml, "ellipse") + cas;
                    setEllipse(sh, attr);
                    
		}
                else if(sc == 'l' ) {
                    attr = getAttributeString(xml, "line") + cas;
                    setLine(sh, attr);
                    
		}
                else if(sc == 'p' && xml.charAt(5) == 'l') {
                    attr = getAttributeString(xml, "polyline") + cas;
                    setPolyline(sh, attr);
                               
		}
                else if(sc == 'p' && xml.charAt(5) == 'g') {
                    attr = getAttributeString(xml, "polygon") + cas;
                    setPolygon(sh, attr);                                     
		}
		
	}
         /**
	Setting text attributes including position, font, size, color, transformations, mask, clip path
	@param text JFX Text instance
	@param s String, the parsing string (e.g. <text .. style="...."/>)
        @param cas String, attribute string that the text will inherit from ancestor elements.
	*/
        public void text(Text text, String s, String cas) { 
            try {
                String attr = getAttributeString(s, "text") + cas;
                byte[] tByte = getContent(s).getBytes("UTF-32");
                text.setText(new String(tByte, "UTF-32"));     
                setText(text, attr);
            } catch (Exception ex) {
                
            }
                 
        }
         /**
	Like text element, setting text attributes including position,  font, size, color, transformations, mask, clip path
	@param text JFX Text instance
	@param s String, the parsing string (e.g. <text .. style="...."/>)
        @param cas String, attribute string that the text will inherit from ancestor elements.
	*/
        public void tspan(Text text, String s, String cas) { 
            try {
                String attr = getAttributeString(s, "tspan") + cas;
                text.setText(new String(getContent(s).getBytes(), "UTF-16"));                       
                setText(text, attr);
            } catch (UnsupportedEncodingException ex) {
               
            }
        }
        /**
         * Setting group attribute including position, transformation, clip path, mask
         * @param group a group instance
         * @param xml String, parsing string (e.g. <g .. style="...."/>)
         * @param cas String, attribute string that the group will inherit from ancestor elements.
         */      
        public void group(Group group, String xml, String cas){
            String attr = "";
            if(xml.charAt(1) == 'g')
                attr = getAttributeString(xml, "g") + cas;
            else 
                attr = getAttributeString(xml, "svg") + cas;                    
            setGroup(group, attr);
            
        }
        /**
         * Setting ImageView attributes including position, transformations, clip path, mask
         * @param img a ImageView instance
         * @param xml String, parsing string (e.g. <image .. style="...."/>)
         * @param cas String, attribute string that text will inherit from ancestor elements.
         */
        public void image(ImageView img, String xml, String cas) {
            String attr = getAttributeString(xml, "image");           
            if(xml.charAt(3) == 'g')
                attr = getAttributeString(xml, "img");                      
            
            int start = attr.indexOf(";base64,");
            if(start > -1){
                start += 8;
                String data = attr.substring(start, attr.indexOf('"', start));            
   
                data = data.replaceAll(" ", "");
                byte[] dc = Base64.getDecoder().decode(data);               
                img.setImage(new Image(new ByteArrayInputStream(dc)));   
            }
            else {
                String fname = getString(attr, "xlink:href");
                if(fname.isEmpty())
                    fname = getString(attr, "href");
              
                try {
                    img.setImage(new Image(new FileInputStream(dir+fname)));
                } catch (Exception ex) {
                   
                }
                
            }
                 
            attr += cas;
            setImage(img, attr);
            
        }
        /**
         * Setting group attribute including position, transformation, clip path, mask for use element
         * @param group a group instance
         * @param xml String, parsing string (e.g. <use .. style="...."/>)
         * @param cas String, attribute string that the group will inherit from ancestor elements.
         */      
        public void use(Group group, String xml, String cas){          
            String attr = getAttributeString(xml, "use")+cas;
            setGroup(group, attr);

        }
	/**
	search and parse double array for Polyline and Polygon, transformation matrix
	@param s String, the parsing string
	@return double array  (see JavaFX or SVG doc)
	*/
	public double[] doubleArray(String s) {
            if(s.indexOf("none") > -1 || s.isEmpty())
                return null;

            return Arrays.stream(s.split("[,\\s*]+"))
                             .mapToDouble(Tool::toDouble)
                             .toArray();

	}
        /**
	search and parse Double array for dash-array
	@param s String, the parsing string
	@return double array with 6 elements (see JavaFX or SVG doc)
	*/
        protected Double[] _doubleArray(String s){
            if(s.indexOf("none") > -1 || s.isEmpty())
                return null;
            
            return Arrays.stream(s.split("[,\\s*]+"))
				.map(Double::valueOf)
                .toArray(Double[]::new);
        }
	/**
	search and parse the shape of a viewBox
	@param s String, the parsing string (e.g. <svg .... viewBox="....." ...</svg>)
	@return double array with (x, y, width, height)
	*/
	public double[] viewBoxData(String s) {	
            return doubleArray(getString(s, "viewBox"));

	}
	/**
	search and parse a value of the given key
	@param s String, the parsing string
	@param key String the designated key (e.g. key width -> <svg .... width="..." ...</svg> or <svg .... style="...width:...;
	..." ...</svg>)
	@return double the value of the given key
	*/
	public double getValue(String s, String key) {
	
		String vs = getString(s, key); 

                if(vs.isEmpty())
                    return 0.0;  
               
                
                String ns = (vs.split("[pxcmm\\s]")[0]).trim();
                       
                double v = 0.0;
                if(!ns.isEmpty())
                    v = Tool.toDouble(ns);  
                else
                    return v;
              
                String u = vs.substring(ns.length());  
                if(u.isEmpty())
                    return v;
                else if(u.charAt(0) == 'p')
                    return v;
                else if(u.charAt(0) == 'm')
                    return v*3.779528;
                else if(u.charAt(0) == 'c')
                    return v*37.79528;
                
		return v;		
	}
	
	/**
	search and parse a string of the given key
	@param s String, the parsing string
	@param key String the designated key (e.g. key rect -> <rect .... .../>)
	@return String the content of the given key
	*/
	public String getString(String s, String key) {
                    
	    int length = key.length();
            int index = s.indexOf(" "+key+"=\"");
	    if( index > -1) {
                index = index + 3 + length;
                return s.substring(index, s.indexOf("\"", index)).trim();
              
	    }                             
	    else if( s.indexOf(key+":") > -1) { //CASE OF CSS FORMAT
               
		index = s.indexOf(key+":");		
                if(index > 0 && s.charAt(index-1) != ';' && s.charAt(index-1) != ' ' && s.charAt(index-1) != '"')
                    return "";

                index =index + 1 +length;
                int c1 = s.indexOf(';', index);
                int c2 = s.indexOf('"', index);
                        
                if((c1 > -1 && c1 < c2) || (c1 > -1 && c2 < 0))                            
                     return s.substring(index, c1).trim();
                else if(c2 > 0)
                    return s.substring(index, c2).trim();
			
                return "";

	    }
		
	    else if(key == "text") {			
		index = s.indexOf('>')+1;
		return s.substring(index, s.indexOf('<', index)).trim();
	    }
	    else if(s.indexOf("<"+key+" ") > -1) {
		index = s.indexOf("<"+key)+1 + length;
		return s.substring(index, s.indexOf('/', index)).trim();
	    }
		
	    return "";	
				
	}
        /**
         * Search class string (e.g. <path ... class="g1_4" />
         * @param cls String name of class
         * @return String class style (e.g. "fill: none; stroke: #E9C348; stroke-width: 1.5277778;stroke-linecap: butt;")

         */
        protected String getClass(String cls) {
          
            int start = SVG.indexOf("."+cls);
            if(start > -1) {
                start = SVG.indexOf("{", start);
                int end = SVG.indexOf("}", start);            
                String sub= SVG.substring(start+1, end-1);
                
                return sub;
            }
            return "";
                    
           
        }
	/**
	search and parse the color of the given key
	@param s String, the parsing string
	@param key String the designated key (e.g. key fill -> <rect...fill="blue".../>)
	@return Color JavaFX color of the given key
	*/
	protected Paint getColor(String s, String key) {
		
		String color = getString(s, key);

		if(color.isEmpty())
			return null;
		else {

			if(color.indexOf("url") > -1){
                            
				String fId = color.substring(color.indexOf('#')+1, color.indexOf(')'));
				String defs = chaseOut(SVG, fId, aKeys);                           
				if(defs.contains("<linearGradient"))
					return getLinearGradient(defs);
				else if(defs.contains("<radialGradient")){                             
					return getRadialGradient(defs);
				}                                
			}
			else {
				double op = opacityValue(s, key+"-opacity");
				return SVGColor.svgColor(color, op); // SVGColor API
			}
			return null;
		}
		
			
	}
        /**
	search and parse the color of the given key with given opacity
	@param s String, the parsing string
	@param key String the designated key (e.g. key fill -> <rect...fill="blue".../>)
        @param opacity opacity value
	@return Color JavaFX color of the given key
	*/
	protected Color getColor(String s, String key, String opacity) {
		
		String c = getString(s, key);		
		
		if (c != null) {
			double op = opacityValue(s, opacity);
			return SVGColor.svgColor(c, op); // SVGColor API
		}
		return null;
			
	}
	/**
	search and parse the Opacity Value of the given key
	@param s String, the parsing string
	@param key String the designated key (e.g. key fill-opacity -> <rect...fill-opacity="0.5".../>)
	@return double opacity value of JavaFX color of the given key*/
	public double opacityValue(String s, String key) {
		
		String valStr = getString(s, key).trim();
               
		if(valStr.isEmpty())
            return 1.0;
		else
            return  Tool.toDouble(valStr);
	
	}
	
	/**
	search and parse SVG object of the given key
	@param S String array of 2 elements, element 1: Parsing string, element 2: the parsed string from the element-1-string
	(as the 2nd returned string)
	@param key String the designated key (e.g. key rect -> <rect...fill-opacity="0.5".../>)
	@return int the index for next search
	*/
	public int svgObject(String[] S, String key, int index) {
		
		int start = S[0].indexOf("<"+key, index);
              
		int length = key.length();
		String rst = "";
		if(start > -1) {	
			int  close = isSelfClose(S[0], start);
			if( close < 0) { 	// Not self close tag														
				close = S[0].indexOf("/"+key+">", start)+ length +2; 	//First close		

				int oInd = start + length + 1; 
			
				while(oInd < close){
					int open = S[0].indexOf("<"+key, oInd); // next open
					if(open > -1 && open + length + 1 < close) {
					
						oInd = open+key.length()+1;
						if(isSelfClose(S[0], oInd) < 0 ) {
							int c = S[0].indexOf("/"+key+">", close); //next close
//                                                        
							if(c > -1) {
								close = c+ length + 2;	
							}
							else {
								System.out.println("SVG file contains errors");
								return close - start;								
							}								
						}						
					}
					else
						break;						
				}				
			}	
          
			rst = S[0].substring(start, close);
            S[1]= rst;
				
            return start + rst.length();
		}
		S[1]= rst;              
		return index;
	}

	/**
	search, parse and load the content of a path from the given string
	@param s String, the parsing string
	@return String the path content extracted from d="..........."
	*/
	public String svgPathContent(String s) {
		return getString(s, "d");			
	}	
	/*------------------Thanh ADDED METHODS--------------------*/
	/**
         * Check whether searching tag is self-close
         * @param s input string to search
         * @param index start search position
         * @return -1 if not self-close, index for next search if self-close
         */
	private int isSelfClose(String s, int index) {
		
	    int close = s.indexOf('>', index);		
		
	    if(close > 0) {
            if(s.charAt(close - 1) == '/')				
                return close + 1;
                  
	    }	
	    return -1;
	}
	/**
         * Search and get content of a balanced tag
         * @param s xml tag string
         * @return searched content string
         */
	protected String getContent(String s) { //Get content of a balanced tag

	    int start = s.indexOf('>');
	    int end = s.lastIndexOf('<');
	    if( end > start && start > -1) {                      
                return s.substring(start + 1, end);			 
	    }			 
	   return "";
	}	
	
    /**
    * Search and build valid xml tag list from string
    * @param s input string to search
    * @param keys array of valid xml tag keys that will help in case of loop search
    * @return List of valid xml tags
    */
    protected List<String> listObjects(String s, String[] keys) { // List with a list of keys
	      
        List<String> list = new ArrayList<String>();
	String[] S = {s, null};
	int index = 0,  length = S[0].length();	
	String key;
                
	while(index < length)
	{
			
            key = findKey(s, index, keys);
                                            
	    if(!key.isEmpty())			
	    {
				
                index = svgObject(S, key, index);
                list.add(S[1]);
	    }
	    else
		return list;
					
	}
     
	return list;
    }
    /**
    * Search and build xml tag list with one specific xml tag key
    * @param s input string to search
    * @param key tag key to build list
    * @return list of xml tags 
    */
    protected List<String> listObjects(String s, String key){ // List with a specific key
        List<String> list = new ArrayList<String>();
        String[] S = {s, null};
        int index = 0,  length = S[0].length();	
          
        while(index < length)
        {                
            index = svgObject(S, key, index);
            if(S[1].isEmpty())
                return list;
            list.add(S[1]);                               
        }

        return list;
    }
        /**
         * Search and build text tags
         * @param s input text string
         * @return list of xml text tags
         */ 
    protected List<String> textSegregate(String s){
			
            List<String> list = new ArrayList<String>();
            String[] S = {s, ""};
            int index = 0, last = 0, length = S[0].length();	
                   
            while(index < length)
            {                
                index = svgObject(S, "tspan", index);
              
                if(S[1].length() == 0){ // Not more tspan
                    String t = "<text>"+S[0].substring(last)+"</text>"; // text all last text as text
                    list.add(t);                   
                    return list;
                }
                else {
                      
                   String t = S[0].substring(last, index - S[1].length());
                   last = index;
                   
                   if(!t.trim().isEmpty()){
                        t = "<text>"+t+"\\s</text>";
                        list.add(t);
                   } 
                   list.add(S[1]);  
               
                } 
                                               
            }
           
            return list;
    }
        /**
         * Search for nearest xml tag key
         * @param s input string to search
         * @param index start position to search
         * @param keys array of valid keys that will help to search key in case of loop search.
         * @return 
         */
	protected String findKey(String s, int index, String[] keys) { // Find nearest tag key (combine methods for better speed)		
		
	    int start = s.indexOf('<', index);
             
            if(start > -1) {
                     
                if(s.indexOf('/', start) == start+1){
                    return loopFindKey(s, index, keys);
                }
                else {
                    int end1 = s.indexOf(' ', start+1);
                    int end2 = s.indexOf('>', start+1);
                    if(end1 > -1 && end1 < end2)
                        return s.substring(start+1, end1);
                    else
                        return s.substring(start+1, end2);
                }            
		    } 
            return "";		
		 
	}
        /**
         * Search for nearest xml tag key
         * @param s input string to search
         * @param index start position to search
         * @param keys array of key to loop
         * @return 
         */
    private String loopFindKey(String s, int index, String[] keys){
        int ind = s.length(),  curInd = -1, length = keys.length; String key = ""; 
                
	for(int i =0; i < length; i++) {
            curInd = s.indexOf("<"+keys[i],index);
            if(curInd > -1 && curInd < ind) {
                ind = curInd;
	        key = keys[i];
	    }
	}
        return key;
    }
    /**
    * Search attribute string for an valid xml tag
    * @param xml input xml tag string to search
    * @param key xml tag
    * @return attribute string 
    */	
    protected String getAttributeString(String xml, String key) { // Get all attribute string of a tag		
        String attr = xml.substring(xml.indexOf('<')+key.length()+1, xml.indexOf('>'));
        int cIndex = attr.indexOf("class");
        if(cIndex < 0)
            return attr;
        else {
             
            String rep = attr.substring(cIndex, attr.indexOf('"', cIndex+7)+1);                
            return  attr.replace(rep, getClass(rep.substring(7, rep.length()-1)));
        }
    }
    /**
    * Search and parse file rule from attribute string
    * @param s String attribute string
    * @return FillRule JavaFx instance
    */
    public FillRule getFillRule(String s) { // Get fill rule attribute
			
        String valStr = getString(s, "fill-rule");
        if(valStr.isEmpty())
            valStr = getString(s, "clip-rule");
        if(valStr.indexOf('e') == 0){                   
            return FillRule.EVEN_ODD;
        }	
        return FillRule.NON_ZERO;
   }
        
       
	/**
	* Search and parse stroke line cap
	* @param s String parsing string	
	* @return StrokeLineCap JavaFx Path stroke line cap
	*/
	public StrokeLineCap getStrokeLineCap(String s) { //
		
	    s = getString(s, "stroke-linecap");
	    if(s.indexOf('r') == 0)
		return StrokeLineCap.ROUND;
	    else if(s.indexOf('s') == 0)
		return StrokeLineCap.SQUARE;	
	    return StrokeLineCap.BUTT;
	}
	/**
	 * Search and parse stroke line join 
	 * @param s String parsing string
	 * @return StrokeLineJoin JavaFX Path stroke line join
	 */
	public StrokeLineJoin getStrokeLineJoin(String s) {
	
	    s = getString(s, "stroke-linejoin");
		
	    if(s.indexOf('m') == 0)
		return StrokeLineJoin.MITER;
            else if(s.indexOf('b') == 0)
		return StrokeLineJoin.BEVEL;
		
	    return StrokeLineJoin.ROUND;
	
	}
	/**
	 * Search and parse stroke miter limit
	 * @param s String parsing string
	 * @return double stroke miter limit value
	 */
	public double getStrokeMiterLimit(String s) {
	    return getValue(s, "stroke-miterlimit");
	}
	/**
	 * Search and parse transform
	 * @param s search string
	 * @return Javafx Transform object
	 */
	public Transform getTransform(String s) {

	    String trans = getString(s, "transform");
            
            if(trans.isEmpty())
                return null;
            else {
                      
	        String arrStr =  trans.substring(trans.indexOf('(')+1, trans.indexOf(')')).trim();
                
              
                if(arrStr.isEmpty())
                    return null;
                else 
                {
                                          
                    double [] arr = doubleArray(arrStr);                
                 
                    int len = arr.length;
                    char fc = trans.charAt(0);
                    if(fc == 'r') { //rotate
                        if(len == 1)
                            return new Rotate(arr[0]);
                        else if(len == 3)
                            return new Rotate(arr[0], arr[1], arr[2]);
                        else if(len == 4)
                            return new Rotate(arr[0], arr[1], arr[2], arr[3]);				
                    }
                    else if(fc == 'm') {                                         
                        if(len==6)
                            return Transform.affine(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
                    }
                    else if(fc == 't'){
                        if(len == 2) 
                            return Transform.translate(arr[0], arr[1]);

                    }
                    return null;
                }			
		}	
        }
        /**
         * Search and parse clipPath
         * @param s String in which has clip-path id 
         * @return Node JavaFx node 
         */
        protected Node getClip(String s ) {
         
            String clipId = getString(s, "clip-path");

            if(!clipId.isEmpty()) {
                clipId =  clipId.substring(clipId.indexOf('(')+2, clipId.indexOf(')'));
                
                String clipPath = chaseOut(SVG, clipId, keys);
                int index = s.indexOf("clip-path=\"");
                String attr = s.replace(s.substring(index, s.indexOf('"', index+11)), "");
                String key = findKey(clipPath, 0, keys);
                if(key.charAt(0) == 'c'){ //clipPath
                    clipPath = getContent(clipPath);  

                    List<String> strList = listObjects(clipPath, keys);
                    if(strList.size() == 1){
                        return createSVG(strList.get(0),attr).get(0);
                    }
                        
                    else {
                        Group g = new Group(buildObjectList(strList, attr));
                        return g;
                    }      
                                      
                }
                else
                    return createSVG(clipPath,"").get(0);
   
            }
            return null;
            
        }
        protected Node getMask(String s){
            String maskId = getString(s, "mask");

            if(!maskId.isEmpty()) {
                maskId =  maskId.substring(maskId.indexOf('(')+2, maskId.indexOf(')'));
               
                String mask = chaseOut(SVG, maskId, keys);
                
                String key = findKey(mask, 0, keys);
                if(key.charAt(0) == 'm'){
                    mask = getContent(mask);  

                    List<String> strList = listObjects(mask, keys);
                    if(strList.size() == 1){
                        return createSVG(strList.get(0),"").get(0);
                    }                        
                    else {
                        Group g = new Group(buildObjectList(strList, ""));
                        return g;
                    }      
                                      
                }
                else
                    return createSVG(mask,"").get(0);
   
            }
            return null;
        }
        
        public List<Node> getSymbol(String attr){
            String symId = getString(attr, "xlink:href");
            if(symId.isEmpty())
                symId = getString(attr, "href");
         
            if(!symId.isEmpty()) {                  
              symId =  symId.replace("#", "");
              int index = attr.indexOf("href");
              String cas = attr.replace(attr.substring(index, attr.indexOf('"', index+6)), " ");

              String symbol = chaseOut(SVG, symId, keys);
              if(isSelfClose(symbol, 0) > 0){
                  return createSVG(symbol,cas);
              }

              if(!symbol.isEmpty()){
                  symbol = getContent(symbol);  

                  List<String> strList = listObjects(symbol, keys);

                  if(strList.size() == 1){
                      return createSVG(strList.get(0),cas);
                  }                        
                  else  {
                      return buildObjectList(strList, "");
                  }      

              }
                return null;
   
            }
            return null;
        }
        protected String chaseOut(String s, String key, String[] keys){

            int pos = s.indexOf("id=\""+key);
            if(pos > 0){
                int begin = s.lastIndexOf('<', pos);
                String tag = findKey(s, begin, keys);
                String[] S = {s, tag};
                svgObject(S, tag, begin);
                return S[1];          
            }
            return "";
        }
        protected RadialGradient getRadialGradient(String s){
                  
            double cx = 0.5, cy = 0.5, r = 0.5, fa = 0.0, fd = 0.0, fx = 0.5, fy = 0.5;
             String cxS = getString(s,"cx"),
                    cyS = getString(s,"cy"),
                    rS = getString(s,"r"),
                    fxS = getString(s,"fx"),
                    fyS = getString(s,"fy");
             		
            if(!cxS.isEmpty())                
                cx = Tool.toDouble(cxS.replace("%", ""))/100;
            if(!cyS.isEmpty())
                cy = Tool.toDouble(cyS.replace("%", ""))/100;
            if(!rS.isEmpty())
                r = Tool.toDouble(rS.replace("%", ""))/100;            
                
            if(!fyS.isEmpty()){
                fy = Tool.toDouble(fyS.replace("%", ""))/100;                
            }
            if(!fxS.isEmpty()){            	
                fx = Tool.toDouble(fxS.replace("%", ""))/100; 
               
                if(fx != 0.5) { 
               	 
                    fa = Math.atan((fy-0.5)/(fx-0.5))*180/Math.PI;
                    if(fx < 0.5)
                       fa += 180;
                    fd = Math.sqrt((fx-0.5)*(fx-0.5)+(fy-0.5)*(fy-0.5));
                 
               }
               else if(fy < 0.5) {
            	   fa = 90;
               }               		
               else if(fy > 0.5) {
            	   fa = 270;
               }                       
            }          		
              
            List<Stop> sList = buildStopList(listObjects(getContent(s), "stop"));
            RadialGradient lg = new RadialGradient(fa, fd, cx, cy, r, true, CycleMethod.NO_CYCLE, sList);
    
            return lg;
        }
        protected LinearGradient getLinearGradient(String s){
            
            double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            String x1Str = getString(s,"x1").replace("%", "");
            String y1Str = getString(s,"y1").replace("%", "");
            String x2Str = getString(s,"x2").replace("%", "");
            String y2Str = getString(s,"y2").replace("%", "");
            if(!x1Str.isEmpty())
                x1 = Tool.toDouble(x1Str)/100;
            if(!y1Str.isEmpty())
                y1 = Tool.toDouble(getString(s,"y1").replace("%", ""))/100;
            if(!x2Str.isEmpty())
                x2 = Tool.toDouble(getString(s,"x2").replace("%", ""))/100;
            if(!y2Str.isEmpty())
                y2 = Tool.toDouble(getString(s,"y2").replace("%", ""))/100;
    
            List<Stop> sList = buildStopList(listObjects(getContent(s), "stop"));

            LinearGradient lg = new LinearGradient(x1, y1, x2, y2, true, CycleMethod.NO_CYCLE, sList);

            return lg;
        }
        
        protected List<Stop> buildStopList(List<String> list){
            List<Stop> sList = new ArrayList<Stop>();
            
            for(String s: list){            	
                sList.add(new Stop(Tool.toDouble(getString(s,"offset").replace("%", ""))/100, 
                                    getColor(s, "stop-color", "stop-opacity")));
               
            }  

            return sList;
        }
        
        private void setGroup(Group group, String attr){
            Transform trans = getTransform(attr); 
            
            if(trans != null)
                group.getTransforms().add(trans);
                
         
            Node clip = getClip(attr);           
            if(clip != null)
                group.setClip(clip); 
            Node mask = getMask(attr);
            if(mask != null)
                group.setClip(mask);
            group.setLayoutX(getValue(attr, "x"));
            group.setLayoutY(getValue(attr, "y"));
        }
        
        public void setPath(SVGPath sh, String attr){          
           
            sh.setContent(svgPathContent(attr));		
            sh.setFillRule(getFillRule(attr));
            sh.setStrokeLineCap(getStrokeLineCap(attr));
            sh.setStrokeLineJoin(getStrokeLineJoin(attr));
            sh.setStrokeMiterLimit(getStrokeMiterLimit(attr));             
            setStyle(sh, attr);
        }
        
        protected void setRect(SVGPath shape, String attr){
            double x = getValue(attr, "x"), 
                y = getValue(attr, "y"), 
                width = getValue(attr, "width"),
                height = getValue(attr, "height"),
                rx = getValue(attr, "rx"),
                ry = getValue(attr, "ry");
      
          
            String c1 = " A"+rx+" "+ry+" "+45+" "+0+" "+1+" "+(x+rx)+" "+(y);            
            String c2 = " A"+rx+" "+ry+" "+135+" "+0+" "+1+" "+(x+width)+" "+(y+ry);
            String c3 = " A"+rx+" "+ry+" "+90+" "+0+" "+1+" "+(x+width-rx)+" "+(y+height);
            String c4 = " A"+rx+" "+ry+" "+135+" "+0+" "+1+" "+(x)+" "+(y+height-ry); 
         
            shape.setContent("M"+x+","+(y+ry)+ c1+
                         " L"+(x+width-rx)+","+y+c2+
                         " L"+(x+width)+","+(y+height-ry)+c3+
                         " L"+(x+rx)+","+(y+height)+c4+
                         " z");
            setStyle(shape, attr);
      
        }
        
        private void setPolyline(SVGPath shape, String attr){
            double[] points = doubleArray(getString(attr, "points"));
            int length = points.length;
            String s = "M"+points[0]+","+points[1];
            for(int i = 2; i < length; i = i+2)
                s +=" L"+points[i]+","+points[i+1];
                     
            shape.setContent(s);
            setStyle(shape, attr);  
        }
        
        private void setPolygon(SVGPath shape, String attr){
          
            double[] points = doubleArray(getString(attr, "points"));		 
            int length = points.length;
            String s = "M"+points[0]+","+points[1];
            for(int i = 2; i < length; i = i+2)
                s +=" L"+points[i]+","+points[i+1];
                     
            shape.setContent(s+" z");
            setStyle(shape, attr);   
        }
        
        private void setLine(SVGPath shape, String attr){

            double x1 = getValue(attr, "x1"),
                   y1 = getValue(attr, "y1"),
                   x2 = getValue(attr, "x2"),
                   y2 = getValue(attr, "y2");
                  	
            shape.setContent("M"+x1+","+y1+" L"+x2+","+y2);
            setStyle(shape, attr);
           
        }
        private void setCircle(SVGPath shape, String attr){
     
            double cx = getValue(attr, "cx"),
                   cy = getValue(attr, "cy"),
                   r = getValue(attr, "r");
            String s = "M " + (cx-r) + ", "+cy+
                      " a"+r+","+r+ " 0 1,0 "+(2*r)+",0"+
                      " a "+r+","+r+ " 0 1,0 "+ "-"+(2*r)+",0"; 
            shape.setContent(s);
            setStyle(shape, attr);
          
        }
        private void setEllipse(SVGPath shape, String attr){
           
            double cx = getValue(attr, "cx"),
                   cy = getValue(attr, "cy"),
                   rx = getValue(attr, "rx"),
                   ry = getValue(attr, "ry");            
                   	
            String s = "M " + (cx-rx) + ", "+cy+
			" a"+rx+","+ry+ " 0 1,0 "+ (2*rx)+",0"+
			" a "+rx+","+ry+ " 0 1,0 "+(-2*rx)+",0";         
        
            shape.setContent(s);         
            setStyle(shape, attr);
             
        }
        
        private void setImage(ImageView img, String s){
            double x = getValue(s, "x");
            double y = getValue(s, "y");
            double height = getValue(s, "height");
            double width = getValue(s, "width");
            
            img.setX(x);            
            img.setY(y);
            img.setFitHeight(height);
            img.setFitWidth(width);
            img.setSmooth(true);
            
            Transform trans = getTransform(s);
		if(trans != null)
			img.getTransforms().add(trans);	
            Node clip = getClip(s);
            if(clip != null)
                 img.setClip(clip);

            Node mask = getMask(s);
            if(mask != null)
                img.setClip(mask);

        }
        private void setText(Text text, String attr){
                    
            double x = getValue(attr, "x");
            double y = getValue(attr, "y");
            
            text.setX(x);
            text.setY(y);
                               
            double fs = getValue(attr, "font-size");
         
            String fwStr = getString(attr,"font-weight");
            FontWeight  fw = FontWeight.NORMAL;
            int len = fwStr.length();
            if(!fwStr.isEmpty()){
                char f = fwStr.charAt(0);
                
                switch(f){
                    case 'b':
                        if(len == 4)
                            fw = FontWeight.BOLD;
                        else
                            fw = FontWeight.BLACK;
                        break;
                    case 'e':
                        if(fwStr.length() == 10)
                            fw = FontWeight.EXTRA_BOLD;
                        else
                            fw = FontWeight.EXTRA_LIGHT;
                        break;
                    case 'm':
                        fw = FontWeight.MEDIUM;
                        break;
                    case 'l':
                        fw = FontWeight.LIGHT;
                        break;
                    case 't':
                        fw = FontWeight.THIN;
                        break;
                    case 's':
                        fw = FontWeight.SEMI_BOLD;
                        break;
                    default:
                        break;
                        
                }
              
            }
            Font font = Font.font(getString(attr, "font-family").replace("'", ""), fw, fs);
            text.setFont(font); //             
            setStyle(text, attr);
        }
        
     
        
    private void setStyle(Shape sh, String s){
               
            Paint strk = getColor(s, "stroke");
            sh.setStroke(strk);
                 
            Paint fill = getColor(s, "fill");

            if(fill != null)                        
                sh.setFill(fill);
            
            double sw = getValue(s, "stroke-width");
        
	    if(!(sw > 0.0000001) )
	        sw = 1;
            
	    sh.setStrokeWidth(sw);
	    sh.setOpacity(opacityValue(s, "opacity"));
		
	    String arr = getString(s, "stroke-dasharray");
            Double[] dArr = _doubleArray(arr);
	    if(dArr != null)
	        sh.getStrokeDashArray().addAll(dArr);		
                
	    Transform trans = getTransform(s);
	    if(trans != null)
	         sh.getTransforms().add(trans);
       
            
            Node clip = getClip(s);
            if(clip != null)
                sh.setClip(clip);  
                    
            Node mask = getMask(s);
            if(mask != null)
                sh.setClip(mask);
               
        }
          
      
        protected boolean validateAttr(String attr){
            if(attr.isEmpty())
                return false;
            else if(attr.indexOf(" x=")> -1 )
                return true;
            else if(attr.indexOf(" y=")>-1)
                return true;
            else if(attr.indexOf("transform") > -1)
                return true;
            else if(attr.indexOf("rotate") > -1)
                return true;
            else if(attr.indexOf("fill") > -1)
                return true;
            else if(attr.indexOf("font-family")>-1)
                return true;
            else if(attr.indexOf("font-size")>-1)
                return true;
            else if(attr.indexOf("clip-path")>-1)
                return true;
            else
                return false;
            
        }
         /**
         * Remove un-cascaded attributes from attribute string
         * @param s attribute String
         * @return String with cascading attributes
         */
    protected String removeUncascadedAttributes(String s){
             
            int index = s.indexOf("transform");                                    
            if(index > -1)
                 s = s.replace(s.substring(index, s.indexOf(')', index+12)+1), "");
            
            index = s.indexOf("clip-path");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf(')', index+12)+1), "");

            index = s.indexOf("mask");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf(')', index+12)+1), "");
            
            index  = s.indexOf("x=");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf('"', index+4)+1), "");
           
            index  = s.indexOf("y=");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf('"', index+4)+1), "");
            
            index  = s.indexOf("width=");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf('"', index+8)+1), "");
            
            index  = s.indexOf("height=");
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf('"', index+9)+1), "");
            
            
            return s;
                
    }
    protected String removeWeirds(String s){
            
            int index = s.indexOf("<?xml");
            
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf("?>", index)+2), "");
                
            index = s.indexOf("<!DOCTYPE");            
            if(index > -1)
                s = s.replace(s.substring(index, s.indexOf('>', index)+1), "");
            
            index = s.indexOf("xmlns:dc=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+11)+1), "");
            
            index = s.indexOf("xmlns=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+8)+1), "");
            
            index = s.indexOf("xmlns:dc=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+11)+1), "");
            
            index = s.indexOf("xmlns:rdf=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+13)+1), "");
            
            index = s.indexOf("xmlns:svg=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+13)+1), "");
            
            index = s.indexOf("xmlns:xlink=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+16)+1), "");
            
            index = s.indexOf("xml:space=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+11)+1), "");
            
            index = s.indexOf("version=\"");
            if(index > -1)
                s= s.replace(s.substring(index, s.indexOf('"', index+9)+1), "");
            
           return s;
                    
    }

    protected String dir;   	
    protected String SVG;
    protected String[] keys = {"path",  "g", "svg",  "text", "tspan",  "image", "img", "use", "polygon", "polyline", "rect", "line", "ellipse", "circle" }; //
    private String[] aKeys = {"defs", "stop", "linearGradient", "radialGradient", "clipPath", "symbol"};
       
}
