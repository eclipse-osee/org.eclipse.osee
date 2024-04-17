/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;

/**
 * @author Donald G. Dunne
 */
public class XSprintHyperlinkWidgetDam extends XSprintHyperlinkWidget implements AttributeWidget {

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      if (artifact != null && artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamWf = atsApi.getWorkItemService().getTeamWf(artifact);
         ArtifactToken relSprint =
            atsApi.getRelationResolver().getRelatedOrSentinel(teamWf, AtsRelationTypes.AgileSprintToItem_AgileSprint);
         if (relSprint.isValid()) {
            sprint = atsApi.getAgileService().getAgileSprint(relSprint);
         }
      }
   }

   @Override
   public boolean handleSelection() {
      boolean selected = super.handleSelection();
      if (selected) {
         IAtsChangeSet changes = atsApi.createChangeSet("Set Sprint from Dam");
         changes.setRelation(teamWf, AtsRelationTypes.AgileSprintToItem_AgileSprint, sprint);
         changes.executeIfNeeded();
      }
      return selected;
   }

   @Override
   public Artifact getArtifact() {
      if (teamWf != null) {
         return (Artifact) teamWf.getStoreObject();
      }
      return null;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

}
