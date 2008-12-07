/*
*    geomerative example
*
*    http://www.ricardmarxer.com/geomerative/rshape_class_rshape.htm
*
*    fjenett 20080419
 *    fjenett 20081203 - updated to geomerative 19
*/

    import geomerative.*;


    RFont[] fnt;
    String inp = "type!";


    void setup ()
    {
        size( 1000, 200 );
        frameRate( 10 );
        strokeWeight( 2 );
    
        RG.init( this );

        fnt = new RFont[]{
            new RFont( "Bangalor.ttf", int(random(70,120)) ),
            new RFont( "BMWCdLt.ttf", int(random(70,120)) ),
            new RFont( "lucon.ttf", int(random(70,120)) ),
            new RFont( "PRO5BA__.ttf", int(random(70,120)) ),
            new RFont( "Smirnof.ttf", int(random(70,120)) )
        };
    }


    void draw ()
    {
        background( color( 222, 111, 5 ) );
        stroke( color( 222/1.75, 111/1.75, 5/1.75 ) );
        translate( 40, 120 );

        if ( !inp.equals("") ) {

            RPolygon rp = new RPolygon();

            for ( int i = 0; i < fnt.length; i++ )
            {
                RGroup grp = fnt[i].toGroup( inp );

                for ( int s = 0; s < grp.elements.length; s++ )
                {
                    RPolygon r2 = grp.elements[s].toPolygon();

                    RMatrix m = new RMatrix();
                    m.translate( random(-5,5) , random(-5,5) );
                    r2.transform( m );

                    rp = rp.union( r2 );
                }
            }

            rp.draw();
        }
    }


    void keyPressed ()
    {
        if (  keyCode == DELETE || keyCode == BACKSPACE )
        {
            if ( inp.length() > 0 )
            {
                inp = inp.substring(0,inp.length()-1);
            }
        }
        else if ( key != CODED )
        {
            inp = inp + key;
        }
    }
