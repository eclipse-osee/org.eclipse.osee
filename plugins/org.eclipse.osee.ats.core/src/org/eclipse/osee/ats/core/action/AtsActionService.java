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
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionService;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.IWorkItemListener;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.internal.util.AtsIdProvider;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsActionService implements IAtsActionService {
   private final AtsApi atsApi;

   private IAtsTeamDefinition topTeamDefinition;
   private JsonFactory jsonFactory;
   private IWorkItemListener workItemListener;
   private static final Collection<INewActionListener> actionListeners = new ArrayList<>();

   public AtsActionService() {
      this(null);
      // for jax-rs
   }

   public AtsActionService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public ActionResult createAction(NewActionData data, IAtsChangeSet changes) {
      ActionResult result = null;
      try {
         AtsUser asUser = atsApi.getUserService().getUserByUserId(data.getAsUserId());
         Conditions.assertNotNull(asUser, "As-User must be specified.");
         AtsUser createdBy = null;
         if (Strings.isValid(data.getCreatedByUserId())) {
            createdBy = atsApi.getUserService().getUserByUserId(data.getCreatedByUserId());
         }
         if (createdBy == null && Strings.isValid(data.getCreatedByUserId())) {
            createdBy = atsApi.getUserService().getUserByUserId(data.getCreatedByUserId());
         }
         Conditions.assertNotNull(createdBy, "Created-By must be specified.");
         Conditions.assertNotNullOrEmpty(data.getAiIds(), "Actionable Items must be specified");
         List<IAtsActionableItem> ais = new LinkedList<>();
         for (String aiId : data.getAiIds()) {
            IAtsActionableItem ai = atsApi.getQueryService().getConfigItem(Long.valueOf(aiId));
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
         result = createAction(asUser, data.getTitle(), data.getDescription(), data.getChangeType(), data.getPriority(),
            data.isValidationRequired(), needByDate, ais, createdDate, createdBy, java.util.Collections.emptyList(),
            changes);

         if (result.getResults().isErrors()) {
            return result;
         }

         if (Strings.isValid(data.getPoints())) {
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               IAgileTeam agileTeam = null;
               if (Strings.isNumeric(data.getAgileTeam())) {
                  agileTeam = atsApi.getQueryService().getConfigItem(ArtifactId.valueOf(data.getAgileTeam()));
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
               AttributeTypeToken attributeType = atsApi.tokenService().getAttributeType(pointsAttrType);

               changes.setSoleAttributeValue(teamWf, attributeType, data.getPoints());
            }
         }

         if (data.isUnplanned()) {
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.UnplannedWork, true);
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
                  if (aTeam != null) {
                     for (IAgileFeatureGroup grp : atsApi.getAgileService().getAgileFeatureGroups(aTeam)) {
                        if (grp.getName().equals(featureGroup)) {
                           group = grp;
                           break;
                        }
                     }
                  }
               }
               if (group != null) {
                  changes.relate(teamWf, AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup, group);
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
                  ArtifactId aTeamArt =
                     atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.AgileTeam, agileTeamStr);
                  if (aTeamArt.isValid()) {
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
            AtsUser originator = atsApi.getUserService().getUserById(ArtifactId.valueOf(data.getOriginatorStr()));
            if (originator != null) {
               for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
                  changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedBy, originator.getUserId());
               }
            }
         }

         // set assignee
         if (Strings.isValid(data.getAssigneeStr())) {
            List<AtsUser> assignees = new LinkedList<>();
            for (String id : data.getAssigneeStr().split(",")) {
               AtsUser user = atsApi.getUserService().getUserById(ArtifactId.valueOf(id));
               if (user != null) {
                  assignees.add(user);
               }
            }
            if (!assignees.isEmpty()) {
               for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
                  changes.setAssignees(teamWf, assignees);
               }
            }
         }

         // set work package
         if (Strings.isValid(data.getWorkPackage())) {
            IAtsWorkPackage workPkg = null;
            if (Strings.isNumeric(data.getWorkPackage())) {
               workPkg = atsApi.getEarnedValueService().getWorkPackage(ArtifactId.valueOf(data.getWorkPackage()));
            } else {
               ArtifactId art = atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.WorkPackage,
                  data.getWorkPackage());
               if (art.isValid()) {
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
            AttributeTypeToken attributeType = atsApi.tokenService().getAttributeType(Long.valueOf(attr.getKey()));
            if (attributeType == null) {
               throw new OseeArgumentException("Invalid attribute type id %s", attr.getKey());
            }
            for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
               changes.setSoleAttributeValue(teamWf, attributeType, attr.getValue());
            }
         }
      } catch (Exception ex) {
         result = new ActionResult(null, null);
         result.getResults().errorf("Exception creating Action %s", Lib.exceptionToString(ex));
      }
      return result;
   }

   @Override
   public ActionResult createAction(AtsUser user, String title, String desc, ChangeTypes changeType, String priority,
      boolean validationRequired, Date needByDate, Collection<IAtsActionableItem> actionableItems, Date createdDate,
      AtsUser createdBy, Collection<INewActionListener> newActionListeners, IAtsChangeSet changes) {
      ActionResult result = null;
      try {
         Conditions.checkNotNullOrEmptyOrContainNull(actionableItems, "actionableItems");
         Conditions.assertNotNullOrEmpty(title, "Title must be specified");

         /**
          * if "tt" is title, this is an action created for development. To make it easier, all fields are automatically
          * filled in for ATS developer
          */
         IAtsAction action = createAction(title, desc, changeType, priority, validationRequired, needByDate, changes);

         // Retrieve Team Definitions corresponding to selected Actionable Items
         Collection<IAtsTeamDefinition> teamDefs =
            atsApi.getTeamDefinitionService().getImpactedTeamDefs(actionableItems);
         if (teamDefs.isEmpty()) {
            StringBuffer sb = new StringBuffer("No teams returned for Action's selected Actionable Items\n");
            for (IAtsActionableItem aia : actionableItems) {
               sb.append("Selected AI \"" + aia + "\" " + aia.getIdString() + "\n");
            }
            throw new OseeStateException(sb.toString());
         }

         // Create team workflow artifacts
         List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
         for (IAtsTeamDefinition teamDef : teamDefs) {
            List<AtsUser> leads =
               new LinkedList<>(atsApi.getTeamDefinitionService().getLeads(teamDef, actionableItems));
            if (leads.isEmpty()) {
               leads.add(AtsCoreUsers.UNASSIGNED_USER);
            }
            IAtsTeamWorkflow teamWf = createTeamWorkflow(action, teamDef, actionableItems, leads, changes, createdDate,
               createdBy, newActionListeners);
            teamWfs.add(teamWf);
            changes.add(teamWf);
         }

         // Notify listener of action creation
         if (newActionListeners != null) {
            for (INewActionListener listener : newActionListeners) {
               listener.actionCreated(action);
            }
         }

         changes.add(action);
         result = new ActionResult(action, teamWfs);
      } catch (Exception ex) {
         result = new ActionResult(null, null);
         result.getResults().errorf("Exception creating Action %s", Lib.exceptionToString(ex));
      }
      return result;
   }

   @Override
   public IAtsAction createAction(String title, String desc, ChangeTypes changeType, String priority,
      boolean validationRequired, Date needByDate, IAtsChangeSet changes) {
      ArtifactToken actionArt = changes.createArtifact(AtsArtifactTypes.Action, title);
      IAtsAction action = atsApi.getWorkItemService().getAction(actionArt);
      IAtsTeamDefinition topTeamDefinition = getTopTeamDef();
      atsApi.getActionService().setAtsId(action, topTeamDefinition, workItemListener, changes);
      changes.add(action);
      setArtifactIdentifyData(action, title, desc, changeType, priority, validationRequired, needByDate, changes);
      return action;
   }

   private IAtsTeamDefinition getTopTeamDef() {
      if (topTeamDefinition == null) {
         topTeamDefinition = atsApi.getTeamDefinitionService().getTopTeamDefinition();
      }
      return topTeamDefinition;
   }

   @Override
   public IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef,
      Collection<IAtsActionableItem> actionableItems, Collection<AtsUser> assignees, IAtsChangeSet changes,
      Date createdDate, AtsUser createdBy, Collection<INewActionListener> newActionListeners,
      CreateTeamOption... createTeamOption) {
      Conditions.assertNotNull(teamDef, "Team Definition can not be null");
      WorkDefinition workDef =
         atsApi.getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef, newActionListeners);
      Conditions.assertNotNull(workDef, "Work Definition can not be null");

      /**
       * Get Team Workflow artifact type from listeners, else from Work Def, else from Team Def
       */
      ArtifactTypeToken teamWorkflowArtifactType = null;
      if (newActionListeners != null) {
         for (INewActionListener listener : newActionListeners) {
            ArtifactTypeToken artType = listener.getOverrideArtifactType(teamDef);
            if (artType.isValid()) {
               if (teamWorkflowArtifactType != null) {
                  throw new OseeArgumentException("Provided listeners can not provide override art type");
               }
               teamWorkflowArtifactType = artType;
            }
         }
      }
      if (teamWorkflowArtifactType == null) {
         teamWorkflowArtifactType = workDef != null ? workDef.getArtType() : null;
      }
      if (teamWorkflowArtifactType == null) {
         teamWorkflowArtifactType = getTeamWorkflowArtifactType(teamDef);
      }
      Conditions.assertNotNull(teamWorkflowArtifactType, "Team Workflow Artifact Type can not be null");

      // NOTE: The persist of the workflow will auto-email the assignees
      IAtsTeamWorkflow teamWf = createTeamWorkflow(action, teamDef, actionableItems, assignees, createdDate, createdBy,
         teamWorkflowArtifactType, newActionListeners, changes, createTeamOption);
      return teamWf;
   }

   public ArtifactTypeToken getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef) {
      return getTeamWorkflowArtifactType(teamDef, atsApi);
   }

   public static ArtifactTypeToken getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, AtsApi atsApi) {
      Conditions.checkNotNull(teamDef, "teamDef");
      ArtifactTypeToken teamWorkflowArtifactType = AtsArtifactTypes.TeamWorkflow;
      if (teamDef.getStoreObject() != null) {
         String artifactTypeName = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef,
            AtsAttributeTypes.TeamWorkflowArtifactType, null);
         if (Strings.isValid(artifactTypeName)) {
            boolean found = false;
            for (ArtifactTypeToken type : atsApi.getArtifactTypes()) {
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
   public IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef,
      Collection<IAtsActionableItem> actionableItems, Collection<AtsUser> assignees, Date createdDate,
      AtsUser createdBy, ArtifactTypeToken artifactType, Collection<INewActionListener> newActionListeners,
      IAtsChangeSet changes, CreateTeamOption... createTeamOption) {

      WorkDefinition workDef = null;
      if (newActionListeners != null && !newActionListeners.isEmpty()) {
         for (INewActionListener actionListener : newActionListeners) {
            AtsWorkDefinitionToken workDefTok = actionListener.getOverrideWorkDefinitionId(teamDef);
            if (workDefTok != null) {
               workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefTok);
            }
         }
      }
      // Determine of any osgi registered listeners want to provide work def
      if (workDef == null && actionListeners != null) {
         for (INewActionListener listener : actionListeners) {
            AtsWorkDefinitionToken overrideWorkDefinitionId = listener.getOverrideWorkDefinitionId(teamDef);
            if (overrideWorkDefinitionId != null) {
               workDef = atsApi.getWorkDefinitionService().getWorkDefinition(overrideWorkDefinitionId);
               break;
            }
         }
      }
      // Else, use normal computed work def
      if (workDef == null) {
         workDef =
            atsApi.getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef, newActionListeners);
      }
      Conditions.assertNotNull(workDef, "Work Definition can no be null");

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
         IAtsTeamDefinition teamDefinitionInherited =
            ai.getAtsApi().getActionableItemService().getTeamDefinitionInherited(ai);
         if (teamDefinitionInherited != null && teamDef.getId().equals(teamDefinitionInherited.getId())) {
            applicableAis.add(ai);
         }
      }

      IAtsTeamWorkflow teamWf = null;
      ArtifactToken artToken = null;
      if (newActionListeners != null) {
         for (INewActionListener listener : newActionListeners) {
            artToken = listener.getArtifactToken(applicableAis);
         }
      }

      if (artToken == null) {
         teamWf = atsApi.getWorkItemService().getTeamWf(changes.createArtifact(artifactType, ""));
      } else {
         teamWf = atsApi.getWorkItemService().getTeamWf(changes.createArtifact(artToken));
      }

      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs((IAtsWorkItem) teamWf, workDef, changes);
      setArtifactIdentifyData(action, teamWf, changes);

      /**
       * Relate Workflow to ActionableItems (by id) if team is responsible for that AI
       */
      for (IAtsActionableItem aia : applicableAis) {
         atsApi.getActionableItemService().addActionableItem(teamWf, aia, changes);
      }

      // Relate WorkFlow to Team Definition (by id due to relation loading issues)
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());

      setAtsId(teamWf, teamWf.getTeamDefinition(), workItemListener, changes);

      // Initialize state machine
      initializeNewStateMachine(teamWf, assignees, createdDate, createdBy, workDef, changes);

      // Notify listener of team creation
      if (newActionListeners != null) {
         for (INewActionListener listener : newActionListeners) {
            listener.teamCreated(action, teamWf, changes);
         }
      }
      // Notify any osgi registered listeners
      if (actionListeners != null) {
         for (INewActionListener listener : actionListeners) {
            listener.teamCreated(action, teamWf, changes);
         }
      }

      // Relate Action to WorkFlow
      changes.relate(action, AtsRelationTypes.ActionToWorkflow_TeamWorkflow, teamWf);

      // Auto-add actions to configured goals
      addActionToConfiguredGoal(teamDef, teamWf, actionableItems, null, changes);

      changes.add(teamWf);

      changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
         AtsCoreUsers.SYSTEM_USER, teamWf, AtsNotifyType.SubscribedTeamOrAi));

      changes.addWorkflowCreated(teamWf);

      return teamWf;
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

   private void logCreatedByChange(IAtsWorkItem workItem, AtsUser user, Date date, AtsUser asUser) {
      if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, null) == null) {
         workItem.getLog().addLog(LogType.Originated, "", "", date, user.getUserId());
      } else {
         workItem.getLog().addLog(LogType.Originated, "", "Changed by " + asUser.getName(), date, user.getUserId());
      }
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

   /**
    * Auto-add actions to a goal configured with relations to the given ActionableItem or Team Definition
    */
   @Override
   public void addActionToConfiguredGoal(IAtsTeamDefinition teamDef, IAtsTeamWorkflow teamWf,
      Collection<IAtsActionableItem> actionableItems, IAtsGoal handledGoal, IAtsChangeSet changes) {
      // Auto-add this team artifact to configured goals
      IRelationResolver relationResolver = atsApi.getRelationResolver();
      for (IAtsGoal goal : relationResolver.getRelated(teamDef, AtsRelationTypes.AutoAddActionToGoal_Goal,
         IAtsGoal.class)) {
         if (goal.equals(handledGoal)) {
            continue;
         }
         if (!relationResolver.areRelated(goal, AtsRelationTypes.Goal_Member, teamWf)) {
            changes.relate(goal, AtsRelationTypes.Goal_Member, teamWf);
            changes.add(goal);
         }
      }

      // Auto-add this actionable item to configured goals
      for (IAtsActionableItem aia : actionableItems) {
         for (IAtsGoal goal : relationResolver.getRelated(aia, AtsRelationTypes.AutoAddActionToGoal_Goal,
            IAtsGoal.class)) {
            if (goal.equals(handledGoal)) {
               continue;
            }
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
      ChangeTypes changeType = ChangeTypes.valueOf(
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.ChangeType, "None"));
      setArtifactIdentifyData(toTeam, fromAction.getName(),
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.Description, ""), changeType,
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.Priority, ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.ValidationRequired, false),
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.NeedBy, (Date) null),
         changes);
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    */
   public void setArtifactIdentifyData(IAtsObject atsObject, String title, String desc, ChangeTypes changeType,
      String priority, Boolean validationRequired, Date needByDate, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(atsObject, CoreAttributeTypes.Name, title);
      if (Strings.isValid(desc)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.Description, desc);
      }
      if (changeType != null) {
         changes.setSoleAttributeValue(atsObject, AtsAttributeTypes.ChangeType, changeType.name());
      }
      if (Strings.isValid(priority)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.Priority, priority);
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
   public void setAtsId(IAtsObject newObject, IAtsTeamDefinition teamDef, IWorkItemListener workItemListener,
      IAtsChangeSet changes) {
      AtsIdProvider atsIdProvider =
         new AtsIdProvider(atsApi.getSequenceProvider(), atsApi.getAttributeResolver(), newObject, teamDef);
      atsIdProvider.setWorkItemListener(workItemListener);
      atsIdProvider.setAtsId(changes);
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

   public void addActionListener(INewActionListener listener) {
      actionListeners.add(listener);
   }

   @Override
   public IAtsGoal createGoal(String title, IAtsChangeSet changes) {
      WorkDefinition workDef =
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
      return createGoal(title, AtsArtifactTypes.Goal, workDef, atsApi.getTeamDefinitionService().getTopTeamDefinition(),
         changes, null);
   }

   @Override
   public IAtsGoal createGoal(String title, ArtifactTypeToken artifactType, WorkDefinition workDefinition,
      IAtsTeamDefinition teamDef, IAtsChangeSet changes, IWorkItemListener workItemListener) {
      ArtifactToken art = changes.createArtifact(artifactType, title);
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
}