package org.eclipse.osee.ats.core.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.ISearchCriteriaProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchDataSearch {

   private final AtsSearchData data;
   private final AtsApi atsApi;
   private final ISearchCriteriaProvider criteriaProvider;
   private AtsUser userById;

   public AtsSearchDataSearch(AtsSearchData data, AtsApi atsApi, ISearchCriteriaProvider criteriaProvider) {
      this.atsApi = atsApi;
      this.criteriaProvider = criteriaProvider;
      this.data = data.copy();
   }

   public Collection<ArtifactToken> performSearch() {

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
      return results;
   }

   public void setUserType(AtsSearchUserType userType, IAtsQuery query) {
      if (Strings.isValid(data.getUserId())) {
         userById = atsApi.getUserService().getUserById(data.getUserId());
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
      IAtsQuery query = atsApi.getQueryService().createQuery(data.getWorkItemTypes().iterator().next(),
         workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
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
      if (criteriaProvider != null) {
         criteriaProvider.andCriteria(query);
      }
      return new Pair<IAtsQuery, Boolean>(query, assigneesWithCompletedOrCancelled);
   }

   public Collection<ArtifactToken> performSearchNew() {
      Pair<IAtsQuery, Boolean> result = createAtsQuery();
      IAtsQuery query = result.getFirst();

      return query.getResultArtifactsNew().getList();
   }

}
