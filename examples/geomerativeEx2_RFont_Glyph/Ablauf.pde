/*
    der weg durch die objekte bis zu den glyph daten.


    RFont
      |
      +-> toGroup(text)
              |
              v
            RGroup
              |
              +-> elements[]
                     |
                     v
                   RShape
                     |
                     +-> paths[]
                             |
                             v
                           RPath
                             |
                             +-> commands[] 
                                    |
                                    v
                                 RCommand ------------+
                                    |                 |
                                    v                 v
                               getHandles()    getCommandType()
                                    |                 |
                                    v                 v
                                  RPoint[]       RCommand.LINETO
                                                 RCommand.QUADBEZIERTO
                                                 RCommand.CUBICBEZIERTO
*/

