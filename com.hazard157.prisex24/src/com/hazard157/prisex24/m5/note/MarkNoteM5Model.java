package com.hazard157.prisex24.m5.note;

import static com.hazard157.common.IHzConstants.*;
import static com.hazard157.common.quants.secint.gui.ISecintM5Constants.*;
import static com.hazard157.prisex24.m5.IPsxM5Constants.*;
import static com.hazard157.prisex24.m5.note.IPsxResources.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.utils.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.errors.*;

import com.hazard157.common.quants.secint.*;
import com.hazard157.common.quants.secint.gui.*;
import com.hazard157.common.quants.secint.valed.*;
import com.hazard157.prisex24.m5.std.*;
import com.hazard157.psx.common.stuff.frame.*;
import com.hazard157.psx.proj3.episodes.*;
import com.hazard157.psx.proj3.episodes.proplines.*;
import com.hazard157.psx.proj3.episodes.story.*;

/**
 * Model of {@link MarkNote}.
 *
 * @author hazard157
 */
public class MarkNoteM5Model
    extends M5Model<MarkNote> {

  /**
   * Field {@link Secint#start() MarkNote.in().start()}
   */
  public static final M5AttributeFieldDef<MarkNote> START = new M5AttributeFieldDef<>( FID_START, DT_VIDEO_POSITION ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN | M5FF_HIDDEN );
      setNameAndDescription( STR_N_FIELD_START, STR_D_FIELD_START );
    }

    @Override
    protected IAtomicValue doGetFieldValue( MarkNote aEntity ) {
      return avInt( aEntity.in().start() );
    }

    @Override
    protected String doGetFieldValueName( MarkNote aEntity ) {
      return HmsUtils.mmmss( aEntity.in().start() );
    }

  };

  /**
   * Field {@link Secint#duration() MarkNote.in().duration()}
   */
  public static final M5AttributeFieldDef<MarkNote> DURATION =
      new M5AttributeFieldDef<>( FID_DURATION, DT_VIDEO_DURATION ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN | M5FF_HIDDEN | M5FF_READ_ONLY );
          setNameAndDescription( STR_N_FIELD_DURATION, STR_D_FIELD_DURATION );
        }

        @Override
        protected IAtomicValue doGetFieldValue( MarkNote aEntity ) {
          return avInt( aEntity.in().start() );
        }

        @Override
        protected String doGetFieldValueName( MarkNote aEntity ) {
          return HmsUtils.mmmss( aEntity.in().duration() );
        }

      };

  /**
   * Field {@link MarkNote#in()}
   */
  public static final M5SingleModownFieldDef<MarkNote, Secint> INTERVAL =
      new M5SingleModownFieldDef<>( FID_INTERVAL, SecintM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_DETAIL );
          setNameAndDescription( STR_N_FIELD_INTERVAL, STR_D_FIELD_INTERVAL );
          setDefaultValue( Secint.MAXIMUM );
          setValedEditor( ValedSecintFactory.FACTORY_NAME );
        }

        @Override
        protected Secint doGetFieldValue( MarkNote aEntity ) {
          return aEntity.in();
        }

      };

  /**
   * Field {@link Secint#duration() MarkNote.in().duration()}
   */
  public static final M5AttributeFieldDef<MarkNote> NOTE = new M5AttributeFieldDef<>( FID_NOTE, DDEF_STRING ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_DETAIL | M5FF_COLUMN );
      setNameAndDescription( STR_N_FIELD_NOTE, STR_D_FIELD_NOTE );
    }

    @Override
    protected IAtomicValue doGetFieldValue( MarkNote aEntity ) {
      return avStr( aEntity.marker() );
    }

  };

  /**
   * Field {@link PlaneGuide#frame()}
   */
  public static final PsxM5FrameFieldDef<MarkNote> FRAME = new PsxM5FrameFieldDef<>() {

    @Override
    protected void doInit() {
      super.doInit();
      setFlags( M5FF_DETAIL | M5FF_HIDDEN | M5FF_READ_ONLY );
    }

    @Override
    protected IFrame doGetFieldValue( MarkNote aEntity ) {
      IUnitEpisodes unitEpisodes = tsContext().get( IUnitEpisodes.class );
      IEpisode e = unitEpisodes.items().findByKey( aEntity.episodeId() );
      if( e != null ) {
        IScene s = e.story().findBestSceneFor( aEntity.interval(), true );
        if( s != null ) {
          return s.frame();
        }
      }
      return IFrame.NONE;
    }

  };

  /**
   * Конструктор.
   */
  public MarkNoteM5Model() {
    super( MID_EP_NOTE, MarkNote.class );
    addFieldDefs( INTERVAL, START, DURATION, NOTE, FRAME );
    setPanelCreator( new M5DefaultPanelCreator<MarkNote>() {

      @Override
      protected IM5CollectionPanel<MarkNote> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<MarkNote> aItemsProvider, IM5LifecycleManager<MarkNote> aLifecycleManager ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_TRUE );
        MultiPaneComponentModown<MarkNote> mpc =
            new MarkNoteMpc( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }

    } );
  }

  @Override
  protected IM5LifecycleManager<MarkNote> doCreateLifecycleManager( Object aMaster ) {
    TsInternalErrorRtException.checkNull( aMaster );
    return new MarkNoteLifecycleManager( this, INoteLine.class.cast( aMaster ) );
  }

}
