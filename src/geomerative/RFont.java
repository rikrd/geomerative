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

import org.apache.batik.svggen.font.*;
import org.apache.batik.svggen.font.table.*;

/**
 * RShape is a reduced interface for creating, holding and drawing text from TrueType Font files. It's a basic interpreter of TrueType fonts enabling to access any String in the form of a group of shapes.  Enabling us in this way to access their geometry.
 * @eexample RFont
 * @usage Geometry
 * @related RGroup
 *
 * @extended
 */
public class RFont implements PConstants{
  Font f;
  float scaleFactor = 0.2F;
  //int scaleFactorFixed = 1;
  
  /**
   * The point size of the font. 
   * @eexample size
   * @related setSize ( )
   * @related RFont
   */
  public int size = DEFAULT_SIZE;
  
  /**
   * The alignment of the font. This property can take the following values: RFont.LEFT, RFont.CENTER and RFont.RIGHT
   * @eexample align
   * @related setAlign ( )
   * @related RFont
   */
  public int align = DEFAULT_ALIGN;
    
  final static int DEFAULT_SIZE = 48;
  final static int DEFAULT_RESOLUTION = 72;
  final static int DEFAULT_ALIGN = RFont.LEFT;
  
  /**
   * Should we try to use ASCII, rather than Unicode?
   */
  public boolean forceAscii = false;
  
  
  /**
   * The constructor of the RFont object.  Use this in order to create a font with which we will be able to draw and obtain outlines of text.
   * @eexample RFont
   * @param fontPath String, the name of the TrueType Font file which should be situated in the data folder of the sketch.
   * @param size int, the point size of the font in points.
   * @param align int, this can only take the following values: RFont.LEFT, RFont.CENTER and RFont.RIGHT.
   * @related toGroup ( )
   * @related toShape ( )
   * @related toPolygon ( )
   * @related toMesh ( )
   * @related draw ( )
   */
  public RFont(String fontPath, int size, int align) throws RuntimeException{   
    // Try to find the font as font path
    byte[] bs = RG.parent().loadBytes(fontPath);
    f = Font.create(bs);
    
    setSize(size);
    setAlign(align);
  }
  
  public RFont(String fontPath, int size) throws RuntimeException{
    this(fontPath, size, DEFAULT_ALIGN);
  }
  
  public RFont(String fontPath) throws RuntimeException{
    this(fontPath, DEFAULT_SIZE, DEFAULT_ALIGN);
  }
  
  /**
   * Use this method to reset the point size of the font.
   * @eexample setSize
   * @param size int, the point size of the font in points.
   * @related size
   * @related RFont
   */
  public void setSize(int size){
    short unitsPerEm = f.getHeadTable().getUnitsPerEm();
    int resolution = RG.dpi();
    this.scaleFactor = ((float)size * (float)resolution) / (72F * (float)unitsPerEm);
    //this.scaleFactorFixed = (int)(this.scaleFactor * 65536F);
    //System.out.println(scaleFactor);
    //System.out.println(scaleFactorFixed);
  }

  public float getLineSpacing() {
    // More info at:
    //    http://fontforge.sourceforge.net/faq.html#linespace
    //    http://typophile.com/node/13081
    short unitsPerEm = f.getHeadTable().getUnitsPerEm();
    System.out.println("UnitsPerEm (emsize): " + unitsPerEm);

    // HHEA table method:
    float hheaLineGap = (f.getHheaTable().getAscender() - f.getHheaTable().getDescender() + f.getHheaTable().getLineGap()) * this.scaleFactor;
    System.out.println("HHEA lineGap: " + hheaLineGap);

    // OS2 table typographic line gap method:
    float os2TypoLineGap = (f.getOS2Table().getTypoAscender() - f.getOS2Table().getTypoDescender() + f.getOS2Table().getTypoLineGap()) * this.scaleFactor;
    System.out.println("Os2 Typo lineGap: " + os2TypoLineGap);

    // OS2 table win line gap method:
    float os2WinLineGap = (f.getOS2Table().getWinAscent() + f.getOS2Table().getWinDescent()) * this.scaleFactor;
    System.out.println("Os2 Win lineGap: " + os2WinLineGap);

    // Automatic calculation
    float autoLineGap = f.getHeadTable().getUnitsPerEm() * 1.25f * this.scaleFactor;
    System.out.println("Automatic lineGap: " + autoLineGap);

    return hheaLineGap;
  }
  
