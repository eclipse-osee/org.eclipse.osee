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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkArtEnumeratedArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWorkPackageArtWidget extends XHyperlinkArtEnumeratedArtWidget {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkWorkPackageArtWidget;

   public XHyperlinkWorkPackageArtWidget() {
      super(ID, "Work Package");
      setAttributeType(AtsAttributeTypes.WorkPackage);
   }

   /**
    * Find enumerated artifact from children of teamDef or children of top team def
    */
   @Override
   public ArtifactToken getEnumeratedArt() {
      if (getEnumeratedArt().isInvalid()) {
         Artifact wfArt = getArtifact();
         if (wfArt != null && wfArt instanceof IAtsTeamWorkflow) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) wfArt;
            IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
            Artifact teamDefArt = (Artifact) teamDef.getStoreObject();
            for (Artifact child : teamDefArt.getChildren()) {
               if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                  setEnumeratedArt(child);
                  break;
               }
            }
            if (getEnumeratedArt().isInvalid()) {
               IAtsTeamDefinition topTeamDef =
                  AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(teamDef);
               Artifact topTeamDefArt = (Artifact) topTeamDef.getStoreObject();
               for (Artifact child : topTeamDefArt.getChildren()) {
                  if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                     setEnumeratedArt(child);
                     break;
                  }
               }
            }
         }
         // Only check once, so set to SENTINEL if not found
         if (getEnumeratedArt().isInvalid()) {
            setEnumeratedArt(ArtifactToken.SENTINEL);
         }
      }
      return getEnumeratedArt();
   }

}
