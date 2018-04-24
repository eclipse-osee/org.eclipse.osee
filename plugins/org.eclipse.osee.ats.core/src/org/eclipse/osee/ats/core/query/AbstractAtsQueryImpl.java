/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryFilter;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryImpl implements IAtsQuery {

   protected final List<AtsAttributeQuery> andAttr;
   protected final List<AtsAttributeQuery> teamWorkflowAttr;
   protected final HashMap<RelationTypeSide, List<IAtsObject>> andRels;
   protected Collection<Long> teamDefIds;
   protected Collection<StateType> stateTypes;
   protected Collection<WorkItemType> workItemTypes;
   protected Collection<IArtifactType> artifactTypes;
   protected Collection<ArtifactId> artifactIds;
   protected final AtsApi atsApi;
   protected Collection<Long> aiIds;
   protected Long versionId;
   protected String stateName;
   protected String colorTeam;
   protected Long programId;
   protected Long insertionId;
   protected Long insertionActivityId;
   protected Long workPackageId;
   protected List<ArtifactId> onlyIds = null;
   private ReleasedOption releasedOption;
   protected final List<IAtsQueryFilter> queryFilters;

   public AbstractAtsQueryImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
      andRels = new HashMap<>();
      andAttr = new ArrayList<>();
      stateTypes = new ArrayList<>();
      workItemTypes = new ArrayList<>();
      artifactTypes = new ArrayList<>();
      teamDefIds = new ArrayList<>();
      aiIds = new ArrayList<>();
      artifactIds = new ArrayList<>();
      teamWorkflowAttr = new ArrayList<>();
      queryFilters = new ArrayList<>();
   }

   @Override
   public Collection<ArtifactId> getItemIds() {
      onlyIds = new LinkedList<>();
      getItems();
      return onlyIds;
   }

   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems() {
      Set<IArtifactType> allArtTypes = getAllArtTypes();

      List<IArtifactType> teamWorkflowArtTypes = getTeamWorkflowArtTypes(allArtTypes);
      boolean teamsTypeDefOrAisOrVersionSearched = isTeamTypeDefAisOrVersionSearched(allArtTypes);

      /**
       * First, search for Team Workflows
       */
      Collection<T> teamWfs = Collections.emptyList();
      Set<T> allResults = new HashSet<>();
      if (!teamWorkflowArtTypes.isEmpty()) {
         teamWfs = getTeamWorkflows(teamWorkflowArtTypes, allResults, allArtTypes);
      }
      if (allArtTypes.contains(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         teamWfs = getTeamWorkflows(allArtTypes, allResults, allArtTypes);
      }

      /**
       * If team workflow's searched by Team Definition, Actionable Item or Version were searched, then the child tasks
       * and reviews are what to use to search against
       */
      if (!teamWorkflowArtTypes.isEmpty() && teamsTypeDefOrAisOrVersionSearched) {
         getTasksAndReviewsFromResultingTeamWfs(teamWfs, allResults, allArtTypes);
      }

      /**
       * Else, perform task and review searches as normal
       */
      else {
         getTasksFromSearchCriteria(allResults, allArtTypes);
         getReviewsFromSearchCriteria(allResults, allArtTypes);
      }

      /**
       * Search Goals, Sprints and Backlogs as normal
       */
      getGoalsFromSearchCriteria(allResults, allArtTypes);
      getSprintsFromSearchCriteria(allResults, allArtTypes);

      for (IAtsQueryFilter filter : queryFilters) {
         allResults = filter.applyFilter(allResults);
      }
      return allResults;
   }

   public abstract Collection<ArtifactId> runQuery();

   @SuppressWarnings("unchecked")
   private <T> Collection<T> collectResults(Set<T> allResults, Set<IArtifactType> allArtTypes) {
      Set<T> workItems = new HashSet<>();
      if (isOnlyIds()) {
         onlyIds.addAll(handleReleaseOption(queryGetIds()));
      }
      // filter on original artifact types
      else {
         Collection<ArtifactId> artifacts = runQuery();
         for (ArtifactId artifact : artifacts) {
            if (allArtTypes.isEmpty() || isArtifactTypeMatch(artifact, allArtTypes)) {
               if (artifact instanceof ArtifactToken) {
                  IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem((ArtifactToken) artifact);
                  if (workItem != null) {
                     workItems.add((T) workItem);
                  }
               }
            }
         }
         addtoResultsWithNullCheck(allResults, handleReleasedOption(workItems));
      }
      return workItems;
   }

   private <T> void addtoResultsWithNullCheck(Set<T> allResults, Collection<? extends T> workItems) {
      if (workItems.contains(null)) {
         System.err.println("Null found in results.");
      } else {
         allResults.addAll(workItems);
      }
   }

   private Collection<ArtifactId> handleReleaseOption(List<ArtifactId> queryGetIds) {
      if (releasedOption != null && releasedOption != ReleasedOption.Both) {
         throw new UnsupportedOperationException("This option not supported");
      }
      return queryGetIds;
   }

   private <T> Collection<? extends T> handleReleasedOption(Set<T> workItems) {
      if (releasedOption == null) {
         return workItems;
      }
      Set<T> results = new HashSet<>();
      if (isVersionSpecified()) {
         for (T workItem : workItems) {
            IAtsVersion version = atsApi.getVersionService().getTargetedVersion((IAtsWorkItem) workItem);
            if (version != null) {
               if (releasedOption == ReleasedOption.Released && version.isReleased()) {
                  results.add(workItem);
               } else if (releasedOption == ReleasedOption.UnReleased && !version.isReleased()) {
                  results.add(workItem);
               }
            }
         }
      }
      return results;
   }

   private boolean isArtifactTypeMatch(ArtifactId artifact, Collection<IArtifactType> artTypes) {
      if (artTypes.isEmpty()) {
         return true;
      }
      for (IArtifactType artType : artTypes) {
         if (atsApi.getArtifactResolver().isOfType(artifact, artType)) {
            return true;
         }
      }
      return false;
   }

   private <T> void getTasksFromSearchCriteria(Set<T> allResults, Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.Task)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         getBaseSearchCriteria(artTypes, true, allArtTypes);

         // teamDef, ai and version
         if (isTeamTypeDefAisOrVersionSearched(allArtTypes)) {
            List<ArtifactId> teamWfIds = getRelatedTeamWorkflowIdsBasedOnTeamDefsAisAndVersions(teamWorkflowAttr);
            queryAndRelatedTo(AtsRelationTypes.TeamWfToTask_TeamWf, teamWfIds);
         }

         addEvConfigCriteria();

         collectResults(allResults, allArtTypes);
      }

   }

   private <T> void getReviewsFromSearchCriteria(Set<T> allResults, Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = getReviewArtifactTypes(allArtTypes);
      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         getBaseSearchCriteria(artTypes, true, allArtTypes);

         // teamDef, ai and version
         if (isTeamTypeDefAisOrVersionSearched(allArtTypes)) {
            List<ArtifactId> teamWfIds = getRelatedTeamWorkflowIdsBasedOnTeamDefsAisAndVersions(teamWorkflowAttr);
            queryAndRelatedTo(AtsRelationTypes.TeamWorkflowToReview_Team, teamWfIds);
         }

         collectResults(allResults, allArtTypes);
      }
   }

   private <T> void getSprintsFromSearchCriteria(Set<T> allResults, Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.AgileSprint)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();
         getBaseSearchCriteria(artTypes, true, allArtTypes);
         collectResults(allResults, allArtTypes);
      }
   }

   private <T> void getGoalsFromSearchCriteria(Set<T> allResults, Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.Goal) || workItemTypes.contains(WorkItemType.AgileBacklog)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();
         getBaseSearchCriteria(artTypes, true, allArtTypes);

         boolean isAgileSpecified = workItemTypes.contains(WorkItemType.AgileBacklog);
         boolean isGoalSpecified = workItemTypes.contains(WorkItemType.Goal);

         if (isAgileSpecified && !isGoalSpecified) {
            queryAndExists(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         } else if (isGoalSpecified && !isAgileSpecified) {
            queryAndNotExists(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         }
         collectResults(allResults, allArtTypes);
      }
   }

   public abstract void queryAndNotExists(RelationTypeSide relationTypeSide);

   public abstract void queryAndExists(RelationTypeSide relationTypeSide);

   private boolean typeIsSpecified(IArtifactType parentArtType, Set<IArtifactType> allArtTypes) {
      for (IArtifactType artifactType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artifactType, parentArtType)) {
            return true;
         }
      }
      return false;
   }

   private List<IArtifactType> getReviewArtifactTypes(Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      boolean isReviewSpecified =
         workItemTypes.contains(WorkItemType.Review) || typeIsSpecified(AtsArtifactTypes.ReviewArtifact, allArtTypes);
      boolean isPeerSpecified =
         workItemTypes.contains(WorkItemType.PeerReview) || typeIsSpecified(AtsArtifactTypes.PeerToPeerReview,
            allArtTypes);
      boolean isDecisionSpecified =
         workItemTypes.contains(WorkItemType.DecisionReview) || typeIsSpecified(AtsArtifactTypes.DecisionReview,
            allArtTypes);
      for (IArtifactType artType : allArtTypes) {
         if (isReviewSpecified && atsApi.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.ReviewArtifact)) {
            artTypes.add(artType);
         } else if (isPeerSpecified && atsApi.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.PeerToPeerReview)) {
            artTypes.add(artType);

         } else if (isDecisionSpecified && atsApi.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.DecisionReview)) {
            artTypes.add(artType);
         }
      }
      return artTypes;
   }

   private <T> void getTasksAndReviewsFromResultingTeamWfs(Collection<T> teamWfs, Set<T> allResults, Set<IArtifactType> allArtTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.Task)) {
            artTypes.add(artType);
         }
      }
      artTypes.addAll(getReviewArtifactTypes(allArtTypes));

      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         List<Long> taskReviewIds = new ArrayList<>();
         for (T teamWf : teamWfs) {
            for (IAtsTask task : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) teamWf)) {
               taskReviewIds.add(task.getId());
            }
            for (IAtsAbstractReview review : atsApi.getReviewService().getReviews((IAtsTeamWorkflow) teamWf)) {
               taskReviewIds.add(review.getId());
            }
         }
         getBaseSearchCriteria(artTypes, false, allArtTypes);

         // team def, ai, version are all covered by team search

         // Start with known task ids
         addIdCriteria(taskReviewIds);

         addEvConfigCriteria();

         collectResults(allResults, allArtTypes);
      }
   }

   private <T extends IAtsWorkItem> Collection<T> getTeamWorkflows(Collection<IArtifactType> teamWorkflowArtTypes, Set<T> allResults, Set<IArtifactType> allArtTypes) {
      createQueryBuilder();
      getBaseSearchCriteria(teamWorkflowArtTypes, true, allArtTypes);

      addTeamWorkflowAttributeCriteria();

      addTeamDefCriteria();

      addAiCriteria();

      addVersionCriteria();

      addEvConfigCriteria();

      return collectResults(allResults, allArtTypes);
   }

   private void addEvConfigCriteria() {
      addColorTeamCriteria();

      addWorkPackageCriteria();

      addInsertionActivityCriteria();

      addInsertionCriteria();

      addProgramCriteria();
   }

   protected boolean isProgramSpecified() {
      return programId != null && programId > 0;
   }

   protected boolean isInsertionSpecified() {
      return insertionId != null && insertionId > 0;
   }

   protected boolean isColorTeamSpecified() {
      return Strings.isValid(colorTeam);
   }

   protected boolean isVersionSpecified() {
      return versionId != null && versionId > 0;
   }

   protected boolean isInsertionActivitySpecified() {
      return insertionActivityId != null && insertionActivityId > 0;
   }

   protected boolean isWorkPackageSpecified() {
      return workPackageId != null && workPackageId > 0;
   }

   public abstract void createQueryBuilder();

   public abstract void queryAndIsOfType(IArtifactType artifactType);

   public boolean isOnlyIds() {
      return onlyIds != null;
   }

   public abstract List<ArtifactId> queryGetIds();

   /**
    * Return team workflow ids based on teamdef, ai and version criteria to use in relatedTo criteria.
    */
   public abstract List<ArtifactId> getRelatedTeamWorkflowIdsBasedOnTeamDefsAisAndVersions(List<AtsAttributeQuery> teamWorkflowAttr);

   private Set<IArtifactType> getAllArtTypes() {
      Set<IArtifactType> allArtTypes = new HashSet<>();
      if (artifactTypes != null && !artifactTypes.isEmpty()) {
         allArtTypes.addAll(artifactTypes);
      } else {
         allArtTypes = getArtifactTypesFromWorkItemTypes();
      }
      return allArtTypes;
   }

   private List<IArtifactType> getTeamWorkflowArtTypes(Set<IArtifactType> allArtTypes) {
      List<IArtifactType> teamWorkflowArtTypes = new LinkedList<>();
      for (IArtifactType artType : allArtTypes) {
         if (atsApi.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.TeamWorkflow)) {
            teamWorkflowArtTypes.add(artType);
         }
      }
      return teamWorkflowArtTypes;
   }

   private boolean isTeamTypeDefAisOrVersionSearched(Set<IArtifactType> allArtTypes) {
      boolean teamDefsSearched = isTeamDefSpecified();
      boolean aisSearched = isActionableItemSpecified();
      boolean versionSearched = versionId != null && versionId > 0L;
      boolean teamWfAttrSpecified = isTeamWfAttrSpecified();
      return teamDefsSearched || teamWfAttrSpecified || aisSearched || versionSearched;
   }

   private boolean isActionableItemSpecified() {
      return aiIds != null && !aiIds.isEmpty();
   }

   private boolean isTeamDefSpecified() {
      return teamDefIds != null && !teamDefIds.isEmpty();
   }

   private boolean isTeamWfAttrSpecified() {
      return teamWorkflowAttr != null && !teamWorkflowAttr.isEmpty();
   }

   private boolean isArtifactTypesSpecified() {
      return !getAllArtTypes().isEmpty();
   }

   @Override
   public IAtsQuery isOfType(IArtifactType... artifactTypes) {
      this.artifactTypes = Arrays.asList(artifactTypes);
      return this;
   }

   @Override
   public IAtsQuery isOfType(Collection<WorkItemType> workItemTypes) {
      for (WorkItemType type : workItemTypes) {
         this.workItemTypes.add(type);
      }
      return this;
   }

   @Override
   public IAtsQuery isOfType(WorkItemType... workItemType) {
      return isOfType(Arrays.asList(workItemType));
   }

   @Override
   public IAtsQuery andTeam(IAtsTeamDefinition teamDef) {
      teamDefIds.add(teamDef.getId());
      return this;
   }

   @Override
   public IAtsQuery andStateType(StateType... stateType) {
      this.stateTypes = Arrays.asList(stateType);
      return this;
   }

   @Override
   public IAtsQuery andTeam(Collection<IAtsTeamDefinition> teamDefs) {
      return andTeam(AtsObjects.toIds(teamDefs));
   }

   @Override
   public IAtsQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOptions) {
      andAttr.add(new AtsAttributeQuery(attributeType, values, queryOptions));
      return this;
   }

   @Override
   public IAtsQuery andRelated(IAtsObject object, RelationTypeSide relation) {
      List<IAtsObject> list = andRels.get(relation);
      if (list == null) {
         list = new LinkedList<>();
         andRels.put(relation, list);
      }
      list.add(object);
      return this;
   }

   @Override
   public IAtsQuery andIds(Long... ids) {
      for (Long artifactId : ids) {
         artifactIds.add(ArtifactId.valueOf(artifactId));
      }
      return this;
   }

   @Override
   public IAtsQuery andAtsIds(Collection<String> atsIds) {
      return andAttr(AtsAttributeTypes.AtsId, atsIds);
   }

   @Override
   public IAtsQuery andLegacyIds(Collection<String> legacyIds) {
      return andAttr(AtsAttributeTypes.LegacyPcrId, legacyIds);
   }

   @Override
   public IAtsWorkItemFilter andFilter() {
      return new AtsWorkItemFilter(getItems(), atsApi);
   }

   protected Set<IArtifactType> getArtifactTypesFromWorkItemTypes() {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      if (workItemTypes != null) {
         for (WorkItemType workItemType : workItemTypes) {
            artifactTypes.add(workItemType.getArtifactType());
         }
      }
      return artifactTypes;
   }

   @Override
   public IAtsQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption) {
      return andAttr(attributeType, Collections.singleton(value), queryOption);
   }

   @Override
   public <T extends IAtsWorkItem> ResultSet<T> getResults() {
      return ResultSets.newResultSet(getItems());
   }

   @Override
   public IAtsQuery andAssignee(IAtsUser... assignees) {
      List<String> userIds = new ArrayList<>();
      for (IAtsUser user : assignees) {
         userIds.add("<" + user.getUserId() + ">");
      }
      return andAttr(AtsAttributeTypes.CurrentState, userIds, QueryOption.CONTAINS_MATCH_OPTIONS);
   }

   @Override
   public IAtsQuery andAssigneeWas(IAtsUser... assignees) {
      List<String> userIds = new ArrayList<>();
      for (IAtsUser user : assignees) {
         userIds.add("<" + user.getUserId() + ">");
      }
      return andAttr(AtsAttributeTypes.State, userIds, QueryOption.CONTAINS_MATCH_OPTIONS);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends ArtifactId> ResultSet<T> getResultArtifacts() {
      List<T> items = new ArrayList<>();
      for (IAtsWorkItem workItem : getResults()) {
         if (workItem == null) {
            System.err.println("Unexpected null workitem");
         } else {
            items.add((T) workItem.getStoreObject());
         }
      }
      // filter on original artifact types
      List<T> artifacts = new LinkedList<>();
      for (ArtifactId artifact : items) {
         boolean artifactTypeMatch = isArtifactTypeMatch(artifact, artifactTypes);
         boolean releaseOptionMatch = isReleaseOptionMatch(artifact);
         if (artifactTypeMatch && releaseOptionMatch) {
            artifacts.add((T) artifact);
         }
      }
      return ResultSets.newResultSet(artifacts);
   }

   private boolean isReleaseOptionMatch(ArtifactId artifact) {
      if (releasedOption == null || releasedOption == ReleasedOption.Both) {
         return true;
      }
      boolean match = false;
      if (artifact instanceof ArtifactToken) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem((ArtifactToken) artifact);
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            boolean released = atsApi.getVersionService().isReleased(teamWf);
            if (releasedOption == ReleasedOption.Released && released || releasedOption == ReleasedOption.UnReleased && !released) {
               match = true;
            }
         }
      }
      return match;
   }

   @Override
   public IAtsQuery andOriginator(IAtsUser atsUser) {
      return andAttr(AtsAttributeTypes.CreatedBy, atsUser.getUserId());
   }

   @Override
   public IAtsQuery andSubscribed(IAtsUser atsUser) {
      return andRelated(atsUser, AtsRelationTypes.SubscribedUser_User);
   }

   @Override
   public IAtsQuery andFavorite(IAtsUser atsUser) {
      return andRelated(atsUser, AtsRelationTypes.FavoriteUser_User);
   }

   @Override
   public IAtsQuery andTeam(List<Long> teamDefIds) {
      this.teamDefIds = teamDefIds;
      return this;
   }

   @Override
   public IAtsQuery andActionableItem(IAtsActionableItem actionableItem) {
      this.aiIds.add(actionableItem.getId());
      return this;
   }

   @Override
   public IAtsQuery andActionableItem(List<Long> aiIds) {
      this.aiIds = aiIds;
      return this;
   }

   @Override
   public IAtsQuery andVersion(Long versionId) {
      this.versionId = versionId;
      return this;
   }

   @Override
   public IAtsQuery andState(String stateName) {
      this.stateName = stateName;
      return this;
   }

   @Override
   public IAtsQuery andProgram(Long programId) {
      this.programId = programId;
      return this;
   }

   @Override
   public IAtsQuery andInsertion(Long insertionId) {
      this.insertionId = insertionId;
      return this;
   }

   @Override
   public IAtsQuery andInsertionActivity(Long insertionActivityId) {
      this.insertionActivityId = insertionActivityId;
      return this;
   }

   @Override
   public IAtsQuery andWorkPackage(Long workPackageId) {
      this.workPackageId = workPackageId;
      return this;
   }

   @Override
   public IAtsQuery andColorTeam(String colorTeam) {
      this.colorTeam = colorTeam;
      return this;
   }

   private void getBaseSearchCriteria(Collection<IArtifactType> artTypes, boolean withIds, Set<IArtifactType> allArtTypes) {
      createQueryBuilder();

      /**
       * Artifact Types and WorkItem type; Do not search by type if teamDef or AI is specified, query runs faster
       * without. If return is only ids, we have to perform the artifact types search.
       */
      boolean teamDefAndAiNotSpecified = !isTeamDefSpecified() && !isActionableItemSpecified();
      if (isArtifactTypesSpecified() && (isOnlyIds() || teamDefAndAiNotSpecified)) {
         queryAndIsOfType(artTypes);
      }

      if (withIds && !artifactIds.isEmpty()) {
         queryAndIds(artifactIds);
      }

      addStateTypeNameAndAttributeCriteria();
   }

   public abstract void queryAndIsOfType(Collection<IArtifactType> artTypes);

   /**
    * Color Team is handled through workpackage, insertion, activity and program if specified. Otherwise, use color team
    * to find all workpackages and add those as work package criteria.
    */
   private void addColorTeamCriteria() {
      if (Strings.isValid(
         colorTeam) && !isWorkPackageSpecified() && !isInsertionActivitySpecified() && !isInsertionSpecified() && !isProgramSpecified()) {
         List<String> workPackageIds = org.eclipse.osee.framework.jdk.core.util.Collections.transform(
            getWorkPackagesForColorTeam(colorTeam), ArtifactId::getIdString);
         queryAnd(AtsAttributeTypes.WorkPackageReference, workPackageIds);
      }
   }

   public abstract List<ArtifactId> getWorkPackagesForColorTeam(String colorTeam);

   private void addWorkPackageCriteria() {
      if (isWorkPackageSpecified()) {
         ArtifactId workPackArt = atsApi.getQueryService().getArtifact(workPackageId);
         if (isColorTeamMatch(workPackArt)) {
            queryAnd(AtsAttributeTypes.WorkPackageReference, workPackArt.getIdString());
         }
      }
   }

   private boolean isColorTeamMatch(ArtifactId workPackArt) {
      return !isColorTeamSpecified() || isColorTeamSpecified() && colorTeam.equals(
         atsApi.getAttributeResolver().getSoleAttributeValue(workPackArt, AtsAttributeTypes.ColorTeam, ""));
   }

   public abstract void queryAnd(AttributeTypeId attrType, String value);

   private void addVersionCriteria() {
      if (versionId != null && versionId > 0) {
         queryAndRelatedToLocalIds(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
            ArtifactId.valueOf(versionId));
      }
   }

   public abstract void queryAndRelatedToLocalIds(RelationTypeSide relationTypeSide, ArtifactId artId);

   private void addAiCriteria() {
      if (isActionableItemSpecified()) {
         List<String> ids = AtsObjects.toIdStringsFromLong(aiIds);
         queryAnd(AtsAttributeTypes.ActionableItemReference, ids);
      }
   }

   private void addTeamDefCriteria() {
      if (isTeamDefSpecified()) {
         List<String> ids = AtsObjects.toIdStringsFromLong(teamDefIds);
         queryAnd(AtsAttributeTypes.TeamDefinitionReference, ids);
      }
   }

   private void addAttributeCriteria() {
      if (!andAttr.isEmpty()) {
         for (AtsAttributeQuery attrQuery : andAttr) {
            queryAnd(attrQuery.getAttrType(), attrQuery.getValues(), attrQuery.getQueryOption());
         }
      }
   }

   private void addTeamWorkflowAttributeCriteria() {
      if (!teamWorkflowAttr.isEmpty()) {
         for (AtsAttributeQuery attrQuery : teamWorkflowAttr) {
            queryAnd(attrQuery.getAttrType(), attrQuery.getValues(), attrQuery.getQueryOption());
         }
      }
   }

   public abstract void queryAnd(AttributeTypeId attrType, Collection<String> values, QueryOption[] queryOption);

   private void addRelationCriteria() {
      if (!andRels.isEmpty()) {
         for (Entry<RelationTypeSide, List<IAtsObject>> entry : andRels.entrySet()) {
            List<ArtifactId> artIds = new LinkedList<>();
            for (IAtsObject object : entry.getValue()) {
               artIds.add(ArtifactId.valueOf(object.getId()));
            }
            queryAndRelatedTo(entry.getKey(), artIds);
         }
      }
   }

   public abstract void queryAndRelatedTo(RelationTypeSide relationTypeSide, List<ArtifactId> artIds);

   private void addStateNameCriteria() {
      if (stateName != null) {
         queryAnd(AtsAttributeTypes.CurrentState, stateName + ";", QueryOption.CONTAINS_MATCH_OPTIONS);
      }
   }

   public abstract void queryAnd(AttributeTypeId attrType, String value, QueryOption[] queryOption);

   private void addStateTypeCriteria() {
      if (!stateTypes.isEmpty()) {
         List<String> stateTypeNames = new ArrayList<>();
         for (StateType type : stateTypes) {
            stateTypeNames.add(type.name());
         }
         queryAnd(AtsAttributeTypes.CurrentStateType, stateTypeNames);
      }
   }

   private void addIdCriteria(Collection<Long> ids) {
      if (ids != null) {
         List<Integer> artIds = new LinkedList<>();
         for (Long id : ids) {
            artIds.add(id.intValue());
         }

      }
   }

   public abstract void queryAndIds(Collection<? extends ArtifactId> artIds);

   public void addProgramCriteria() {
      if (!isInsertionSpecified()) {
         if (programId != null && programId > 0) {
            ArtifactId programArt = atsApi.getQueryService().getArtifact(programId);
            List<String> workPackageIds = new LinkedList<>();
            for (ArtifactId insertionArt : atsApi.getRelationResolver().getRelated(programArt,
               AtsRelationTypes.ProgramToInsertion_Insertion)) {
               for (ArtifactId insertionActivityArt : atsApi.getRelationResolver().getRelated(insertionArt,
                  AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
                  for (ArtifactId workPackageArt : atsApi.getRelationResolver().getRelated(insertionActivityArt,
                     AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
                     if (isColorTeamMatch(workPackageArt)) {
                        workPackageIds.add(workPackageArt.getIdString());
                     }
                  }
               }
            }
            if (!workPackageIds.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageReference, workPackageIds);
            }
         }
      }
   }

   public void addInsertionCriteria() {
      if (!isInsertionActivitySpecified()) {
         if (insertionId != null && insertionId > 0) {
            ArtifactId insertionArt = atsApi.getQueryService().getArtifact(insertionId);
            List<String> workPackageIds = new LinkedList<>();
            for (ArtifactId insertionActivityArt : atsApi.getRelationResolver().getRelated(insertionArt,
               AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
               for (ArtifactId workPackageArt : atsApi.getRelationResolver().getRelated(insertionActivityArt,
                  AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
                  if (isColorTeamMatch(workPackageArt)) {
                     workPackageIds.add(workPackageArt.getIdString());
                  }
               }
            }
            if (!workPackageIds.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageReference, workPackageIds);
            }
         }
      }
   }

   public abstract void queryAnd(AttributeTypeId attrType, Collection<String> values);

   public void addInsertionActivityCriteria() {
      if (!isWorkPackageSpecified()) {
         if (insertionActivityId != null && insertionActivityId > 0) {
            List<String> workPackageIds = getWorkPackageIdsFromActivity();
            if (!workPackageIds.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageReference, workPackageIds);
            }
         }
      }
   }

   private List<String> getWorkPackageIdsFromActivity() {
      List<String> ids = new LinkedList<>();
      if (insertionActivityId != null && insertionActivityId > 0) {
         ArtifactId insertionActivityArt = atsApi.getQueryService().getArtifact(insertionActivityId);
         for (ArtifactId workPackageArt : atsApi.getRelationResolver().getRelated(insertionActivityArt,
            AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
            if (isColorTeamMatch(workPackageArt)) {
               ids.add(workPackageArt.getIdString());
            }
         }
      }
      return ids;
   }

   private void addStateTypeNameAndAttributeCriteria() {
      // stateTypes
      addStateTypeCriteria();

      // stateName
      addStateNameCriteria();

      // attributes
      addAttributeCriteria();

      // relations
      addRelationCriteria();
   }

   @Override
   public IAtsWorkItemFilter createFilter() {
      return new AtsWorkItemFilter(getItems(), atsApi);
   }

   @Override
   public IAtsQuery andReleased(ReleasedOption releasedOption) {
      this.releasedOption = releasedOption;
      return this;
   }

   public List<AtsAttributeQuery> getTeamWorkflowAttr() {
      return teamWorkflowAttr;
   }

   @Override
   public IAtsQuery andTeamWorkflowAttr(AttributeTypeId attributeType, List<String> values, QueryOption... queryOptions) {
      teamWorkflowAttr.add(new AtsAttributeQuery(attributeType, values, queryOptions));
      return this;
   }

   @Override
   public IAtsQuery andFilter(IAtsQueryFilter queryFilter) {
      queryFilters.add(queryFilter);
      return this;
   }

   @Override
   public boolean exists() {
      return !getItemIds().isEmpty();
   }

   @Override
   public IAtsQuery andTag(String... tags) {
      List<String> values = Arrays.asList(tags);
      return andAttr(CoreAttributeTypes.StaticId, values, QueryOption.EXACT_MATCH_OPTIONS);
   }

   @Override
   public IAtsQuery andActive(boolean active) {
      return andAttr(CoreAttributeTypes.Active, active ? "true" : "false");
   }

   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems(Class<T> clazz) {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getItems());
   }

   @Override
   public IAtsQuery andName(String name) {
      return andAttr(CoreAttributeTypes.Name, name);
   }

   @Override
   public IAtsQuery andName(String name, QueryOption... queryOption) {
      return andAttr(CoreAttributeTypes.Name, name, queryOption);
   }

   @Override
   public IAtsQuery andWorkItemType(WorkItemType... workItemTypes) {
      this.workItemTypes.clear();
      for (WorkItemType type : workItemTypes) {
         this.workItemTypes.add(type);
      }
      return this;
   }

   @Override
   public IAtsQuery andVersion(IAtsVersion version) {
      andVersion(version.getId());
      return this;
   }

   @Override
   public IAtsQuery andNotExists(AttributeTypeId attributeType) {
      createQueryBuilder();
      queryAndNotExists(attributeType);
      return this;
   }

   protected abstract void queryAndNotExists(AttributeTypeId attributeType);

   @Override
   public IAtsQuery andExists(AttributeTypeToken attributeType) {
      createQueryBuilder();
      queryAndExists(attributeType);
      return this;
   }

   protected abstract void queryAndExists(AttributeTypeToken attributeType);

}
