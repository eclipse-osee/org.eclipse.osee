/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.core.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileWriterResult;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AgileItemWriter {

   private final AtsApi atsApi;
   private final JaxAgileItem newItem;
   private final IAgileService agileService;
   private List<IAtsWorkItem> workItems;

   public AgileItemWriter(AtsApi atsApi, IAgileService agileService, JaxAgileItem newItem) {
      this.atsApi = atsApi;
      this.agileService = agileService;
      this.newItem = newItem;
   }

   public AgileWriterResult write() {
      AgileWriterResult result = new AgileWriterResult();
      result.setJaxAgileItem(newItem);
      try {
         IAtsChangeSet changes =
            atsApi.getStoreService().createAtsChangeSet("Update new Agile Item", AtsCoreUsers.SYSTEM_USER);
         if (Strings.isValid(newItem.getToState())) {
            List<IAtsWorkItem> workItems = getWorkItems();

            for (IAtsWorkItem workItem : workItems) {
               // just assignee change
               if (workItem.getStateMgr().getCurrentStateName().equals(newItem.getToState())) {
                  Collection<AtsUser> toStateAssignees = getAssignees(newItem.getToStateUsers());
                  resolveAssignees(toStateAssignees);
                  workItem.getStateMgr().setAssignees(toStateAssignees);
                  changes.add(workItem);
               }
               // transition change
               else {
                  Collection<AtsUser> toStateAssignees = getAssignees(newItem.getToStateUsers());
                  resolveAssignees(toStateAssignees);
                  TransitionHelper helper = new TransitionHelper("Transition Agile Workflow", Arrays.asList(workItem),
                     newItem.getToState(), toStateAssignees, "Cancelled via Agile Kanban", changes, atsApi,
                     TransitionOption.OverrideAssigneeCheck);
                  helper.setTransitionUser(AtsCoreUsers.SYSTEM_USER);
                  TransitionManager mgr = new TransitionManager(helper);
                  TransitionResults results = new TransitionResults();
                  mgr.handleTransitionValidation(results);

                  if (!results.isEmpty()) {
                     throw new OseeArgumentException("Exception transitioning " + results.toString());
                  }
                  mgr.handleTransition(results);
                  if (!results.isEmpty()) {
                     throw new OseeArgumentException("Exception transitioning " + results.toString());
                  }
               }
            }

         }

         if (newItem.isSetFeatures()) {
            Collection<IAgileFeatureGroup> features = agileService.getAgileFeatureGroups(newItem.getFeatures());
            List<ArtifactToken> featureArts = new LinkedList<>();
            for (IAgileFeatureGroup feature : features) {
               featureArts.add(feature.getStoreObject());
            }
            for (ArtifactToken awa : atsApi.getQueryService().getArtifacts(newItem.getIds())) {
               for (IAgileFeatureGroup feature : features) {
                  ArtifactToken featureArt = feature.getStoreObject();
                  if (!atsApi.getRelationResolver().areRelated(featureArt,
                     AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup, awa)) {
                     changes.relate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
                  }
               }
               for (ArtifactToken featureArt : atsApi.getRelationResolver().getRelated(awa,
                  AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup)) {
                  if (!featureArts.contains(featureArt)) {
                     changes.unrelate(featureArt, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
                  }
               }
            }
         } else if (newItem.isRemoveFeatures()) {
            for (ArtifactToken awa : atsApi.getQueryService().getArtifacts(newItem.getIds())) {
               for (ArtifactToken feature : atsApi.getRelationResolver().getRelated(awa,
                  AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup)) {
                  changes.unrelate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
         }

         if (newItem.isSetSprint()) {
            ArtifactToken sprintArt = atsApi.getQueryService().getArtifact(newItem.getSprintId());
            IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintArt);
            for (ArtifactToken awa : atsApi.getQueryService().getArtifacts(newItem.getIds())) {
               if (sprint != null) {
                  changes.setRelation(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, awa);
               } else {
                  changes.unrelateAll(awa, AtsRelationTypes.AgileSprintToItem_AtsItem);
               }
               changes.add(sprint);
            }
         }

         if (newItem.isSetBacklog()) {
            ArtifactToken backlogArt = atsApi.getQueryService().getArtifact(newItem.getBacklogId());
            IAgileSprint backlog = atsApi.getAgileService().getAgileSprint(backlogArt);
            for (ArtifactToken awa : atsApi.getQueryService().getArtifacts(newItem.getIds())) {
               if (backlog != null) {
                  changes.setRelation(backlog, AtsRelationTypes.Goal_Member, awa);
               } else {
                  changes.unrelateAll(awa, AtsRelationTypes.Goal_Member);
               }
               changes.add(backlog);
            }
         }

         if (newItem.isSetAssignees()) {
            Collection<AtsUser> assignees = getAssignees(newItem.getAssigneesAccountIds());
            for (IAtsWorkItem workItem : getWorkItems()) {
               workItem.getStateMgr().setAssignees(assignees);
               changes.add(workItem);
            }

            String assigneesStr = Collections.toString(assignees, "; ", Named::getName);
            newItem.setAssigneesStr(assigneesStr);
            newItem.setAssigneesStrShort(Strings.truncate(assigneesStr, 30, true));
         }

         if (!changes.isEmpty()) {
            changes.execute();
         }
      } catch (Exception ex) {
         result.getResults().errorf("Error Updating Work Items [%s]", Lib.exceptionToString(ex));
      }
      return result;
   }

   /**
    * Ensure UnAssigned is not an assignee if another assignee exists. Ensure UnAssigned is assigned if non else.
    */
   private void resolveAssignees(Collection<AtsUser> toStateAssignees) {
      if (toStateAssignees.size() > 1 && toStateAssignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
         toStateAssignees.remove(AtsCoreUsers.UNASSIGNED_USER);
      }
      if (toStateAssignees.isEmpty()) {
         toStateAssignees.add(AtsCoreUsers.UNASSIGNED_USER);
      }
   }

   private List<IAtsWorkItem> getWorkItems() {
      if (workItems == null) {
         workItems = new LinkedList<>();
         for (long id : newItem.getIds()) {
            IAtsWorkItem workItem = atsApi.getQueryService().getTeamWf(id);
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   private Collection<AtsUser> getAssignees(List<String> usersAccountIds) {
      List<AtsUser> users = new ArrayList<>();
      for (String userId : usersAccountIds) {
         users.add(atsApi.getUserService().getUserById(ArtifactId.valueOf(userId)));
      }
      return users;
   }
}
