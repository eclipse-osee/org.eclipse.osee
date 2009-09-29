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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.SMAUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class UserWorldSearchItem {

   private final Collection<TeamDefinitionArtifact> teamDefs;
   private final Collection<VersionArtifact> versions;
   private final Collection<UserSearchOption> options;
   private final User user;

   public static enum UserSearchOption {
      None,
      Favorites,
      Subscribed,
      Originator,
      IncludeCompleted,
      IncludeCancelled,
      IncludeTeamWorkflows,
      IncludeReviews,
      IncludeTasks
   };

   public UserWorldSearchItem(User user, Collection<TeamDefinitionArtifact> teamDefs, Collection<VersionArtifact> versions, UserSearchOption... userSearchOption) {
      this.user = user;
      this.teamDefs = teamDefs;
      this.versions = versions;
      this.options = Collections.getAggregate(userSearchOption);
   }

   public Collection<StateMachineArtifact> performSearch() throws OseeCoreException {
      Set<StateMachineArtifact> searchArts = new HashSet<StateMachineArtifact>();
      if (options.contains(UserSearchOption.Originator)) {
         searchArts.addAll(getOriginatorArtifacts());
      } else if (options.contains(UserSearchOption.Subscribed)) {
         searchArts.addAll(getSubscribedArtifacts());
      } else if (options.contains(UserSearchOption.Favorites)) {
         searchArts.addAll(getFavoritesArtifacts());
      } else {
         searchArts.addAll(SMAUtil.getSMAs(RelationManager.getRelatedArtifacts(Arrays.asList(user), 1,
               CoreRelationEnumeration.Users_Artifact)));
         // If include cancelled or completed, need to perform extra search
         // Note: Don't need to do this for Originator, Subscribed or Favorites, cause it does completed canceled in it's own searches
         if (options.contains(UserSearchOption.IncludeCancelled) || options.contains(UserSearchOption.IncludeCompleted)) {
            searchArts.addAll(SMAUtil.getSMAs(ArtifactQuery.getArtifactListFromAttribute(
                  ATSAttributes.STATE_ATTRIBUTE.getStoreName(), "%<" + user.getUserId() + ">%", AtsUtil.getAtsBranch())));
         }
      }

      Collection<Class<?>> filterClasses = new ArrayList<Class<?>>();
      if (!options.contains(UserSearchOption.IncludeReviews)) {
         filterClasses.add(ReviewSMArtifact.class);
      }
      if (!options.contains(UserSearchOption.IncludeTeamWorkflows)) {
         filterClasses.add(TeamWorkFlowArtifact.class);
      }
      if (!options.contains(UserSearchOption.IncludeTasks)) {
         filterClasses.add(TaskArtifact.class);
      }

      Collection<StateMachineArtifact> filteredArts = SMAUtil.filterOutTypes(searchArts, filterClasses);

      if (teamDefs != null && teamDefs.size() > 0) {
         filteredArts =
               SMAUtil.getTeamDefinitionWorkflows(filteredArts, teamDefs);
      }

      if (versions != null && versions.size() > 0) {
         filteredArts = SMAUtil.getVersionWorkflows(filteredArts, versions);
      }

      // Handle include completed/cancelled option
      if (options.contains(UserSearchOption.IncludeCompleted) && options.contains(UserSearchOption.IncludeCancelled)) {
         return filteredArts;
      }

      if (!options.contains(UserSearchOption.IncludeCancelled)) {
         filteredArts =
               SMAUtil.filterOutState(filteredArts, java.util.Collections.singleton(DefaultTeamState.Cancelled.name()));
      }

      if (!options.contains(UserSearchOption.IncludeCompleted)) {
         filteredArts =
               SMAUtil.filterOutState(filteredArts, java.util.Collections.singleton(DefaultTeamState.Completed.name()));
      }

      return filteredArts;
   }

   private Collection<StateMachineArtifact> getOriginatorArtifacts() throws OseeCoreException {
      Collection<StateMachineArtifact> originators = new ArrayList<StateMachineArtifact>();
      Collection<StateMachineArtifact> artifacts =
            Collections.castAll(ArtifactQuery.getArtifactListFromAttribute(ATSAttributes.LOG_ATTRIBUTE.getStoreName(),
                  "%type=\"Originated\" userId=\"" + user.getUserId() + "\"%", AtsUtil.getAtsBranch()));
      // omit historical originators; list current originators
      for (StateMachineArtifact art : artifacts) {
         if (art.getWorldViewOriginator().equals(user.getName())) {
            originators.add(art);
         }
      }
      return originators;
   }

   private Collection<StateMachineArtifact> getSubscribedArtifacts() throws OseeCoreException {
      return user.getRelatedArtifactsOfType(AtsRelation.SubscribedUser_Artifact, StateMachineArtifact.class);
   }

   private Collection<StateMachineArtifact> getFavoritesArtifacts() throws OseeCoreException {
      return user.getRelatedArtifactsOfType(AtsRelation.FavoriteUser_Artifact, StateMachineArtifact.class);
   }

}
