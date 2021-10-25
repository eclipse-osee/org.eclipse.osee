/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;

/**
 * @author Audrey Denk
 */
public class XProductLineApprovalWidget extends XAbstractSignDateAndByButton {

   public static String WIDGET_ID = XProductLineApprovalWidget.class.getSimpleName();

   public XProductLineApprovalWidget() {
      super("PL ARB Approved", "Sign or clear changes", AtsAttributeTypes.ProductLineApprovedDate,
         AtsAttributeTypes.ProductLineApprovedBy, FrameworkImage.RUN_EXC, true);
   }

   @Override
   public boolean userHasPermission() {
      Artifact storeArt = getArtifact();
      String teamArtifactId =
         storeArt.getSoleAttribute(AtsAttributeTypes.TeamDefinitionReference).getValue().toString();
      Artifact teamArtifact = ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(teamArtifactId), CoreBranches.COMMON);
      if (teamArtifact.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead).contains(UserManager.getUser())) {
         return true;
      }
      AWorkbench.popup("Insufficient Privileges",
         "Only a team lead may perform this action.  Team Leads for this workflow are: " + teamArtifact.getRelatedArtifacts(
            AtsRelationTypes.TeamLead_Lead).toString());
      return false;
   }

}
