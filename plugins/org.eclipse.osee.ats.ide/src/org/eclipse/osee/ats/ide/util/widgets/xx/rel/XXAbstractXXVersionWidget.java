/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.xx.rel;

import java.util.Collection;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class XXAbstractXXVersionWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractXXVersionWidget(WidgetId widgetId, String label) {
      this(widgetId, label, RelationTypeSide.SENTINEL);
   }

   public XXAbstractXXVersionWidget(WidgetId widgetId, String label, RelationTypeSide relType) {
      super(widgetId, label);
      setRelationTypeSide(relType);
      // Default options
      set(XOption.WRITE, XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      if (xxWid.isTeamWf()) {
         Collection<IAtsVersion> versions = xxWid.getSelectableVersions();
         return AtsObjects.toArtifactTokens(versions);
      }
      return super.getSelectable();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected boolean isIconWidget() {
      return true;
   }

   /**
    * @return true if the version has been loaded (may still be null if no version is set)
    */
   protected abstract boolean isVersionLoaded();

   /**
    * Load the version from the server. This is called in a background thread.
    */
   protected abstract void loadVersion();

   /**
    * @return the version for this widget (may be null if not set)
    */
   protected abstract IAtsVersion getVersion();

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof ArtifactToken;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (!xxWid.isArtifact()) {
         return;
      }
      if (!isVersionLoaded()) {
         new Thread("Load " + getLabel()) {
            @Override
            public void run() {
               if (!isVersionLoaded()) {
                  loadVersion();
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     initializeSelected();
                  }
               });
            }
         }.start();
         return;
      }
      IAtsVersion version = getVersion();
      if (version != null) {
         setSelected(version.getArtifactToken(), false);
      }
      refresh();
   }

}
