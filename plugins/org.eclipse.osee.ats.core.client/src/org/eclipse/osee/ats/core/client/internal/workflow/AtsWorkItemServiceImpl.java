/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.workflow;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.internal.IAtsWorkItemArtifactService;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AtsWorkData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private final IAtsWorkItemArtifactService workItemArtifactProvider;

   public AtsWorkItemServiceImpl(IAtsWorkItemArtifactService workItemArtifactProvider) {
      this.workItemArtifactProvider = workItemArtifactProvider;
   }

   @Override
   public IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException {
      return new AtsWorkData((AbstractWorkflowArtifact) workItemArtifactProvider.get(workItem));
   }

   @Override
   public IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException {
      return workItemArtifactProvider.get(workItem).getArtifactType();
   }

   @Override
   public Collection<Object> getAttributeValues(IAtsObject workItem, IAttributeType attributeType) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());

      IAttributeType attrType = AttributeTypeManager.getType(attributeType);
      if (attrType == null) {
         throw new OseeArgumentException(String.format("Can't resolve Attribute Type [%s]", attributeType));
      }

      return artifact.getAttributeValues(attributeType);
   }

   @Override
   public boolean isOfType(IAtsWorkItem workItem, IArtifactType matchType) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return artifact.isOfType(matchType);
   }

   @Override
   public IAtsWorkItem getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         return awa.getParentTeamWorkflow();
      }
      return null;
   }

   @Override
   public int getTransactionNumber(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return artifact.getTransactionNumber();
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeams(IAtsAction action) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(action);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", action.toString());
      return action.getTeamWorkflows();
   }

   @Override
   public IStateToken getCurrentState(IAtsWorkItem workItem) throws OseeCoreException {
      IStateToken state = null;
      Artifact artifact = workItemArtifactProvider.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         state = awa.getStateDefinitionByName(awa.getCurrentStateName());
      }
      return state;
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow atsObject, IStateToken relatedToState) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(atsObject);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", atsObject.toString());
      return Collections.castAll(((TeamWorkFlowArtifact) atsObject).getTaskArtifacts());
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow atsObject) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(atsObject);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", atsObject.toString());
      return Collections.castAll(ReviewManager.getReviews((TeamWorkFlowArtifact) atsObject));
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow atsObject, IStateToken state) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(atsObject);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", atsObject.toString());
      return Collections.castAll(ReviewManager.getReviews((TeamWorkFlowArtifact) atsObject, state));
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow iAtsTeamWorkflow) throws OseeCoreException {
      Artifact artifact = workItemArtifactProvider.get(iAtsTeamWorkflow);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", iAtsTeamWorkflow.toString());
      return Collections.castAll(((TeamWorkFlowArtifact) iAtsTeamWorkflow).getTaskArtifacts());
   }

   @Override
   public IAtsTeamWorkflow getFirstTeam(IAtsAction action) throws OseeCoreException {
      IAtsTeamWorkflow firstTeam = null;
      Artifact artifact = workItemArtifactProvider.get(action);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", action.toString());
      Collection<IAtsTeamWorkflow> teamWorkflows = action.getTeamWorkflows();
      if (!teamWorkflows.isEmpty()) {
         firstTeam = teamWorkflows.iterator().next();
      }
      return firstTeam;
   }

   public IAtsWorkItemArtifactService getWorkItemArtifactProvider() {
      return workItemArtifactProvider;
   }

   @Override
   public boolean isReadOnly(IAtsWorkItem workItem) {
      boolean readOnly = true;
      try {
         Artifact artifact = AtsClientService.get().getArtifact(workItem);
         readOnly = artifact.isReadOnly();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return readOnly;
   }

   @Override
   public boolean isAccessControlWrite(IAtsWorkItem workItem) {
      boolean isWrite = false;
      try {
         Artifact artifact = AtsClientService.get().getArtifact(workItem);
         isWrite = AccessControlManager.hasPermission(artifact, PermissionEnum.WRITE);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return isWrite;
   }

}
