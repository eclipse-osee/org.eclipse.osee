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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.ats.impl.internal.AtsServerService;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private final IAtsWorkItemFactory workItemFactory;
   private final IArtifactProvider artifactProvider;

   public AtsWorkItemServiceImpl(IAtsWorkItemFactory workItemFactory, IArtifactProvider artifactProvider) {
      this.workItemFactory = workItemFactory;
      this.artifactProvider = artifactProvider;
   }

   @Override
   public IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException {
      return new WorkData(workItemFactory.getWorkItem(workItem), artifactProvider.getArtifact(workItem));
   }

   @Override
   public IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException {
      return artifactProvider.getArtifact(workItem).getArtifactType();
   }

   @Override
   public Collection<Object> getAttributeValues(IAtsObject atsObject, IAttributeType attributeType) throws OseeCoreException {
      return AtsServerService.get().getArtifact(atsObject).getAttributeValues(attributeType);
   }

   @Override
   public boolean isOfType(IAtsWorkItem workItem, IArtifactType matchType) throws OseeCoreException {
      return artifactProvider.getArtifact(workItem).getArtifactType().matches(matchType);
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException {
      return workItemFactory.getWorkItem(workItem).getParentTeamWorkflow();
   }

   @Override
   public int getTransactionNumber(IAtsWorkItem workItem) throws OseeCoreException {
      return artifactProvider.getArtifact(workItem).getTransaction();
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeams(IAtsAction action) throws OseeCoreException {
      List<IAtsTeamWorkflow> teams = new ArrayList<IAtsTeamWorkflow>();
      for (ArtifactReadable art : AtsServerService.get().getArtifact(action).getRelated(
         AtsRelationTypes.ActionToWorkflow_WorkFlow)) {
         teams.add(AtsServerService.get().getWorkItemFactory().getTeamWf(art));
      }
      return teams;
   }

   @Override
   public IStateToken getCurrentState(IAtsWorkItem workItem) throws OseeCoreException {
      return new SimpleTeamState(getCurrentStateName(workItem), StateType.valueOf(AtsServerService.get().getArtifact(
         workItem).getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "")));
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf, IStateToken state) throws OseeCoreException {
      final List<IAtsTask> tasks = new ArrayList<IAtsTask>();
      for (ArtifactReadable art : AtsServerService.get().getArtifact(teamWf).getRelated(
         AtsRelationTypes.TeamWfToTask_Task)) {
         String relatedState = art.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "");
         if (state.getName().equals(relatedState)) {
            tasks.add(AtsServerService.get().getWorkItemFactory().getTask(art));
         }
      }
      return tasks;
   }

   @Override
   public Collection<? extends IAtsTask> getTasks(IAtsWorkItem workItem, IStateToken state) {
      if (workItem instanceof IAtsTeamWorkflow) {
         return getTasks((IAtsTeamWorkflow) workItem, state);
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      final List<IAtsAbstractReview> reviews = new ArrayList<IAtsAbstractReview>();
      for (ArtifactReadable art : AtsServerService.get().getArtifact(teamWf).getRelated(
         AtsRelationTypes.TeamWorkflowToReview_Review)) {
         reviews.add(AtsServerService.get().getWorkItemFactory().getReview(art));
      }
      return reviews;
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) throws OseeCoreException {
      final List<IAtsAbstractReview> reviews = new ArrayList<IAtsAbstractReview>();
      for (ArtifactReadable art : AtsServerService.get().getArtifact(teamWf).getRelated(
         AtsRelationTypes.TeamWorkflowToReview_Review)) {
         String relatedState = art.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "");
         if (state.getName().equals(relatedState)) {
            reviews.add(AtsServerService.get().getWorkItemFactory().getReview(art));
         }
      }
      return reviews;
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      final List<IAtsTask> tasks = new ArrayList<IAtsTask>();
      for (ArtifactReadable art : AtsServerService.get().getArtifact(teamWf).getRelated(
         AtsRelationTypes.TeamWfToTask_Task)) {
         tasks.add(AtsServerService.get().getWorkItemFactory().getTask(art));
      }
      return tasks;
   }

   @Override
   public IAtsTeamWorkflow getFirstTeam(IAtsAction action) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      Collection<IAtsTeamWorkflow> teams = getTeams(action);
      if (!teams.isEmpty()) {
         team = teams.iterator().next();
      }
      return team;
   }

   @Override
   public String getCurrentStateName(IAtsWorkItem workItem) throws OseeCoreException {
      return artifactProvider.getArtifact(workItem).getSoleAttributeValue(AtsAttributeTypes.CurrentState, "").replaceAll(
         ";.*$", "");
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      // do nothing; no cache on server
   }

   @Override
   public Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) {
      return Collections.emptyList();
   }

   @Override
   public Collection<IAtsTask> getTaskArtifacts(IAtsWorkItem workItem) throws OseeCoreException {
      if (workItem.isTeamWorkflow()) {
         return getTasks((IAtsTeamWorkflow) workItem);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return Collections.emptyList();
   }

   @Override
   public String getTargetedVersionStr(IAtsTeamWorkflow teamWf) {
      String version = "";
      try {
         ArtifactReadable art =
            artifactProvider.getArtifact(teamWf).getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).getOneOrNull();
         if (art != null) {
            version = art.getName();
         }
      } catch (OseeCoreException ex) {
         version = "exception: " + ex;
      }
      return version;
   }

   @Override
   public String getTeamName(IAtsTeamWorkflow teamWf) {
      return teamWf.getTeamDefinition().getName();
   }

}
