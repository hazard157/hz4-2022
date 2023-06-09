package com.hazard157.psx.proj3.movies;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;

import com.hazard157.common.quants.secint.*;
import com.hazard157.psx.common.stuff.frame.*;

/**
 * Хранитель объектов типа {@link Chunk}.
 *
 * @author hazard157
 */
public class ChunkKeeper
    extends AbstractEntityKeeper<Chunk> {

  /**
   * Экземпляр-синглтон хранителя.
   */
  public static final IEntityKeeper<Chunk> KEEPER = new ChunkKeeper();

  private ChunkKeeper() {
    super( Chunk.class, EEncloseMode.ENCLOSES_BASE_CLASS, null );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов класса AbstractEntityKeeper
  //

  @Override
  protected void doWrite( IStrioWriter aSw, Chunk aEntity ) {
    aSw.writeAsIs( aEntity.episodeId() );
    aSw.writeSeparatorChar();
    aSw.writeAsIs( aEntity.cameraId() );
    aSw.writeSeparatorChar();
    aSw.writeQuotedString( aEntity.name() );
    aSw.writeSeparatorChar();
    Secint.KEEPER.write( aSw, aEntity.interval() );
    aSw.writeSeparatorChar();
    Frame.KEEPER.write( aSw, aEntity.frame() );
  }

  @Override
  protected Chunk doRead( IStrioReader aSr ) {
    String episodeId = aSr.readIdPath();
    aSr.ensureSeparatorChar();
    String camId = aSr.readIdPath();
    aSr.ensureSeparatorChar();
    String name = aSr.readQuotedString();
    aSr.ensureSeparatorChar();
    Secint in = Secint.KEEPER.read( aSr );
    aSr.ensureSeparatorChar();
    IFrame frame = Frame.KEEPER.read( aSr );
    return new Chunk( episodeId, camId, name, in, frame );
  }

}
