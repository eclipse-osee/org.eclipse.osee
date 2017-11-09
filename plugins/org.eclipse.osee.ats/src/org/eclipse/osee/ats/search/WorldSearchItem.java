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
package org.eclipse.osee.ats.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorldSearchItem extends WorldUISearchItem {

   AtsSearchData data;

   public WorldSearchItem(AtsSearchData data) {
      super(data.getSearchName());
      this.data = data.copy();
   }

   public WorldSearchItem(String searchName) {
      super(searchName);
      data = new AtsSearchData(searchName);
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return super.getSelectedName(searchType);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {

      boolean assigneesWithCompletedOrCancelled = false;
      List<StateType> stateTypes = data.getStateTypes();
      AtsSearchUserType userType = data.getUserType();

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
      IAtsQuery query = AtsClientService.get().getQueryService().createQuery(data.getWorkItemTypes().iterator().next(),
         workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
      if (Strings.isValid(data.getTitle())) {
         query.andAttr(AtsAttributeTypes.Title, data.getTitle(), QueryOption.CONTAINS_MATCH_OPTIONS);
      }
      if (!data.getStateTypes().isEmpty()) {
         query.andStateType(data.getStateTypes().toArray(new StateType[data.getStateTypes().size()]));
      }
      if (Strings.isValid(data.getUserId())) {
         IAtsUser userById = AtsClientService.get().getUserService().getUserById(data.getUserId());
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
      if (!data.getTeamDefIds().isEmpty()) {
         query.andTeam(data.getTeamDefIds());
      }
      if (!data.getAiIds().isEmpty()) {
         query.andActionableItem(data.getAiIds());
      }
      if (data.getVersionId() != null && data.getVersionId() > 0L) {
         query.andVersion(data.getVersionId());
      }
      if (Strings.isValid(data.getState())) {
         query.andState(data.getState());
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
      if (data.getWorkPackageId() > 0L) {
         query.andWorkPackage(data.getWorkPackageId());
      }
      if (Strings.isValid(data.getColorTeam())) {
         query.andColorTeam(data.getColorTeam());
      }
      performSearch(query);

      Set<Artifact> results = new HashSet<>();
      results.addAll(Collections.castAll(query.getResultArtifacts().getList()));

      /**
       * Perform a second search (see above) and add results to return set. This is because the framework search api
       * does not support OR-ing attribute values. This should be removed once that is implemented.
       */
      if (assigneesWithCompletedOrCancelled) {
         AtsSearchData data2 = data.copy();
         data2.setUserType(AtsSearchUserType.AssigneeWas);
         WorldSearchItem item2 = new WorldSearchItem(data2);
         results.addAll(item2.performSearch(SearchType.Search));
      }
      return results;
   }

   /**
    * Implement to populate query with extended options
    */
   protected void performSearch(IAtsQuery query) {
      // do nothing
   }

   @Override
   public WorldUISearchItem copy() {
      return new WorldSearchItem(data);
   }

   public AtsSearchData getData() {
      return data;
   }

}
