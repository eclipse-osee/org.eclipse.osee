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
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class UserWorldSearchItem {

   private final Collection<IAtsTeamDefinition> teamDefs;
   private final Collection<IAtsVersion> versions;
   private final Collection<UserSearchOption> options;
   private final IAtsUser user;
   private final String selectedState;

   public static enum UserSearchOption {
      None,
      Assignee,
      Favorites,
      Subscribed,
      Originator,
      IncludeCompleted,
      IncludeCancelled,
      IncludeTeamWorkflows,
      IncludeReviews,
      IncludeTasks
   };

   public UserWorldSearchItem(IAtsUser user, Collection<IAtsTeamDefinition> teamDefs, Collection<IAtsVersion> versions, String selectedState, UserSearchOption... userSearchOption) {
      this.user = user;
      this.teamDefs = teamDefs;
      this.versions = versions;
      this.selectedState = selectedState;
      this.options = Collections.getAggregate(userSearchOption);
   }

   public Collection<AbstractWorkflowArtifact> performSearch() throws OseeCoreException {
      Set<AbstractWorkflowArtifact> searchArts = new HashSet<>();
      if (options.contains(UserSearchOption.Originator)) {
         searchArts.addAll(getOriginatorArtifacts());
      } else if (options.contains(UserSearchOption.Subscribed)) {
         searchArts.addAll(getSubscribedArtifacts());
      } else if (options.contains(UserSearchOption.Favorites)) {
         searchArts.addAll(getFavoritesArtifacts());
      } else if (options.contains(UserSearchOption.Assignee)) {
         searchArts.addAll(Collections.castMatching(AbstractWorkflowArtifact.class, AtsUtil.getAssigned(user)));
         // If include cancelled or completed, need to perform extra search
         // Note: Don't need to do this for Originator, Subscribed or Favorites, cause it does completed canceled in it's own searches
         if (options.contains(UserSearchOption.IncludeCancelled) || options.contains(UserSearchOption.IncludeCompleted)) {
            searchArts.addAll(WorkflowManager.getAwas(ArtifactQuery.getArtifactListFromAttribute(
               AtsAttributeTypes.State, "<" + user.getUserId() + ">", AtsUtilCore.getAtsBranch(),
               QueryOption.CONTAINS_MATCH_OPTIONS)));
         }
      }

      Collection<Class<?>> filterClasses = new ArrayList<>();
      if (!options.contains(UserSearchOption.IncludeReviews)) {
         filterClasses.add(AbstractReviewArtifact.class);
      }
      if (!options.contains(UserSearchOption.IncludeTeamWorkflows)) {
         filterClasses.add(TeamWorkFlowArtifact.class);
      }
      if (!options.contains(UserSearchOption.IncludeTasks)) {
         filterClasses.add(TaskArtifact.class);
      }

      Collection<AbstractWorkflowArtifact> filteredArts = WorkflowManager.filterOutTypes(searchArts, filterClasses);

      if (teamDefs != null && teamDefs.size() > 0) {
         filteredArts = WorkflowManager.getTeamDefinitionWorkflows(filteredArts, teamDefs);
      }

      if (versions != null && versions.size() > 0) {
         filteredArts = WorkflowManager.getVersionWorkflows(filteredArts, versions);
      }

      if (Strings.isValid(selectedState)) {
         filteredArts = WorkflowManager.getAwas(WorkflowManager.filterState(selectedState, filteredArts));
      }

      // Handle include completed/cancelled option
      if (options.contains(UserSearchOption.IncludeCompleted) && options.contains(UserSearchOption.IncludeCancelled)) {
         return filteredArts;
      }

      if (!options.contains(UserSearchOption.IncludeCancelled)) {
         filteredArts = WorkflowManager.filterOutCancelled(filteredArts);
      }

      if (!options.contains(UserSearchOption.IncludeCompleted)) {
         filteredArts = WorkflowManager.filterOutCompleted(filteredArts);
      }

      return filteredArts;
   }

   private Collection<AbstractWorkflowArtifact> getOriginatorArtifacts() throws OseeCoreException {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.CreatedBy,
         user.getUserId(), AtsUtilCore.getAtsBranch()));
   }

   private Collection<AbstractWorkflowArtifact> getSubscribedArtifacts() throws OseeCoreException {
      return AtsClientService.get().getUserServiceClient().getOseeUser(user).getRelatedArtifactsOfType(
         AtsRelationTypes.SubscribedUser_Artifact, AbstractWorkflowArtifact.class);
   }

   private Collection<AbstractWorkflowArtifact> getFavoritesArtifacts() throws OseeCoreException {
      return AtsClientService.get().getUserServiceClient().getOseeUser(user).getRelatedArtifactsOfType(
         AtsRelationTypes.FavoriteUser_Artifact, AbstractWorkflowArtifact.class);
   }

}
