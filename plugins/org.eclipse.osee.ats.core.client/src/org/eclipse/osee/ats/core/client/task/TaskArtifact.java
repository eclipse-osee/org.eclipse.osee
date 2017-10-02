/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends AbstractWorkflowArtifact implements IAtsTask, IATSStateMachineArtifact {
   Set<Long> taskHasNoParent = new HashSet<>();

   public TaskArtifact(Long id, String guid, BranchId branch, ArtifactTypeId artifactType) {
      super(id, guid, branch, artifactType);
   }

   public boolean isRelatedToParentWorkflowCurrentState()  {
      return getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, "").equals(
         getParentAWA().getStateMgr().getCurrentStateName());
   }

   public boolean isRelatedToUsed()  {
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
   public double getManHrsPerDayPreference()  {
      return getParentAWA().getManHrsPerDayPreference();
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA()  {
      if (parentAwa != null) {
         return parentAwa;
      }
      parentAwa = (AbstractWorkflowArtifact) getRelatedArtifactOrNull(AtsRelationTypes.TeamWfToTask_TeamWf);
      // only display error once
      if (parentAwa == null && !taskHasNoParent.contains(getId())) {
         taskHasNoParent.add(getId());
         throw new OseeStateException("Task has no parent [%s]", getAtsId());
      }
      return parentAwa;
   }

   @Override
   public ActionArtifact getParentActionArtifact()  {
      if (parentAction != null) {
         return parentAction;
      }
      parentAction = getParentTeamWorkflow().getParentActionArtifact();
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow()  {
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
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   @Override
   public String getWorldViewSWEnhancement()  {
      AbstractWorkflowArtifact awa = getParentAWA();
      if (awa != null) {
         return awa.getWorldViewSWEnhancement();
      }
      return "";
   }
}
