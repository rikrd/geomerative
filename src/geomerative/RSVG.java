/**
    Copyright 2004-2008 Ricard Marxer  <email@ricardmarxer.com>

    This file is part of Geomerative.

    Geomerative is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Geomerative is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Geomerative.  If not, see <http://www.gnu.org/licenses/>.
*/

package geomerative;

import processing.core.*;
import processing.xml.*;
import java.lang.*;
import java.io.*;

public class RSVG
{    
  public void draw(String filename, PGraphics g)
  {
    this.toGroup(filename).draw(g);  
  }
  
  public void draw(String filename, PApplet p)
  {
    this.toGroup(filename).draw(p);
  }
  
  public void draw(String filename)
  {
    this.toGroup(filename).draw();
  }
  
  
  public RGroup toGroup(String filename)
  {
    XMLElement svg = new XMLElement(RGeomerative.parent, filename);
    if (!svg.getName().equals("svg")) {
      throw new RuntimeException("root is not <svg>, it's <" + svg.getName() + ">");
    }
    
    return elemToGroup(svg);
  }
  
  public RShape toShape(String filename)
  {
    return toGroup(filename).toShape();
  }
  
  public RPolygon toPolygon(String filename)
  {
    return toGroup(filename).toPolygon();
  }
  
  public RMesh toMesh(String filename)
  {
    return toGroup(filename).toMesh();
  }
  
  /**
   * @invisible
   */
  public RGroup elemToGroup(XMLElement elem)
  {
    RGroup grp = new RGroup();

    // Set the defaults SVG styles for the root
    if(elem.getName().toLowerCase().equals("svg")){
      grp.setFill(0);  // By default in SVG it's black
      grp.setFillAlpha(255);  // By default in SVG it's 1
      grp.setStroke(false);  // By default in SVG it's none
      grp.setStrokeWeight(1F);  // By default in SVG it's none
      grp.setStrokeCap("butt");  // By default in SVG it's 'butt'
      grp.setStrokeJoin("miter");  // By default in SVG it's 'miter'
      grp.setStrokeAlpha(255);  // By default in SVG it's 1
      grp.setAlpha(255);  // By default in SVG it's 1F
    }
    
    XMLElement elems[] = elem.getChildren();
    for (int i = 0; i < elems.length; i++) {
      String name = elems[i].getName().toLowerCase();
      XMLElement element = elems[i];
      
      // Parse and create the geometrical element
      RGeomElem geomElem = null;
      if(name.equals("g")){
        geomElem = elemToGroup(element);
        
      }else if (name.equals("path")) {
        geomElem = elemToShape(element);
        
      }else if(name.equals("polygon")){
        geomElem = elemToPolygon(element);
        
      }else if(name.equals("polyline")){
        geomElem = elemToPolyline(element);
        
      }else if(name.equals("circle")){
        geomElem = elemToCircle(element);
        
      }else if(name.equals("ellipse")){
        geomElem = elemToEllipse(element);
        
      }else if(name.equals("rect")){
        geomElem = elemToRect(element);
        
      }else if(name.equals("line")){
        geomElem = elemToLine(element);
        
      }else if(name.equals("defs")){
        // Do nothing normally we should make a hashmap 
        // to apply everytime they are called in the actual objects
      }else{
        RGeomerative.parent.println("Element '" + name + "' not know. Ignoring it.");
      }
      
      // If the geometrical element has been correctly created
      if((geomElem != null)){
        // Transform geometrical element
        if(element.hasAttribute("transform")){
          String transformString = element.getStringAttribute("transform");
          RMatrix transf = new RMatrix(transformString);
          geomElem.transform(transf);
        }
        
        // Get the id for the geometrical element
        if(element.hasAttribute("id")){
          geomElem.id = element.getStringAttribute("id");
        }
                
        // Get the style for the geometrical element
        if(element.hasAttribute("style")){
          geomElem.setStyle(element.getStringAttribute("style"));
        }

        // Get the fill for the geometrical element
        if(element.hasAttribute("fill")){
          geomElem.setFill(element.getStringAttribute("fill"));
        }

        // Get the fill-linejoin for the geometrical element
        if(element.hasAttribute("fill-opacity")){
          geomElem.setFillAlpha(element.getStringAttribute("fill-opacity"));
        }

        // Get the stroke for the geometrical element
        if(element.hasAttribute("stroke")){
          geomElem.setStroke(element.getStringAttribute("stroke"));
        }

        // Get the stroke-width for the geometrical element
        if(element.hasAttribute("stroke-width")){
          geomElem.setStrokeWeight(element.getStringAttribute("stroke-width"));
        }

        // Get the stroke-linecap for the geometrical element
        if(element.hasAttribute("stroke-linecap")){
          geomElem.setStrokeCap(element.getStringAttribute("stroke-linecap"));
        }

        // Get the stroke-linejoin for the geometrical element
        if(element.hasAttribute("stroke-linejoin")){
          geomElem.setStrokeJoin(element.getStringAttribute("stroke-linejoin"));
        }

        // Get the stroke-linejoin for the geometrical element
        if(element.hasAttribute("stroke-opacity")){
          geomElem.setStrokeAlpha(element.getStringAttribute("stroke-opacity"));
        }

        // Get the opacity for the geometrical element
        if(element.hasAttribute("opacity")){
          geomElem.setAlpha(element.getStringAttribute("opacity"));
        }
        
        // Get the style for the geometrical element
        grp.addElement(geomElem);      
      }
    }

    return grp;
  }
  
