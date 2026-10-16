/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.xx;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsEnumArtWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Work Package selection where Work Package Enumerated Artifact is child to Team Def or Top Team Def marked with
 * WORK_PKG_STATIC_ID or widget configured EnumeratedArtifact. Selected options are loaded from attributes of that
 * artifact.
 */
@Component(service = XWidget.class, immediate = true)
public class XXWorkPackageEnumArtWidget extends XXStringsEnumArtWidget {

   public static final WidgetId ID = WidgetIdAts.XXWorkPackageEnumArtWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXWorkPackageEnumArtWidget() {
      super(ID, "Work Package");
      setAttributeType(AtsAttributeTypes.WorkPackage);
      set(XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   /**
    * Find enumerated artifact from children of teamDef or children of top team def
    */
   @Override
   public ArtifactToken getEnumeratedArt() {
      ArtifactToken enumArt = super.getEnumeratedArt();
      if (enumArt.isInvalid()) {
         if (xxWid.isTeamWf()) {
            Artifact teamDefArt = (Artifact) xxWid.getTeamDef().getStoreObject();
            for (Artifact child : teamDefArt.getChildren()) {
               if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                  setEnumeratedArt(child);
                  return child;
               }
            }
            IAtsTeamDefinition topTeamDef =
               AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(xxWid.getTeamDef());
            Artifact topTeamDefArt = (Artifact) topTeamDef.getStoreObject();
            for (Artifact child : topTeamDefArt.getChildren()) {
               if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                  setEnumeratedArt(child);
                  return child;
               }
            }
         }
      }
      return super.getEnumeratedArt();
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
