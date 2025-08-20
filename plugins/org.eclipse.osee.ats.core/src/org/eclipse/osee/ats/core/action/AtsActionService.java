/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.core.action;

import com.fasterxml.jackson.core.JsonFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.action.ICreateNewActionFieldsProvider;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.CreateNewActionField;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionService;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IWorkItemListener;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.internal.util.AtsIdProvider;
import org.eclipse.osee.ats.core.task.track.ScriptTaskTrackingOperation;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsActionService implements IAtsActionService {
   protected final AtsApi atsApi;

   protected IAtsTeamDefinition topTeamDefinition;
   protected JsonFactory jsonFactory;
   protected IWorkItemListener workItemListener;

   public AtsActionService() {
      this(null);
      // for jax-rs
   }

   public AtsActionService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public NewActionData createAction(NewActionData data, IAtsChangeSet changes) {
      CreateActionOperation op = new CreateActionOperation(data, changes, atsApi);
      return op.createAction();
   }

   @Override
   public NewActionResult createActionAndWorkingBranch(NewActionData newActionData) {
      NewActionData resultActionData = null;
      try {
         Collection<IAtsActionableItem> ais = new ArrayList<>();
         ActionableItem ai =
            atsApi.getQueryService().getConfigItem(Long.valueOf(newActionData.getAiIds().iterator().next()));
         ais.add(ai);

         if (Strings.isInvalid(newActionData.getOpName())) {
            newActionData.setOpName("createActionAndWorkingBranch");
         }

         AtsUser asUser = CreateActionOperation.getAsUser(newActionData, atsApi.user(), atsApi);
         if (newActionData.getRd().isErrors()) {
            NewActionResult result = new NewActionResult();
            result.getResults().merge(newActionData.getRd());
            return result;
         }

         IAtsChangeSet changes = atsApi.createChangeSet(newActionData.getOpName(), asUser);
         IAtsVersion version =
            atsApi.getVersionService().getVersionById(ArtifactId.valueOf(newActionData.getVersionId()));
         resultActionData = atsApi.getActionService().createAction(newActionData, changes);

         if (resultActionData.getRd().isErrors()) {
            NewActionResult result = new NewActionResult();
            result.setResults(resultActionData.getRd());
            return result;
         }

         IAtsTeamWorkflow teamWf = resultActionData.getActResult().getAtsTeamWfs().iterator().next();

         atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);
         TransactionToken tx = changes.execute();
         if (tx.isInvalid()) {
            resultActionData.getActResult().getResults().error("Error setting targted version");
            return resultActionData.getActResult();
         }
         resultActionData.getActResult().setTransaction(tx);

         BranchData workingBranch = atsApi.getBranchService().createWorkingBranch(teamWf);
         resultActionData.getActResult().setWorkingBranchId(workingBranch.getNewBranch());
      } catch (Exception ex) {
         NewActionResult result = new NewActionResult();
         result.getResults().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
         return result;
      }
      return resultActionData.getActResult();

   }

   /**
    * Auto-add actions to a goal configured with relations to the given ActionableItem or Team Definition
    */
   @Override
   public void addActionToConfiguredGoal(IAtsTeamDefinition teamDef, IAtsTeamWorkflow teamWf,
      Collection<IAtsActionableItem> actionableItems, IAtsGoal handledGoal, IAtsChangeSet changes) {

      // Auto-add this team artifact to configured goals
      IRelationResolver relationResolver = atsApi.getRelationResolver();
      for (ArtifactToken goal : relationResolver.getRelated(teamDef, AtsRelationTypes.AutoAddActionToGoal_Goal)) {
         if (goal.isOfType(AtsArtifactTypes.Goal)) {
            if (handledGoal != null && goal.equals(handledGoal.getStoreObject())) {
               continue;
            }
            if (!relationResolver.areRelated(goal, AtsRelationTypes.Goal_Member, teamWf.getStoreObject())) {
               changes.relate(goal, AtsRelationTypes.Goal_Member, teamWf.getStoreObject());
               changes.add(goal);
            }
         }
      }

      // Auto-add this actionable item to configured goals
      for (IAtsActionableItem aia : actionableItems) {
         for (ArtifactToken goal : relationResolver.getRelated(aia, AtsRelationTypes.AutoAddActionToGoal_Goal)) {
            if (goal.isOfType(AtsArtifactTypes.Goal)) {
               if (handledGoal != null && goal.equals(handledGoal.getStoreObject())) {
                  continue;
               }
               if (!relationResolver.areRelated(goal, AtsRelationTypes.Goal_Member, teamWf.getStoreObject())) {
                  changes.relate(goal, AtsRelationTypes.Goal_Member, teamWf.getStoreObject());
                  changes.add(goal);
               }
            }
         }
      }
   }

   @Override
   public Collection<IAtsTeamWorkflow> getSiblingTeamWorkflows(IAtsTeamWorkflow teamWf) {
      List<IAtsTeamWorkflow> teams = new LinkedList<>();
      IAtsAction action = getAction(teamWf);
      for (IAtsTeamWorkflow teamChild : atsApi.getRelationResolver().getRelated(action,
         AtsRelationTypes.ActionToWorkflow_TeamWorkflow, IAtsTeamWorkflow.class)) {
         if (teamChild.notEqual(teamWf)) {
            teams.add(teamChild);
         }
      }
      return teams;
   }

   @Override
   public IAtsAction getAction(IAtsTeamWorkflow teamWf) {
      return atsApi.getRelationResolver().getRelatedOrNull(teamWf, AtsRelationTypes.ActionToWorkflow_Action,
         IAtsAction.class);
   }

   @Override
   public String setAtsId(IAtsObject newObject, IAtsTeamDefinition teamDef, IWorkItemListener workItemListener,
      IAtsChangeSet changes) {
      AtsIdProvider atsIdProvider =
         new AtsIdProvider(atsApi.getSequenceProvider(), atsApi.getAttributeResolver(), newObject, teamDef);
      atsIdProvider.setWorkItemListener(workItemListener);
      String atsId = atsIdProvider.setAtsId(changes);
      return atsId;
   }

   @Override
   public String getActionStateJson(Collection<IAtsWorkItem> workItems) {
      try {
         ActionServiceOperations ops = new ActionServiceOperations(atsApi);
         return ops.getActionStateJson(workItems, getJsonFactory());
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private JsonFactory getJsonFactory() {
      if (jsonFactory == null) {
         jsonFactory = JsonUtil.getFactory();
      }
      return jsonFactory;
   }

   public IWorkItemListener getWorkItemListener() {
      return workItemListener;
   }

   public void setWorkItemListener(IWorkItemListener workItemListener) {
      this.workItemListener = workItemListener;
   }

   @Override
   public IAtsGoal createGoal(String title, IAtsChangeSet changes) {
      WorkDefinition workDef =
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
      return createGoal(title, AtsArtifactTypes.Goal, workDef, atsApi.getTeamDefinitionService().getTopTeamDefinition(),
         changes, null);
   }

   @Override
   public IAtsGoal createGoal(ArtifactToken token, IAtsTeamDefinition teamDef, AtsApi atsApi, IAtsChangeSet changes) {
      return createGoal(token.getName(), token, token.getArtifactType(),
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal), teamDef, changes,
         workItemListener);
   }

   @Override
   public IAtsGoal createGoal(String title, ArtifactTypeToken artifactType, WorkDefinition workDefinition,
      IAtsTeamDefinition teamDef, IAtsChangeSet changes, IWorkItemListener workItemListener) {
      return createGoal(title, ArtifactId.SENTINEL, artifactType, workDefinition, teamDef, changes, workItemListener);
   }

   private IAtsGoal createGoal(String title, ArtifactId id, ArtifactTypeToken artifactType,
      WorkDefinition workDefinition, IAtsTeamDefinition teamDef, IAtsChangeSet changes,
      IWorkItemListener workItemListener) {
      ArtifactToken art = null;
      if (id.isValid()) {
         art = changes.createArtifact(artifactType, title, id.getId());
      } else {
         art = changes.createArtifact(artifactType, title);
      }
      IAtsGoal goal = atsApi.getWorkItemService().getGoal(art);

      if (goal == null) {
         throw new OseeCoreException(
            "In AtsActionService.createGoal, the local vairable \"goal\" is null which is dereferenced");
      }

      Conditions.assertNotNull(teamDef, "Team Definition can not be null for %s", goal.toStringWithId());
      atsApi.getActionService().setAtsId(goal, teamDef, workItemListener, changes);

      WorkDefinition useWorkDefinition = workDefinition;
      if (useWorkDefinition == null) {
         useWorkDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
      }
      Conditions.assertNotNull(workDefinition, "Work Definition can not be null for %s", goal.toStringWithId());
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(goal, workDefinition, changes);

      atsApi.getActionService().initializeNewStateMachine(goal, Arrays.asList(atsApi.getUserService().getCurrentUser()),
         new Date(), atsApi.getUserService().getCurrentUser(), workDefinition, changes);

      return goal;
   }

   @Override
   public void initializeNewStateMachine(IAtsWorkItem workItem, Collection<AtsUser> assignees, Date createdDate,
      AtsUser createdBy, WorkDefinition workDefinition, IAtsChangeSet changes) {
      Conditions.checkNotNull(createdDate, "createdDate");
      Conditions.checkNotNull(createdBy, "createdBy");
      Conditions.checkNotNull(changes, "changes");
      Conditions.checkNotNull(workDefinition, "workDefinition");

      // set for bootstrapping issues only when creating initial work item
      atsApi.getWorkDefinitionService().internalSetWorkDefinition(workItem, workDefinition);

      StateDefinition startState = workDefinition.getStartState();
      changes.initalizeWorkflow(workItem, startState, assignees);

      AtsUser user = createdBy;
      setCreatedBy(workItem, user, true, createdDate, changes);
      TransitionManager.logStateStartedEvent(workItem, startState, createdDate, user);
   }

   @Override
   public void setCreatedBy(IAtsWorkItem workItem, AtsUser user, boolean logChange, Date date, IAtsChangeSet changes) {
      if (logChange) {
         logCreatedByChange(workItem, user, date, changes.getAsUser());
      }
      if (atsApi.getAttributeResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      if (atsApi.getAttributeResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedDate)) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedDate, date);
      }
      changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(changes.getAsUser(),
         workItem, AtsNotifyType.Originator));
   }

   private void logCreatedByChange(IAtsWorkItem workItem, AtsUser user, Date date, AtsUser asUser) {
      if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, null) == null) {
         workItem.getLog().addLog(LogType.Originated, "", "", date, user.getUserId());
      } else {
         workItem.getLog().addLog(LogType.Originated, "", "Changed by " + asUser.getName(), date, user.getUserId());
      }
   }

   @Override
   public Collection<CreateNewActionField> getCreateActionFields(Collection<IAtsActionableItem> actionableItems) {
      List<CreateNewActionField> fields = new LinkedList<>();
      for (ICreateNewActionFieldsProvider provider : CreateNewActionFieldsProviderService.getCreateNewActionProviders()) {
         if (provider.actionableItemHasFields(atsApi, actionableItems)) {
            fields.addAll(provider.getCreateNewActionFields(atsApi));
         }
      }
      return fields;
   }

   @Override
   public TaskTrackingData createUpdateScriptTaskTrack(TaskTrackingData taskTrackingData) {
      ScriptTaskTrackingOperation op = new ScriptTaskTrackingOperation(taskTrackingData, atsApi);
      return op.run();
   }

   @Override
   public void setScriptTaskCompleted(TaskTrackingData taskTrackingData) {
      ScriptTaskTrackingOperation op = new ScriptTaskTrackingOperation(taskTrackingData, atsApi);
      op.setTaskCompleted();
   }

   @Override
   public NewActionData createActionData(String opName, String title, String desc) {
      return createActionData(opName, title, desc, null);
   }

   @Override
   public NewActionData createActionData(String opName, String title, ArtifactToken aiTok) {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(aiTok);
      return createActionData(opName, title, "see title", Arrays.asList(ai));
   }

   @Override
   public NewActionData createActionData(String opName, String title, String desc, Collection<IAtsActionableItem> ais) {
      Conditions.assertNotNullOrEmpty(opName, "OpName can not be null or emtpy");
      NewActionData data = new NewActionData().andOpName(opName).andTitle(title).andDescription(desc);
      data.andAsUser(user());
      data.andCreatedBy(user());
      data.andCreatedDate(new Date());
      if (ais != null) {
         data.andAis(ais);
      }
      return data;
   }

   @Override
   public NewActionData createTeamWfData(String opName, IAtsAction action, IAtsTeamDefinition teamDef) {
      Conditions.assertNotNullOrEmpty(opName, "OpName can not be null or emtpy");
      NewActionData data = new NewActionData();
      data.setOpName(opName);
      data.andAsUser(user());
      data.andCreatedBy(user());
      data.andCreatedDate(new Date());
      data.setParentAction(action.getArtifactId());
      data.setTeamDef(teamDef.getArtifactId());
      return data;
   }

   @Override
   public AtsUser user() {
      return atsApi.getUserService().getCurrentUser();
   }

}