  /**
   * @invisible
   */
  public RShape elemToPolyline(XMLElement elem)
  {
    return getPolyline(elem.getStringAttribute("points").trim());
  }
  
  /**
   * @invisible
   */
  public RShape elemToPolygon(XMLElement elem)
  {
    RShape poly = elemToPolyline(elem);
    
    poly.addClose();
    
    return poly;    
  }
  
  /**
   * @invisible
   */
  public RShape elemToRect(XMLElement elem)
  {
    return getRect(elem.getFloatAttribute("x"), elem.getFloatAttribute("y"), elem.getFloatAttribute("width"), elem.getFloatAttribute("height"));
  }
  
  /**
   * @invisible
   */
  public RShape elemToLine(XMLElement elem)
  {
    return getLine(elem.getFloatAttribute("x1"), elem.getFloatAttribute("y1"), elem.getFloatAttribute("x2"), elem.getFloatAttribute("y2"));
  }
  
  
  /**
   * @invisible
   */
  public RShape elemToEllipse(XMLElement elem)
  {
    return getEllipse(elem.getFloatAttribute("cx"), elem.getFloatAttribute("cy"), elem.getFloatAttribute("rx"), elem.getFloatAttribute("ry"));
  }
  
  
  /**
   * @invisible
   */
  public RShape elemToCircle(XMLElement elem)
  {
    float r = elem.getFloatAttribute("r");
    return getEllipse(elem.getFloatAttribute("cx"), elem.getFloatAttribute("cy"), r, r);
  }
  
  /**
   * @invisible
   */
  public RShape elemToShape(XMLElement elem)
  {
    return getShape(elem.getStringAttribute("d"));
  }
  
  /**
   * @invisible
   */
  private RShape getRect(float x, float y, float w, float h)
  {
    return RShape.createRect(x, y, w, h);
  }
  
  /**
   * @invisible
   */
  private RShape getLine(float x1, float y1, float x2, float y2)
  {
    RShape shp = new RShape();
    
    shp.addMoveTo(x1, y1);
    shp.addLineTo(x2, y2);

    return shp;
  }
  
  
  /**
   * @invisible
   */
  private RShape getEllipse(float cx, float cy, float rx, float ry)
  {
    return RShape.createEllipse(cx, cy, rx, ry);
  }
  
  /**
   * @invisible
   */
  private RShape getPolyline(String s)
  {
    RShape poly = new RShape();
    boolean first = true;

    //format string to usable format
    char charline[]=s.toCharArray();
    for(int i=0;i<charline.length;i++)
      {
        switch(charline[i])
          {
          case '-':
            charline=RGeomerative.parent.splice(charline,' ',i);
            i++;
            break;   
          case ',':
          case '\n':
          case '\r':
          case '\t':
            charline[i]=' ';
            break;
          }
      }
    String formatted=new String(charline);
    String tags[]=RGeomerative.parent.splitTokens(formatted,", ");
    for(int i=0;i<tags.length;i++){
      float x = RGeomerative.parent.parseFloat(tags[i]);
      float y = RGeomerative.parent.parseFloat(tags[i+1]);
      i++;
      if(first){
        poly.addMoveTo(x,y);
        first = false;
      }else{
        poly.addLineTo(x,y);
      }
    }
    return poly;
  }
  
