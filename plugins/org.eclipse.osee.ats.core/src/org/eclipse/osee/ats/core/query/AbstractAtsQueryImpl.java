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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryImpl implements IAtsQuery {

   protected final List<AtsAttributeQuery> andAttr;
   protected final HashMap<IRelationTypeSide, List<IAtsObject>> andRels;
   protected IAtsTeamDefinition teamDef;
   protected Collection<Long> teamDefUuids;
   protected Collection<StateType> stateTypes;
   protected Collection<WorkItemType> workItemTypes;
   protected Collection<IArtifactType> artifactTypes;
   protected Collection<Long> uuids;
   protected final IAtsServices services;
   protected Collection<Long> aiUuids;
   protected Long versionUuid;
   protected String stateName;
   protected String colorTeam;
   protected Long programUuid;
   protected Long insertionUuid;
   protected Long insertionActivityUuid;
   protected Long workPackageUuid;

   public AbstractAtsQueryImpl(IAtsServices services) {
      this.services = services;
      andRels = new HashMap<>();
      andAttr = new ArrayList<>();
      stateTypes = new ArrayList<>();
      workItemTypes = new ArrayList<>();
      artifactTypes = new ArrayList<>();
      teamDefUuids = new ArrayList<>();
      aiUuids = new ArrayList<>();
      uuids = new ArrayList<>();
   }

   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems() throws OseeCoreException {

      List<IArtifactType> teamWorkflowArtTypes = getTeamWorkflowArtTypes();
      boolean teamsTypeDefOrAisOrVersionSearched = isTeamTypeDefAisOrVersionSearched();

      /**
       * First, search for Team Workflows
       */
      Collection<T> teamWfs = Collections.emptyList();
      Set<T> allResults = new HashSet<>();
      if (!teamWorkflowArtTypes.isEmpty()) {
         teamWfs = getTeamWorkflows(teamWorkflowArtTypes, allResults);
      }

      /**
       * If team workflow's searched by Team Definition, Actionable Item or Version were searched, then the child tasks
       * and reviews are what to use to search against
       */
      if (teamsTypeDefOrAisOrVersionSearched) {
         getTasksAndReviewsFromResultingTeamWfs(teamWfs, allResults);
      }
      /**
       * Else, perform task and review searches as normal
       */
      else {
         getTasksFromSearchCriteria(allResults);
         getReviewsFromSearchCriteria(allResults);
      }
      /**
       * Search Goals, Sprints and Backlogs as normal
       */
      getGoalsFromSearchCriteria(allResults);
      getSprintsFromSearchCriteria(allResults);

      return allResults;
   }

   public abstract Collection<ArtifactId> runQuery();

   @SuppressWarnings("unchecked")
   private <T> Collection<T> collectResults(Set<T> allResults) {
      Set<T> workItems = new HashSet<>();
      for (ArtifactId artifact : runQuery()) {
         workItems.add((T) services.getWorkItemFactory().getWorkItem(artifact));
      }
      allResults.addAll(workItems);
      return workItems;
   }

   private <T> void getTasksFromSearchCriteria(Set<T> allResults) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.Task)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         getBaseSearchCriteria(artTypes, true);
         // teamDef, ai and version
         if (isTeamTypeDefAisOrVersionSearched()) {
            List<Integer> teamWfUuids = getRelatedTeamWorkflowUuidsBasedOnTeamDefsAisAndVersions();
            queryAndRelatedToLocalIds(AtsRelationTypes.TeamWfToTask_TeamWf, teamWfUuids);
         }

         addEvConfigCriteria();

         collectResults(allResults);
      }

   }

   private <T> void getReviewsFromSearchCriteria(Set<T> allResults) {
      List<IArtifactType> artTypes = getReviewArtifactTypes();
      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         getBaseSearchCriteria(artTypes, true);
         // teamDef, ai and version
         if (isTeamTypeDefAisOrVersionSearched()) {
            List<Integer> teamWfUuids = getRelatedTeamWorkflowUuidsBasedOnTeamDefsAisAndVersions();
            queryAndRelatedToLocalIds(AtsRelationTypes.TeamWorkflowToReview_Review, teamWfUuids);
         }

         collectResults(allResults);
      }
   }

   private <T> void getSprintsFromSearchCriteria(Set<T> allResults) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.AgileSprint)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();
         getBaseSearchCriteria(artTypes, true);
         collectResults(allResults);
      }
   }

   private <T> void getGoalsFromSearchCriteria(Set<T> allResults) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.Goal) || workItemTypes.contains(WorkItemType.AgileBacklog)) {
            artTypes.add(artType);
         }
      }
      if (!artTypes.isEmpty()) {
         createQueryBuilder();
         getBaseSearchCriteria(artTypes, true);

         boolean isAgileSpecified = workItemTypes.contains(WorkItemType.AgileBacklog);
         boolean isGoalSpecified = workItemTypes.contains(WorkItemType.Goal);

         if (isAgileSpecified && !isGoalSpecified) {
            queryAndExists(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         } else if (isGoalSpecified && !isAgileSpecified) {
            queryAndNotExists(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         }
         collectResults(allResults);
      }
   }

   public abstract void queryAndNotExists(IRelationTypeSide relationTypeSide);

   public abstract void queryAndExists(IRelationTypeSide relationTypeSide);

   private boolean typeIsSpecified(IArtifactType parentArtType) {
      for (IArtifactType artType2 : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType2, parentArtType)) {
            return true;
         }
      }
      return false;
   }

   private List<IArtifactType> getReviewArtifactTypes() {
      List<IArtifactType> artTypes = new LinkedList<>();
      boolean isReviewSpecified =
         workItemTypes.contains(WorkItemType.Review) || typeIsSpecified(AtsArtifactTypes.ReviewArtifact);
      boolean isPeerSpecified =
         workItemTypes.contains(WorkItemType.PeerReview) || typeIsSpecified(AtsArtifactTypes.PeerToPeerReview);
      boolean isDecisionSpecified =
         workItemTypes.contains(WorkItemType.DecisionReview) || typeIsSpecified(AtsArtifactTypes.DecisionReview);
      for (IArtifactType artType : getAllArtTypes()) {
         if (isReviewSpecified && services.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.ReviewArtifact)) {
            artTypes.add(artType);
         } else if (isPeerSpecified && services.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.PeerToPeerReview)) {
            artTypes.add(artType);

         } else if (isDecisionSpecified && services.getArtifactResolver().inheritsFrom(artType,
            AtsArtifactTypes.DecisionReview)) {
            artTypes.add(artType);
         }
      }
      return artTypes;
   }

   private <T> void getTasksAndReviewsFromResultingTeamWfs(Collection<T> teamWfs, Set<T> allResults) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (IArtifactType artType : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.Task)) {
            artTypes.add(artType);
         }
      }
      artTypes.addAll(getReviewArtifactTypes());

      if (!artTypes.isEmpty()) {
         createQueryBuilder();

         List<Long> taskReviewUuids = new ArrayList<>();
         for (T teamWf : teamWfs) {
            for (IAtsTask task : services.getTaskService().getTasks((IAtsTeamWorkflow) teamWf)) {
               taskReviewUuids.add(task.getUuid());
            }
            for (IAtsAbstractReview review : services.getReviewService().getReviews((IAtsTeamWorkflow) teamWf)) {
               taskReviewUuids.add(review.getUuid());
            }
         }
         getBaseSearchCriteria(artTypes, false);

         // team def, ai, version are all covered by team search

         // Start with known task uuids
         addUuidCriteria(taskReviewUuids);

         addEvConfigCriteria();

         collectResults(allResults);
      }
   }

   private <T extends IAtsWorkItem> Collection<T> getTeamWorkflows(List<IArtifactType> teamWorkflowArtTypes, Set<T> allResults) {
      createQueryBuilder();
      getBaseSearchCriteria(teamWorkflowArtTypes, true);

      addTeamDefCriteria();

      addAiCriteria();

      addVersionCriteria();

      addEvConfigCriteria();

      return collectResults(allResults);
   }

   private void addEvConfigCriteria() {
      addColorTeamCriteria();

      addWorkPackageCriteria();

      addInsertionActivityCriteria();

      addInsertionCriteria();

      addProgramCriteria();
   }

   protected boolean isProgramSpecified() {
      return programUuid != null && programUuid > 0;
   }

   protected boolean isInsertionSpecified() {
      return insertionUuid != null && insertionUuid > 0;
   }

   protected boolean isColorTeamSpecified() {
      return Strings.isValid(colorTeam);
   }

   protected boolean isInsertionActivitySpecified() {
      return insertionActivityUuid != null && insertionActivityUuid > 0;
   }

   protected boolean isWorkPackageSpecified() {
      return workPackageUuid != null && workPackageUuid > 0;
   }

   public abstract void createQueryBuilder();

   public abstract void queryAndIsOfType(IArtifactType teamworkflow);

   public abstract List<Integer> queryGetIds();

   /**
    * Return team workflow ids based on teamdef, ai and version criteria to use in relatedTo criteria.
    */
   public abstract List<Integer> getRelatedTeamWorkflowUuidsBasedOnTeamDefsAisAndVersions();

   private Set<IArtifactType> getAllArtTypes() {
      Set<IArtifactType> allArtTypes = getArtifactTypesFromWorkItemTypes();
      if (artifactTypes != null) {
         for (IArtifactType artType : artifactTypes) {
            allArtTypes.add(artType);
         }
      }
      return allArtTypes;
   }

   private List<IArtifactType> getTeamWorkflowArtTypes() {
      List<IArtifactType> teamWorkflowArtTypes = new LinkedList<>();
      for (IArtifactType artType : getAllArtTypes()) {
         if (services.getArtifactResolver().inheritsFrom(artType, AtsArtifactTypes.TeamWorkflow)) {
            teamWorkflowArtTypes.add(artType);
         }
      }
      return teamWorkflowArtTypes;
   }

   private boolean isTeamTypeDefAisOrVersionSearched() {
      boolean teamDefsSearched = teamDef != null && !teamDefUuids.isEmpty();
      boolean aisSearched = aiUuids != null && !aiUuids.isEmpty();
      boolean versionSearched = versionUuid != null && versionUuid > 0L;
      boolean teamsTypeDefOrAisOrVersionSearched =
         !getTeamWorkflowArtTypes().isEmpty() && (teamDefsSearched || aisSearched || versionSearched);
      return teamsTypeDefOrAisOrVersionSearched;
   }

   @Override
   public IAtsQuery isOfType(IArtifactType... artifactTypes) {
      this.artifactTypes = org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(artifactTypes);
      return this;
   }

   @Override
   public IAtsQuery isOfType(Collection<WorkItemType> workItemTypes) {
      List<IArtifactType> artTypes = new LinkedList<>();
      for (WorkItemType type : workItemTypes) {
         this.workItemTypes.add(type);
         if (type == WorkItemType.TeamWorkflow) {
            artTypes.add(AtsArtifactTypes.TeamWorkflow);
         } else if (type == WorkItemType.Task) {
            artTypes.add(AtsArtifactTypes.Task);
         } else if (type == WorkItemType.Review) {
            artTypes.add(AtsArtifactTypes.ReviewArtifact);
         } else if (type == WorkItemType.PeerReview) {
            artTypes.add(AtsArtifactTypes.PeerToPeerReview);
         } else if (type == WorkItemType.DecisionReview) {
            artTypes.add(AtsArtifactTypes.DecisionReview);
         } else if (type == WorkItemType.AgileSprint) {
            artTypes.add(AtsArtifactTypes.AgileSprint);
         } else if (type == WorkItemType.AgileBacklog) {
            artTypes.add(AtsArtifactTypes.Goal);
         } else if (type == WorkItemType.Goal) {
            artTypes.add(AtsArtifactTypes.Goal);
         }
      }
      return isOfType(artTypes.toArray(new IArtifactType[artTypes.size()]));
   }

   @Override
   public IAtsQuery isOfType(WorkItemType... workItemType) {
      return isOfType(org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(workItemType));
   }

   @Override
   public IAtsQuery fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException {
      this.teamDef = teamDef;
      return this;
   }

   @Override
   public IAtsQuery andStateType(StateType... stateType) throws OseeCoreException {
      this.stateTypes = org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(stateType);
      return this;
   }

   @Override
   public IAtsQuery andAttr(IAttributeType attributeType, Collection<String> values, QueryOption... queryOptions) throws OseeCoreException {
      andAttr.add(new AtsAttributeQuery(attributeType, values, queryOptions));
      return this;
   }

   @Override
   public IAtsQuery andRelated(IAtsObject object, IRelationTypeSide relation) {
      List<IAtsObject> list = andRels.get(relation);
      if (list == null) {
         list = new LinkedList<>();
         andRels.put(relation, list);
      }
      list.add(object);
      return this;
   }

   @Override
   public IAtsQuery andUuids(Long... uuids) {
      this.uuids = org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(uuids);
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
   public IAtsWorkItemFilter andFilter() throws OseeCoreException {
      return new AtsWorkItemFilter(getItems(), services);
   }

   protected Set<IArtifactType> getArtifactTypesFromWorkItemTypes() {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      if (workItemTypes != null) {
         for (WorkItemType workItemType : workItemTypes) {
            artifactTypes.add(workItemType.getArtifactType());
         }
      }
      if (workItemTypes.contains(WorkItemType.WorkItem)) {
         for (WorkItemType workItemType : WorkItemType.values()) {
            artifactTypes.add(workItemType.getArtifactType());
         }
      }
      return artifactTypes;
   }

   @Override
   public IAtsQuery andAttr(IAttributeType attributeType, String value, QueryOption... queryOption) {
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
      return ResultSets.newResultSet(items);
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
   public IAtsQuery andTeam(List<Long> teamDefUuids) {
      this.teamDefUuids = teamDefUuids;
      return this;
   }

   @Override
   public IAtsQuery andActionableItem(List<Long> aiUuids) {
      this.aiUuids = aiUuids;
      return this;
   }

   @Override
   public IAtsQuery andVersion(Long versionUuid) {
      this.versionUuid = versionUuid;
      return this;
   }

   @Override
   public IAtsQuery andState(String stateName) {
      this.stateName = stateName;
      return this;
   }

   @Override
   public IAtsQuery andProgram(Long programUuid) {
      this.programUuid = programUuid;
      return this;
   }

   @Override
   public IAtsQuery andInsertion(Long insertionUuid) {
      this.insertionUuid = insertionUuid;
      return this;
   }

   @Override
   public IAtsQuery andInsertionActivity(Long insertionActivityUuid) {
      this.insertionActivityUuid = insertionActivityUuid;
      return this;
   }

   @Override
   public IAtsQuery andWorkPackage(Long workPackageUuid) {
      this.workPackageUuid = workPackageUuid;
      return this;
   }

   @Override
   public IAtsQuery andColorTeam(String colorTeam) {
      this.colorTeam = colorTeam;
      return this;
   }

   private List<String> getGuidsFromUuids(Collection<Long> uuids) {
      new LinkedList<>();
      List<String> guids = new LinkedList<>();
      for (Long uuid : uuids) {
         guids.add(getGuidFromUuid(uuid));
      }
      return guids;
   }

   private String getGuidFromUuid(Long uuid) {
      String guid = AtsUtilCore.getGuid(uuid);
      if (!Strings.isValid(guid)) {
         guid = AtsUtilCore.getGuid(uuid);
         if (!Strings.isValid(guid)) {
            ArtifactId artifact = services.getArtifact(uuid);
            if (artifact != null) {
               guid = artifact.getGuid();
            } else {
               throw new OseeArgumentException("No artifact found with uuid %d", uuid);
            }
         }
      }
      return guid;
   }

   private void getBaseSearchCriteria(List<IArtifactType> artTypes, boolean withUuids) {
      createQueryBuilder();

      // Artifact Types and WorkItem type
      if (artTypes != null && artTypes.size() > 0) {
         queryAndIsOfType(artTypes);
      }

      if (withUuids && uuids != null && uuids.size() > 0) {
         addUuidCriteria(uuids);
      }

      addStateTypeNameAndAttributeCriteria();
   }

   public abstract void queryAndIsOfType(List<IArtifactType> artTypes);

   /**
    * Color Team is handled through workpackage, insertion, activity and program if specified. Otherwise, use color team
    * to find all workpackages and add those as work package criteria.
    */
   private void addColorTeamCriteria() {
      if (Strings.isValid(
         colorTeam) && !isWorkPackageSpecified() && !isInsertionActivitySpecified() && !isInsertionSpecified() && !isProgramSpecified()) {
         List<String> workPackageGuids = getWorkPackagesForColorTeam(colorTeam);
         queryAnd(AtsAttributeTypes.WorkPackageGuid, workPackageGuids);
      }
   }

   public abstract List<String> getWorkPackagesForColorTeam(String colorTeam);

   private void addWorkPackageCriteria() {
      if (isWorkPackageSpecified()) {
         ArtifactId workPackArt = services.getArtifact(workPackageUuid);
         if (isColorTeamMatch(workPackArt)) {
            String guid = workPackArt.getGuid();
            queryAnd(AtsAttributeTypes.WorkPackageGuid, guid);
         }
      }
   }

   private boolean isColorTeamMatch(ArtifactId workPackArt) {
      return !isColorTeamSpecified() || isColorTeamSpecified() && colorTeam.equals(
         services.getAttributeResolver().getSoleAttributeValue(workPackArt, AtsAttributeTypes.ColorTeam, ""));
   }

   public abstract void queryAnd(IAttributeType attrType, String value);

   private void addVersionCriteria() {
      if (versionUuid != null && versionUuid > 0) {
         queryAndRelatedToLocalIds(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
            Long.valueOf(versionUuid).intValue());
      }
   }

   public abstract void queryAndRelatedToLocalIds(IRelationTypeSide relationTypeSide, int artId);

   private void addAiCriteria() {
      if (aiUuids != null && !aiUuids.isEmpty()) {
         List<String> guids = getGuidsFromUuids(aiUuids);
         queryAnd(AtsAttributeTypes.ActionableItem, guids);
      }
   }

   private void addTeamDefCriteria() {
      if (teamDef != null) {
         queryAnd(AtsAttributeTypes.TeamDefinition, Collections.singleton(AtsUtilCore.getGuid(teamDef)));
      } else if (teamDefUuids != null && !teamDefUuids.isEmpty()) {
         List<String> guids = getGuidsFromUuids(teamDefUuids);
         queryAnd(AtsAttributeTypes.TeamDefinition, guids);
      }
   }

   private void addAttributeCriteria() {
      if (!andAttr.isEmpty()) {
         for (AtsAttributeQuery attrQuery : andAttr) {
            queryAnd(attrQuery.getAttrType(), attrQuery.getValues(), attrQuery.getQueryOption());
         }
      }
   }

   public abstract void queryAnd(IAttributeType attrType, Collection<String> values, QueryOption[] queryOption);

   private void addRelationCriteria() {
      if (!andRels.isEmpty()) {
         for (Entry<IRelationTypeSide, List<IAtsObject>> entry : andRels.entrySet()) {
            List<Integer> artIds = new LinkedList<>();
            for (IAtsObject object : entry.getValue()) {
               artIds.add(new Long(object.getUuid()).intValue());
            }
            queryAndRelatedToLocalIds(entry.getKey(), artIds);
         }
      }
   }

   public abstract void queryAndRelatedToLocalIds(IRelationTypeSide relationTypeSide, List<Integer> artIds);

   private void addStateNameCriteria() {
      if (stateName != null) {
         queryAnd(AtsAttributeTypes.CurrentState, stateName + ";", QueryOption.CONTAINS_MATCH_OPTIONS);
      }
   }

   public abstract void queryAnd(IAttributeType attrType, String value, QueryOption[] queryOption);

   private void addStateTypeCriteria() {
      if (!stateTypes.isEmpty()) {
         List<String> stateTypeNames = new ArrayList<>();
         for (StateType type : stateTypes) {
            stateTypeNames.add(type.name());
         }
         queryAnd(AtsAttributeTypes.CurrentStateType, stateTypeNames);
      }
   }

   private void addUuidCriteria(Collection<Long> uuids) {
      if (uuids != null) {
         List<Integer> artIds = new LinkedList<>();
         for (Long uuid : uuids) {
            artIds.add(uuid.intValue());
         }
         queryAndLocalIds(artIds);
      }
   }

   public abstract void queryAndLocalIds(List<Integer> artIds);

   public void addProgramCriteria() {
      if (!isInsertionSpecified()) {
         if (programUuid != null && programUuid > 0) {
            ArtifactId programArt = services.getArtifact(programUuid);
            List<String> workPackageGuids = new LinkedList<>();
            for (ArtifactId insertionArt : services.getRelationResolver().getRelated(programArt,
               AtsRelationTypes.ProgramToInsertion_Insertion)) {
               for (ArtifactId insertionActivityArt : services.getRelationResolver().getRelated(insertionArt,
                  AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
                  for (ArtifactId workPackageArt : services.getRelationResolver().getRelated(insertionActivityArt,
                     AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
                     if (isColorTeamMatch(workPackageArt)) {
                        workPackageGuids.add(workPackageArt.getGuid());
                     }
                  }
               }
            }
            if (!workPackageGuids.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageGuid, workPackageGuids);
            }
         }
      }
   }

   public void addInsertionCriteria() {
      if (!isInsertionActivitySpecified()) {
         if (insertionUuid != null && insertionUuid > 0) {
            ArtifactId insertionArt = services.getArtifact(insertionUuid);
            List<String> workPackageGuids = new LinkedList<>();
            for (ArtifactId insertionActivityArt : services.getRelationResolver().getRelated(insertionArt,
               AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
               for (ArtifactId workPackageArt : services.getRelationResolver().getRelated(insertionActivityArt,
                  AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
                  if (isColorTeamMatch(workPackageArt)) {
                     workPackageGuids.add(workPackageArt.getGuid());
                  }
               }
            }
            if (!workPackageGuids.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageGuid, workPackageGuids);
            }
         }
      }
   }

   public abstract void queryAnd(IAttributeType attrType, Collection<String> values);

   public void addInsertionActivityCriteria() {
      if (!isWorkPackageSpecified()) {
         if (insertionActivityUuid != null && insertionActivityUuid > 0) {
            List<String> workPackageGuids = getWorkPackageGuidsFromActivity();
            if (!workPackageGuids.isEmpty()) {
               queryAnd(AtsAttributeTypes.WorkPackageGuid, workPackageGuids);
            }
         }
      }
   }

   private List<String> getWorkPackageGuidsFromActivity() {
      List<String> guids = new LinkedList<>();
      if (insertionActivityUuid != null && insertionActivityUuid > 0) {
         ArtifactId insertionActivityArt = services.getArtifact(insertionActivityUuid);
         for (ArtifactId workPackageArt : services.getRelationResolver().getRelated(insertionActivityArt,
            AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
            if (isColorTeamMatch(workPackageArt)) {
               guids.add(workPackageArt.getGuid());
            }
         }
      }
      return guids;
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

}
