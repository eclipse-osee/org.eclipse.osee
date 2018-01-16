/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.ats.core.internal.util.AtsIdProvider;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.state.StateManagerUtility;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ActionFactory implements IAtsActionFactory {

   private final IAtsWorkItemFactory workItemFactory;
   private final IAttributeResolver attrResolver;
   private final AtsApi atsApi;
   private IAtsTeamDefinition topTeamDefinition;

   public ActionFactory(AtsApi atsApi) {
      this.workItemFactory = atsApi.getWorkItemFactory();
      this.attrResolver = atsApi.getAttributeResolver();
      this.atsApi = atsApi;
   }

   public ActionFactory(IAtsWorkItemFactory workItemFactory, ISequenceProvider sequenceProvider, IAtsActionableItemService actionableItemManager, IAttributeResolver attrResolver, IAtsStateFactory stateFactory, AtsApi atsApi) {
      this.workItemFactory = workItemFactory;
      this.attrResolver = attrResolver;
      this.atsApi = atsApi;
   }

   @Override
   public ActionResult createAction(NewActionData data, IAtsChangeSet changes) {
      IAtsUser asUser = atsApi.getUserService().getUserById(data.getAsUserId());
      Conditions.assertNotNull(asUser, "As-User must be specified.");
      IAtsUser createdBy = null;
      if (Strings.isValid(data.getCreatedByUserId())) {
         createdBy = atsApi.getUserService().getUserById(data.getCreatedByUserId());
      }
      if (createdBy == null && Strings.isValid(data.getCreatedByUserId())) {
         createdBy = atsApi.getUserService().getUserByAccountId(Long.valueOf(data.getCreatedByUserId()));
      }
      Conditions.assertNotNull(createdBy, "Created-By must be specified.");
      Conditions.assertNotNullOrEmpty(data.getAiIds(), "Actionable Items must be specified");
      List<IAtsActionableItem> ais = new LinkedList<>();
      for (String aiId : data.getAiIds()) {
         IAtsActionableItem ai = atsApi.getConfigItem(Long.valueOf(aiId));
         Conditions.assertNotNull(ai, "Actionable Item must be specified.");
         ais.add(ai);
      }

      Date needByDate = null;
      if (Strings.isNumeric(data.getNeedByDateLong())) {
         needByDate = new Date(Long.valueOf(data.getNeedByDateLong()));
      } else if (Strings.isValid(data.getNeedByDate())) {
         try {
            needByDate = DateUtil.getDate("yyyy-MM-dd", data.getNeedByDate());
         } catch (Exception ex) {
            throw new OseeCoreException("Error parsing date.  Must be mm/dd/yyyy.", ex);
         }
      }
      Date createdDate = null;
      if (Strings.isNumeric(data.getCreatedDateLong())) {
         createdDate = new Date(Long.valueOf(data.getCreatedDateLong()));
      } else {
         createdDate = new Date();
      }
      ActionResult result = createAction(asUser, data.getTitle(), data.getDescription(), data.getChangeType(),
         data.getPriority(), data.isValidationRequired(), needByDate, ais, createdDate, createdBy, null, changes);

      if (Strings.isValid(data.getPoints())) {
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            IAgileTeam agileTeam = null;
            if (Strings.isNumeric(data.getAgileTeam())) {
               agileTeam = atsApi.getConfigItem(ArtifactId.valueOf(data.getAgileTeam()));
            }
            if (agileTeam == null) {
               IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
               agileTeam = atsApi.getAgileService().getAgileTeam(teamDef);
            }
            String pointsAttrType = atsApi.getAttributeResolver().getSoleAttributeValue(agileTeam,
               AtsAttributeTypes.PointsAttributeType, null);
            if (Strings.isInValid(pointsAttrType)) {
               pointsAttrType = atsApi.getAttributeResolver().getSoleAttributeValue(teamWf.getTeamDefinition(),
                  AtsAttributeTypes.PointsAttributeType, null);
            }
            if (!Strings.isValid(pointsAttrType)) {
               throw new OseeArgumentException(
                  "Points Attribute Type must be specified on either Agile Team or Team Defintion to set Points",
                  agileTeam.toStringWithId());
            }
            AttributeTypeToken attributeType = atsApi.getAttributeResolver().getAttributeType(pointsAttrType);
            if (attributeType == null) {
               throw new OseeArgumentException("Invalid Points Attribute Type [%s] on Agile Team or Team Definition",
                  pointsAttrType);
            }
            changes.setSoleAttributeValue(teamWf, attributeType, data.getPoints());
         }
      }

      if (data.isUnplanned()) {
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.UnPlannedWork, true);
         }
      }

      String featureGroup = data.getFeatureGroup();
      if (Strings.isValid(featureGroup)) {
         IAgileFeatureGroup group = null;
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            if (Strings.isNumeric(featureGroup)) {
               group = atsApi.getAgileService().getAgileFeatureGroup(ArtifactId.valueOf(featureGroup));
            } else {
               IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(teamWf.getTeamDefinition());
               for (IAgileFeatureGroup grp : atsApi.getAgileService().getAgileFeatureGroups(aTeam)) {
                  if (grp.getName().equals(featureGroup)) {
                     group = grp;
                     break;
                  }
               }
            }
            if (group != null) {
               changes.relate(teamWf, AtsRelationTypes.AgileFeatureToItem_FeatureGroup, group);
            }
         }
      }

      // Set sprint
      String sprintStr = data.getSprint();
      if (Strings.isValid(sprintStr)) {
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            IAgileSprint sprint = null;
            if (Strings.isNumeric(sprintStr)) {
               sprint = atsApi.getAgileService().getAgileSprint(Long.valueOf(sprintStr));
            } else {
               IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(sprint);
               for (IAgileSprint aSprint : atsApi.getAgileService().getAgileSprints(aTeam)) {
                  if (aSprint.getName().equals(sprintStr)) {
                     sprint = aSprint;
                     break;
                  }
               }
            }
            if (sprint != null) {
               changes.relate(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, teamWf);
            }
         }
      }

      // Set backlog if not already set
      // NOTE: This may cause a problem if team already configured to add new items to backlog
      String agileTeamStr = data.getAgileTeam();
      if (Strings.isValid(agileTeamStr)) {
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            IAgileTeam aTeam = null;
            if (Strings.isNumeric(agileTeamStr)) {
               aTeam = atsApi.getAgileService().getAgileTeam(Long.valueOf(agileTeamStr));
            } else {
               ArtifactId aTeamArt = atsApi.getArtifactByName(AtsArtifactTypes.AgileTeam, agileTeamStr);
               if (aTeamArt != null) {
                  aTeam = atsApi.getAgileService().getAgileTeam(aTeamArt);
               }
            }
            if (aTeam != null) {
               IAgileBacklog backlog = atsApi.getAgileService().getBacklogForTeam(aTeam.getId());
               if (backlog != null) {
                  if (!atsApi.getRelationResolver().areRelated(backlog, AtsRelationTypes.Goal_Member, teamWf)) {
                     changes.relate(backlog, AtsRelationTypes.Goal_Member, teamWf);
                  }
               }
            }
         }
      }

      // set originator
      if (Strings.isNumeric(data.getOriginatorStr())) {
         IAtsUser originator = atsApi.getUserService().getUserByAccountId(Long.valueOf(data.getOriginatorStr()));
         if (originator != null) {
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedBy, originator.getUserId());
            }
         }
      }

      // set assignee
      if (Strings.isValid(data.getAssigneeStr())) {
         List<IAtsUser> assignees = new LinkedList<>();
         for (String id : data.getAssigneeStr().split(",")) {
            IAtsUser user = atsApi.getUserService().getUserByAccountId(Long.valueOf(id));
            if (user != null) {
               assignees.add(user);
            }
         }
         if (!assignees.isEmpty()) {
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               teamWf.getStateMgr().setAssignees(assignees);
               changes.add(teamWf);
            }
         }
      }

      // set work package
      if (Strings.isValid(data.getWorkPackage())) {
         IAtsWorkPackage workPkg = null;
         if (Strings.isNumeric(data.getWorkPackage())) {
            workPkg = atsApi.getEarnedValueService().getWorkPackage(ArtifactId.valueOf(data.getWorkPackage()));
         } else {
            ArtifactId art = atsApi.getArtifactByName(AtsArtifactTypes.WorkPackage, data.getWorkPackage());
            if (art != null) {
               workPkg = atsApi.getEarnedValueService().getWorkPackage(art);
            }
         }
         if (workPkg != null) {
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               atsApi.getEarnedValueService().setWorkPackage(workPkg, teamWf, changes);
            }
         } else {
            result.getResults().errorf("Inavlid Work Package id or name [%s]", data.getWorkPackage());
         }
      }

      // set any additional values
      for (Entry<String, String> attr : data.getAttrValues().entrySet()) {
         if (!Strings.isNumeric(attr.getKey())) {
            throw new OseeArgumentException("Invalid attribute type id %s", attr.getKey());
         }
         AttributeTypeId attributeType = atsApi.getStoreService().getAttributeType(Long.valueOf(attr.getKey()));
         if (attributeType == null) {
            throw new OseeArgumentException("Invalid attribute type id %s", attr.getKey());
         }
         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            changes.setSoleAttributeValue(teamWf, attributeType, attr.getValue());
         }
      }
      return result;
   }

   @Override
   public ActionResult createAction(IAtsUser user, String title, String desc, ChangeType changeType, String priority, boolean validationRequired, Date needByDate, Collection<IAtsActionableItem> actionableItems, Date createdDate, IAtsUser createdBy, INewActionListener newActionListener, IAtsChangeSet changes) {
      Conditions.checkNotNullOrEmptyOrContainNull(actionableItems, "actionableItems");
      Conditions.assertNotNullOrEmpty(title, "Title must be specified");
      // if "tt" is title, this is an action created for development. To
      // make it easier, all fields are automatically filled in for ATS developer

      ArtifactToken actionArt = changes.createArtifact(AtsArtifactTypes.Action, title);
      IAtsAction action = workItemFactory.getAction(actionArt);
      IAtsTeamDefinition topTeamDefinition = getTopTeamDef();
      atsApi.getActionFactory().setAtsId(action, topTeamDefinition, changes);
      changes.add(action);
      setArtifactIdentifyData(action, title, desc, changeType, priority, validationRequired, needByDate, changes);

      // Retrieve Team Definitions corresponding to selected Actionable Items
      Collection<IAtsTeamDefinition> teamDefs = TeamDefinitions.getImpactedTeamDefs(actionableItems);
      if (teamDefs.isEmpty()) {
         StringBuffer sb = new StringBuffer("No teams returned for Action's selected Actionable Items\n");
         for (IAtsActionableItem aia : actionableItems) {
            sb.append("Selected AI \"" + aia + "\" " + aia.getId() + "\n");
         }
         throw new OseeStateException(sb.toString());
      }

      // Create team workflow artifacts
      List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         List<IAtsUser> leads = new LinkedList<>(teamDef.getLeads(actionableItems));
         if (leads.isEmpty()) {
            leads.add(AtsCoreUsers.UNASSIGNED_USER);
         }
         IAtsTeamWorkflow teamWf = createTeamWorkflow(action, teamDef, actionableItems, leads, changes, createdDate,
            createdBy, newActionListener);
         teamWfs.add(teamWf);
         changes.add(teamWf);
      }

      // Notify listener of action creation
      if (newActionListener != null) {
         newActionListener.actionCreated(action);
      }

      changes.add(action);
      ActionResult result = new ActionResult(action, teamWfs);
      return result;
   }

   private IAtsTeamDefinition getTopTeamDef() {
      if (topTeamDefinition == null) {
         topTeamDefinition = TeamDefinitions.getTopTeamDefinition(atsApi.getQueryService());
      }
      return topTeamDefinition;
   }

   @Override
   public IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<IAtsUser> assignees, IAtsChangeSet changes, Date createdDate, IAtsUser createdBy, INewActionListener newActionListener, CreateTeamOption... createTeamOption) {
      IArtifactType teamWorkflowArtifactType = getTeamWorkflowArtifactType(teamDef);

      // NOTE: The persist of the workflow will auto-email the assignees
      IAtsTeamWorkflow teamWf = createTeamWorkflow(action, teamDef, actionableItems, assignees, createdDate, createdBy,
         teamWorkflowArtifactType, newActionListener, changes, createTeamOption);
      return teamWf;
   }

   public IArtifactType getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef) {
      return getTeamWorkflowArtifactType(teamDef, atsApi);
   }

   public static IArtifactType getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, AtsApi atsApi) {
      Conditions.checkNotNull(teamDef, "teamDef");
      IArtifactType teamWorkflowArtifactType = AtsArtifactTypes.TeamWorkflow;
      if (teamDef.getStoreObject() != null) {
         String artifactTypeName = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef,
            AtsAttributeTypes.TeamWorkflowArtifactType, null);
         if (Strings.isValid(artifactTypeName)) {
            boolean found = false;
            for (IArtifactType type : atsApi.getArtifactTypes()) {
               if (type.getName().equals(artifactTypeName)) {
                  teamWorkflowArtifactType = type;
                  found = true;
                  break;
               }
            }
            if (!found) {
               throw new OseeArgumentException(
                  "Team Workflow Artifact Type name [%s] off Team Definition %s could not be found.", artifactTypeName,
                  teamDef.toStringWithId());
            }
         }
      }
      return teamWorkflowArtifactType;
   }

   @Override
   public IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, ArtifactTypeId artifactType, INewActionListener newActionListener, IAtsChangeSet changes, CreateTeamOption... createTeamOption) {

      if (!Arrays.asList(createTeamOption).contains(CreateTeamOption.Duplicate_If_Exists)) {
         // Make sure team doesn't already exist
         for (IAtsTeamWorkflow teamArt : action.getTeamWorkflows()) {
            if (teamArt.getTeamDefinition().equals(teamDef)) {
               throw new OseeArgumentException("Team [%s] already exists for Action [%s]", teamDef,
                  atsApi.getAtsId(action));
            }
         }
      }

      List<IAtsActionableItem> applicableAis = new LinkedList<>();
      for (IAtsActionableItem ai : actionableItems) {
         IAtsTeamDefinition teamDefinitionInherited = ai.getTeamDefinitionInherited();
         if (teamDefinitionInherited != null && teamDef.getId().equals(teamDefinitionInherited.getId())) {
            applicableAis.add(ai);
         }
      }

      IAtsTeamWorkflow teamWf = null;
      ArtifactToken artToken = null;
      if (newActionListener != null) {
         artToken = newActionListener.getArtifactToken(applicableAis);
      }
      if (artToken == null) {
         teamWf = workItemFactory.getTeamWf(changes.createArtifact(artifactType, ""));
      } else {
         teamWf = workItemFactory.getTeamWf(changes.createArtifact(artToken));
      }

      setArtifactIdentifyData(action, teamWf, changes);

      /**
       * Relate Workflow to ActionableItems (by guid) if team is responsible for that AI
       */
      for (IAtsActionableItem aia : applicableAis) {
         atsApi.getActionableItemService().addActionableItem(teamWf, aia, changes);
      }

      // Relate WorkFlow to Team Definition (by guid due to relation loading issues)
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());

      setAtsId(teamWf, teamWf.getTeamDefinition(), changes);

      // If work def id is specified by listener, set as attribute
      boolean set = false;
      if (newActionListener != null) {
         String overrideWorkDefId = newActionListener.getOverrideWorkDefinitionId(teamWf);
         if (Strings.isValid(overrideWorkDefId)) {
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.WorkflowDefinition, overrideWorkDefId);
            set = true;
         }
      }
      // else if work def is specified by provider, set as attribute
      if (!set) {
         for (ITeamWorkflowProvider provider : atsApi.getWorkItemService().getTeamWorkflowProviders().getProviders()) {
            String overrideWorkDefId = provider.getOverrideWorkflowDefinitionId(teamWf);
            if (Strings.isValid(overrideWorkDefId)) {
               changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.WorkflowDefinition, overrideWorkDefId);
            }
         }
      }

      // Initialize state machine
      String workDefinitionName = getWorkDefinitionName(teamDef);
      if (!Strings.isValid(workDefinitionName)) {
         throw new OseeStateException("Work Definition for Team Def [%s] does not exist", teamDef);
      }
      initializeNewStateMachine(teamWf, assignees, createdDate, createdBy, changes);

      // Notify listener of team creation
      if (newActionListener != null) {
         newActionListener.teamCreated(action, teamWf, changes);
      }

      // Relate Action to WorkFlow
      changes.relate(action, AtsRelationTypes.ActionToWorkflow_WorkFlow, teamWf);

      // Auto-add actions to configured goals
      addActionToConfiguredGoal(teamDef, teamWf, actionableItems, changes);

      changes.add(teamWf);

      changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
         AtsCoreUsers.SYSTEM_USER, teamWf, AtsNotifyType.SubscribedTeamOrAi));

      changes.addWorkflowCreated(teamWf);

      /**
       * Add guid TeamDef and AI attributes. This can be removed after 0.26.0 where guids will no longer be needed.
       */
      String createGuidAttrs = atsApi.getConfigValue("CreateGuidAttrs");
      if (createGuidAttrs != null && createGuidAttrs.equals("true")) {
         ConvertAtsConfigGuidAttributesOperations.convertActionableItemsIfNeeded(changes, teamWf.getStoreObject(),
            atsApi);
         ConvertAtsConfigGuidAttributesOperations.convertTeamDefinitionIfNeeded(changes, teamWf.getStoreObject(),
            atsApi);
      }

      return teamWf;
   }

   public String getWorkDefinitionName(IAtsTeamDefinition teamDef) {
      String workDefName =
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workDefName)) {
         return workDefName;
      }

      IAtsTeamDefinition parentTeamDef = teamDef.getParentTeamDef();
      if (parentTeamDef == null) {
         return "WorkDef_Team_Default";
      }
      return getWorkDefinitionName(parentTeamDef);
   }

   @Override
   public void initializeNewStateMachine(IAtsWorkItem workItem, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) {
      initializeNewStateMachine(workItem, assignees, createdDate, createdBy, null, changes);
   }

   @Override
   public void initializeNewStateMachine(IAtsWorkItem workItem, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsWorkDefinition workDefinition, IAtsChangeSet changes) {
      Conditions.checkNotNull(createdDate, "createdDate");
      Conditions.checkNotNull(createdBy, "createdBy");
      Conditions.checkNotNull(changes, "changes");
      IAtsStateDefinition startState = null;
      if (workDefinition == null) {
         startState = workItem.getWorkDefinition().getStartState();
      } else {
         startState = workDefinition.getStartState();
         changes.addAttribute(workItem, AtsAttributeTypes.WorkflowDefinition, workDefinition.getName());
      }
      StateManager stateMgr = new StateManager(workItem, atsApi.getLogFactory(), atsApi);
      workItem.setStateMgr(stateMgr);

      StateManagerUtility.initializeStateMachine(stateMgr, startState, assignees,
         createdBy == null ? changes.getAsUser() : createdBy, changes);
      IAtsUser user = createdBy == null ? changes.getAsUser() : createdBy;
      setCreatedBy(workItem, user, true, createdDate, changes);
      TransitionManager.logStateStartedEvent(workItem, startState, createdDate, user);
   }

   private void logCreatedByChange(IAtsWorkItem workItem, IAtsUser user, Date date, IAtsUser asUser) {
      if (attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, null) == null) {
         workItem.getLog().addLog(LogType.Originated, "", "", date, user.getUserId());
      } else {
         workItem.getLog().addLog(LogType.Originated, "", "Changed by " + asUser.getName(), date, user.getUserId());
      }
   }

   @Override
   public void setCreatedBy(IAtsWorkItem workItem, IAtsUser user, boolean logChange, Date date, IAtsChangeSet changes) {
      if (logChange) {
         logCreatedByChange(workItem, user, date, changes.getAsUser());
      }

      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedDate)) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedDate, date);
      }
      changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(changes.getAsUser(),
         workItem, AtsNotifyType.Originator));
   }

   /**
    * Auto-add actions to a goal configured with relations to the given ActionableItem or Team Definition
    */
   @Override
   public void addActionToConfiguredGoal(IAtsTeamDefinition teamDef, IAtsTeamWorkflow teamWf, Collection<IAtsActionableItem> actionableItems, IAtsChangeSet changes) {
      // Auto-add this team artifact to configured goals
      IRelationResolver relationResolver = atsApi.getRelationResolver();
      for (IAtsGoal goal : relationResolver.getRelated(teamDef, AtsRelationTypes.AutoAddActionToGoal_Goal,
         IAtsGoal.class)) {
         if (!relationResolver.areRelated(goal, AtsRelationTypes.Goal_Member, teamWf)) {
            changes.relate(goal, AtsRelationTypes.Goal_Member, teamWf);
            changes.add(goal);
         }
      }

      // Auto-add this actionable item to configured goals
      for (IAtsActionableItem aia : actionableItems) {
         for (IAtsGoal goal : relationResolver.getRelated(aia, AtsRelationTypes.AutoAddActionToGoal_Goal,
            IAtsGoal.class)) {
            if (!relationResolver.areRelated(goal, AtsRelationTypes.Goal_Member, teamWf)) {
               changes.relate(goal, AtsRelationTypes.Goal_Member, teamWf);
               changes.add(goal);
            }
         }
      }
   }

   /**
    * Set Team Workflow attributes off given action artifact
    */
   public void setArtifactIdentifyData(IAtsAction fromAction, IAtsTeamWorkflow toTeam, IAtsChangeSet changes) {
      Conditions.checkNotNull(fromAction, "fromAction");
      Conditions.checkNotNull(toTeam, "toTeam");
      Conditions.checkNotNull(changes, "changes");
      setArtifactIdentifyData(toTeam, fromAction.getName(),
         attrResolver.getSoleAttributeValue(fromAction, AtsAttributeTypes.Description, ""),
         atsApi.getChangeType(fromAction),
         attrResolver.getSoleAttributeValue(fromAction, AtsAttributeTypes.PriorityType, ""),
         attrResolver.getSoleAttributeValue(fromAction, AtsAttributeTypes.ValidationRequired, false),
         attrResolver.getSoleAttributeValue(fromAction, AtsAttributeTypes.NeedBy, (Date) null), changes);
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    */
   public void setArtifactIdentifyData(IAtsObject atsObject, String title, String desc, ChangeType changeType, String priority, Boolean validationRequired, Date needByDate, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(atsObject, CoreAttributeTypes.Name, title);
      if (Strings.isValid(desc)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.Description, desc);
      }
      if (changeType != null) {
         atsApi.setChangeType(atsObject, changeType, changes);
      }
      if (Strings.isValid(priority)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.PriorityType, priority);
      }
      if (needByDate != null) {
         changes.addAttribute(atsObject, AtsAttributeTypes.NeedBy, needByDate);
      }
      if (validationRequired) {
         changes.addAttribute(atsObject, AtsAttributeTypes.ValidationRequired, true);
      }
   }

   @Override
   public Collection<IAtsTeamWorkflow> getSiblingTeamWorkflows(IAtsTeamWorkflow teamWf) {
      List<IAtsTeamWorkflow> teams = new LinkedList<>();
      IAtsAction action = getAction(teamWf);
      for (IAtsTeamWorkflow teamChild : atsApi.getRelationResolver().getRelated(action,
         AtsRelationTypes.ActionToWorkflow_WorkFlow, IAtsTeamWorkflow.class)) {
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
   public void setAtsId(IAtsObject newObject, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      new AtsIdProvider(atsApi.getSequenceProvider(), atsApi.getAttributeResolver(), newObject, teamDef).setAtsId(
         changes);
   }

}
