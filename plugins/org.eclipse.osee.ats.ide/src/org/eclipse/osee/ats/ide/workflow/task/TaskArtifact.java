/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends AbstractWorkflowArtifact implements IAtsTask, IATSStateMachineArtifact {
   Set<Long> taskHasNoParent = new HashSet<>();

   public TaskArtifact(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   public boolean isRelatedToParentWorkflowCurrentState() {
      return getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, "").equals(
         ((IAtsWorkItem) getParentAWA()).getCurrentStateName());
   }

   public boolean isRelatedToUsed() {
      return Strings.isValid(getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, ""));
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public double getManHrsPerDayPreference() {
      return getParentAWA().getManHrsPerDayPreference();
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() {
      if (parentAwa != null) {
         return parentAwa;
      }
      parentAwa = (AbstractWorkflowArtifact) getRelatedArtifactOrNull(AtsRelationTypes.TeamWfToTask_TeamWorkflow);

      // only display error once
      if (parentAwa == null && !taskHasNoParent.contains(getId())) {
         taskHasNoParent.add(getId());
         throw new OseeStateException("Task has no parent [%s]", getAtsId());
      }
      return parentAwa;
   }

   @Override
   public IAtsAction getParentAction() {
      if (parentAction != null) {
         return parentAction;
      }
      parentAction =
         (IAtsAction) ((AbstractWorkflowArtifact) getParentTeamWorkflow()).getParentAction().getStoreObject();
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      if (parentTeamArt != null) {
         return parentTeamArt;
      }
      AbstractWorkflowArtifact awa = getParentAWA();
      if (awa != null && awa.isTeamWorkflow()) {
         parentTeamArt = (TeamWorkFlowArtifact) awa;
      }
      return parentTeamArt;
   }

   @Override
   public BooleanState isParentAtsArtifactLoaded() {
      return parentTeamArt == null ? BooleanState.No : BooleanState.Yes;
   }

}
