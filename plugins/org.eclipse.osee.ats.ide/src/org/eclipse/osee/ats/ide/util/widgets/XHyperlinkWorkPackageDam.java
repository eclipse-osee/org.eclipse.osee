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

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelEnumeratedArtDam;

public class XHyperlinkWorkPackageDam extends XHyperlinkLabelEnumeratedArtDam {

   public XHyperlinkWorkPackageDam() {
      super("Work Package");
   }

   /**
    * Find enumerated artifact from children of teamDef or children of top team def
    */
   @Override
   public ArtifactToken getEnumeratedArt() {
      if (enumeratedArt == null) {
         Artifact wfArt = getArtifact();
         if (wfArt != null && wfArt instanceof IAtsTeamWorkflow) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) wfArt;
            IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
            Artifact teamDefArt = (Artifact) teamDef.getStoreObject();
            for (Artifact child : teamDefArt.getChildren()) {
               if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                  enumeratedArt = child;
                  break;
               }
            }
            if (enumeratedArt == null) {
               IAtsTeamDefinition topTeamDef =
                  AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(teamDef);
               Artifact topTeamDefArt = (Artifact) topTeamDef.getStoreObject();
               for (Artifact child : topTeamDefArt.getChildren()) {
                  if (child.hasTag(AtsUtil.WORK_PKG_STATIC_ID)) {
                     enumeratedArt = child;
                     break;
                  }
               }
            }
         }
         // Only check once, so set to SENTINEL if not found
         if (enumeratedArt == null) {
            enumeratedArt = ArtifactToken.SENTINEL;
         }
      }
      return enumeratedArt;
   }

}
