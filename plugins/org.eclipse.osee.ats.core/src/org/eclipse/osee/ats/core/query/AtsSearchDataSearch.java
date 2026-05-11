/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.core.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.ISearchCriteriaProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AttributeValue;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchDataSearch {

   private final AtsSearchData data;
   private final AtsApi atsApi;
   private final ISearchCriteriaProvider criteriaProvider;
   private AtsUser userById;
   private final XResultData rd;

   public AtsSearchDataSearch(AtsSearchData data, AtsApi atsApi, ISearchCriteriaProvider criteriaProvider) {
      this.atsApi = atsApi;
      this.criteriaProvider = criteriaProvider;
      this.data = data.copy();
      rd = new XResultData();
   }

   public AtsSearchDataResults performSearch() {
      Pair<IAtsQuery, Boolean> result = createAtsQuery();
      boolean assigneesWithCompletedOrCancelled = result.getSecond();
      IAtsQuery query = result.getFirst();
      setUserType(data.getUserType(), query);

      Set<ArtifactToken> results = new HashSet<>();
      results.addAll(Collections.castAll(query.getResultArtifacts().getList()));

      /**
       * Perform a second search (see above) and add results to return set. This is because the framework search api
       * does not support OR-ing attribute values. This should be removed once that is implemented.
       */
      if (assigneesWithCompletedOrCancelled) {
         Pair<IAtsQuery, Boolean> result2 = createAtsQuery();
         IAtsQuery query2 = result2.getFirst();
         setUserType(AtsSearchUserType.AssigneeWas, query);
         results.addAll(Collections.castAll(query2.getResultArtifacts().getList()));
      }
      List<ArtifactToken> arts = Collections.castAll(AtsObjects.getArtifacts(results));
      return new AtsSearchDataResults(arts, rd);
   }

   public AtsSearchDataResults performSearchNew() {
      Pair<IAtsQuery, Boolean> result = createAtsQuery();
      boolean assigneesWithCompletedOrCancelled = result.getSecond();
      IAtsQuery query = result.getFirst();
      setUserType(data.getUserType(), query);

      Map<String, String> bidNametoBidState = new HashMap<>();
      loadBidNameToStateMap(bidNametoBidState, data);
      boolean hasBuildImpactCriteria = !bidNametoBidState.isEmpty();

      ResultSet<ArtifactToken> resultSet = query.getResultArtifactsNew();
      Set<ArtifactToken> resultPrArts = new HashSet<>();

      boolean debugBuildImpactMatch = true;

      /**
       * If BuildImpact, query loaded all related; now filter out those that don't match.</br>
       * NOTE: Build Impact can match by just name or name and state
       */
      if (hasBuildImpactCriteria) {
         filterByBuildImpactCriteria(bidNametoBidState, resultSet, resultPrArts, debugBuildImpactMatch);
      } else {
         resultPrArts.addAll(Collections.castAll(resultSet.getList()));
      }

      /**
       * Perform a second search (see above) and add results to return set. This is because the framework search api
       * does not support OR-ing attribute values. This should be removed once that is implemented.
       */
      if (assigneesWithCompletedOrCancelled) {
         Pair<IAtsQuery, Boolean> result2 = createAtsQuery();
         IAtsQuery query2 = result2.getFirst();
         setUserType(AtsSearchUserType.AssigneeWas, query2);
         resultPrArts.addAll(Collections.castAll(query2.getResultArtifactsNew().getList()));
      }
      List<ArtifactToken> arts = Collections.castAll(AtsObjects.getArtifacts(resultPrArts));
      return new AtsSearchDataResults(arts, rd);
   }

   private void filterByBuildImpactCriteria(Map<String, String> bidNametoBidState, ResultSet<ArtifactToken> resultSet,
      Set<ArtifactToken> resultPrArts, boolean debugBuildImpactMatch) {
      XResultData buildImpactDebug = new XResultData(false);
      if (debugBuildImpactMatch) {
         buildImpactDebug.logf("Match criteria %s\n", bidNametoBidState);
      }
      for (ArtifactToken prArtTok : resultSet.getList()) {
         if (debugBuildImpactMatch) {
            buildImpactDebug.logf("\nChecking PR %s\n", prArtTok.toStringWithId());
         }
         ArtifactReadable prArt = (ArtifactReadable) prArtTok;
         // If one BID's name matches name in query, add to results
         for (ArtifactReadable bidArt : prArt.getRelated(AtsRelationTypes.ProblemReportToBid_Bid)) {
            if (debugBuildImpactMatch) {
               buildImpactDebug.logf("--- Checking BIT %s\n", bidArt.toStringWithId());
            }
            // If BID name matches one we're looking for, see if match and thus add to PR results
            if (bidNametoBidState.containsKey(bidArt.getName())) {
               String buildImpactStateName = bidNametoBidState.get(bidArt.getName());
               // If state is specified, check against state name
               if (Strings.isValid(buildImpactStateName)) {
                  // only add if state matches
                  String bidArtState = atsApi.getAttributeResolver().getSoleAttributeValueAsString(bidArt,
                     AtsAttributeTypes.BitState, "");
                  if (bidArtState.equals(buildImpactStateName)) {
                     if (debugBuildImpactMatch) {
                        buildImpactDebug.logf("--- Name [%s] and State [%s] Match; Adding PR \n", bidArt.getName(),
                           buildImpactStateName);
                     }
                     resultPrArts.add(prArt);
                     break;
                  }
               }
               // Else no state specified, add
               else {
                  if (debugBuildImpactMatch) {
                     buildImpactDebug.logf("--- Name [%s] (no State specified) Match; Adding PR \n", bidArt.getName());
                  }
                  resultPrArts.add(prArt);
                  break;
               }
            }
         }
      }
   }

   private void loadBidNameToStateMap(Map<String, String> bidNametoBidState, AtsSearchData data2) {
      String buildImpactName = data.getBuildImpact();
      String buildImpactState = data.getBuildImpactState();
      if (Strings.isValid(buildImpactName)) {
         bidNametoBidState.put(buildImpactName, buildImpactState);
      }

      String buildImpactName2 = data.getBuildImpact2();
      String buildImpactState2 = data.getBuildImpactState2();
      if (Strings.isValid(buildImpactName2)) {
         bidNametoBidState.put(buildImpactName2, buildImpactState2);
      }
   }

   public void setUserType(AtsSearchUserType userType, IAtsQuery query) {
      if (Strings.isValid(data.getUserId())) {
         userById = atsApi.getUserService().getUserByUserId(data.getUserId());
         if (userType == AtsSearchUserType.Originated) {
            query.andOriginator(userById);
         } else if (userType == AtsSearchUserType.Subscribed) {
            query.andSubscribed(userById);
         } else if (userType == AtsSearchUserType.Favorites) {
            query.andFavorite(userById);
         } else if (userType == AtsSearchUserType.AssigneeWas) {
            query.andAssigneeWas(userById);
         } else if (userType == AtsSearchUserType.Assignee) {
            query.andAssignee(userById);
         }
      }

   }

   public Pair<IAtsQuery, Boolean> createAtsQuery() {
      boolean assigneesWithCompletedOrCancelled = false;
      List<StateType> stateTypes = data.getStateTypes();
      AtsSearchUserType userType = data.getUserType();

      // Note: Most User/UserType search criteria added after call to createAtsQuery due to complexity

      /**
       * Case where searching Assignee and either Completed or Cancelled; Search must be performed multiple times, once
       * for Assignees and another for AssigeesWas (user was assigned in some state)
       */
      if (userType == AtsSearchUserType.Assignee && (stateTypes.contains(StateType.Completed) || stateTypes.contains(
         StateType.Cancelled))) {
         assigneesWithCompletedOrCancelled = true;
      }

      List<WorkItemType> workItemTypes = data.getWorkItemTypes();
      if (workItemTypes.isEmpty()) {
         workItemTypes.add(WorkItemType.WorkItem);
      }
      IAtsQuery query = null;
      BranchViewToken configTok = data.getConfiguration();
      if (configTok.isValid()) {
         Long configBranchId = data.getConfiguration().getId();
         Conditions.assertTrue(configBranchId > 0, "Configuration Branch must be specified");
         BranchToken branch = atsApi.getBranchService().getBranch(BranchId.valueOf(configBranchId));
         Conditions.assertTrue(branch.isValid(), "Configuration Branch must valid");
         query = atsApi.getQueryService().createQueryWithApplic(configTok, branch);
         query.andWorkItemType(workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
      } else {
         query = atsApi.getQueryService().createQuery(data.getWorkItemTypes().iterator().next(),
            workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
      }
      if (Strings.isValid(data.getTitle())) {
         query.andAttr(AtsAttributeTypes.Title, data.getTitle(), QueryOption.CONTAINS_MATCH_OPTIONS);
      }
      if (!data.getStateTypes().isEmpty()) {
         query.andStateType(data.getStateTypes().toArray(new StateType[data.getStateTypes().size()]));
      }
      if (!data.getTeamDefIds().isEmpty()) {
         query.andTeam(data.getTeamDefIds());
      }
      if (!data.getAiIds().isEmpty()) {
         query.andActionableItem(data.getAiIds());
      }
      if (data.getVersionId() != null && data.getVersionId() > 0L) {
         query.andVersion(data.getVersionId());
      }
      if (data.getStates() != null && !data.getStates().isEmpty()) {
         query.andStates(data.getStates());
      }
      if (data.getHoldState() != null) {
         query.andHoldState(data.getHoldState());
      }
      if (data.getChangeTypes() != null && !data.getChangeTypes().isEmpty()) {
         query.andChangeTypes(data.getChangeTypes());
      }
      if (data.getPriorities() != null && !data.getPriorities().isEmpty()) {
         query.andPriorities(data.getPriorities());
      }
      if (data.getProgramId() > 0L) {
         query.andProgram(data.getProgramId());
      }
      if (data.getInsertionId() > 0L) {
         query.andInsertion(data.getInsertionId());
      }
      if (data.getInsertionActivityId() > 0L) {
         query.andInsertionActivity(data.getInsertionActivityId());
      }
      if (Strings.isValid(data.getWorkPackage())) {
         query.andWorkPackage(data.getWorkPackage());
      }
      if (Strings.isValid(data.getBuildImpact()) || Strings.isValid(data.getBuildImpact2())) {
         query.andBuildImpact();
      }
      for (AttributeValue attrVal : data.getAttrValues().getAttributes()) {
         if (attrVal.isNotExists()) {
            query.andNotExists(attrVal.getAttrType());
         } else if (attrVal.isExists()) {
            query.andExists(attrVal.getAttrType());
         } else if (!attrVal.getValues().isEmpty()) {
            if (attrVal.getQueryOptions() != null) {
               query.andAttr(attrVal.getAttrType(), attrVal.getValues(),
                  attrVal.getQueryOptions().toArray(new QueryOption[attrVal.getQueryOptions().size()]));
            } else {
               query.andAttr(attrVal.getAttrType(), attrVal.getValues());
            }
         }
      }
      if (criteriaProvider != null) {
         criteriaProvider.andCriteria(query);
      }
      return new Pair<IAtsQuery, Boolean>(query, assigneesWithCompletedOrCancelled);
   }

}
