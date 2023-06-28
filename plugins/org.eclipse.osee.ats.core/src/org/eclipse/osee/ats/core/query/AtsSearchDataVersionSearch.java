/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AttributeValue;
import org.eclipse.osee.ats.api.util.AttributeValues;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Search by version usually does better by starting with the version and filtering down results. This should only be
 * called when a valid data.versionId is specified.
 *
 * @author Donald G. Dunne
 */
public class AtsSearchDataVersionSearch {

   private final AtsSearchData data;
   private final AtsApi atsApi;
   private final XResultData rd;

   public AtsSearchDataVersionSearch(AtsSearchData data, AtsApi atsApi) {
      this.atsApi = atsApi;
      this.data = data.copy();
      rd = new XResultData();
   }

   public AtsSearchDataResults performSearch() {

      Set<IAtsWorkItem> results = new HashSet<>();

      Long versionId = data.getVersionId();
      IAtsVersion version = atsApi.getVersionService().getVersionById(ArtifactId.valueOf(versionId));
      if (version == null) {
         rd.errorf("Invalid use of AtsSearchDataVersion, must specify Version; found ", versionId);
         return new AtsSearchDataResults(java.util.Collections.emptyList(), rd);
      }

      List<WorkItemType> workItemTypes = data.getWorkItemTypes();
      if (workItemTypes.contains(WorkItemType.Goal)) {
         rd.errorf("Invalid Goal Type for Version Search");
         return new AtsSearchDataResults(java.util.Collections.emptyList(), rd);
      }
      if (workItemTypes.contains(WorkItemType.AgileBacklog) || workItemTypes.contains(WorkItemType.AgileSprint)) {
         rd.errorf("Invalid Agile Type(s) for Version Search");
         return new AtsSearchDataResults(java.util.Collections.emptyList(), rd);
      }

      for (IAtsTeamWorkflow teamWf : atsApi.getVersionService().getTargetedForTeamWorkflows(version)) {
         results.addAll(addWorkItems(teamWf));
      }
      results = filterStateType(data, results);
      results = filterStateName(data, results);
      results = filterChangeType(data, results);
      results = filterUserType(data, results);
      results = filterAttrValues(data, results);

      List<ArtifactToken> arts = Collections.castAll(AtsObjects.getArtifacts(results));
      return new AtsSearchDataResults(arts, rd);
   }

   private Collection<IAtsWorkItem> addWorkItems(IAtsTeamWorkflow teamWf) {
      Set<IAtsWorkItem> results = new HashSet<>();
      // Double check teamDefs and Ais just in case something is miss-targeted
      if (!matchTeamDefs(teamWf) && !matchAis(teamWf)) {
         return results;
      }
      if (data.getWorkItemTypes().contains(WorkItemType.TeamWorkflow)) {
         results.add(teamWf);
      }
      if (data.getWorkItemTypes().contains(WorkItemType.Task)) {
         results.addAll(atsApi.getTaskService().getTasks(teamWf));
      }
      boolean includeDecRevs = data.getWorkItemTypes().contains(WorkItemType.DecisionReview);
      boolean includePeerRevs = data.getWorkItemTypes().contains(WorkItemType.PeerReview);
      if (includePeerRevs || includeDecRevs) {
         for (IAtsAbstractReview rev : atsApi.getReviewService().getReviews(teamWf)) {
            if (rev.isDecisionReview() && includeDecRevs) {
               results.add(rev);
            } else if (rev.isPeerReview() && includePeerRevs) {
               results.add(rev);
            }
         }
      }
      return results;
   }

   private Set<IAtsWorkItem> filterAttrValues(AtsSearchData data, Set<IAtsWorkItem> workItems) {
      AttributeValues attrValues = data.getAttrValues();
      if (attrValues.isEmpty()) {
         return workItems;
      }
      Set<IAtsWorkItem> results = new HashSet<>();
      for (IAtsWorkItem workItem : workItems) {
         for (AttributeValue attrValue : attrValues.getAttributes()) {
            AttributeTypeToken attrType = attrValue.getAttrType();
            List<String> values = attrValue.getValues();
            List<String> workItemAttrValues =
               atsApi.getAttributeResolver().getAttributesToStringList(workItem, attrType);
            if (values.isEmpty() && workItemAttrValues.isEmpty()) {
               results.add(workItem);
            } else if (!Collections.setIntersection(values, workItemAttrValues).isEmpty()) {
               results.add(workItem);
            }
         }
      }
      return results;
   }

   private Set<IAtsWorkItem> filterUserType(AtsSearchData data, Set<IAtsWorkItem> workItems) {
      AtsSearchUserType userType = data.getUserType();
      String userId = data.getUserId();
      if (Strings.isInvalid(userId)) {
         return workItems;
      }
      if (userType.isNone()) {
         return workItems;
      }
      AtsUser user = atsApi.getUserService().getUserByUserId(userId);
      if (user == null) {
         return workItems;
      }
      Set<IAtsWorkItem> results = new HashSet<>();
      for (IAtsWorkItem workItem : workItems) {
         if (userType.isAssignee() && workItem.getAssignees().contains(user)) {
            results.add(workItem);
         } else if (userType.isOriginated() && workItem.getCreatedBy().equals(user)) {
            results.add(workItem);
         } else if (userType.isFavorites() && atsApi.getWorkItemService().isFavorite(workItem, user)) {
            results.add(workItem);
         } else if (userType.isSubscribed() && atsApi.getWorkItemService().isSubcribed(workItem, user)) {
            results.add(workItem);
         }
      }
      return results;
   }

   private Set<IAtsWorkItem> filterChangeType(AtsSearchData data, Set<IAtsWorkItem> workItems) {
      String changeType = data.getChangeType();
      if (Strings.isInValid(changeType)) {
         return workItems;
      }
      Set<IAtsWorkItem> results = new HashSet<>();
      for (IAtsWorkItem workItem : workItems) {
         if (changeType.equals(
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, ""))) {
            results.add(workItem);
         }
      }
      return results;
   }

   private Set<IAtsWorkItem> filterStateName(AtsSearchData data, Set<IAtsWorkItem> workItems) {
      String stateName = data.getState();
      if (Strings.isInValid(stateName)) {
         return workItems;
      }
      Set<IAtsWorkItem> results = new HashSet<>();
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.getCurrentStateName().equals(stateName)) {
            results.add(workItem);
         }
      }
      return results;
   }

   private Set<IAtsWorkItem> filterStateType(AtsSearchData data, Set<IAtsWorkItem> workItems) {
      List<StateType> stateTypes = data.getStateTypes();
      if (stateTypes.isEmpty()) {
         return workItems;
      }
      Set<IAtsWorkItem> results = new HashSet<>();
      for (IAtsWorkItem workItem : workItems) {
         if (stateTypes.contains(workItem.getCurrentStateType())) {
            results.add(workItem);
         }
      }
      return results;
   }

   private boolean matchTeamDefs(IAtsTeamWorkflow teamWf) {
      List<Long> teamDefIds = data.getTeamDefIds();
      if (!teamDefIds.isEmpty()) {
         return teamDefIds.contains(teamWf.getTeamDefinition().getId());
      }
      return false;
   }

   private boolean matchAis(IAtsTeamWorkflow teamWf) {
      List<Long> aiIds = data.getAiIds();
      if (!aiIds.isEmpty()) {
         for (IAtsActionableItem ai : teamWf.getActionableItems()) {
            if (aiIds.contains(ai.getId())) {
               return true;
            }
         }
      }
      return false;
   }

}