  /**
   * Use this method to reset the alignment of the font. This property can take the following values: RFont.LEFT, RFont.CENTER and RFont.RIGHT
   * @eexample setAlign
   * @param align int, this can only take the following values: RFont.LEFT, RFont.CENTER and RFont.RIGHT.
   * @related align
   * @related RFont
   */
  public void setAlign(int align) throws RuntimeException{
    if(align!=LEFT && align!=CENTER && align!=RIGHT){
      throw new RuntimeException("Alignment unknown.  The only accepted values are: RFont.LEFT, RFont.CENTER and RFont.RIGHT");
    }
    this.align = align;
  }
  
  /**
   * @invisible
   **/
  public String getFamily(){
    return f.getNameTable().getRecord(org.apache.batik.svggen.font.table.Table.nameFontFamilyName);
  }
  
  /**
   * Use this method to get the outlines of a character in the form of an RShape.
   * @eexample RFont_toShape
   * @param character char, the character we want the outline from.
   * @return RShape, the outline of the character.
   * @related toGroup ( )
   * @related toPolygon ( )
   * @related draw ( )
   */
  public RShape toShape(char character){
    RGroup grp = toGroup(Character.toString(character));
    if(grp.countElements()>0) return (RShape)(grp.elements[0]);
    return new RShape();
  }
  
  /**
   * Use this method to get the outlines of a character in the form of an RPolygon.
   * @eexample RFont_toPolygon
   * @param character char, the character we want the outline from.
   * @return RPolygon, the outline of the character.
   * @related toGroup ( )
   * @related toShape ( )
   * @related draw ( )
   */
  public RPolygon toPolygon(char character) {
    return toShape(character).toPolygon();
  }
  
  
  private CmapFormat getCmapFormat() {
    if (forceAscii) {
      // We've been asked to use the ASCII/Macintosh cmap format
      return f.getCmapTable().getCmapFormat(
                                            org.apache.batik.svggen.font.table.Table.platformMacintosh,
                                            org.apache.batik.svggen.font.table.Table.encodingRoman );
    } else {
      short[] platforms = new short[] {
        org.apache.batik.svggen.font.table.Table.platformMicrosoft,
        org.apache.batik.svggen.font.table.Table.platformAppleUnicode,
        org.apache.batik.svggen.font.table.Table.platformMacintosh
      };
      short[] encodings = new short[] {
        org.apache.batik.svggen.font.table.Table.encodingUGL,
        org.apache.batik.svggen.font.table.Table.encodingKorean,
        org.apache.batik.svggen.font.table.Table.encodingHebrew,
        org.apache.batik.svggen.font.table.Table.encodingUndefined
      };
      
      CmapFormat cmapFmt;
      for(int i = 0; i < encodings.length; i++) {
        for(int j = 0; j < platforms.length; j++) {
          
          cmapFmt = f.getCmapTable().getCmapFormat(platforms[j], encodings[i]);
          if (cmapFmt != null) {
            return cmapFmt;
          }                    
                }
      }
      return null;
    }
  }

  
  /**
   * Use this method to get the outlines of a string in the form of an RGroup.  All the elements of the group will be RShapes.
   * @eexample RFont_toGroup
   * @param text String, the string we want the outlines from.
   * @return RGroup, the group of outlines of the character.  All the elements are RShapes.
   * @related toShape ( )
   * @related draw ( )
   */
  public RGroup toGroup(String text)  throws RuntimeException{
    RGroup result = new RGroup();
    
    // Decide upon a cmap table to use for our character to glyph look-up
    CmapFormat cmapFmt = getCmapFormat();
    
    if (cmapFmt == null) {
      throw new RuntimeException("Cannot find a suitable cmap table");
    }
    
    // If this font includes arabic script, we want to specify
    // substitutions for initial, medial, terminal & isolated
    // cases.
    /*
      GsubTable gsub = (GsubTable) f.getTable(Table.GSUB);
      SingleSubst initialSubst = null;
      SingleSubst medialSubst = null;
      SingleSubst terminalSubst = null;
      if (gsub != null) {
      Script s = gsub.getScriptList().findScript(ScriptTags.SCRIPT_TAG_ARAB);
      if (s != null) {
      LangSys ls = s.getDefaultLangSys();
      if (ls != null) {
      Feature init = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_INIT);
      Feature medi = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_MEDI);
      Feature fina = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_FINA);
      
      initialSubst = (SingleSubst)
      gsub.getLookupList().getLookup(init, 0).getSubtable(0);
      medialSubst = (SingleSubst)
      gsub.getLookupList().getLookup(medi, 0).getSubtable(0);
      terminalSubst = (SingleSubst)
      gsub.getLookupList().getLookup(fina, 0).getSubtable(0);
      }
      }
      }*/
    
    int x = 0;
    for (short i = 0; i < text.length(); i++) {
      int glyphIndex = cmapFmt.mapCharCode(text.charAt(i));
      Glyph glyph = f.getGlyph(glyphIndex);
      int default_advance_x = f.getHmtxTable().getAdvanceWidth(glyphIndex);
      if (glyph != null) {
        glyph.scale(scaleFactor);
        // Add the Glyph to the Shape with an horizontal offset of x
        result.addElement(getGlyphAsShape(f,glyph, glyphIndex,x));
        x += glyph.getAdvanceWidth();
      }else{
        x += (int)((float)default_advance_x*scaleFactor);
      }
      
    }
    
    if(align!=LEFT && align!=CENTER && align!=RIGHT){
      throw new RuntimeException("Alignment unknown.  The only accepted values are: RFont.LEFT, RFont.CENTER and RFont.RIGHT");
    }
    
    RRectangle r;
    RMatrix mattrans;

    switch(this.align){
    case RFont.CENTER:
      r = result.getBounds();
      mattrans = new RMatrix();
      mattrans.translate((r.getMinX()-r.getMaxX())/2,0);
      result.transform(mattrans);
      break;
    case RFont.RIGHT:
      r = result.getBounds();
      mattrans = new RMatrix();
      mattrans.translate((r.getMinX()-r.getMaxX()),0);
      result.transform(mattrans);
      break;
    case RFont.LEFT:
      break;
    }
    return result;
  }

