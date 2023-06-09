package com.hazard157.prisex24.m5.svin;

import static com.hazard157.prisex24.m5.IPsxM5Constants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.valeds.singlelookup.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;

import com.hazard157.prisex24.*;
import com.hazard157.prisex24.m5.std.*;
import com.hazard157.psx.common.stuff.svin.*;

/**
 * Controller for SVIN editor {@link IM5EntityPanel}.
 *
 * @author hazard157
 */
class SvinM5EntityPanelWithValedsController
    extends M5EntityPanelWithValedsController<Svin>
    implements IPsxGuiContextable {

  SvinM5EntityPanelWithValedsController() {
    // nop
  }

  @SuppressWarnings( "unchecked" )
  private void updateCamerasListOfEpisode() {
    IValedControl<String> epEditor = (IValedControl<String>)editors().getByKey( PsxM5EpisodeIdFieldDef.FID_EPISODE_ID );
    AbstractValedSingleLookupEditor<String> epCamerasList =
        (AbstractValedSingleLookupEditor<String>)editors().getByKey( FID_CAMERA_ID );
    String episodeId = epEditor.canGetValue().isError() ? null : epEditor.getValue();
    IStringList camIds;
    if( episodeId != null ) {
      camIds = unitSourceVideos().episodeSourceVideos( episodeId ).keys();
    }
    else {
      camIds = unitCameras().items().keys();
    }
    String selCamId = epCamerasList.canGetValue().isError() ? null : epCamerasList.getValue();
    epCamerasList.setLookupProvider( new IM5LookupProvider<String>() {

      @Override
      public IList<String> listItems() {
        return camIds;
      }

      @Override
      public String getName( String aItem ) {
        return aItem;
      }

    } );
    epCamerasList.setValue( selCamId );
  }

  @Override
  public void afterEditorsCreated() {
    updateCamerasListOfEpisode();
  }

  @Override
  public void afterSetValues( IM5Bunch<Svin> aValues ) {
    updateCamerasListOfEpisode();
  }

  @Override
  public boolean doProcessEditorValueChange( IValedControl<?> aEditor, IM5FieldDef<Svin, ?> aFieldDef,
      boolean aEditFinished ) {
    if( aFieldDef.id().equals( PsxM5EpisodeIdFieldDef.FID_EPISODE_ID ) ) {
      updateCamerasListOfEpisode();
    }
    return true;
  }

}
