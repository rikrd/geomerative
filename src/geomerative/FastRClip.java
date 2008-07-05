/*
 * The SEI Software Open Source License, Version 1.0
 *
 * Copyright (c) 2004, Solution Engineering, Inc.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Solution Engineering, Inc. (http://www.seisw.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 3. The name "Solution Engineering" must not be used to endorse or
 *    promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    admin@seisw.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL SOLUTION ENGINEERING, INC. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package geomerative;

import java.util.ArrayList ;
import java.util.List ;

/**
 * <code>Clip</code> is a Java version of the <i>General RPolygon Clipper</i> algorithm
 * developed by Alan Murta (gpc@cs.man.ac.uk).  The home page for the original source can be 
 * found at <a href="http://www.cs.man.ac.uk/aig/staff/alan/software/" target="_blank">
 * http://www.cs.man.ac.uk/aig/staff/alan/software/</a>.
 * <p>
 * <b><code>polyClass:</code></b> Some of the public methods below take a <code>polyClass</code>
 * argument.  This <code>java.lang.Class</code> object is assumed to implement the <code>RPolygon</code>
 * interface and have a no argument constructor.  This was done so that the user of the algorithm
 * could create their own classes that implement the <code>RPolygon</code> interface and still uses
 * this algorithm.
 * <p>
 * <strong>Implementation Note:</strong> The converted algorithm does support the <i>difference</i>
 * operation, but a public method has not been provided and it has not been tested.  To do so,
 * simply follow what has been done for <i>intersection</i>.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
class FastRClip
{
  // -----------------
  // --- Constants ---
  // -----------------
  private static final boolean DEBUG = false ;
  
  // Maximum precision for floats
  private static final double GPC_EPSILON = 2.2204460492503131e-016 ;
  //private static final float GPC_EPSILON = 1.192092896e-07F;
  //private static final float GPC_EPSILON = 1F;
  private static final String GPC_VERSION = "2.31" ;
  
  private static final int LEFT  = 0 ;
  private static final int RIGHT = 1 ;
  
  private static final int ABOVE = 0 ;
  private static final int BELOW = 1 ;
  
  private static final int CLIP = 0 ;
  private static final int SUBJ = 1 ;
  
  private static final boolean INVERT_TRISTRIPS = false ;
  
  // ------------------------
  // --- Member Variables ---
  // ------------------------
  
  // --------------------
  // --- Constructors ---
  // --------------------
  /** Creates a new instance of Clip */
  private FastRClip()
  {
  }
  
  // ----------------------
  // --- Static Methods ---
  // ----------------------
  /**
   * Return the intersection of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>polyClass</code>.  See the note in the class description
   * for more on <ocde>polyClass</code>.
   *
   * @param p1        One of the polygons to performt he intersection with
   * @param p2        One of the polygons to performt he intersection with
   * @param polyClass The type of <code>RPolygon</code> to return
   */
  static RPolygon intersection( RPolygon p1, RPolygon p2, Class polyClass )
  {
    return clip( OperationType.GPC_INT, p1, p2, polyClass );
  }
  
  /**
   * Return the union of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>polyClass</code>.  See the note in the class description
   * for more on <ocde>polyClass</code>.
   *
   * @param p1        One of the polygons to performt he union with
   * @param p2        One of the polygons to performt he union with
   * @param polyClass The type of <code>RPolygon</code> to return
   */
  static RPolygon union( RPolygon p1, RPolygon p2, Class polyClass )
  {
    return clip( OperationType.GPC_UNION, p1, p2, polyClass );
  }
  
  /**
   * Return the xor of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>polyClass</code>.  See the note in the class description
   * for more on <ocde>polyClass</code>.
   *
   * @param p1        One of the polygons to performt he xor with
   * @param p2        One of the polygons to performt he xor with
   * @param polyClass The type of <code>RPolygon</code> to return
   */
  static RPolygon xor( RPolygon p1, RPolygon p2, Class polyClass )
  {
    return clip( OperationType.GPC_XOR, p1, p2, polyClass );
  }
  
  /**
   * Return the diff of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>polyClass</code>.  See the note in the class description
   * for more on <ocde>polyClass</code>.
   *
   * @param p1        One of the polygons to performt he diff with
   * @param p2        One of the polygons to performt he diff with
   * @param polyClass The type of <code>RPolygon</code> to return
   */
  static RPolygon diff( RPolygon p1, RPolygon p2, Class polyClass )
  {
    return clip( OperationType.GPC_DIFF, p1, p2, polyClass );
  }
  
  /**
   * Return the intersection of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>PolyDefault</code>. 
   *
   * @param p1 One of the polygons to performt he intersection with
   * @param p2 One of the polygons to performt he intersection with
   */
  static RPolygon intersection( RPolygon p1, RPolygon p2 )
  {
    return clip( OperationType.GPC_INT, p1, p2, RPolygon.class );
  }
  
  /**
   * Return the union of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>PolyDefault</code>. 
   *
   * @param p1 One of the polygons to performt he union with
   * @param p2 One of the polygons to performt he union with
   */
  static RPolygon union( RPolygon p1, RPolygon p2 )
  {
    return clip( OperationType.GPC_UNION, p1, p2, RPolygon.class );
  }
  
  /**
   * Return the xor of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>PolyDefault</code>. 
   *
   * @param p1 One of the polygons to performt he xor with
   * @param p2 One of the polygons to performt he xor with
   */
  static RPolygon xor( RPolygon p1, RPolygon p2 )
  {
    return clip( OperationType.GPC_XOR, p1, p2, RPolygon.class );
  }
  
  /**
   * Return the diff of <code>p1</code> and <code>p2</code> where the
   * return type is of <code>PolyDefault</code>. 
   *
   * @param p1 One of the polygons to performt he diff with
   * @param p2 One of the polygons to performt he diff with
   */
  static RPolygon diff( RPolygon p1, RPolygon p2 )
  {
    return clip( OperationType.GPC_DIFF, p1, p2, RPolygon.class );
  }
  
  /**
   * Updates <code>p1</code>. 
   *
   * @param p1 One of the polygons to performt he diff with
   */
  static RPolygon update( RPolygon p1 )
  {
    return clip( OperationType.GPC_DIFF, p1, new RPolygon(), RPolygon.class );
  }
  
  
  // -----------------------
  // --- Private Methods ---
  // -----------------------
  
  /**
   * Create a new <code>RPolygon</code> type object using <code>polyClass</code>.
   */
  private static RPolygon createNewPoly( Class polyClass )
  {
    try
      {
        return (RPolygon)polyClass.newInstance();
      }
    catch( Exception e )
      {
        throw new RuntimeException(e);
      }
  }
  
  /**
   * <code>clip()</code> is the main method of the clipper algorithm.
   * This is where the conversion from really begins.
   */
  private static RPolygon clip( OperationType op, RPolygon subj, RPolygon clip, Class polyClass )
  {
    RPolygon result = createNewPoly( polyClass ) ;
    
    /* Test for trivial NULL result cases */
    if( (subj.isEmpty() && clip.isEmpty()) ||
        (subj.isEmpty() && ((op == OperationType.GPC_INT) || (op == OperationType.GPC_DIFF))) ||
        (clip.isEmpty() &&  (op == OperationType.GPC_INT)) )
      {
        return result ;
      }
    
    /* Identify potentialy contributing contours */
    if( ((op == OperationType.GPC_INT) || (op == OperationType.GPC_DIFF)) && 
        !subj.isEmpty() && !clip.isEmpty() )
      {
        minimax_test(subj, clip, op);
      }
    
    /* Build LMT */
    LmtTable lmt_table = new LmtTable();
    ScanBeamTreeEntries sbte = new ScanBeamTreeEntries();
    EdgeTable s_heap = null ;
    EdgeTable c_heap = null ;
    if (!subj.isEmpty())
      {
        s_heap = build_lmt(lmt_table, sbte, subj, SUBJ, op);
      }
    if( DEBUG )
      {
        System.out.println("");
        System.out.println(" ------------ After build_lmt for subj ---------");
        lmt_table.print();
      }
    if (!clip.isEmpty())
      {
        c_heap = build_lmt(lmt_table, sbte, clip, CLIP, op);
      }
    if( DEBUG )
      {
        System.out.println("");
        System.out.println(" ------------ After build_lmt for clip ---------");
        lmt_table.print();
      }
    
    /* Return a NULL result if no contours contribute */
    if (lmt_table.top_node == null)
      {
        return result;
      }
    
    /* Build scanbeam table from scanbeam tree */
    float[] sbt = sbte.build_sbt();
    
    int parity_clip = LEFT;
    int parity_subj = LEFT;
    
    /* Invert clip polygon for difference operation */
    if (op == OperationType.GPC_DIFF)
      {
        parity_clip= RIGHT;
      }
    
    if( DEBUG )
      {
        print_sbt(sbt);
      }
    
    LmtNode local_min = lmt_table.top_node ;
    
    TopPolygonNode out_poly = new TopPolygonNode(); // used to create resulting RPolygon
    
    AetTree aet = new AetTree();
    int scanbeam = 0 ;
    
    /* Process each scanbeam */
    while( scanbeam < sbt.length )
      {
        /* Set yb and yt to the bottom and top of the scanbeam */
        float yb = sbt[scanbeam++];
        float yt = 0.0F ;
        float dy = 0.0F ;
        if( scanbeam < sbt.length )
          {
            yt = sbt[scanbeam];
            dy = yt - yb;
          }
        
        /* === SCANBEAM BOUNDARY PROCESSING ================================ */
        
        /* If LMT node corresponding to yb exists */
        if (local_min != null )
          {
            if (local_min.y == yb)
              {
                /* Add edges starting at this local minimum to the AET */
                for( EdgeNode edge = local_min.first_bound; (edge != null) ; edge= edge.next_bound)
                  {
                    add_edge_to_aet( aet, edge );
                  }
                
                local_min = local_min.next;
              }
          }
        
        if( DEBUG )
          {
            aet.print();
          }
        /* Set dummy previous x value */
        float px = -Float.MAX_VALUE ;
        
        /* Create bundles within AET */
        EdgeNode e0 = aet.top_node ;
        EdgeNode e1 = aet.top_node ;
        
        /* Set up bundle fields of first edge */
        aet.top_node.bundle_above[ aet.top_node.type ] = (aet.top_node.top_y != yb) ? 1 : 0;
        aet.top_node.bundle_above[ ((aet.top_node.type==0) ? 1 : 0) ] = 0;
        aet.top_node.bstate_above = BundleState.UNBUNDLED;
        
        for (EdgeNode next_edge= aet.top_node.next ; (next_edge != null); next_edge = next_edge.next)
          {
            int ne_type = next_edge.type ;
            int ne_type_opp = ((next_edge.type==0) ? 1 : 0); //next edge type opposite
            
            /* Set up bundle fields of next edge */
            next_edge.bundle_above[ ne_type     ]= (next_edge.top_y != yb) ? 1 : 0;
            next_edge.bundle_above[ ne_type_opp ] = 0 ;
            next_edge.bstate_above = BundleState.UNBUNDLED;
            
            /* Bundle edges above the scanbeam boundary if they coincide */
            if ( next_edge.bundle_above[ne_type] == 1 )
              {
                if (EQ(e0.xb, next_edge.xb) && EQ(e0.dx, next_edge.dx) && (e0.top_y != yb))
                  {
                    next_edge.bundle_above[ ne_type     ] ^= e0.bundle_above[ ne_type     ];
                    next_edge.bundle_above[ ne_type_opp ]  = e0.bundle_above[ ne_type_opp ];
                    next_edge.bstate_above = BundleState.BUNDLE_HEAD;
                    e0.bundle_above[CLIP] = 0;
                    e0.bundle_above[SUBJ] = 0;
                    e0.bstate_above = BundleState.BUNDLE_TAIL;
                  }
                e0 = next_edge;
              }
          }
        
        int horiz_clip = HState.NH;
        int horiz_subj = HState.NH;
        
        int exists_clip = 0 ;
        int exists_subj = 0 ;
        
        PolygonNode cf = null ;
        
        /* Process each edge at this scanbeam boundary */
        for (EdgeNode edge= aet.top_node ; (edge != null); edge = edge.next )
          {
            exists_clip = edge.bundle_above[CLIP] + (edge.bundle_below_clip << 1);
            exists_subj = edge.bundle_above[SUBJ] + (edge.bundle_below_subj << 1);
            
            if( (exists_clip != 0) || (exists_subj != 0) )
              {
                /* Set bundle side */
                edge.bside_clip = parity_clip;
                edge.bside_subj = parity_subj;
                
                boolean contributing = false ;
                int br=0, bl=0, tr=0, tl=0 ;
                /* Determine contributing status and quadrant occupancies */
                if( (op == OperationType.GPC_DIFF) || (op == OperationType.GPC_INT) )
                  {
                    contributing= ((exists_clip!=0) && ((parity_subj!=0) || (horiz_subj!=0))) ||
                      ((exists_subj!=0) && ((parity_clip!=0) || (horiz_clip!=0))) ||
                      ((exists_clip!=0) && (exists_subj!=0) && (parity_clip == parity_subj));
                    br = ((parity_clip!=0) && (parity_subj!=0)) ? 1 : 0;
                    bl = ( ((parity_clip ^ edge.bundle_above[CLIP])!=0) &&
                           ((parity_subj ^ edge.bundle_above[SUBJ])!=0) ) ? 1 : 0;
                    tr = ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0)) !=0) && 
                           ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0)) !=0) ) ? 1 : 0;
                    tl = (((parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)!=0) &&
                          ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj)!=0))?1:0;
                  }
                else if( op == OperationType.GPC_XOR )
                  {
                    contributing= (exists_clip!=0) || (exists_subj!=0);
                    br= (parity_clip) ^ (parity_subj);
                    bl= (parity_clip ^ edge.bundle_above[CLIP]) ^ (parity_subj ^ edge.bundle_above[SUBJ]);
                    tr= (parity_clip ^ ((horiz_clip!=HState.NH)?1:0)) ^ (parity_subj ^ ((horiz_subj!=HState.NH)?1:0));
                    tl= (parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)
                      ^ (parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj);
                  }
                else if( op == OperationType.GPC_UNION )
                  {
                    contributing= ((exists_clip!=0) && (!(parity_subj!=0) || (horiz_subj!=0))) ||
                      ((exists_subj!=0) && (!(parity_clip!=0) || (horiz_clip!=0))) ||
                      ((exists_clip!=0) && (exists_subj!=0) && (parity_clip == parity_subj));
                    br= ((parity_clip!=0) || (parity_subj!=0))?1:0;
                    bl= (((parity_clip ^ edge.bundle_above[CLIP])!=0) || ((parity_subj ^ edge.bundle_above[SUBJ])!=0))?1:0;
                    tr= ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0))!=0) || 
                          ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0))!=0) ) ?1:0;
                    tl= ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)!=0) ||
                          ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj)!=0) ) ? 1:0;
                  }
                else
                  {
                    throw new IllegalStateException("Unknown op");
                  }
                
                /* Update parity */
                parity_clip ^= edge.bundle_above[CLIP];
                parity_subj ^= edge.bundle_above[SUBJ];
                
                /* Update horizontal state */
                if (exists_clip!=0)
                  {
                    horiz_clip = HState.next_h_state[horiz_clip][((exists_clip - 1) << 1) + parity_clip];
                  }
                if( exists_subj!=0)
                  {
                    horiz_subj = HState.next_h_state[horiz_subj][((exists_subj - 1) << 1) + parity_subj];
                  }
                
                if (contributing)
                  {
                    float xb = edge.xb;
                    
                    int vclass = VertexType.getType( tr, tl, br, bl );
                    switch (vclass)
                      {
                      case VertexType.EMN:
                      case VertexType.IMN:
                        edge.outp_above = out_poly.add_local_min(xb, yb);
                        px = xb;
                        cf = edge.outp_above;
                        break;
                      case VertexType.ERI:
                        if (xb != px)
                          {
                            cf.add_right( xb, yb);
                            px= xb;
                          }
                        edge.outp_above= cf;
                        cf= null;
                        break;
                      case VertexType.ELI:
                        edge.outp_below.add_left( xb, yb);
                        px= xb;
                        cf= edge.outp_below;
                        break;
                      case VertexType.EMX:
                        if (xb != px)
                          {
                            cf.add_left( xb, yb);
                            px= xb;
                          }
                        out_poly.merge_right(cf, edge.outp_below);
                        cf= null;
                        break;
                      case VertexType.ILI:
                        if (xb != px)
                          {
                            cf.add_left( xb, yb);
                            px= xb;
                          }
                        edge.outp_above= cf;
                        cf= null;
                        break;
                      case VertexType.IRI:
                        edge.outp_below.add_right( xb, yb );
                        px= xb;
                        cf= edge.outp_below;
                        edge.outp_below= null;
                        break;
                      case VertexType.IMX:
                        if (xb != px)
                          {
                            cf.add_right( xb, yb );
                            px= xb;
                          }
                        out_poly.merge_left(cf, edge.outp_below);
                        cf= null;
                        edge.outp_below= null;
                        break;
                      case VertexType.IMM:
                        if (xb != px)
                          {
                            cf.add_right( xb, yb);
                            px= xb;
                          }
                        out_poly.merge_left(cf, edge.outp_below);
                        edge.outp_below= null;
                        edge.outp_above = out_poly.add_local_min(xb, yb);
                        cf= edge.outp_above;
                        break;
                      case VertexType.EMM:
                        if (xb != px)
                          {
                            cf.add_left( xb, yb);
                            px= xb;
                          }
                        out_poly.merge_right(cf, edge.outp_below);
                        edge.outp_below= null;
                        edge.outp_above = out_poly.add_local_min(xb, yb);
                        cf= edge.outp_above;
                        break;
                      case VertexType.LED:
                        if (edge.bot_y == yb)
                          edge.outp_below.add_left( xb, yb);
                        edge.outp_above= edge.outp_below;
                        px= xb;
                        break;
                      case VertexType.RED:
                        if (edge.bot_y == yb)
                          edge.outp_below.add_right( xb, yb );
                        edge.outp_above= edge.outp_below;
                        px= xb;
                        break;
                      default:
                        break;
                      } /* End of switch */
                  } /* End of contributing conditional */
              } /* End of edge exists conditional */
            if( DEBUG )
              {
                out_poly.print();
              }
          } /* End of AET loop */
        
        /* Delete terminating edges from the AET, otherwise compute xt */
        for (EdgeNode edge = aet.top_node ; (edge != null); edge = edge.next)
          {
            if (edge.top_y == yb)
              {
                EdgeNode prev_edge = edge.prev;
                EdgeNode next_edge= edge.next;
                
                if (prev_edge != null)
                  prev_edge.next = next_edge;
                else
                  aet.top_node = next_edge;
                
                if (next_edge != null )
                  next_edge.prev = prev_edge;
                
                /* Copy bundle head state to the adjacent tail edge if required */
                if ((edge.bstate_below == BundleState.BUNDLE_HEAD) && (prev_edge!=null))
                  {
                    if (prev_edge.bstate_below == BundleState.BUNDLE_TAIL)
                      {
                        prev_edge.outp_below= edge.outp_below;
                        prev_edge.bstate_below= BundleState.UNBUNDLED;
                        if ( prev_edge.prev != null)
                          {
                            if (prev_edge.prev.bstate_below == BundleState.BUNDLE_TAIL)
                              {
                                prev_edge.bstate_below = BundleState.BUNDLE_HEAD;
                              }
                          }
                      }
                  }
              }
            else
              {
                if (edge.top_y == yt)
                  edge.xt= edge.top_x;
                else
                  edge.xt= edge.bot_x + edge.dx * (yt - edge.bot_y);
              }
          }
        
        if (scanbeam < sbte.sbt_entries )
          {
            /* === SCANBEAM INTERIOR PROCESSING ============================== */
            
            /* Build intersection table for the current scanbeam */
            ItNodeTable it_table = new ItNodeTable();
            it_table.build_intersection_table(aet, dy);
            
            /* Process each node in the intersection table */
            for (ItNode intersect = it_table.top_node ; (intersect != null); intersect = intersect.next)
              {
                e0= intersect.ie0;
                e1= intersect.ie1;
                
                /* Only generate output for contributing intersections */
                if ( ((e0.bundle_above[CLIP]!=0) || (e0.bundle_above[SUBJ]!=0)) &&
                     ((e1.bundle_above[CLIP]!=0) || (e1.bundle_above[SUBJ]!=0)))
                  {
                    PolygonNode p = e0.outp_above;
                    PolygonNode q = e1.outp_above;
                    float ix = intersect.point.x;
                    float iy = intersect.point.y + yb;
                    
                    int in_clip = ( ( (e0.bundle_above[CLIP]!=0) && !(e0.bside_clip!=0)) ||
                                    ( (e1.bundle_above[CLIP]!=0) &&  (e1.bside_clip!=0)) ||
                                    (!(e0.bundle_above[CLIP]!=0) && !(e1.bundle_above[CLIP]!=0) &&
                                     (e0.bside_clip!=0) && (e1.bside_clip!=0) ) ) ? 1 : 0;
                    
                    int in_subj = ( ( (e0.bundle_above[SUBJ]!=0) && !(e0.bside_subj!=0)) ||
                                    ( (e1.bundle_above[SUBJ]!=0) &&  (e1.bside_subj!=0)) ||
                                    (!(e0.bundle_above[SUBJ]!=0) && !(e1.bundle_above[SUBJ]!=0) &&
                                     (e0.bside_subj!=0) && (e1.bside_subj!=0) ) ) ? 1 : 0;
                    
                    int tr=0, tl=0, br=0, bl=0 ;
                    /* Determine quadrant occupancies */
                    if( (op == OperationType.GPC_DIFF) || (op == OperationType.GPC_INT) )
                      {
                        tr= ((in_clip!=0) && (in_subj!=0)) ? 1 : 0;
                        tl= (((in_clip ^ e1.bundle_above[CLIP])!=0) && ((in_subj ^ e1.bundle_above[SUBJ])!=0))?1:0;
                        br= (((in_clip ^ e0.bundle_above[CLIP])!=0) && ((in_subj ^ e0.bundle_above[SUBJ])!=0))?1:0;
                        bl= (((in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])!=0) &&
                             ((in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ])!=0) ) ? 1:0;
                      }
                    else if( op == OperationType.GPC_XOR )
                      {
                        tr= (in_clip)^ (in_subj);
                        tl= (in_clip ^ e1.bundle_above[CLIP]) ^ (in_subj ^ e1.bundle_above[SUBJ]);
                        br= (in_clip ^ e0.bundle_above[CLIP]) ^ (in_subj ^ e0.bundle_above[SUBJ]);
                        bl= (in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])
                          ^ (in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ]);
                      }
                    else if( op == OperationType.GPC_UNION )
                      {
                        tr= ((in_clip!=0) || (in_subj!=0)) ? 1 : 0;
                        tl= (((in_clip ^ e1.bundle_above[CLIP])!=0) || ((in_subj ^ e1.bundle_above[SUBJ])!=0)) ? 1 : 0;
                        br= (((in_clip ^ e0.bundle_above[CLIP])!=0) || ((in_subj ^ e0.bundle_above[SUBJ])!=0)) ? 1 : 0;
                        bl= (((in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])!=0) ||
                             ((in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ])!=0)) ? 1 : 0;
                      }
                    else
                      {
                        throw new IllegalStateException("Unknown op type, "+op);
                      }
                    
                    int vclass = VertexType.getType( tr, tl, br, bl );
                    switch (vclass)
                      {
                      case VertexType.EMN:
                        e0.outp_above = out_poly.add_local_min(ix, iy);
                        e1.outp_above = e0.outp_above;
                        break;
                      case VertexType.ERI:
                        if (p != null)
                          {
                            p.add_right(ix, iy);
                            e1.outp_above= p;
                            e0.outp_above= null;
                          }
                        break;
                      case VertexType.ELI:
                        if (q != null)
                          {
                            q.add_left(ix, iy);
                            e0.outp_above= q;
                            e1.outp_above= null;
                          }
                        break;
                      case VertexType.EMX:
                        if ((p!=null) && (q!=null))
                          {
                            p.add_left( ix, iy);
                            out_poly.merge_right(p, q);
                            e0.outp_above= null;
                            e1.outp_above= null;
                          }
                        break;
                      case VertexType.IMN:
                        e0.outp_above = out_poly.add_local_min(ix, iy);
                        e1.outp_above= e0.outp_above;
                        break;
                      case VertexType.ILI:
                        if (p != null)
                          {
                            p.add_left(ix, iy);
                            e1.outp_above= p;
                            e0.outp_above= null;
                          }
                        break;
                      case VertexType.IRI:
                        if (q!=null)
                          {
                            q.add_right(ix, iy);
                            e0.outp_above= q;
                            e1.outp_above= null;
                          }
                        break;
                      case VertexType.IMX:
                        if ((p!=null) && (q!=null))
                          {
                            p.add_right(ix, iy);
                            out_poly.merge_left(p, q);
                            e0.outp_above= null;
                            e1.outp_above= null;
                          }
                        break;
                      case VertexType.IMM:
                        if ((p!=null) && (q!=null))
                          {
                            p.add_right(ix, iy);
                            out_poly.merge_left(p, q);
                            e0.outp_above = out_poly.add_local_min(ix, iy);
                            e1.outp_above= e0.outp_above;
                          }
                        break;
                      case VertexType.EMM:
                        if ((p!=null) && (q!=null))
                          {
                            p.add_left(ix, iy);
                            out_poly.merge_right(p, q);
                            e0.outp_above = out_poly.add_local_min(ix, iy);
                            e1.outp_above = e0.outp_above;
                          }
                        break;
                      default:
                        break;
                      } /* End of switch */
                  } /* End of contributing intersection conditional */                  
                
                /* Swap bundle sides in response to edge crossing */
                if (e0.bundle_above[CLIP]!=0)
                  e1.bside_clip = (e1.bside_clip==0)?1:0;
                if (e1.bundle_above[CLIP]!=0)
                  e0.bside_clip= (e0.bside_clip==0)?1:0;
                if (e0.bundle_above[SUBJ]!=0)
                  e1.bside_subj= (e1.bside_subj==0)?1:0;
                if (e1.bundle_above[SUBJ]!=0)
                  e0.bside_subj= (e0.bside_subj==0)?1:0;
                
                /* Swap e0 and e1 bundles in the AET */
                EdgeNode prev_edge = e0.prev;
                EdgeNode next_edge = e1.next;
                if (next_edge != null)
                  {
                    next_edge.prev = e0;
                  }
                
                if (e0.bstate_above == BundleState.BUNDLE_HEAD)
                  {
                    boolean search = true;
                    while (search)
                      {
                        prev_edge= prev_edge.prev;
                        if (prev_edge != null)
                          {
                            if (prev_edge.bstate_above != BundleState.BUNDLE_TAIL)
                              {
                                search= false;
                              }
                          }
                        else
                          {
                            search= false;
                          }
                      }
                  }
                if (prev_edge == null)
                  {
                    aet.top_node.prev = e1;
                    e1.next           = aet.top_node;
                    aet.top_node      = e0.next;
                  }
                else
                  {
                    prev_edge.next.prev = e1;
                    e1.next             = prev_edge.next;
                    prev_edge.next      = e0.next;
                  }
                e0.next.prev = prev_edge;
                e1.next.prev = e1;
                e0.next      = next_edge;
                if( DEBUG )
                  {
                    out_poly.print();
                  }
              } /* End of IT loop*/
            
            /* Prepare for next scanbeam */
            for ( EdgeNode edge = aet.top_node; (edge != null); edge = edge.next)
              {
                EdgeNode next_edge = edge.next;
                EdgeNode succ_edge = edge.succ;
                if ((edge.top_y == yt) && (succ_edge!=null))
                  {
                    /* Replace AET edge by its successor */
                    succ_edge.outp_below= edge.outp_above;
                    succ_edge.bstate_below= edge.bstate_above;
                    succ_edge.bundle_below_clip= edge.bundle_above[CLIP];
                    succ_edge.bundle_below_subj= edge.bundle_above[SUBJ];
                    EdgeNode prev_edge = edge.prev;
                    if ( prev_edge != null )
                      prev_edge.next = succ_edge;
                    else
                      aet.top_node = succ_edge;
                    if (next_edge != null)
                      next_edge.prev= succ_edge;
                    succ_edge.prev = prev_edge;
                    succ_edge.next = next_edge;
                  }
                else
                  {
                    /* Update this edge */
                    edge.outp_below= edge.outp_above;
                    edge.bstate_below= edge.bstate_above;
                    edge.bundle_below_clip= edge.bundle_above[CLIP];
                    edge.bundle_below_subj= edge.bundle_above[SUBJ];
                    edge.xb= edge.xt;
                  }
                edge.outp_above= null;
              }
          }
      } /* === END OF SCANBEAM PROCESSING ================================== */
    
    /* Generate result polygon from out_poly */
    result = out_poly.getResult(polyClass);
    
    return result;
  }
  
  /**
   * Clipper to output tristrips
   */
  private static PolygonNode clip( OperationType op, RPolygon subj, RPolygon clip )
  {
    PolygonNode tlist=null, tnn, tn;
    EdgeNode prev_edge, next_edge, edge, cf=null, succ_edge, e0, e1;
    VertexNode lt, ltn, rt, rtn;
    int cft=VertexType.LED;
    float []sbt;
    float xb, px, nx=0, yb, yt, dy, ix, iy;
    
    /* Test for trivial NULL result cases */
    if( (subj.isEmpty() && clip.isEmpty()) ||
        (subj.isEmpty() && ((op == OperationType.GPC_INT) || (op == OperationType.GPC_DIFF))) ||
        (clip.isEmpty() &&  (op == OperationType.GPC_INT)) )
      {
        return null;
      }
    
    /* Identify potentialy contributing contours */
    if( ((op == OperationType.GPC_INT) || (op == OperationType.GPC_DIFF)) && 
        !subj.isEmpty() && !clip.isEmpty() )
      {
        minimax_test(subj, clip, op);
      }
    
    /* Build LMT */
    LmtTable lmt_table = new LmtTable();
    ScanBeamTreeEntries sbte = new ScanBeamTreeEntries();
    EdgeTable s_heap = null ;
    EdgeTable c_heap = null ;
    if (!subj.isEmpty())
      {
        s_heap = build_lmt(lmt_table, sbte, subj, SUBJ, op);
      }
    if( DEBUG )
      {
        System.out.println("");
        System.out.println(" ------------ After build_lmt for subj ---------");
        lmt_table.print();
      }
    if (!clip.isEmpty())
      {
        c_heap = build_lmt(lmt_table, sbte, clip, CLIP, op);
      }
    if( DEBUG )
      {
        System.out.println("");
        System.out.println(" ------------ After build_lmt for clip ---------");
        lmt_table.print();
      }
    
    /* Return a NULL result if no contours contribute */
    if (lmt_table.top_node == null)
      {
        return null;
      }
    
    /* Build scanbeam table from scanbeam tree */
    sbt = sbte.build_sbt();
    
    int parity_clip = LEFT;
    int parity_subj = LEFT;
    
    /* Invert clip polygon for difference operation */
    if (op == OperationType.GPC_DIFF)
      {
        parity_clip= RIGHT;
      }
    
    if( DEBUG )
      {
        print_sbt(sbt);
      }
    
    LmtNode local_min = lmt_table.top_node ;
    
    AetTree aet = new AetTree();
    int scanbeam = 0 ;
    
    /* Process each scanbeam */
    while( scanbeam < sbt.length )
      {
        /* Set yb and yt to the bottom and top of the scanbeam */
        yb = sbt[scanbeam++];
        yt = 0.0F ;
        dy = 0.0F ;
        if( scanbeam < sbt.length )
          {
            yt = sbt[scanbeam];
            dy = yt - yb;
          }
        
        /* === SCANBEAM BOUNDARY PROCESSING ================================ */

        /* If LMT node corresponding to yb exists */
        if (local_min != null )
        {
          if (local_min.y == yb)
          {
            /* Add edges starting at this local minimum to the AET */
            for( edge = local_min.first_bound; (edge != null) ; edge= edge.next_bound)
            {
              add_edge_to_aet( aet, edge );
            }

            local_min = local_min.next;
          }
        }

        if( DEBUG )
        {
          aet.print();
        }
        /* Set dummy previous x value */
        px = -Float.MAX_VALUE ;

        /* Create bundles within AET */
        e0 = aet.top_node ;
        e1 = aet.top_node ;

        /* Set up bundle fields of first edge */
        aet.top_node.bundle_above[ aet.top_node.type ] = (aet.top_node.top_y != yb) ? 1 : 0;
        aet.top_node.bundle_above[ ((aet.top_node.type==0) ? 1 : 0) ] = 0;
        aet.top_node.bstate_above = BundleState.UNBUNDLED;

        for (next_edge= aet.top_node.next ; (next_edge != null); next_edge = next_edge.next)
        {
          int ne_type = next_edge.type ;
          int ne_type_opp = ((next_edge.type==0) ? 1 : 0); //next edge type opposite

          /* Set up bundle fields of next edge */
          next_edge.bundle_above[ ne_type     ]= (next_edge.top_y != yb) ? 1 : 0;
          next_edge.bundle_above[ ne_type_opp ] = 0 ;
          next_edge.bstate_above = BundleState.UNBUNDLED;

          /* Bundle edges above the scanbeam boundary if they coincide */
          if ( next_edge.bundle_above[ne_type] == 1 )
          {
            if (EQ(e0.xb, next_edge.xb) && EQ(e0.dx, next_edge.dx) && (e0.top_y != yb))
            {
              next_edge.bundle_above[ ne_type     ] ^= e0.bundle_above[ ne_type     ];
              next_edge.bundle_above[ ne_type_opp ]  = e0.bundle_above[ ne_type_opp ];
              next_edge.bstate_above = BundleState.BUNDLE_HEAD;
              e0.bundle_above[CLIP] = 0;
              e0.bundle_above[SUBJ] = 0;
              e0.bstate_above = BundleState.BUNDLE_TAIL;
            }
            e0 = next_edge;
          }
        }

        int horiz_clip = HState.NH;
        int horiz_subj = HState.NH;

        int exists_clip = 0 ;
        int exists_subj = 0 ;

        /* Process each edge at this scanbeam boundary */
        for (edge= aet.top_node ; (edge != null); edge = edge.next )
        {
          exists_clip = edge.bundle_above[CLIP] + (edge.bundle_below_clip << 1);
          exists_subj = edge.bundle_above[SUBJ] + (edge.bundle_below_subj << 1);

          if( (exists_clip != 0) || (exists_subj != 0) )
          {
            /* Set bundle side */
            edge.bside_clip = parity_clip;
            edge.bside_subj = parity_subj;

            boolean contributing = false ;
            int br=0, bl=0, tr=0, tl=0 ;
            /* Determine contributing status and quadrant occupancies */
            if( (op == OperationType.GPC_DIFF) || (op == OperationType.GPC_INT) )
            {
              contributing= ((exists_clip!=0) && ((parity_subj!=0) || (horiz_subj!=0))) ||
                ((exists_subj!=0) && ((parity_clip!=0) || (horiz_clip!=0))) ||
                ((exists_clip!=0) && (exists_subj!=0) && (parity_clip == parity_subj));
              br = ((parity_clip!=0) && (parity_subj!=0)) ? 1 : 0;
              bl = ( ((parity_clip ^ edge.bundle_above[CLIP])!=0) &&
                ((parity_subj ^ edge.bundle_above[SUBJ])!=0) ) ? 1 : 0;
              tr = ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0)) !=0) && 
                ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0)) !=0) ) ? 1 : 0;
              tl = (((parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)!=0) &&
                ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj)!=0))?1:0;
            }
            else if( op == OperationType.GPC_XOR )
            {
              contributing= (exists_clip!=0) || (exists_subj!=0);
              br= (parity_clip) ^ (parity_subj);
              bl= (parity_clip ^ edge.bundle_above[CLIP]) ^ (parity_subj ^ edge.bundle_above[SUBJ]);
              tr= (parity_clip ^ ((horiz_clip!=HState.NH)?1:0)) ^ (parity_subj ^ ((horiz_subj!=HState.NH)?1:0));
              tl= (parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)
                ^ (parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj);
            }
            else if( op == OperationType.GPC_UNION )
            {
              contributing= ((exists_clip!=0) && (!(parity_subj!=0) || (horiz_subj!=0))) ||
                ((exists_subj!=0) && (!(parity_clip!=0) || (horiz_clip!=0))) ||
                ((exists_clip!=0) && (exists_subj!=0) && (parity_clip == parity_subj));
              br= ((parity_clip!=0) || (parity_subj!=0))?1:0;
              bl= (((parity_clip ^ edge.bundle_above[CLIP])!=0) || ((parity_subj ^ edge.bundle_above[SUBJ])!=0))?1:0;
              tr= ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0))!=0) || 
                ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0))!=0) ) ?1:0;
              tl= ( ((parity_clip ^ ((horiz_clip!=HState.NH)?1:0) ^ edge.bundle_below_clip)!=0) ||
                ((parity_subj ^ ((horiz_subj!=HState.NH)?1:0) ^ edge.bundle_below_subj)!=0) ) ? 1:0;
            }
            else
            {
              throw new IllegalStateException("Unknown op");
            }

            /* Update parity */
            parity_clip ^= edge.bundle_above[CLIP];
            parity_subj ^= edge.bundle_above[SUBJ];

            /* Update horizontal state */
            if (exists_clip!=0)
            {
              horiz_clip = HState.next_h_state[horiz_clip][((exists_clip - 1) << 1) + parity_clip];
            }
            if( exists_subj!=0)
            {
              horiz_subj = HState.next_h_state[horiz_subj][((exists_subj - 1) << 1) + parity_subj];
            }

            if (contributing)
            {
              xb = edge.xb;


              int vclass = VertexType.getType( tr, tl, br, bl );
              switch (vclass)
              {
                case VertexType.EMN:
                tlist=new_tristrip(tlist, edge, xb, yb);
                cf= edge;
                break;
                case VertexType.ERI:
                edge.outp_above= cf.outp_above;
                if (xb != cf.xb)
                {
                  edge.outp_above.v_right=add_vertex(edge.outp_above.v_right,  xb,  yb);
                  edge.outp_above.active++;
                }
                cf= null;
                break;
                case VertexType.ELI:
                edge.outp_below.v_left=add_vertex(edge.outp_below.v_left,  xb,  yb);
                edge.outp_above.active++;
                edge.outp_above= null;
                cf= edge;
                break;
                case VertexType.EMX:
                if (xb != cf.xb)
                {
                  edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                  edge.outp_above.active++;
                }
                edge.outp_above = null;
                cf= null;
                break;
                case VertexType.IMN:
                if (cft == VertexType.LED)
                {
                  if (cf.bot_y != yb)
                  {
                    cf.outp_below.v_left=add_vertex(cf.outp_below.v_left,  cf.xb,  yb);
                    cf.outp_below.active++;
                  }
                  tlist=new_tristrip(tlist, cf, cf.xb, yb);
                }
                edge.outp_above= cf.outp_above;
                edge.outp_above.v_right=add_vertex(edge.outp_above.v_right,  xb,  yb);
                edge.outp_above.active++;
                break;
                case VertexType.ILI:
                tlist=new_tristrip(tlist, edge, xb, yb);
                cf= edge;
                cft= VertexType.ILI;
                break;
                case VertexType.IRI:
                if (cft == VertexType.LED)
                {
                  if (cf.bot_y != yb)
                  {
                    cf.outp_below.v_left=add_vertex(cf.outp_below.v_left,  cf.xb,  yb);
                    cf.outp_below.active++;
                  }
                  tlist=new_tristrip(tlist, cf, cf.xb, yb);
                }
                edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                edge.outp_above.active++;
                edge.outp_above= null;
                break;
                case VertexType.IMX:
                edge.outp_below.v_left=add_vertex(edge.outp_below.v_left,  xb,  yb);
                edge.outp_above.active++;
                edge.outp_above= null;
                cft= VertexType.IMX;
                break;
                case VertexType.IMM:
                edge.outp_below.v_left=add_vertex(edge.outp_below.v_left,  xb,  yb);
                edge.outp_above.active++;
                edge.outp_above= cf.outp_above;
                if (xb != cf.xb)
                {
                  cf.outp_above.v_right=add_vertex(cf.outp_above.v_right,  xb,  yb);
                  cf.outp_above.active++;
                }
                cf= edge;
                break;
                case VertexType.EMM:
                edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                edge.outp_above.active++;
                edge.outp_above= null;
                tlist=new_tristrip(tlist, edge, xb, yb);
                cf= edge;
                break;
                case VertexType.LED:
                if (edge.bot_y == yb) {
                  edge.outp_below.v_left=add_vertex(edge.outp_below.v_left,  xb,  yb);
                  edge.outp_above.active++;
                }
                edge.outp_above= edge.outp_below;
                cf= edge;
                cft= VertexType.LED;
                break;
                case VertexType.RED:
                edge.outp_above= cf.outp_above;
                if (cft == VertexType.LED)
                {
                  if (cf.bot_y == yb)
                  {
                    edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                    edge.outp_above.active++;
                  }
                  else
                  {
                    if (edge.bot_y == yb)
                    {
                      cf.outp_below.v_left=add_vertex(cf.outp_below.v_left,  cf.xb,  yb);
                      cf.outp_below.active++;
                      edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                      edge.outp_above.active++;
                    }
                  }
                }
                else
                {
                  edge.outp_below.v_right=add_vertex(edge.outp_below.v_right,  xb,  yb);
                  edge.outp_above.active++;
                  edge.outp_above.v_right=add_vertex(edge.outp_above.v_right,  xb,  yb);
                  edge.outp_above.active++;
                }
                cf= null;
                break;
                default:
                break;
              } /* End of switch */
            } /* End of contributing conditional */
          } /* End of edge exists conditional */
        } /* End of AET loop */

        /* Delete terminating edges from the AET, otherwise compute xt */
        for (edge = aet.top_node ; (edge != null); edge = edge.next)
        {
          if (edge.top_y == yb)
          {
            prev_edge = edge.prev;
            next_edge= edge.next;

            if (prev_edge != null)
              prev_edge.next = next_edge;
            else
              aet.top_node = next_edge;

            if (next_edge != null )
              next_edge.prev = prev_edge;

            /* Copy bundle head state to the adjacent tail edge if required */
            if ((edge.bstate_below == BundleState.BUNDLE_HEAD) && (prev_edge!=null))
            {
              if (prev_edge.bstate_below == BundleState.BUNDLE_TAIL)
              {
                prev_edge.outp_below= edge.outp_below;
                prev_edge.bstate_below= BundleState.UNBUNDLED;
                if ( prev_edge.prev != null)
                {
                  if (prev_edge.prev.bstate_below == BundleState.BUNDLE_TAIL)
                  {
                    prev_edge.bstate_below = BundleState.BUNDLE_HEAD;
                  }
                }
              }
            }
          }
          else
          {
            if (edge.top_y == yt)
              edge.xt= edge.top_x;
            else
              edge.xt= edge.bot_x + edge.dx * (yt - edge.bot_y);
          }
        }

        if (scanbeam < sbte.sbt_entries )
        {
          /* === SCANBEAM INTERIOR PROCESSING ============================== */
          /* Build intersection table for the current scanbeam */
          ItNodeTable it_table = new ItNodeTable();
          it_table.build_intersection_table(aet, dy);

          /* Process each node in the intersection table */
          for (ItNode intersect = it_table.top_node ; (intersect != null); intersect = intersect.next)
          {
            e0= intersect.ie0;
            e1= intersect.ie1;

            /* Only generate output for contributing intersections */
            if ( ((e0.bundle_above[CLIP]!=0) || (e0.bundle_above[SUBJ]!=0)) &&
              ((e1.bundle_above[CLIP]!=0) || (e1.bundle_above[SUBJ]!=0)))
            {
              PolygonNode p = e0.outp_above;
              PolygonNode q = e1.outp_above;
              ix = intersect.point.x;
              iy = intersect.point.y + yb;

              int in_clip = ( ( (e0.bundle_above[CLIP]!=0) && !(e0.bside_clip!=0)) ||
                ( (e1.bundle_above[CLIP]!=0) &&  (e1.bside_clip!=0)) ||
                (!(e0.bundle_above[CLIP]!=0) && !(e1.bundle_above[CLIP]!=0) &&
                (e0.bside_clip!=0) && (e1.bside_clip!=0) ) ) ? 1 : 0;

              int in_subj = ( ( (e0.bundle_above[SUBJ]!=0) && !(e0.bside_subj!=0)) ||
                ( (e1.bundle_above[SUBJ]!=0) &&  (e1.bside_subj!=0)) ||
                (!(e0.bundle_above[SUBJ]!=0) && !(e1.bundle_above[SUBJ]!=0) &&
                (e0.bside_subj!=0) && (e1.bside_subj!=0) ) ) ? 1 : 0;

              int tr=0, tl=0, br=0, bl=0 ;
              /* Determine quadrant occupancies */
              if( (op == OperationType.GPC_DIFF) || (op == OperationType.GPC_INT) )
              {
                tr= ((in_clip!=0) && (in_subj!=0)) ? 1 : 0;
                tl= (((in_clip ^ e1.bundle_above[CLIP])!=0) && ((in_subj ^ e1.bundle_above[SUBJ])!=0))?1:0;
                br= (((in_clip ^ e0.bundle_above[CLIP])!=0) && ((in_subj ^ e0.bundle_above[SUBJ])!=0))?1:0;
                bl= (((in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])!=0) &&
                  ((in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ])!=0) ) ? 1:0;
              }
              else if( op == OperationType.GPC_XOR )
              {
                tr= (in_clip)^ (in_subj);
                tl= (in_clip ^ e1.bundle_above[CLIP]) ^ (in_subj ^ e1.bundle_above[SUBJ]);
                br= (in_clip ^ e0.bundle_above[CLIP]) ^ (in_subj ^ e0.bundle_above[SUBJ]);
                bl= (in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])
                  ^ (in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ]);
              }
              else if( op == OperationType.GPC_UNION )
              {
                tr= ((in_clip!=0) || (in_subj!=0)) ? 1 : 0;
                tl= (((in_clip ^ e1.bundle_above[CLIP])!=0) || ((in_subj ^ e1.bundle_above[SUBJ])!=0)) ? 1 : 0;
                br= (((in_clip ^ e0.bundle_above[CLIP])!=0) || ((in_subj ^ e0.bundle_above[SUBJ])!=0)) ? 1 : 0;
                bl= (((in_clip ^ e1.bundle_above[CLIP] ^ e0.bundle_above[CLIP])!=0) ||
                  ((in_subj ^ e1.bundle_above[SUBJ] ^ e0.bundle_above[SUBJ])!=0)) ? 1 : 0;
              }
              else
              {
                throw new IllegalStateException("Unknown op type, "+op);
              }

              next_edge = e1.next;
              prev_edge = e0.prev;

              int vclass = VertexType.getType( tr, tl, br, bl );
              switch (vclass)
              {
                case VertexType.EMN:
                tlist=new_tristrip(tlist, e1, ix, iy);
                e1.outp_above = e0.outp_above;
                break;
                case VertexType.ERI:
                if (p != null)
                {
                  px = P_EDGE(prev_edge, e0, ABOVE, px, iy);
                  prev_edge.outp_above.v_left=add_vertex(prev_edge.outp_above.v_left,  px,  iy);
                  prev_edge.outp_above.active++;
                  e0.outp_above.v_right=add_vertex(e0.outp_above.v_right,  ix,  iy);
                  e0.outp_above.active++;
                  e1.outp_above= e0.outp_above;
                  e0.outp_above= null;
                }
                break;
                case VertexType.ELI:
                if (q != null)
                {
                  nx = N_EDGE(next_edge, e1, ABOVE, nx, iy);
                  e1.outp_above.v_left=add_vertex(e1.outp_above.v_left,  ix,  iy);
                  e1.outp_above.active++;
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                  e0.outp_above= e1.outp_above;
                  e1.outp_above= null;
                }
                break;
                case VertexType.EMX:
                if ((p!=null) && (q!=null))
                {
                  e0.outp_above.v_left=add_vertex(e0.outp_above.v_left,  ix,  iy);
                  e0.outp_above.active++;
                  e0.outp_above= null;
                  e1.outp_above= null;
                }
                break;
                case VertexType.IMN:
                px = P_EDGE(prev_edge, e0, ABOVE, px, iy);
                prev_edge.outp_above.v_left=add_vertex(prev_edge.outp_above.v_left,  px,  iy);
                prev_edge.outp_above.active++;
                nx = N_EDGE(next_edge, e1, ABOVE, nx, iy);
                next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                next_edge.outp_above.active++;
                tlist=new_tristrip(tlist, prev_edge, px, iy); 
                e1.outp_above= prev_edge.outp_above;
                e1.outp_above.v_right=add_vertex(e1.outp_above.v_right,  ix,  iy);
                e1.outp_above.active++;
                tlist=new_tristrip(tlist, e0, ix, iy);
                next_edge.outp_above= e0.outp_above;
                next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                next_edge.outp_above.active++;
                break;
                case VertexType.ILI:
                if (p != null)
                {
                  e0.outp_above.v_left=add_vertex(e0.outp_above.v_left,  ix,  iy);
                  e0.outp_above.active++;
                  nx = N_EDGE(next_edge, e1, ABOVE, nx, iy);
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                  e1.outp_above= e0.outp_above;
                  e0.outp_above= null;
                }
                break;
                case VertexType.IRI:
                if (q!=null)
                {
                  e1.outp_above.v_right=add_vertex(e1.outp_above.v_right,  ix,  iy);
                  e1.outp_above.active++;
                  px = P_EDGE(prev_edge, e0, ABOVE, px, iy);
                  prev_edge.outp_above.v_left=add_vertex(prev_edge.outp_above.v_left,  px,  iy);
                  prev_edge.outp_above.active++;
                  e0.outp_above= e1.outp_above;
                  e1.outp_above= null;
                }
                break;
                case VertexType.IMX:
                if ((p!=null) && (q!=null))
                {
                  e0.outp_above.v_right=add_vertex(e0.outp_above.v_right,  ix,  iy);
                  e0.outp_above.active++;
                  e1.outp_above.v_left=add_vertex(e1.outp_above.v_left,  ix,  iy);
                  e1.outp_above.active++;
                  e0.outp_above= null;
                  e1.outp_above= null;
                  px = P_EDGE(prev_edge, e0, ABOVE, px, iy);
                  prev_edge.outp_above.v_left=add_vertex(prev_edge.outp_above.v_left,  px,  iy);
                  prev_edge.outp_above.active++;
                  tlist=new_tristrip(tlist, prev_edge, px, iy);
                  nx = N_EDGE(next_edge, e1, ABOVE, nx, iy);
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                  next_edge.outp_above= prev_edge.outp_above;
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                }
                break;
                case VertexType.IMM:
                if ((p!=null) && (q!=null))
                {
                  e0.outp_above.v_right=add_vertex(e0.outp_above.v_right,  ix,  iy);
                  e0.outp_above.active++;
                  e1.outp_above.v_left=add_vertex(e1.outp_above.v_left,  ix,  iy);
                  e1.outp_above.active++;
                  px = P_EDGE(prev_edge, e0, ABOVE, px, iy);
                  prev_edge.outp_above.v_left=add_vertex(prev_edge.outp_above.v_left,  px,  iy);
                  prev_edge.outp_above.active++;
                  tlist=new_tristrip(tlist, prev_edge, px, iy);
                  nx = N_EDGE(next_edge, e1, ABOVE, nx, iy);
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                  e1.outp_above= prev_edge.outp_above;
                  e1.outp_above.v_right=add_vertex(e1.outp_above.v_right,  ix,  iy);
                  e1.outp_above.active++;
                  tlist=new_tristrip(tlist, e0, ix, iy);
                  next_edge.outp_above= e0.outp_above;
                  next_edge.outp_above.v_right=add_vertex(next_edge.outp_above.v_right,  nx,  iy);
                  next_edge.outp_above.active++;
                }
                break;
                case VertexType.EMM:
                if ((p!=null) && (q!=null))
                {
                  e0.outp_above.v_left=add_vertex(e0.outp_above.v_left,  ix,  iy);
                  e0.outp_above.active++;
                  tlist=new_tristrip(tlist, e1, ix, iy);
                  e1.outp_above = e0.outp_above;
                }
                break;
                default:
                break;
              } /* End of switch */
            } /* End of contributing intersection conditional */                  

            /* Swap bundle sides in response to edge crossing */
            if (e0.bundle_above[CLIP]!=0)
              e1.bside_clip = (e1.bside_clip==0)?1:0;
            if (e1.bundle_above[CLIP]!=0)
              e0.bside_clip= (e0.bside_clip==0)?1:0;
            if (e0.bundle_above[SUBJ]!=0)
              e1.bside_subj= (e1.bside_subj==0)?1:0;
            if (e1.bundle_above[SUBJ]!=0)
              e0.bside_subj= (e0.bside_subj==0)?1:0;

            /* Swap e0 and e1 bundles in the AET */
            prev_edge = e0.prev;
            next_edge = e1.next;
            if (next_edge != null)
            {
              next_edge.prev = e0;
            }

            if (e0.bstate_above == BundleState.BUNDLE_HEAD)
            {
              boolean search = true;
              while (search)
              {
                prev_edge= prev_edge.prev;
                if (prev_edge != null)
                {
                  if (prev_edge.bundle_above[CLIP]!=0
                    || prev_edge.bundle_above[SUBJ]!=0
                    || (prev_edge.bstate_above == BundleState.BUNDLE_HEAD))
                  {
                    search= false;
                  }
                }
                else
                {
                  search= false;
                }
              }
            }
            if (prev_edge == null)
            {
              e1.next           = aet.top_node;
              aet.top_node      = e0.next;
            }
            else
            {
              e1.next             = prev_edge.next;
              prev_edge.next      = e0.next;
            }
            e0.next.prev = prev_edge;
            e1.next.prev = e1;
            e0.next      = next_edge;

          } /* End of IT loop*/

          /* Prepare for next scanbeam */
          for ( edge = aet.top_node; (edge != null); edge = edge.next)
          {
            next_edge = edge.next;
            succ_edge = edge.succ;
            if ((edge.top_y == yt) && (succ_edge!=null))
            {
              /* Replace AET edge by its successor */
              succ_edge.outp_below= edge.outp_above;
              succ_edge.bstate_below= edge.bstate_above;
              succ_edge.bundle_below_clip= edge.bundle_above[CLIP];
              succ_edge.bundle_below_subj= edge.bundle_above[SUBJ];
              prev_edge = edge.prev;
              if ( prev_edge != null )
                prev_edge.next = succ_edge;
              else
                aet.top_node = succ_edge;
              if (next_edge != null)
                next_edge.prev= succ_edge;
              succ_edge.prev = prev_edge;
              succ_edge.next = next_edge;
            }
            else
            {
              /* Update this edge */
              edge.outp_below= edge.outp_above;
              edge.bstate_below= edge.bstate_above;
              edge.bundle_below_clip= edge.bundle_above[CLIP];
              edge.bundle_below_subj= edge.bundle_above[SUBJ];
              edge.xb= edge.xt;
            }
            edge.outp_above= null;
          }
        }
      }
      /* === END OF SCANBEAM PROCESSING ================================== */

      return tlist;
  }
  
  public static RMesh polygonToMesh(RPolygon s)
  {
    RPolygon empty = new RPolygon();
    RPolygon s_clean = s.removeOpenContours();
    PolygonNode very_clean = clip(OperationType.GPC_UNION, s_clean, empty);
    if(very_clean == null) {
      return new RMesh();
    } else {
      return polygonNodeToMesh(very_clean);
    }
  }

  /*
  public static RMesh polygonToTriMesh(RPolygon s)
  {
    RPolygon empty = new RPolygon();
    RPolygon s_clean = s.removeOpenContours();
    PolygonNode very_clean = clip(OperationType.GPC_UNION, s_clean, empty);
    return polygonNodeToTriMesh(very_clean);
  }
  
  private static RTriMesh polygonNodeToTriMesh(PolygonNode tlist) {
      
  }
  */
  
  private static RMesh polygonNodeToMesh(PolygonNode tlist)
  {
      PolygonNode tn,tnn;
      VertexNode rt,lt, ltn,rtn;
      
      /* Generate result tristrip from tlist */
      RMesh result = new RMesh();
      if (count_tristrips(tlist) > 0)
        {
        	int s, v;

      	s= 0;
      	for (tn= tlist; tn!=null; tn= tnn)
            {
              tnn= tn.next;

              if (tn.active > 2)
                {
                  /* Valid tristrip: copy the vertices and free the heap */
                  RStrip strip = new RStrip();
                  v= 0;
                  if (INVERT_TRISTRIPS == true)
                    {
                      lt= tn.v_right;
                      rt= tn.v_left;
                    }
                  else
                    {
                      lt= tn.v_left;
                      rt= tn.v_right;
                    }
                  while (lt!=null || rt!=null)
                    {
                      if (lt!=null)
                        {
                          ltn= lt.next;
                          strip.add(lt.x,lt.y);
                          v++;
                          lt= ltn;
                        }
                      if (rt!=null)
                        {
                          rtn= rt.next;
                          strip.add(rt.x,rt.y);
                          v++;
                          rt= rtn;
                        }
                    }
                  result.addStrip(strip);
                  s++;
                }
              else
                {
                  /* Invalid tristrip: just free the heap */
                  for (lt= tn.v_left; lt!=null; lt= ltn)
                    {
                      ltn= lt.next;
                    }
                  for (rt= tn.v_right; rt!=null; rt= rtn)
                    {
                      rtn= rt.next;
                    }
                }
            }
        }
      return result ;
  }
  
  private static boolean EQ(float a, float b)
  {
    return (Math.abs(a - b) <= GPC_EPSILON);
  }
  
  private static int PREV_INDEX(int i, int n)
  {
    return ((i - 1 + n) % n);
  }
  
  private static int NEXT_INDEX(int i, int n)
  {
    return ((i + 1) % n);
  }
  
  private static boolean OPTIMAL( RPoint[] p, int i )
  {
    return p[PREV_INDEX(i, p.length)].y != p[i].y
        || p[NEXT_INDEX(i, p.length)].y != p[i].y;
  }
    
  private static float P_EDGE( EdgeNode d, EdgeNode e, int p, float i, float j)
  {
    d= e;
    do
      {
      	d= d.prev;
      }
    while(d.outp_above == null);
    return d.bot_x + d.dx*(j-d.bot_y);
  }
  
  private static float N_EDGE( EdgeNode d, EdgeNode e, int p, float i, float j)
  {
    d= e;
    do
      {
      	d= d.next;
      }
    while(d.outp_above == null);
    return d.bot_x + d.dx*(j-d.bot_y);
  }
  
  private static RRectangle[] create_contour_bboxes( RPolygon p )
  {
    RRectangle[] box = new RRectangle[p.getNumInnerPoly()];
    
    /* Construct contour bounding boxes */
    for ( int c= 0; c < p.getNumInnerPoly(); c++)
      {
        RPolygon inner_poly = p.getInnerPoly(c);
        box[c] = inner_poly.getBBox();
      }
    return box;  
  }
  
  private static void minimax_test( RPolygon subj, RPolygon clip, OperationType op )
  {
    RRectangle[] s_bbox = create_contour_bboxes(subj);
    RRectangle[] c_bbox = create_contour_bboxes(clip);
    
    int subj_num_poly = subj.getNumInnerPoly();
    int clip_num_poly = clip.getNumInnerPoly();
    boolean[][] o_table = new boolean[subj_num_poly][clip_num_poly];
    
    /* Check all subject contour bounding boxes against clip boxes */
    for( int s = 0; s < subj_num_poly; s++ )
      {
        for( int c= 0; c < clip_num_poly ; c++ )
          {
            o_table[s][c] =
              (!((s_bbox[s].getMaxX() < c_bbox[c].getMinX()) ||
                 (s_bbox[s].getMinX() > c_bbox[c].getMaxX()))) &&
              (!((s_bbox[s].getMaxY() < c_bbox[c].getMinY()) ||
                 (s_bbox[s].getMinY() > c_bbox[c].getMaxY())));
          }
      }
    
    /* For each clip contour, search for any subject contour overlaps */
    for( int c = 0; c < clip_num_poly; c++ )
      {
        boolean overlap = false;
        for( int s = 0; !overlap && (s < subj_num_poly) ; s++)
          {
            overlap = o_table[s][c];
          }
        if (!overlap)
          {
            clip.setContributing( c, false ); // Flag non contributing status
          }
      }  
    
    if (op == OperationType.GPC_INT)
      {  
        /* For each subject contour, search for any clip contour overlaps */
        for ( int s= 0; s < subj_num_poly; s++)
          {
            boolean overlap = false;
            for ( int c= 0; !overlap && (c < clip_num_poly); c++)
              {
                overlap = o_table[s][c];
              }
            if (!overlap)
              {
                subj.setContributing( s, false ); // Flag non contributing status
              }
          }  
      }
  }
  
  private static LmtNode bound_list( LmtTable lmt_table, float y )
  {
    if( lmt_table.top_node == null )
      {
        lmt_table.top_node = new LmtNode(y);
        return lmt_table.top_node ;
      }
    else
      {
        LmtNode prev = null ;
        LmtNode node = lmt_table.top_node ;
        boolean done = false ;
        while( !done )
          {
            if( y < node.y )
              {
                /* Insert a new LMT node before the current node */
                LmtNode existing_node = node ;
                node = new LmtNode(y);
                node.next = existing_node ;
                if( prev == null )
                  {
                    lmt_table.top_node = node ;
                  }
                else
                  {
                    prev.next = node ;
                  }
                //               if( existing_node == lmt_table.top_node )
                //               {
                //                  lmt_table.top_node = node ;
                //               }
                done = true ;
              }
            else if ( y > node.y )
              {
                /* Head further up the LMT */
                if( node.next == null )
                  {
                    node.next = new LmtNode(y);
                    node = node.next ;
                    done = true ;
                  }
                else
                  {
                    prev = node ;
                    node = node.next ;
                  }
              }
            else
              {
                /* Use this existing LMT node */
                done = true ;
              }
          }
        return node ;
      }
  }
  
  private static void insert_bound( LmtNode lmt_node, EdgeNode e)
  {
    if( lmt_node.first_bound == null )
      {
        /* Link node e to the tail of the list */
        lmt_node.first_bound = e ;
      }
    else
      {
        boolean done = false ;
        EdgeNode prev_bound = null ;
        EdgeNode current_bound = lmt_node.first_bound ;
        while( !done )
          {
            /* Do primary sort on the x field */
            if (e.bot_x <  current_bound.bot_x)
              {
                /* Insert a new node mid-list */
                if( prev_bound == null )
                  {
                    lmt_node.first_bound = e ;
                  }
                else
                  {
                    prev_bound.next_bound = e ;
                  }
                e.next_bound = current_bound ;
                
                //               EdgeNode existing_bound = current_bound ;
                //               current_bound = e ;
                //               current_bound.next_bound = existing_bound ;
                //               if( lmt_node.first_bound == existing_bound )
                //               {
                //                  lmt_node.first_bound = current_bound ;
                //               }
                done = true ;
              }
            else if (e.bot_x == current_bound.bot_x)
              {
                /* Do secondary sort on the dx field */
                if (e.dx < current_bound.dx)
                  {
                    /* Insert a new node mid-list */
                    if( prev_bound == null )
                      {
                        lmt_node.first_bound = e ;
                      }
                    else
                      {
                        prev_bound.next_bound = e ;
                      }
                    e.next_bound = current_bound ;
                    //                  EdgeNode existing_bound = current_bound ;
                    //                  current_bound = e ;
                    //                  current_bound.next_bound = existing_bound ;
                    //                  if( lmt_node.first_bound == existing_bound )
                    //                  {
                    //                     lmt_node.first_bound = current_bound ;
                    //                  }
                    done = true ;
                  }
                else
                  {
                    /* Head further down the list */
                    if( current_bound.next_bound == null )
                      {
                        current_bound.next_bound = e ;
                        done = true ;
                      }
                    else
                      {
                        prev_bound = current_bound ;
                        current_bound = current_bound.next_bound ;
                      }
                  }
              }
            else
              {
                /* Head further down the list */
                if( current_bound.next_bound == null )
                  {
                    current_bound.next_bound = e ;
                    done = true ;
                  }
                else
                  {
                    prev_bound = current_bound ;
                    current_bound = current_bound.next_bound ;
                  }
              }
          }
      }
  }
  
  private static void add_edge_to_aet( AetTree aet , EdgeNode edge )
  {
    if ( aet.top_node == null )
      {
        /* Append edge onto the tail end of the AET */
        aet.top_node = edge;
        edge.prev = null ;
        edge.next= null;
      }
    else
      {
        EdgeNode current_edge = aet.top_node ;
        EdgeNode prev = null ;
        boolean done = false ;
        while( !done )
          {
            /* Do primary sort on the xb field */
            if (edge.xb < current_edge.xb)
              {
                /* Insert edge here (before the AET edge) */
                edge.prev= prev;
                edge.next= current_edge ;
                current_edge.prev = edge ;
                if( prev == null )
                  {
                    aet.top_node = edge ;
                  }
                else
                  {
                    prev.next = edge ;
                  }
                //               if( current_edge == aet.top_node )
                //               {
                //                  aet.top_node = edge ;
                //               }
                //               current_edge = edge ;
                done = true;
              }
            else if (edge.xb == current_edge.xb)
              {
                /* Do secondary sort on the dx field */
                if (edge.dx < current_edge.dx)
                  {
                    /* Insert edge here (before the AET edge) */
                    edge.prev= prev;
                    edge.next= current_edge ;
                    current_edge.prev = edge ;
                    if( prev == null )
                      {
                        aet.top_node = edge ;
                      }
                    else
                      {
                        prev.next = edge ;
                      }
                    //                  if( current_edge == aet.top_node )
                    //                  {
                    //                     aet.top_node = edge ;
                    //                  }
                    //                  current_edge = edge ;
                    done = true;
                  }
                else
                  {
                    /* Head further into the AET */
                    prev = current_edge ;
                    if( current_edge.next == null )
                      {
                        current_edge.next = edge ;
                        edge.prev = current_edge ;
                        edge.next = null ;
                        done = true ;
                      }
                    else
                      {
                        current_edge = current_edge.next ;
                      }
                  }
              }
            else
              {
                /* Head further into the AET */
                prev = current_edge ;
                if( current_edge.next == null )
                  {
                    current_edge.next = edge ;
                    edge.prev = current_edge ;
                    edge.next = null ;
                    done = true ;
                  }
                else
                  {
                    current_edge = current_edge.next ;
                  }
              }
          }
      }
  }
  
  private static void add_to_sbtree( ScanBeamTreeEntries sbte, float y )
  {
    if( sbte.sb_tree == null )
      {
        /* Add a new tree node here */
        sbte.sb_tree = new ScanBeamTree( y );
        sbte.sbt_entries++ ;
        return ;
      }
    ScanBeamTree tree_node = sbte.sb_tree ;
    boolean done = false ;
    while( !done )
      {
        if ( tree_node.y > y)
          {
            if( tree_node.less == null )
              {
                tree_node.less = new ScanBeamTree(y);
                sbte.sbt_entries++ ;
                done = true ;
              }
            else
              {
                tree_node = tree_node.less ;
              }
          }
        else if ( tree_node.y < y)
          {
            if( tree_node.more == null )
              {
                tree_node.more = new ScanBeamTree(y);
                sbte.sbt_entries++ ;
                done = true ;
              }
            else
              {
                tree_node = tree_node.more ;
              }
          }
        else
          {
            done = true ;
          }
      }
  }
  
  private static EdgeTable build_lmt( LmtTable lmt_table, 
                                      ScanBeamTreeEntries sbte,
                                      RPolygon p, 
                                      int type, //poly type SUBJ/CLIP
                                      OperationType op)
  {
    /* Create the entire input polygon edge table in one go */
    EdgeTable edge_table = new EdgeTable(0);
    
    for ( int c= 0; c < p.getNumInnerPoly(); c++)
      {
        RPolygon ip = p.getInnerPoly(c);
        RPoint[] ipp = ip.contours[0].points;
        if( !ip.isContributing(0) )
          {
            /* Ignore the non-contributing contour */
            ip.setContributing(0, true);
          }
        else
          {
            /* Perform contour optimisation */
            int num_vertices= 0;
            int numPoints = ip.getNumPoints();
            int e_index = 0 ;
            edge_table = new EdgeTable(numPoints);
            for ( int i= 0; i < numPoints; i++)
              {
                if( OPTIMAL(ipp, i) )
                  {
                    float x = ipp[i].x;
                    float y = ipp[i].y;
                    edge_table.addNode( x, y );
                    
                    /* Record vertex in the scanbeam table */
                    add_to_sbtree( sbte, ip.getY(i) );
                    
                    num_vertices++;
                  }
              }
            
            /* Do the contour forward pass */
            for ( int min= 0; min < num_vertices; min++)
              {
                /* If a forward local minimum... */
                if( edge_table.FWD_MIN( min ) )
                  {
                    /* Search for the next local maximum... */
                    int num_edges = 1;
                    int max = NEXT_INDEX( min, num_vertices );
                    while( edge_table.NOT_FMAX( max ) )
                      {
                        num_edges++;
                        max = NEXT_INDEX( max, num_vertices );
                      }
                    
                    /* Build the next edge list */
                    int v = min;
                    EdgeNode e = edge_table.getNode( e_index );
                    e.bstate_below = BundleState.UNBUNDLED;
                    e.bundle_below_clip = 0;
                    e.bundle_below_subj = 0;
                    
                    for ( int i= 0; i < num_edges; i++)
                      {
                        EdgeNode ei = edge_table.getNode( e_index+i );
                        EdgeNode ev = edge_table.getNode( v );
                        
                        ei.xb    = ev.vertex_x;
                        ei.bot_x = ev.vertex_x;
                        ei.bot_y = ev.vertex_y;
                        
                        v = NEXT_INDEX(v, num_vertices);
                        ev = edge_table.getNode( v );
                        
                        ei.top_x= ev.vertex_x;
                        ei.top_y= ev.vertex_y;
                        ei.dx= (ev.vertex_x - ei.bot_x) / (ei.top_y - ei.bot_y);
                        ei.type = type;
                        ei.outp_above = null ;
                        ei.outp_below = null;
                        ei.next = null;
                        ei.prev = null;
                        ei.succ = ((num_edges > 1) && (i < (num_edges - 1))) ? edge_table.getNode(e_index+i+1) : null;
                        ei.pred = ((num_edges > 1) && (i > 0)) ? edge_table.getNode(e_index+i-1) : null ;
                        ei.next_bound = null ;
                        ei.bside_clip = (op == OperationType.GPC_DIFF) ? RIGHT : LEFT;
                        ei.bside_subj = LEFT ;
                      }
                    insert_bound( bound_list(lmt_table, edge_table.getNode(min).vertex_y), e);
                    if( DEBUG )
                      {
                        System.out.println("fwd");
                        lmt_table.print();
                      }
                    e_index += num_edges;
                  }
              }
            
            /* Do the contour reverse pass */
            for ( int min= 0; min < num_vertices; min++)
              {
                /* If a reverse local minimum... */
                if ( edge_table.REV_MIN( min ) )
                  {
                    /* Search for the previous local maximum... */
                    int num_edges= 1;
                    int max = PREV_INDEX(min, num_vertices);
                    while( edge_table.NOT_RMAX( max ) )
                      {
                        num_edges++;
                        max = PREV_INDEX(max, num_vertices);
                      }
                    
                    /* Build the previous edge list */
                    int v = min;
                    EdgeNode e = edge_table.getNode( e_index );
                    e.bstate_below = BundleState.UNBUNDLED;
                    e.bundle_below_clip = 0;
                    e.bundle_below_subj = 0;
                    
                    for (int i= 0; i < num_edges; i++)
                      {
                        EdgeNode ei = edge_table.getNode( e_index+i );
                        EdgeNode ev = edge_table.getNode( v );
                        
                        ei.xb    = ev.vertex_x;
                        ei.bot_x = ev.vertex_x;
                        ei.bot_y = ev.vertex_y;
                        
                        v= PREV_INDEX(v, num_vertices);
                        ev = edge_table.getNode( v );
                        
                        ei.top_x = ev.vertex_x;
                        ei.top_y = ev.vertex_y;
                        ei.dx = (ev.vertex_x - ei.bot_x) / (ei.top_y - ei.bot_y);
                        ei.type = type;
                        ei.outp_above = null;
                        ei.outp_below = null;
                        ei.next = null ;
                        ei.prev = null;
                        ei.succ = ((num_edges > 1) && (i < (num_edges - 1))) ? edge_table.getNode(e_index+i+1) : null;
                        ei.pred = ((num_edges > 1) && (i > 0)) ? edge_table.getNode(e_index+i-1) : null ;
                        ei.next_bound = null ;
                        ei.bside_clip = (op == OperationType.GPC_DIFF) ? RIGHT : LEFT;
                        ei.bside_subj = LEFT;
                      }
                    insert_bound( bound_list(lmt_table, edge_table.getNode(min).vertex_y), e);
                    if( DEBUG )
                      {
                        System.out.println("rev");
                        lmt_table.print();
                      }
                    e_index+= num_edges;
                  }
              }
          }
      }
    return edge_table;
  }
  
  private static StNode add_st_edge( StNode st, ItNodeTable it, EdgeNode edge, float dy)
  {
    if (st == null)
      {
        /* Append edge onto the tail end of the ST */
        st = new StNode( edge, null );
      }
    else
      {
        float den= (st.xt - st.xb) - (edge.xt - edge.xb);
        
        /* If new edge and ST edge don't cross */
        if( (edge.xt >= st.xt) || (edge.dx == st.dx) || (Math.abs(den) <= GPC_EPSILON))
          {
            /* No intersection - insert edge here (before the ST edge) */
            StNode existing_node = st;
            st = new StNode( edge, existing_node );
          }
        else
          {
            /* Compute intersection between new edge and ST edge */
            float r= (edge.xb - st.xb) / den;
            float x= st.xb + r * (st.xt - st.xb);
            float y= r * dy;
            
            /* Insert the edge pointers and the intersection point in the IT */
            it.top_node = add_intersection(it.top_node, st.edge, edge, x, y);
            
            /* Head further into the ST */
            st.prev = add_st_edge(st.prev, it, edge, dy);
          }
      }
    return st ;
  }
  
  private static ItNode add_intersection( ItNode it_node, 
                                          EdgeNode edge0, 
                                          EdgeNode  edge1,
                                          float x, 
                                          float y)
  {
    if (it_node == null)
      {
        /* Append a new node to the tail of the list */
        it_node = new ItNode( edge0, edge1, x, y, null );
      }
    else
      {
        if ( it_node.point.y > y)
          {
            /* Insert a new node mid-list */
            ItNode existing_node = it_node ;
            it_node = new ItNode( edge0, edge1, x, y, existing_node );
          }
        else
          {
            /* Head further down the list */
            it_node.next = add_intersection( it_node.next, edge0, edge1, x, y);
          }
      }
    return it_node ;
  }
  
  private static int count_tristrips(PolygonNode tn)
  {
    int total;
    
    for (total= 0; tn!=null; tn= tn.next)
      {
     	if (tn.active > 2)
          {
            total++;
          }
      }
    return total;
  }
  
  private static VertexNode add_vertex(VertexNode ve_node, float x, float y)
  {
    if (ve_node == null)
      {
        /* Append a new node to the tail of the list */
        ve_node = new VertexNode( x, y);
      }
    else
      {
      	/* Head further down the list */
        ve_node.next = add_vertex( ve_node.next, x, y);
      }
    return ve_node;
  }
  
  private static PolygonNode new_tristrip(PolygonNode po_node, EdgeNode edge, float x, float y)
  {
    if (po_node == null)
      {
        /* Append a new node to the tail of the list */
        po_node = new PolygonNode();
        po_node.v_left=add_vertex(po_node.v_left, x, y);
        edge.outp_above= po_node;
      }
    else
      {
      	/* Head further down the list */
        po_node.next = new_tristrip( po_node.next, edge, x, y);
      }
    return po_node;
  }
  
  // ---------------------
  // --- Inner Classes ---
  // ---------------------
  private static class OperationType
  {
    private String m_Type ;
    private OperationType( String type ) { m_Type = type; }
    
    public static final OperationType GPC_DIFF  = new OperationType( "Difference" );
    public static final OperationType GPC_INT   = new OperationType( "Intersection" );
    public static final OperationType GPC_XOR   = new OperationType( "Exclusive or" );
    public static final OperationType GPC_UNION = new OperationType( "Union" );
    
    public String toString() { return m_Type; }
  }
  
  /**
   * Edge intersection classes
   */
  private static class VertexType
  {
    public static final int NUL =  0 ; /* Empty non-intersection            */
    public static final int EMX =  1 ; /* External maximum                  */
    public static final int ELI =  2 ; /* External left intermediate        */
    public static final int TED =  3 ; /* Top edge                          */
    public static final int ERI =  4 ; /* External right intermediate       */
    public static final int RED =  5 ; /* Right edge                        */
    public static final int IMM =  6 ; /* Internal maximum and minimum      */
    public static final int IMN =  7 ; /* Internal minimum                  */
    public static final int EMN =  8 ; /* External minimum                  */
    public static final int EMM =  9 ; /* External maximum and minimum      */
    public static final int LED = 10 ; /* Left edge                         */
    public static final int ILI = 11 ; /* Internal left intermediate        */
    public static final int BED = 12 ; /* Bottom edge                       */
    public static final int IRI = 13 ; /* Internal right intermediate       */
    public static final int IMX = 14 ; /* Internal maximum                  */
    public static final int FUL = 15 ; /* Full non-intersection             */
    
    public static int getType( int tr, int tl, int br, int bl )
    {
      return tr + (tl << 1) + (br << 2) + (bl << 3);
    }
  }
  
  /**
   * Horizontal edge states            
   */
  private static class HState
  {
    public static final int NH = 0 ; /* No horizontal edge                */
    public static final int BH = 1 ; /* Bottom horizontal edge            */
    public static final int TH = 2 ; /* Top horizontal edge               */
    
    /* Horizontal edge state transitions within scanbeam boundary */
    public static final int[][] next_h_state =
      {
        /*        ABOVE     BELOW     CROSS */
        /*        L   R     L   R     L   R */  
        /* NH */ {BH, TH,   TH, BH,   NH, NH},
        /* BH */ {NH, NH,   NH, NH,   TH, TH},
        /* TH */ {NH, NH,   NH, NH,   BH, BH}
      };
  }
  
  /**
   * Edge bundle state                 
   */
  private static class BundleState
  {
    private String m_State ;
    private BundleState( String state ) { m_State = state ; }
    
    public static final BundleState UNBUNDLED   = new BundleState( "UNBUNDLED"   ); // Isolated edge not within a bundle
    public static final BundleState BUNDLE_HEAD = new BundleState( "BUNDLE_HEAD" ); // Bundle head node
    public static final BundleState BUNDLE_TAIL = new BundleState( "BUNDLE_TAIL" ); // Passive bundle tail node
    
    public String toString() { return m_State; }
  }
  
  /**
   * Internal vertex list datatype
   */
  private static class VertexNode
  {
    float     x;    // X coordinate component
    float     y;    // Y coordinate component
    VertexNode next; // Pointer to next vertex in list
    
    public VertexNode( float x, float y )
    {
      this.x = x ;
      this.y = y ;
      this.next = null ;
    }
  }
  
  /**
   * Internal contour / tristrip type
   */
  private static class PolygonNode
  {
    int          active;                 /* Active flag / vertex count        */
    boolean      hole;                   /* Hole / external contour flag      */
    VertexNode   v_left,v_right; /* Left and right vertex list ptrs   */
    PolygonNode  next;                   /* Pointer to next polygon contour   */
    PolygonNode  proxy;                  /* Pointer to actual structure used  */
    
    public PolygonNode()
    {
      /* Make v[LEFT] and v[RIGHT] point to new vertex */
      this.v_left = null ;
      this.v_right = null ;
      this.next = null ;
      this.proxy = this ; /* Initialise proxy to point to p itself */
      this.active = 1 ; //TRUE
    }
    
    public PolygonNode( PolygonNode next, float x, float y )
    {
      /* Make v[LEFT] and v[RIGHT] point to new vertex */
      VertexNode vn = new VertexNode( x, y );
      this.v_left = vn ;
      this.v_right = vn ;
      
      this.next = next ;
      this.proxy = this ; /* Initialise proxy to point to p itself */
      this.active = 1 ; //TRUE
    }
    
    public void add_right( float x, float y )
    {
      VertexNode nv = new VertexNode( x, y );
      
      /* Add vertex nv to the right end of the polygon's vertex list */
      proxy.v_right.next= nv;
      
      /* Update proxy->v[RIGHT] to point to nv */
      proxy.v_right= nv;
    }
    
    public void add_left( float x, float y)
    {
      VertexNode nv = new VertexNode( x, y );
      
      /* Add vertex nv to the left end of the polygon's vertex list */
      nv.next= proxy.v_left;
      
      /* Update proxy->[LEFT] to point to nv */
      proxy.v_left= nv;
    }
    
  }
  
  private static class TopPolygonNode
  {
    PolygonNode top_node = null ;
    
    public PolygonNode add_local_min( float x, float y )
    {
      PolygonNode existing_min = top_node;
      
      top_node = new PolygonNode( existing_min, x, y );
      
      return top_node ;
    }
    
    public void merge_left( PolygonNode p, PolygonNode q )
    {
      /* Label contour as a hole */
      q.proxy.hole = true ;
      
      if (p.proxy != q.proxy)
        {
          /* Assign p's vertex list to the left end of q's list */
          p.proxy.v_right.next= q.proxy.v_left;
          q.proxy.v_left= p.proxy.v_left;
          
          /* Redirect any p.proxy references to q.proxy */
          PolygonNode target = p.proxy ;
          for(PolygonNode node = top_node; (node != null); node = node.next)
            {
              if (node.proxy == target)
                {
                  node.active= 0;
                  node.proxy= q.proxy;
                }
            }
        }
    }
    
    public void merge_right( PolygonNode p, PolygonNode q )
    {
      /* Label contour as external */
      q.proxy.hole = false ;
      
      if (p.proxy != q.proxy)
        {
          /* Assign p's vertex list to the right end of q's list */
          q.proxy.v_right.next= p.proxy.v_left;
          q.proxy.v_right= p.proxy.v_right;
          
          /* Redirect any p->proxy references to q->proxy */
          PolygonNode target = p.proxy ;
          for (PolygonNode node = top_node ; (node != null ); node = node.next)
            {
              if (node.proxy == target)
                {
                  node.active = 0;
                  node.proxy= q.proxy;
                }
            }
        }
    }
    
    public int count_contours()
    {
      int nc = 0 ;
      for ( PolygonNode polygon = top_node; (polygon != null) ; polygon = polygon.next)
        {
          if (polygon.active != 0)
            {
              /* Count the vertices in the current contour */
              int nv= 0;
              for (VertexNode v= polygon.proxy.v_left; (v != null); v = v.next)
                {
                  nv++;
                }
              
              /* Record valid vertex counts in the active field */
              if (nv > 2)
                {
                  polygon.active = nv;
                  nc++;
                }
              else
                {
                  /* Invalid contour: just free the heap */
                  //                  VertexNode nextv = null ;
                  //                  for (VertexNode v= polygon.proxy.v_left; (v != null); v = nextv)
                  //                  {
                  //                     nextv= v.next;
                  //                     v = null ;
                  //                  }
                  polygon.active= 0;
                }
            }
        }
      return nc;
    }
    
    public RPolygon getResult( Class polyClass )
    {
      //RPolygon result = createNewPoly( polyClass );
      RPolygon result = new RPolygon();
      int num_contours = count_contours();
      if (num_contours > 0)
      {
        int c= 0;
        PolygonNode npoly_node = null ;
        for (PolygonNode poly_node= top_node; (poly_node != null); poly_node = npoly_node)
        {
          npoly_node = poly_node.next;
          if (poly_node.active != 0)
          {
            RContour contour;     
            if(result.countContours()>0){
              contour = result.contours[0];
            }else{
              contour = new RContour();
            }
            //RPolygon poly = result ;
            if( num_contours > 0 )
            {
              contour = new RContour();
              //poly = createNewPoly( polyClass );
            }
            if( poly_node.proxy.hole )
            {
              contour.isHole = poly_node.proxy.hole;
              //poly.setIsHole( poly_node.proxy.hole );
            }

            // ------------------------------------------------------------------------
            // --- This algorithm puts the verticies into the poly in reverse order ---
            // ------------------------------------------------------------------------
            ArrayList points = new ArrayList();
            for (VertexNode vtx = poly_node.proxy.v_left; vtx != null; vtx = vtx.next )
            {
              points.add(new RPoint(vtx.x, vtx.y));
            }
            contour.addPoints(points);
            
            if( num_contours > 0 )
            {
              result.addContour(contour);
              //result.add( poly );
            }
            c++;
          }
        }

        // -----------------------------------------
        // --- Sort holes to the end of the list ---
        // -----------------------------------------
        RPolygon orig = new RPolygon(result) ;
        result = new RPolygon();
        //result = createNewPoly( polyClass );
        for( int i = 0 ; i < orig.countContours() ; i++ )
          //for( int i = 0 ; i < orig.getNumInnerPoly() ; i++ )
        {
          RContour inner = orig.contours[i];
          //RPolygon inner = orig.getInnerPoly(i);
          if( !inner.isHole() )
          {
            result.addContour(inner);
            //result.add(inner);
          }
        }

        for( int i = 0 ; i < orig.countContours() ; i++ )
          //for( int i = 0 ; i < orig.getNumInnerPoly() ; i++ )
        {
          RContour inner = orig.contours[i];
          //RPolygon inner = orig.getInnerPoly(i);
          if( inner.isHole() )
          {
            result.addContour(inner);
          }
        }
      }
      return result ;
    }
    
    public void print()
    {
      System.out.println("---- out_poly ----");
      int c= 0;
      PolygonNode npoly_node = null ;
      for (PolygonNode poly_node= top_node; (poly_node != null); poly_node = npoly_node)
        {
          System.out.println("contour="+c+"  active="+poly_node.active+"  hole="+poly_node.proxy.hole);
          npoly_node = poly_node.next;
          if (poly_node.active != 0)
            {
              int v=0 ;
              for (VertexNode vtx = poly_node.proxy.v_left; (vtx != null) ; vtx = vtx.next )
                {
                  System.out.println("v="+v+"  vtx.x="+vtx.x+"  vtx.y="+vtx.y);
                }
              c++;
            }
        }
    }         
  }
  
  private static class EdgeNode
  {
    float vertex_x,vertex_y;      /* Piggy-backed contour vertex data  */
    float bot_x,bot_y;            /* Edge upper (x, y) coordinate      */
    float top_x,top_y;            /* Edge upper (x, y) coordinate      */
    float         xb;           /* Scanbeam bottom x coordinate      */
    float         xt;           /* Scanbeam top x coordinate         */
    float         dx;           /* Change in x for a unit y increase */
    int            type;         /* Clip / subject edge flag          */
    int[] bundle_above = new int[2];
    int bundle_below_clip;
    int bundle_below_subj;
    int bside_clip;
    int bside_subj;
    
    BundleState  bstate_above;
    BundleState  bstate_below;
    PolygonNode outp_above;
    PolygonNode outp_below;
    EdgeNode       prev;         /* Previous edge in the AET          */
    EdgeNode       next;         /* Next edge in the AET              */
    EdgeNode       pred;         /* Edge connected at the lower end   */
    EdgeNode       succ;         /* Edge connected at the upper end   */
    EdgeNode       next_bound;   /* Pointer to next bound in LMT      */
  }
  
  private static class AetTree
  {
    EdgeNode top_node ;
    
    public void print()
    {
      System.out.println("");
      System.out.println("aet");
      for( EdgeNode edge = top_node ; (edge != null) ; edge = edge.next )
        {
          System.out.println("edge.vertex_x="+edge.vertex_x+"  edge.vertex_y="+edge.vertex_y);
        }
    }
  }
  
  private static class EdgeTable
  {
    private EdgeNode[] m_List;
    private int len;
    
    public EdgeTable(int maxSize) {
      m_List = new EdgeNode[maxSize];
      len = 0;
    }
    
    public void addNode( float x, float y )
    {
      EdgeNode node = new EdgeNode();
      node.vertex_x = x;
      node.vertex_y = y;
      m_List[len] = node;
      len++;
    }
    
    public EdgeNode getNode( int index )
    {
      return m_List[index];
    }
    
    public boolean FWD_MIN( int i )
    {
      EdgeNode prev = m_List[PREV_INDEX(i, len)];
      EdgeNode next = m_List[NEXT_INDEX(i, len)];
      EdgeNode ith  = m_List[i];
      return ((prev.vertex_y >= ith.vertex_y) &&
              (next.vertex_y >  ith.vertex_y));
    }
    
    public boolean NOT_FMAX( int i )
    {
      EdgeNode next = m_List[NEXT_INDEX(i, len)];
      EdgeNode ith  = m_List[i];
      return(next.vertex_y > ith.vertex_y);
    }
    
    public boolean REV_MIN( int i )
    {
      EdgeNode prev = m_List[PREV_INDEX(i, len)];
      EdgeNode next = m_List[NEXT_INDEX(i, len)];
      EdgeNode ith  = m_List[i];
      return ((prev.vertex_y >  ith.vertex_y) &&
              (next.vertex_y >= ith.vertex_y));
    }
    
    public boolean NOT_RMAX( int i )
    {
      EdgeNode prev = m_List[PREV_INDEX(i, len)];
      EdgeNode ith  = m_List[i];
      return (prev.vertex_y > ith.vertex_y) ;
    }
  }
  
  /**
   * Local minima table
   */
  private static class LmtNode
  {
    float   y;            /* Y coordinate at local minimum     */
    EdgeNode first_bound;  /* Pointer to bound list             */
    LmtNode  next;         /* Pointer to next local minimum     */
    
    public LmtNode( float yvalue )
    {
      y = yvalue ;
    }
  }
  
  private static class LmtTable
  {
    LmtNode top_node ;
    
    public void print()
    {
      int n = 0 ;
      LmtNode lmt = top_node ;
      while( lmt != null )
        {
          System.out.println("lmt("+n+")");
          for( EdgeNode edge = lmt.first_bound ; (edge != null) ; edge = edge.next_bound )
            {
              System.out.println("edge.vertex_x="+edge.vertex_x+"  edge.vertex_y="+edge.vertex_y);
            }
          n++ ;
          lmt = lmt.next ;
        }
    }
  }
  
  /**
   * Scanbeam tree 
   */
  private static class ScanBeamTree
  {
    float       y;            /* Scanbeam node y value             */
    ScanBeamTree less;         /* Pointer to nodes with lower y     */
    ScanBeamTree more;         /* Pointer to nodes with higher y    */
    
    public ScanBeamTree( float yvalue )
    {
      y = yvalue ;
    }
  }
  
  /**
   *
   */
  private static class ScanBeamTreeEntries
  {
    int sbt_entries ;
    ScanBeamTree sb_tree ;
    
    public float[] build_sbt()
    {
      float[] sbt = new float[sbt_entries];
      
      int entries = 0 ;
      entries = inner_build_sbt( entries, sbt, sb_tree );
      if( entries != sbt_entries )
        {
          throw new IllegalStateException("Something went wrong buildign sbt from tree.");
        }
      return sbt ;
    }
    
    private int inner_build_sbt( int entries, float[] sbt, ScanBeamTree sbt_node )
    {
      if( sbt_node.less != null )
        {
          entries = inner_build_sbt(entries, sbt, sbt_node.less);
        }
      sbt[entries]= sbt_node.y;
      entries++;
      if( sbt_node.more != null )
        {
          entries = inner_build_sbt(entries, sbt, sbt_node.more );
        }
      return entries ;
    }
  }
  
  /**
   * Intersection table
   */
  private static class ItNode
  {
    EdgeNode ie0,ie1;      /* Intersecting edge (bundle) pair   */
    RPoint point = new RPoint(); /* Point of intersection             */
    ItNode next;                         /* The next intersection table node  */
    
    public ItNode( EdgeNode edge0, EdgeNode edge1, float x, float y, ItNode next )
    {
      this.ie0 = edge0;
      this.ie1 = edge1;
      this.point.x = x;
      this.point.y = y;
      this.next = next;
    }
  }
  
  private static class ItNodeTable
  {
    ItNode top_node ;
    
    public void build_intersection_table(AetTree aet, float dy)
    {
      StNode st = null ;
      
      /* Process each AET edge */
      for (EdgeNode edge = aet.top_node ; (edge != null); edge = edge.next)
        {
          if( (edge.bstate_above == BundleState.BUNDLE_HEAD) ||
              (edge.bundle_above[CLIP] != 0) ||
              (edge.bundle_above[SUBJ] != 0) )
            {
              st = add_st_edge(st, this, edge, dy);
            }
        }
    }
  }
  
  /**
   * Sorted edge table
   */
  private static class StNode
  {
    EdgeNode edge;         /* Pointer to AET edge               */
    float   xb;           /* Scanbeam bottom x coordinate      */
    float   xt;           /* Scanbeam top x coordinate         */
    float   dx;           /* Change in x for a unit y increase */
    StNode   prev;         /* Previous edge in sorted list      */
    
    public StNode( EdgeNode edge, StNode prev )
    {
      this.edge = edge ;
      this.xb = edge.xb ;
      this.xt = edge.xt ;
      this.dx = edge.dx ;
      this.prev = prev ;
    }      
  }
  
  // -------------
  // --- DEBUG ---
  // -------------
  private static void print_sbt( float[] sbt )
  {
    System.out.println("");
    System.out.println("sbt.length="+sbt.length);
    for( int i = 0 ; i < sbt.length ; i++ )
      {
        System.out.println("sbt["+i+"]="+sbt[i]);
      }
  }
}