  public RShape toShape(String text)  throws RuntimeException{
    RShape result = new RShape();
    
    // Decide upon a cmap table to use for our character to glyph look-up
    CmapFormat cmapFmt = getCmapFormat();
    
    if (cmapFmt == null) {
      throw new RuntimeException("Cannot find a suitable cmap table");
    }
    
    // If this font includes arabic script, we want to specify
    // substitutions for initial, medial, terminal & isolated
    // cases.
    /*
      GsubTable gsub = (GsubTable) f.getTable(Table.GSUB);
      SingleSubst initialSubst = null;
      SingleSubst medialSubst = null;
      SingleSubst terminalSubst = null;
      if (gsub != null) {
      Script s = gsub.getScriptList().findScript(ScriptTags.SCRIPT_TAG_ARAB);
      if (s != null) {
      LangSys ls = s.getDefaultLangSys();
      if (ls != null) {
      Feature init = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_INIT);
      Feature medi = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_MEDI);
      Feature fina = gsub.getFeatureList().findFeature(ls, FeatureTags.FEATURE_TAG_FINA);
      
      initialSubst = (SingleSubst)
      gsub.getLookupList().getLookup(init, 0).getSubtable(0);
      medialSubst = (SingleSubst)
      gsub.getLookupList().getLookup(medi, 0).getSubtable(0);
      terminalSubst = (SingleSubst)
      gsub.getLookupList().getLookup(fina, 0).getSubtable(0);
      }
      }
      }*/
    
    int x = 0;
    for (short i = 0; i < text.length(); i++) {
      int glyphIndex = cmapFmt.mapCharCode(text.charAt(i));
      Glyph glyph = f.getGlyph(glyphIndex);
      int default_advance_x = f.getHmtxTable().getAdvanceWidth(glyphIndex);
      if (glyph != null) {
        glyph.scale(scaleFactor);
        // Add the Glyph to the Shape with an horizontal offset of x
        result.addChild(getGlyphAsShape(f,glyph, glyphIndex,x));
        x += glyph.getAdvanceWidth();
      }else{
        x += (int)((float)default_advance_x*scaleFactor);
      }
      
    }
    
    if(align!=LEFT && align!=CENTER && align!=RIGHT){
      throw new RuntimeException("Alignment unknown.  The only accepted values are: RFont.LEFT, RFont.CENTER and RFont.RIGHT");
    }
    
    RRectangle r;
    RMatrix mattrans;

    switch(this.align){
    case RFont.CENTER:
      r = result.getBounds();
      mattrans = new RMatrix();
      mattrans.translate((r.getMinX()-r.getMaxX())/2,0);
      result.transform(mattrans);
      break;
    case RFont.RIGHT:
      r = result.getBounds();
      mattrans = new RMatrix();
      mattrans.translate((r.getMinX()-r.getMaxX()),0);
      result.transform(mattrans);
      break;
    case RFont.LEFT:
      break;
    }
    return result;
  }
  
