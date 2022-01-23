package svgloader;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public class SVGLoader extends SVGParser {   
	/**    Constructor    
	 * @param svgName  String, svg document Name    
	 * */   

	public SVGLoader(String svgName) throws Exception {        
		super(svgName);
		              	
	}    
	
	/**    Constructor    
	 * @param svgName  String, svg document Name    
	 * @param scaled double, scaling graphics between min.0.05 .... max. 5.0 (5x larger)    
	 * */    
	public SVGLoader(String svgName, double scaled) throws Exception {        
	    super(svgName);	 
	}    
	
	/**    svgTitle    
	 * @return String the SVG title    
	 * */    
	public String svgTitle(){
            String[] S = {SVG, ""};
            svgObject(S, "title", 0);
            return S[1];
	}
	
	/**    loadSVG()    
	 * @return Pane JavaFX Pane with SVG image    
	 * */    
    public Pane loadSVG(){              
        pane.getChildren().addAll(createSVG(SVG, "")); 
        return pane;
    }



    @Override
    public List<Node> createSVG(String xml, String cas) { 
        
        String key = findKey(xml, 0, keys); 
        List<Node> nList = new ArrayList<Node>();
        char fc = key.charAt(0);
        if(fc == 's' && key.charAt(2)== 'g') { //svg
	    xml = removeWeirds(xml);
	    String cont = getContent(xml);
	    if(!cont.isEmpty()) {
	        String attr = getAttributeString(xml, "svg");

	        double x = getValue(attr, "x");
                double y = getValue(attr, "y");
                Group group = new Group();
                nList.add(group);
                group.setLayoutX(x);
	        group.setLayoutY(y);
                                group(group, xml, cas);
                List<String>  list = listObjects(cont, keys);

                attr = removeUncascadedAttributes(attr) + cas;

                group.getChildren().addAll(buildObjectList(list, attr));                                 
                                
                return nList;
            }			 
	}
	else if(fc == 'g') { //g
                         
            String attr = getAttributeString(xml, "g");
            String cont = getContent(xml);

            if(validateAttr(attr)){

                if(!cont.isEmpty()) {   
                                 
                    Group group = new Group(); 
                    nList.add(group);
                    group(group, xml, cas);

                    List<String>  list = listObjects(cont, keys);
                    attr = removeUncascadedAttributes(attr) + cas;
                                    
                    group.getChildren().addAll(buildObjectList(list, attr));                                  
                    return nList;
                }
                else
                    return nList;
                                
            }
            else {

                List<String> list = listObjects(cont, keys);                       
                return buildObjectList(list, cas);
            }
                         	
	}
        else if(fc == 'u'){
                       
            Group use = new Group(); 
            nList.add(use);
                        use(use, xml, cas);

            String attr = getAttributeString(xml, "use")+cas;
                     
            attr = removeUncascadedAttributes(attr);
            use.getChildren().addAll(getSymbol(attr));

            return nList;
                   
        }
        else if(fc == 'd'){ //defs
            return nList;
        }
        else if(fc == 't' && key.length() == 4){ //text
            if(xml.contains("<tspan")){
                String cont = getContent(xml);
                xml = xml.replace("<text", "<g");
                xml = xml.replace("/text>", "/g>");
                        
                Group group = new Group(); 
                nList.add(group);
                group(group, xml, cas);

                String attr = getAttributeString(xml, "g");

                attr = removeUncascadedAttributes(attr) + cas;
                List<String>  list = textSegregate(cont);                 
  
                group.getChildren().addAll(buildObjectList(list, attr)); 
                return nList;
                        
            }
            else {
                Text text = new Text();
                text(text, xml, cas);
                nList.add(text);
                return nList;    
            }
                     
        }
        else if(fc == 't' && key.length() == 5){
                      
            Text text = new Text(); 
            nList.add(text);
          text(text, xml, cas);

            return nList;   
        }
        else if(fc == 'i'){ // image/img
                    
            ImageView img = new ImageView();
            nList.add(img);   
          image(img, xml, cas);

            return nList;
        }              
	else if(!key.isEmpty()) {
                    
            SVGPath shape = new SVGPath();
            nList.add(shape);
            shape(shape, xml, cas);

            return nList;          
                     
	}
                
	return nList;
    }
        
    
        
    /**
    * Build JavaFx objects a string list of SVG elements
    * @param list String List of SVG elements	
    * @param cas String svg object attribute to cascade style to elements in the list
    * @return Javafx object list
    */
    @Override  
    public List<Node> buildObjectList(List<String> list, String cas){
       
        List<Node> oList = new ArrayList<>();
              
        int length = list.size();
	    for(int i = 0; i < length; i++) {
                  
            List<Node> nodes = createSVG(list.get(i), cas);
            oList.addAll(nodes); 
        }
        return oList;

    }

    private Pane pane = new Pane();

}




