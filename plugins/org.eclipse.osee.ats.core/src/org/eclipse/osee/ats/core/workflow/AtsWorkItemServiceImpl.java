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
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final IAtsServices services;

   public AtsWorkItemServiceImpl(IAtsServices services, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.services = services;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   @Override
   public int getTransactionNumber(IAtsWorkItem workItem) throws OseeCoreException {
      ArtifactId artifact = services.getArtifactResolver().get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return services.getArtifactResolver().getTransactionNumber(artifact);
   }

   @Override
   public IStateToken getCurrentState(IAtsWorkItem workItem) throws OseeCoreException {
      ArtifactId artifact = services.getArtifactResolver().get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return workItem.getStateDefinition();
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      ArtifactId artifact = services.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      return services.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowToReview_Review,
         IAtsAbstractReview.class);
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) throws OseeCoreException {
      ArtifactId artifact = services.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      List<IAtsAbstractReview> reviews = new LinkedList<>();
      for (IAtsAbstractReview review : services.getRelationResolver().getRelated(teamWf,
         AtsRelationTypes.TeamWorkflowToReview_Review, IAtsAbstractReview.class)) {
         if (services.getAttributeResolver().getSoleAttributeValue(review, AtsAttributeTypes.RelatedToState, "").equals(
            state.getName())) {
            reviews.add(review);
         }
      }
      return reviews;
   }

   @Override
   public IAtsTeamWorkflow getFirstTeam(IAtsAction action) throws OseeCoreException {
      Collection<IAtsTeamWorkflow> related = getTeams(action);
      return related.isEmpty() ? null : related.iterator().next();
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeams(IAtsAction action) {
      ArtifactId artifact = services.getArtifactResolver().get(action);
      Conditions.checkNotNull(artifact, "action", "Can't Find Artifact matching [%s]", action.toString());
      Collection<IAtsTeamWorkflow> related = services.getRelationResolver().getRelated(action,
         AtsRelationTypes.ActionToWorkflow_WorkFlow, IAtsTeamWorkflow.class);
      return related;
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      services.clearImplementersCache(workItem);
   }

   @Override
   public Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeStateException {
      return services.validateWidgetTransition(workItem, toStateDef);
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return services.getTransitionListeners();
   }

   @Override
   public String getTargetedVersionStr(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion targetedVersion = services.getVersionService().getTargetedVersionByTeamWf(teamWf);
      if (targetedVersion != null) {
         return targetedVersion.getName();
      }
      return "";
   }

   @Override
   public String getArtifactTypeShortName(IAtsTeamWorkflow teamWf) {
      for (ITeamWorkflowProvider atsTeamWorkflow : teamWorkflowProvidersLazy.getProviders()) {
         String typeName = atsTeamWorkflow.getArtifactTypeShortName(teamWf);
         if (Strings.isValid(typeName)) {
            return typeName;
         }
      }
      return null;
   }

}