  /**
   * Use this method to draw a character on a certain canvas.
   * @eexample RFont_draw
   * @param character  the character to be drawn
   * @param g   the canvas where to draw
   * @related toShape ( )
   * @related toGroup ( )
   */
  public void draw(char character, PGraphics g) throws RuntimeException{
    this.toShape(character).draw(g);
  }
  
  /**
   * Use this method to draw a character on a certain canvas.
   * @eexample RFont_draw
   * @param text  the string to be drawn
   * @param g  the canvas where to draw
   * @related toShape ( )
   * @related toGroup ( )
   */  
  public void draw(String text, PGraphics g) throws RuntimeException{
    this.toGroup(text).draw(g);
  }
  
  /**
   * Use this method to draw a character on a certain canvas.
   * @eexample RFont_draw
   * @param character char, the character to be drawn
   * @param g  the canvas where to draw
   * @related toShape ( )
   * @related toGroup ( )
   */
  public void draw(char character, PApplet g) throws RuntimeException{
    this.toShape(character).draw(g);
  }
  
  /**
   * Use this method to draw a character on a certain canvas.
   * @eexample RFont_draw
   * @param text  the string to be drawn
   * @param g  the canvas where to draw
   * @related toShape ( )
   * @related toGroup ( )
   */  
  public void draw(String text, PApplet g) throws RuntimeException{
    this.toGroup(text).draw(g);
  }
  
  public void draw(String text) throws RuntimeException{
    this.toGroup(text).draw();
  }
  
  public void draw(char character) throws RuntimeException{
    this.toShape(character).draw();
  }
  
  
  
  private static float midValue(float a, float b) {
    return a + (b - a)/2;
  }
  
  protected static RShape getContourAsShape(Glyph glyph, int startIndex, int count) {
    return getContourAsShape(glyph, startIndex, count, 0);
  }
  
