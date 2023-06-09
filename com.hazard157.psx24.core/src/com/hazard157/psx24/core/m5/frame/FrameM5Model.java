package com.hazard157.psx24.core.m5.frame;

import static com.hazard157.psx24.core.m5.IPsxM5Constants.*;
import static com.hazard157.psx24.core.m5.frame.IPsxResources.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.eclipse.swt.graphics.*;
import org.toxsoft.core.tsgui.graphics.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.graphics.image.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;

import com.hazard157.psx.common.stuff.frame.*;
import com.hazard157.psx.common.utils.*;
import com.hazard157.psx24.core.e4.services.filesys.*;
import com.hazard157.psx24.core.m5.std.*;
import com.hazard157.psx24.core.valeds.frames.*;

/**
 * Модель сущностей типа {@link IFrame}.
 *
 * @author hazard157
 */
public class FrameM5Model
    extends M5Model<IFrame> {

  /**
   * ID of field {@link #FRAME_NO}.
   */
  public static final String FID_FRAME_NO = "FrameNo"; //$NON-NLS-1$

  /**
   * ID of field {@link #IS_ANIMATED}.
   */
  public static final String FID_IS_ANIMATED = "IsAnimated"; //$NON-NLS-1$

  /**
   * ID of field {@link #FRAME_IMAGE}.
   */
  public static final String FID_FRAME_IMAGE = "FrameImage"; //$NON-NLS-1$

  /**
   * Field {@link IFrame#episodeId()}.
   */
  public static final M5SingleLookupFieldDef<IFrame, String> EPISODE_ID = new PsxM5EpisodeIdFieldDef<>();

  /**
   * Field {@link IFrame#cameraId()}.
   */
  public static final M5SingleLookupFieldDef<IFrame, String> CAM_ID = new PsxM5CameraIdFieldDef<>();

  /**
   * Field {@link IFrame#frameNo()}.
   */
  public static final M5AttributeFieldDef<IFrame> FRAME_NO = new M5AttributeFieldDef<>( FID_FRAME_NO, DDEF_INTEGER ) {

    @Override
    protected void doInit() {
      setNameAndDescription( STR_N_FR_FRAME_NO, STR_D_FR_FRAME_NO );
      setFlags( M5FF_COLUMN );
    }

    @Override
    protected IAtomicValue doGetFieldValue( IFrame aEntity ) {
      return avInt( aEntity.frameNo() );
    }

    @Override
    protected String doGetFieldValueName( IFrame aEntity ) {
      return HhMmSsFfUtils.mmssff( aEntity.frameNo() );
    }

  };

  /**
   * Field {@link IFrame#isAnimated()}.
   */
  public static final M5AttributeFieldDef<IFrame> IS_ANIMATED =
      new M5AttributeFieldDef<>( FID_IS_ANIMATED, DDEF_TS_BOOL ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_FR_IS_ANIMATED, STR_D_FR_IS_ANIMATED );
          setFlags( M5FF_COLUMN );
          M5_OPDEF_COLUMN_ALIGN.setValue( params(), avValobj( EHorAlignment.CENTER ) );
        }

        @Override
        protected IAtomicValue doGetFieldValue( IFrame aEntity ) {
          return avBool( aEntity.isAnimated() );
        }

      };

  /**
   * The {@link IFrame} itself.
   */
  public static final M5FieldDef<IFrame, IFrame> FRAME_IMAGE = new M5FieldDef<>( FID_FRAME_IMAGE, IFrame.class ) {

    @Override
    protected void doInit() {
      params().setValueIfNull( OPID_CREATE_UNEDITABLE, AV_TRUE );
      setValedEditor( ValedFrameFactory.FACTORY_NAME );
      setFlags( M5FF_DETAIL );
    }

    @Override
    protected IFrame doGetFieldValue( IFrame aEntity ) {
      return aEntity;
    }

    @Override
    protected Image doGetFieldValueIcon( IFrame aEntity, EIconSize aIconSize ) {
      IPsxFileSystem fileSystem = tsContext().get( IPsxFileSystem.class );
      EThumbSize thumbSize = EThumbSize.findIncluding( aIconSize ).prevSize();
      TsImage mi = fileSystem.findThumb( aEntity, thumbSize );
      if( mi != null ) {
        return mi.image();
      }
      return null;
    }

  };

  static class FrameM5LifecycleManager
      extends M5LifecycleManager<IFrame, Object> {

    public FrameM5LifecycleManager( IM5Model<IFrame> aModel, Object aMaster ) {
      super( aModel, true, true, true, false, aMaster );
    }

    private static IFrame makeFrame( IM5Bunch<IFrame> aValues ) {
      String episodeId = EPISODE_ID.getFieldValue( aValues );
      String cameraId = CAM_ID.getFieldValue( aValues );
      int frameNo = FRAME_NO.getFieldValue( aValues ).asInt();
      boolean isAnimated = IS_ANIMATED.getFieldValue( aValues ).asBool();
      return new Frame( episodeId, cameraId, frameNo, isAnimated );
    }

    @Override
    protected IFrame doCreate( IM5Bunch<IFrame> aValues ) {
      return makeFrame( aValues );
    }

    @Override
    protected IFrame doEdit( IM5Bunch<IFrame> aValues ) {
      return makeFrame( aValues );
    }

    @Override
    protected void doRemove( IFrame aEntity ) {
      // nop
    }

  }

  /**
   * Constructor.
   */
  public FrameM5Model() {
    super( MID_FRAME, IFrame.class );
    setNameAndDescription( STR_N_M5M_FRAME, STR_D_M5M_FRAME );
    addFieldDefs( EPISODE_ID, CAM_ID, IS_ANIMATED, FRAME_NO, FRAME_IMAGE );
  }

  @Override
  protected IM5LifecycleManager<IFrame> doCreateDefaultLifecycleManager() {
    return new FrameM5LifecycleManager( this, null );
  }

  @Override
  protected IM5LifecycleManager<IFrame> doCreateLifecycleManager( Object aMaster ) {
    return getLifecycleManager( null );
  }

}
