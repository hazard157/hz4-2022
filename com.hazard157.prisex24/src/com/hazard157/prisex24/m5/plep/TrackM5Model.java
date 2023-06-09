package com.hazard157.prisex24.m5.plep;

import static com.hazard157.common.IHzConstants.*;
import static com.hazard157.common.quants.secint.gui.ISecintM5Constants.*;
import static com.hazard157.prisex24.m5.IPsxM5Constants.*;
import static com.hazard157.prisex24.m5.plep.IPsxResources.*;
import static com.hazard157.psx.proj3.pleps.IUnitPlepsConstants.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tsgui.utils.HmsUtils.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.av.*;

import com.hazard157.common.quants.secint.*;
import com.hazard157.common.quants.secint.valed.*;
import com.hazard157.psx.proj3.pleps.*;
import com.hazard157.psx.proj3.songs.*;

/**
 * M5-model of {@link ITrack}.
 *
 * @author hazard157
 */
public class TrackM5Model
    extends M5Model<ITrack> {

  /**
   * Field: sequence number of the track in the {@link IPlep#tracks()}, starting from 1.
   */
  public static final M5AttributeFieldDef<ITrack> SEQ_NO = new M5AttributeFieldDef<>( FID_SEQ_NO, DDEF_INTEGER ) {

    @Override
    protected void doInit() {
      setNameAndDescription( STR_TRACK_SEQ_NO, STR_TRACK_SEQ_NO_D );
      setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
    }

    @Override
    protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
      int index = aEntity.plep().tracks().indexOf( aEntity );
      return avInt( index + 1 );
    }

  };

  /**
   * Field: {@link ITrack#songId()}.
   */
  public static final IM5SingleLookupKeyFieldDef<ITrack, ISong> SONG_ID =
      new M5SingleLookupKeyFieldDef<>( FID_SONG_ID, MID_SONG, FID_ID, String.class ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_TRACK_SONG_ID, STR_TRACK_SONG_ID_D );
          setFlags( M5FF_DETAIL );
        }

        @Override
        protected ISong doGetFieldValue( ITrack aEntity ) {
          IUnitSongs unitSongs = tsContext().get( IUnitSongs.class );
          return unitSongs.items().findByKey( aEntity.songId() );
        }

      };

  /**
   * Field: {@link ITrack#interval()}.
   */
  public static final IM5AttributeFieldDef<ITrack> INTERVAL =
      new M5AttributeFieldDef<>( FID_INTERVAL, DT_VIDEO_INTERVAL ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_TRACK_INTERVAL, STR_TRACK_INTERVAL_D );
          setFlags( M5FF_COLUMN );
        }

        @Override
        protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
          return avValobj( aEntity.interval() );
        }

      };

  /**
   * Field: {@link ITrack#duration()}.
   */
  public static final M5AttributeFieldDef<ITrack> START = new M5AttributeFieldDef<>( FID_START, DT_VIDEO_POSITION ) {

    @Override
    protected void doInit() {
      setNameAndDescription( STR_TRACK_START, STR_TRACK_START_D );
      setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
    }

    private int getStart( ITrack aEntity ) {
      int index = aEntity.plep().tracks().indexOf( aEntity );
      if( index < 0 ) {
        return 0;
      }
      int start = 0;
      for( int i = 0; i < index; i++ ) {
        start += aEntity.plep().tracks().get( i ).duration();
      }
      return start;
    }

    @Override
    protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
      return avInt( getStart( aEntity ) );
    }

    @Override
    protected String doGetFieldValueName( ITrack aEntity ) {
      return mmss( getStart( aEntity ) );
    }

  };

  /**
   * Field: track name (uses {@link ISong#nmName()}.
   */
  public static final IM5AttributeFieldDef<ITrack> NAME = new M5AttributeFieldDef<>( FID_NAME, DDEF_STRING ) {

    @Override
    protected void doInit() {
      setNameAndDescription( STR_TRACK_NAME, STR_TRACK_NAME_D );
      setFlags( M5FF_COLUMN | M5FF_READ_ONLY | M5FF_HIDDEN );
    }

    @Override
    protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
      IUnitSongs unitSongs = tsContext().get( IUnitSongs.class );
      ISong song = unitSongs.items().findByKey( aEntity.songId() );
      String name = STR_UNKNOWN_SONG;
      if( song != null ) {
        name = song.nmName();
      }
      return avStr( name );
    }

  };

  /**
   * Field: {@link ITrack#duration()}.
   */
  public static final M5AttributeFieldDef<ITrack> DURATION =
      new M5AttributeFieldDef<>( FID_DURATION, DT_VIDEO_DURATION ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_TRACK_DURATION, STR_TRACK_DURATION_D );
          setFlags( M5FF_COLUMN | M5FF_HIDDEN );
          setDefaultValue( AV_1 );
        }

        @Override
        protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
          return avInt( aEntity.duration() );
        }

        @Override
        protected String doGetFieldValueName( ITrack aEntity ) {
          return mmss( aEntity.duration() );
        }

      };

  /**
   * Скрытое поле - индекс для вставления нового {@link ITrack}.
   * <p>
   * Используется для передачи в менеджер ЖЦ предполагаемого места вставления нового ITrack.
   */
  public static final IM5AttributeFieldDef<ITrack> INSERTION_INDEX =
      new M5AttributeFieldDef<>( OPID_TRACK_INSERTION_INDEX, DDEF_INTEGER ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_HIDDEN );
          setDefaultValue( OP_TRACK_INSERTION_INDEX.defaultValue() );
        }

        @Override
        protected IAtomicValue doGetFieldValue( ITrack aEntity ) {
          return AV_N1;
        }

      };

  /**
   * Конструктор.
   */
  public TrackM5Model() {
    super( MID_TRACK, ITrack.class );
    addFieldDefs( SEQ_NO, SONG_ID, START, INTERVAL, DURATION, NAME, INSERTION_INDEX );
    setPanelCreator( new M5DefaultPanelCreator<ITrack>() {

      @Override
      protected IM5CollectionPanel<ITrack> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<ITrack> aItemsProvider, IM5LifecycleManager<ITrack> aLifecycleManager ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_TRUE );
        OPDEF_IS_ACTIONS_REORDER.setValue( aContext.params(), AV_TRUE );
        OPDEF_IS_SUMMARY_PANE.setValue( aContext.params(), AV_TRUE );
        MultiPaneComponentModown<ITrack> mpc = new TrackMpc( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }

      @Override
      protected IM5EntityPanel<ITrack> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<ITrack> aLifecycleManager ) {
        M5EntityPanelWithValedsController<ITrack> controller = new M5EntityPanelWithValedsController<>() {

          @SuppressWarnings( { "rawtypes" } )
          private void updateMaxEnd() {
            ISong song = (ISong)editors().getByKey( SONG_ID.id() ).getValue();
            if( song != null ) {
              int end = song.duration() - 1;
              if( end > 1 ) {
                IValedControl vc = editors().getByKey( INTERVAL.id() );
                vc.params().setInt( ValedAvSecint.MAX_END, end );
              }
            }
          }

          @Override
          public void afterSetValues( IM5Bunch<ITrack> aValues ) {
            updateMaxEnd();
          }

          @SuppressWarnings( { "rawtypes", "unchecked" } )
          @Override
          public boolean doProcessEditorValueChange( IValedControl<?> aEditor, IM5FieldDef<ITrack, ?> aFieldDef,
              boolean aEditFinished ) {
            if( aFieldDef == SONG_ID ) {
              Secint in = INTERVAL.defaultValue().asValobj();
              IValedControl vc = editors().getByKey( INTERVAL.id() );
              ISong song = (ISong)aEditor.getValue();
              if( song != null ) {
                int end = song.duration() - 1;
                if( end > 1 ) {
                  in = new Secint( 0, end );
                  vc.params().setInt( ValedAvSecint.MAX_END, end );
                }
              }
              vc.setValue( avValobj( in ) );
            }
            return true;
          }
        };
        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, controller );
      }

    } );

  }

  @Override
  protected IM5LifecycleManager<ITrack> doCreateDefaultLifecycleManager() {
    return null;
  }

  @Override
  protected IM5LifecycleManager<ITrack> doCreateLifecycleManager( Object aMaster ) {
    return new TrackLifecycleManager( this, IPlep.class.cast( aMaster ) );
  }

}