  protected static RShape getContourAsShape(Glyph glyph, int startIndex, int count, float xadv) {
    
    // If this is a single point on it's own, weSystem.out.println("Value of pointx: "+pointx); can't do anything with it
    if (glyph.getPoint(startIndex).endOfContour) {
      return new RShape();
    }
    
    RShape result = new RShape();
    int offset = 0;
    //float originx = 0F,originy = 0F;
    
    while (offset < count) {
      Point point = glyph.getPoint(startIndex + offset%count);
      Point point_plus1 = glyph.getPoint(startIndex + (offset+1)%count);
      Point point_plus2 = glyph.getPoint(startIndex + (offset+2)%count);
      
      float pointx = ((float)point.x + xadv);
      float pointy = ((float)point.y);
      float point_plus1x = ((float)point_plus1.x + xadv);
      float point_plus1y = ((float)point_plus1.y);
      float point_plus2x = ((float)point_plus2.x + xadv);
      float point_plus2y = ((float)point_plus2.y);
      
      if (offset == 0) {
        // move command
        result.addMoveTo(pointx,pointy);
      }
      
      if (point.onCurve && point_plus1.onCurve) {
        // line command
        result.addLineTo(point_plus1x,point_plus1y);
        offset++;
      } else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
        // This is a curve with no implied points
        // quadratic bezier command
        result.addQuadTo(point_plus1x, point_plus1y, point_plus2x, point_plus2y);
        offset+=2;
      } else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
        // This is a curve with one implied point
        // quadratic bezier command avec le endPoint implicit
        result.addQuadTo(point_plus1x, point_plus1y, midValue(point_plus1x, point_plus2x), midValue(point_plus1y, point_plus2y));
        offset+=2;
      } else if (!point.onCurve && !point_plus1.onCurve) {
        // This is a curve with two implied points
        // quadratic bezier with
        result.addQuadTo(pointx, pointy, midValue(pointx, point_plus1x), midValue(pointy, point_plus1y));
        offset++;
      } else if (!point.onCurve && point_plus1.onCurve) {
        // This is a curve with no implied points
        result.addQuadTo(pointx, pointy, point_plus1x, point_plus1y);
        offset++;
      } else {
        System.out.println("drawGlyph case not catered for!!");
        break;
      }
    }
    result.addClose();
    return result;
  }
  
  protected static RShape getGlyphAsShape(Font font, Glyph glyph, int glyphIndex) {
    return getGlyphAsShape(font,glyph,glyphIndex,0);
  }
  
  protected static RShape getGlyphAsShape(Font font, Glyph glyph, int glyphIndex,float xadv) {
    
    RShape result = new RShape();
    int firstIndex = 0;
    int count = 0;
    int i;
    
    if (glyph != null) {
      for (i = 0; i < glyph.getPointCount(); i++) {
        count++;
        if (glyph.getPoint(i).endOfContour) {
          result.addShape(getContourAsShape(glyph, firstIndex, count, xadv));
          firstIndex = i + 1;
          count = 0;
        }
      }
    }
    
    return result;
  }
  
  protected static RShape getGlyphAsShape(Font font, Glyph glyph, int glyphIndex, SingleSubst arabInitSubst, SingleSubst arabMediSubst, SingleSubst arabTermSubst) {
    return getGlyphAsShape(font, glyph, glyphIndex, arabInitSubst, arabMediSubst, arabTermSubst, 0);
  }
  
  protected static RShape getGlyphAsShape(Font font, Glyph glyph, int glyphIndex, SingleSubst arabInitSubst, SingleSubst arabMediSubst, SingleSubst arabTermSubst, float xadv) {
    
    RShape result = new RShape();
    boolean substituted = false;
    
    // arabic = "initial | medial | terminal | isolated"
    int arabInitGlyphIndex = glyphIndex;
    int arabMediGlyphIndex = glyphIndex;
    int arabTermGlyphIndex = glyphIndex;
    if (arabInitSubst != null) {
      arabInitGlyphIndex = arabInitSubst.substitute(glyphIndex);
    }
    if (arabMediSubst != null) {
      arabMediGlyphIndex = arabMediSubst.substitute(glyphIndex);
    }
    if (arabTermSubst != null) {
      arabTermGlyphIndex = arabTermSubst.substitute(glyphIndex);
    }
    
    if (arabInitGlyphIndex != glyphIndex) {
      result.addShape(getGlyphAsShape(font,font.getGlyph(arabInitGlyphIndex),arabInitGlyphIndex));
      substituted = true;
    }
    
    if (arabMediGlyphIndex != glyphIndex) {
      result.addShape(getGlyphAsShape(font,font.getGlyph(arabMediGlyphIndex),arabMediGlyphIndex));
      substituted = true;
    }
    
    if (arabTermGlyphIndex != glyphIndex) {
      result.addShape(getGlyphAsShape(font,font.getGlyph(arabTermGlyphIndex),arabTermGlyphIndex));
      substituted = true;
    }
    
    if (substituted) {
      result.addShape(getGlyphAsShape(font,glyph,glyphIndex));
    } else {
      result.addShape(getGlyphAsShape(font,glyph,glyphIndex));
    }
    
    return result;
  }
}