  /**
   * @invisible
   */
  private RShape getShape(String s)
  {
    RShape shp = new RShape();
    
    if(s == null){
      return shp;
    }

    //format string to usable format
    char charline[] = s.toCharArray();
    for( int i = 0 ; i < charline.length ; i++)
      {
        switch(charline[i])
          {
          case 'M':
          case 'm':
          case 'Z':
          case 'z':
          case 'C':
          case 'c':
          case 'S':
          case 's':
          case 'L':
          case 'l':
          case 'H':
          case 'h':
          case 'V':
          case 'v':
            charline = RGeomerative.parent.splice(charline,' ',i);
            i ++;
            charline = RGeomerative.parent.splice(charline,' ',i+1);
            i ++;      
            break;
            
          case '-':
            charline = RGeomerative.parent.splice(charline,' ',i);
            i ++;
            break;
            
          case ',':
          case '\n':
          case '\r':
          case '\t':
            charline[i] = ' ';
            break;
            
          }
      }
    String formatted = new String(charline);
    String[] tags = RGeomerative.parent.splitTokens(formatted);

    //RGeomerative.parent.println("formatted: " + formatted);
    //RGeomerative.parent.println("tags: ");
    //RGeomerative.parent.println(tags);

    //build points
    RPoint curp = new RPoint();
    RPoint relp = new RPoint();
    RPoint refp = new RPoint();
    RPoint strp = new RPoint();
    
    char command = 'a';

    for(int i=0;i<tags.length;i++)
      {
        
        char nextChar = tags[i].charAt(0);
        switch(nextChar)
          {
          case 'm':
          case 'M':
          case 'c':
          case 'C':
          case 's':
          case 'S':
          case 'l':
          case 'L':
          case 'h':
          case 'H':
          case 'v':
          case 'V':
            i += 1;
          case 'z':
          case 'Z':
            command = nextChar;
            break;
        }
        
        relp.setLocation(0F, 0F);

        switch(command)
          {
          case 'm':
            relp.setLocation(curp.x, curp.y);
          case 'M':            
            i = move(shp, curp, relp, refp, strp, tags, i);
            break;
            
          case 'z':
            relp.setLocation(curp.x, curp.y);
          case 'Z':
            shp.addClose();
            break;
            
          case 'c':
            relp.setLocation(curp.x, curp.y);
          case 'C':
            i = curve(shp, curp, relp, refp, strp, tags, i);
            break;
            
          case 's':
            relp.setLocation(curp.x, curp.y);
          case 'S':
            i = smooth(shp, curp, relp, refp, strp, tags, i);
            break;
            
          case 'l':
            relp.setLocation(curp.x, curp.y);
          case 'L':
            i = line(shp, curp, relp, refp, strp, tags, i);
            break;
            
          case 'h':
            relp.setLocation(curp.x, curp.y);
          case 'H':
            i = horizontal(shp, curp, relp, refp, strp, tags, i);
            break;
            
          case 'v':
            relp.setLocation(curp.x, curp.y);
          case 'V':
            i = vertical(shp, curp, relp, refp, strp, tags, i);
            break;
          }
      }
    return shp;
  }  

  private int move(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addMoveTo(RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y);

    curp.setLocation(RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y);
    refp.setLocation(curp.x,curp.y);
    strp.setLocation(curp.x,curp.y);
    //relp.setLocation(0F, 0F);
    return i + 1;
  }

  private int curve(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addBezierTo(RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y, RGeomerative.parent.parseFloat(tags[i+2])+relp.x, RGeomerative.parent.parseFloat(tags[i+3])+relp.y, RGeomerative.parent.parseFloat(tags[i+4])+relp.x, RGeomerative.parent.parseFloat(tags[i+5])+relp.y);

    curp.setLocation(RGeomerative.parent.parseFloat(tags[i+4])+relp.x, RGeomerative.parent.parseFloat(tags[i+5])+relp.y);
    refp.setLocation(2.0f*curp.x-(RGeomerative.parent.parseFloat(tags[i+2])+relp.x), 2.0f*curp.y-(RGeomerative.parent.parseFloat(tags[i+3])+relp.y));
    return i + 5;
  }

  private int smooth(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addBezierTo(refp.x, refp.y, RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y, RGeomerative.parent.parseFloat(tags[i+2])+relp.x, RGeomerative.parent.parseFloat(tags[i+3])+relp.y);

    curp.setLocation(RGeomerative.parent.parseFloat(tags[i+2])+relp.x, RGeomerative.parent.parseFloat(tags[i+3])+relp.y);
    refp.setLocation(2.0f*curp.x-(RGeomerative.parent.parseFloat(tags[i])+relp.x), 2.0f*curp.y-(RGeomerative.parent.parseFloat(tags[i+1])+relp.y));
    return i + 3;
  }

  private int line(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addLineTo(RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y);

    curp.setLocation(RGeomerative.parent.parseFloat(tags[i])+relp.x, RGeomerative.parent.parseFloat(tags[i+1])+relp.y);
    refp.setLocation(curp.x, curp.y);
    return i + 1;        
  }

  private int horizontal(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addLineTo(RGeomerative.parent.parseFloat(tags[i])+relp.x, curp.y);

    curp.setLocation(RGeomerative.parent.parseFloat(tags[i])+relp.x, curp.y);
    refp.setLocation(curp.x, curp.y);
    return i;
  }

  private int vertical(RShape shp, RPoint curp, RPoint relp, RPoint refp, RPoint strp, String[] tags, int i){
    shp.addLineTo(curp.x, RGeomerative.parent.parseFloat(tags[i])+relp.y);

    curp.setLocation(curp.x, RGeomerative.parent.parseFloat(tags[i])+relp.y);
    refp.setLocation(curp.x, curp.y);
    return i;
  }
}
