/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import java.util.ArrayList;
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
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.CreateOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionMemberData;
import org.eclipse.osee.ats.api.workflow.NewActionRel;
import org.eclipse.osee.ats.api.workflow.NewActionTeamData;
import org.eclipse.osee.ats.core.workflow.Action;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateActionOperation {

   private final NewActionData data;
   private final IAtsChangeSet changes;
   private final AtsApi atsApi;
   private AtsUser createdBy;
   private List<IAtsActionableItem> ais;
   private Date needByDate;
   private Date createdDate;
   private final XResultData rd;
   private IAtsAction action;
   private final List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
   private static IAtsTeamDefinition topTeamDefinition;

   public CreateActionOperation(NewActionData data, IAtsChangeSet changes, AtsApi atsApi) {
      this.data = data;
      this.changes = changes;
      this.atsApi = atsApi;
      this.rd = data.getRd();
   }

   public NewActionData createAction() {
      try {
         if (data.isCreateAction() && Strings.isInvalidOrBlank(data.getTitle())) {
            rd.errorf("Title must be specified");
            return data;
         }
         createdBy = getCreatedBy(data, atsApi.user());
         if (data.getRd().isErrors()) {
            return data;
         }
         ais = CreateActionUtil.getActionableItems(data, atsApi);
         if (rd.isErrors()) {
            return data;
         }
         if (data.isCreateAction() && ais.isEmpty()) {
            rd.errorf("Actionable Item(s) must be specified for new Action");
            return data;
         }

         needByDate = CreateActionUtil.getNeedByDate(data, atsApi);
         if (rd.isErrors()) {
            return data;
         }

         createdDate = null;
         if (Strings.isNumeric(data.getCreatedDateLong())) {
            createdDate = new Date(Long.valueOf(data.getCreatedDateLong()));
         } else {
            createdDate = new Date();
         }

         createActionAndTwsInternal();

         if (rd.isErrors()) {
            return data;
         }

         setPoints();
         setUnplanned();
         setFeatureGroups();
         setSprint();
         setBacklog();
         setOriginator();
         setWorkPackage();
         setTeamSpecificData();
         addRelations();
         addAdditionalAttrs();
         addToMemberWorkflow();

         // Set return payload
         data.getActResult().setAction(ArtifactId.valueOf(action.getId()));
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            data.getActResult().getTeamWfs().add(ArtifactId.valueOf(teamWf.getId()));
         }

         // Set convenience values that will only live on server; but reloaded on client when return
         data.getActResult().setAtsTeamWfs(teamWfs);
         data.getActResult().setAtsAction(action);

      } catch (Exception ex) {
         rd.errorf("Exception creating Action %s", Lib.exceptionToString(ex));
      }

      return data;
   }

   /**
    * Get user that originated this action. This can be different than getAsUser who initiated the creation if
    * Originator was another user, which is often the case.
    *
    * @return user or null. Error string in data.getRd().
    */
   public AtsUser getCreatedBy(NewActionData data, AtsUser currUser) {
      AtsUser createdBy = null;
      String createdByUserArtId = data.getCreatedByUserArtId();
      if (Strings.isNumeric(createdByUserArtId)) {
         Long userArtId = Long.valueOf(createdByUserArtId);
         if (userArtId != ArtifactId.SENTINEL.getId() && userArtId <= 0) {
            data.getRd().errorf("Invalid Created By Art Id [%s]", data.getCreatedByUserArtId());
            return null;
         }
         if (userArtId != ArtifactId.SENTINEL.getId()) {
            AtsUser user = atsApi.getUserService().getUserById(ArtifactId.valueOf(userArtId));
            if (user == null || user.isUnAssigned()) {
               data.getRd().errorf("Invalid Created By Art Id [%s]", data.getCreatedByUserArtId());
               return createdBy;
            } else {
               return user;
            }
         }
      }
      if (currUser != null && !currUser.isUnAssigned()) {
         return currUser;
      } else {
         data.getRd().errorf("Invalid Current User [%s]", currUser);
      }
      return null;
   }

   /**
    * Get user that initiated this action. If data.AsUser, use that first. Else attempt to set from current user. Else
    * error.
    *
    * @return user or null. Error string in data.getRd().
    */
   public static AtsUser getAsUser(NewActionData data, AtsUser currUser, AtsApi atsApi) {
      AtsUser resultUser = getAsUser(data.getAsUser(), currUser, data.getRd(), atsApi);
      data.setAsUser(resultUser.getArtifactId());
      return resultUser;
   }

   public static AtsUser getAsUser(ArtifactId asUser, AtsUser currUser, XResultData rd, AtsApi atsApi) {
      AtsUser resultUser = null;
      if (asUser.isValid()) {
         resultUser = atsApi.getUserService().getUserById(asUser);
         if (resultUser == null) {
            rd.errorf("Invalid AsUserArtId [%s]", asUser.getIdString());
            return resultUser;
         }
      } else {
         if (currUser != null && !currUser.isUnAssigned()) {
            return atsApi.getUserService().getUserById(currUser.getArtifactId());
         } else {
            rd.errorf("Invalid Current User [%s]", currUser);
         }
      }
      return resultUser;
   }

   private List<AtsUser> getAssignees(IAtsTeamWorkflow teamWf) {
      List<AtsUser> assignees = new ArrayList<>();
      if (Strings.isValid(data.getAssigneesArtIds())) {
         assignees.addAll(atsApi.getUserService().getCommDelimAssignees(data.getAssigneesArtIds()));
      }
      if (assignees.isEmpty()) {
         List<AtsUser> leads =
            new LinkedList<>(atsApi.getTeamDefinitionService().getLeads(teamWf.getTeamDefinition(), ais));
         assignees.addAll(leads);
      }
      return assignees;
   }

   /**
    * Internal method to create initial Action and TeamWf artifacts
    */
   private ActionResult createActionAndTwsInternal() {
      ActionResult result = null;
      try {

         // Create Action if necessary, else load existing Action
         if (data.getParentAction().isValid()) {
            action = new Action(atsApi, atsApi.getQueryService().getArtifact(data.getParentAction()));
            if (action == null) {
               rd.errorf("Can't load Action [%s]", data.getParentAction());
            }
         } else {
            action = createActionInternal();
         }

         // Determine Team Definitions
         Collection<IAtsTeamDefinition> teamDefs = new ArrayList<IAtsTeamDefinition>();
         if (data.getTeamDef() != null && data.getTeamDef().isValid()) {
            IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(data.getTeamDef());
            if (teamDef == null) {
               rd.errorf("No Team Def returned for %s", data.getTeamDef());
               return result;
            }
            teamDefs.add(teamDef);
         }
         if (teamDefs.isEmpty()) {
            teamDefs.addAll(atsApi.getTeamDefinitionService().getImpactedTeamDefs(ais));
            if (teamDefs.isEmpty()) {
               rd.errorf("No Team Defs returned for Actionable Item(s) %s", ais);
               return result;
            }
         }

         // Create Team Workflow(s)
         for (IAtsTeamDefinition teamDef : teamDefs) {
            IAtsTeamWorkflow teamWf = createTeamWorkflowInternal(action, teamDef);
            if (teamWf != null) {
               teamWfs.add(teamWf);
            }
            if (data.getRd().isErrors()) {
               return result;
            }
         }

         result = new ActionResult(action, teamWfs);

      } catch (Exception ex) {
         result = new ActionResult(null, null);
         result.getResults().errorf("Exception creating Action %s", Lib.exceptionToString(ex));
      }
      return result;
   }

   private IAtsTeamDefinition getTopTeamDef() {
      if (topTeamDefinition == null) {
         topTeamDefinition = atsApi.getTeamDefinitionService().getTopTeamDefinition();
      }
      return topTeamDefinition;
   }

   private IAtsAction createActionInternal() {
      ArtifactToken actionArt = changes.createArtifact(AtsArtifactTypes.Action, data.getTitle());
      action = atsApi.getWorkItemService().getAction(actionArt);
      IAtsTeamDefinition topTeamDefinition = getTopTeamDef();
      atsApi.getActionService().setAtsId(action, topTeamDefinition, null, changes);
      changes.add(action);
      setArtifactIdentifyData(action, data.getTitle(), data.getDescription(), data.getChangeType(), data.getPriority(),
         data.isValidationRequired(), needByDate, changes);
      return action;
   }

   @SuppressWarnings("unlikely-arg-type")
   private IAtsTeamWorkflow createTeamWorkflowInternal(IAtsAction action, IAtsTeamDefinition teamDef) {

      WorkDefinition workDef = getWorkflowDefinition(teamDef, data);

      ArtifactTypeToken teamWorkflowArtifactType = getTeamWorkflowArtifactType(teamDef, workDef);

      if (!data.getCreateOptions().contains(CreateOption.Duplicate_If_Exists)) {
         // Make sure team doesn't already exist
         for (IAtsTeamWorkflow teamArt : action.getTeamWorkflows()) {
            if (teamArt.getTeamDefinition().equals(teamDef)) {
               data.getRd().errorf("Team [%s] already exists for Action [%s]", teamDef, atsApi.getAtsId(action));
               return null;
            }
         }
      }

      List<IAtsActionableItem> applicableAis = new LinkedList<>();
      for (IAtsActionableItem ai : ais) {
         IAtsTeamDefinition teamDefinitionInherited =
            ai.getAtsApi().getActionableItemService().getTeamDefinitionInherited(ai);
         if (teamDefinitionInherited != null && teamDef.getId().equals(teamDefinitionInherited.getId())) {
            applicableAis.add(ai);
         }
      }

      // See if there is an ArtifactToken specified for the give AIs (usually for tests), else create normally
      IAtsTeamWorkflow teamWf = null;
      ArtifactToken artToken = null;
      if (data.getAiToArtToken() != null) {
         for (Entry<ArtifactId, ArtifactToken> aiToArtTok : data.getAiToArtToken().entrySet()) {
            if (applicableAis.contains(aiToArtTok.getKey())) {
               artToken = aiToArtTok.getValue();
            }
         }
      }

      String title = null;
      if (artToken == null) {
         title = data.getTitle();
         if (Strings.isInValidOrBlank(title)) {
            title = action.getName();
         }
         if (Strings.isInValidOrBlank(title)) {
            data.getRd().errorf("Title must be specified");
            return null;
         }
         Conditions.assertTrue(teamWorkflowArtifactType.isValid(), "Artifact Type must be specified");
         teamWf = atsApi.getWorkItemService().getTeamWf(changes.createArtifact(teamWorkflowArtifactType, title));
      } else {
         title = artToken.getName();
         if (Strings.isInValidOrBlank(title)) {
            data.getRd().errorf("Title must be specified");
            return null;
         }
         teamWf = atsApi.getWorkItemService().getTeamWf(changes.createArtifact(artToken));
      }

      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs((IAtsWorkItem) teamWf, workDef, changes);
      setArtifactIdentifyData(title, action, teamWf, changes);

      /**
       * Relate Workflow to ActionableItems (by id) if team is responsible for that AI
       */
      for (IAtsActionableItem aia : applicableAis) {
         atsApi.getActionableItemService().addActionableItem(teamWf, aia, changes);
      }

      // Relate WorkFlow to Team Definition (by id due to relation loading issues)
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());

      atsApi.getActionService().setAtsId(teamWf, teamWf.getTeamDefinition(), null, changes);

      // Initialize state machine
      List<AtsUser> assignees = getAssignees(teamWf);
      atsApi.getActionService().initializeNewStateMachine(teamWf, assignees, createdDate, createdBy, workDef, changes);

      // Relate Action to WorkFlow
      changes.relate(action, AtsRelationTypes.ActionToWorkflow_TeamWorkflow, teamWf);

      // Set targeted version
      if (data.getVersionId().isValid()) {
         IAtsVersion version = atsApi.getVersionService().getVersionById(ArtifactId.valueOf(data.getVersionId()));
         if (version != null) {
            atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);
         }
      }

      // Auto-add actions to configured goals
      atsApi.getActionService().addActionToConfiguredGoal(teamDef, teamWf, ais, null, changes);

      changes.add(teamWf);

      changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
         AtsCoreUsers.SYSTEM_USER, teamWf, AtsNotifyType.SubscribedTeam, AtsNotifyType.SubscribedAi));

      changes.addWorkflowCreated(teamWf);

      return teamWf;
   }

   /**
    * Set Team Workflow attributes off given action artifact
    */
   private void setArtifactIdentifyData(String title, IAtsAction fromAction, IAtsTeamWorkflow toTeam,
      IAtsChangeSet changes) {
      Conditions.checkNotNullOrEmpty(title, "title");
      Conditions.checkNotNull(fromAction, "fromAction");
      Conditions.checkNotNull(toTeam, "toTeam");
      Conditions.checkNotNull(changes, "changes");
      ChangeTypes changeType = ChangeTypes.valueOf(
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.ChangeType, "None"));

      setArtifactIdentifyData(toTeam, //
         title, //
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.Description, ""), //
         changeType, //
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.Priority, ""), //
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.ValidationRequired, false), //
         atsApi.getAttributeResolver().getSoleAttributeValue(fromAction, AtsAttributeTypes.NeedBy, (Date) null),
         changes);
   }

   /**
    * Since there is no shared attribute yet, action and workflow arts are all populate with identify data
    */
   private void setArtifactIdentifyData(IAtsObject atsObject, String title, String desc, ChangeTypes changeType,
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

   private ArtifactTypeToken getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef, WorkDefinition workDef) {
      /**
       * Get Team Workflow artifact type from data, else from Work Def, else from Team Def
       */
      ArtifactTypeToken teamWorkflowArtifactType = null;
      if (data.getArtifactType() != null && data.getArtifactType().isValid()) {
         teamWorkflowArtifactType = data.getArtifactType();
      }
      if (teamWorkflowArtifactType == null) {
         for (WorkType workType : teamDef.getWorkTypes()) {
            ArtifactTypeToken artType = data.getWorkTypeToArtType().get(workType);
            if (artType != null && artType.isValid()) {
               teamWorkflowArtifactType = artType;
               break;
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
      return teamWorkflowArtifactType;
   }

   private WorkDefinition getWorkflowDefinition(IAtsTeamDefinition teamDef, NewActionData data) {
      WorkDefinition workDef = null;
      if (data.getWorkDef() != null) {
         workDef = atsApi.getWorkDefinitionService().getWorkDefinition(data.getWorkDef());
      }
      if (workDef == null) {
         for (WorkType workType : teamDef.getWorkTypes()) {
            AtsWorkDefinitionToken wd = data.getWorkTypeToWorkDef().get(workType);
            if (wd != null) {
               workDef = atsApi.getWorkDefinitionService().getWorkDefinition(wd);
               break;
            }
         }
      }
      // Else, use normal computed work def
      if (workDef == null) {
         workDef = atsApi.getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef);
      }
      Conditions.assertNotNull(workDef, "Work Definition can no be null");
      return workDef;
   }

   public ArtifactTypeToken getTeamWorkflowArtifactType(IAtsTeamDefinition teamDef) {
      Conditions.checkNotNull(teamDef, "teamDef");
      ArtifactTypeToken teamWorkflowArtifactType = AtsArtifactTypes.TeamWorkflow;
      if (teamDef.getStoreObject() != null) {
         String artifactTypeName = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef,
            AtsAttributeTypes.TeamWorkflowArtifactType, null);
         if (Strings.isValid(artifactTypeName)) {
            boolean found = false;
            Collection<ArtifactTypeToken> artifactTypes = atsApi.getArtifactTypes();
            for (ArtifactTypeToken type : artifactTypes) {
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

   private void addToMemberWorkflow() {
      NewActionMemberData mData = data.getMemberData();
      if (mData != null) {
         ArtifactReadable collectorArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(mData.getMemberArt());
         ArtifactReadable dropTargetArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(mData.getDropTargetArt());

         RelationTypeToken relationType = atsApi.tokenService().getRelationType(mData.getRelationType().getId());
         RelationTypeSide relationTypeSide = RelationTypeSide.create(relationType, SIDE_B);

         for (IAtsTeamWorkflow teamWf : teamWfs) {
            if (!atsApi.getRelationResolver().areRelated(collectorArt, relationTypeSide, teamWf.getArtifactId())) {

               changes.relate(collectorArt, relationTypeSide, teamWf.getStoreObject());

               if (dropTargetArt != null) {
                  List<ArtifactToken> related = new ArrayList<ArtifactToken>();
                  for (ArtifactToken art : atsApi.getRelationResolver().getRelated(collectorArt, relationTypeSide)) {
                     if (art.equals(dropTargetArt)) {
                        related.add(teamWf.getStoreObject());
                     }
                     related.add(art);
                  }
                  changes.setRelationsAndOrder(collectorArt, AtsRelationTypes.Goal_Member, related);
               }
            }
         }
      }
   }

   private void addAdditionalAttrs() {
      for (Entry<String, String> attr : data.getAttrValues().entrySet()) {
         if (!Strings.isNumeric(attr.getKey())) {
            rd.errorf("Invalid attribute type id %s", attr.getKey());
            return;
         }
         AttributeTypeToken attributeType = atsApi.tokenService().getAttributeType(Long.valueOf(attr.getKey()));
         if (attributeType == null) {
            rd.errorf("Invalid attribute type id %s", attr.getKey());
            return;
         }
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            if (attributeType.isDate()) {
               if (!Strings.isNumeric(attr.getValue())) {
                  rd.errorf("Date Attribute must be Long string, not [%s]", attr.getValue());
                  return;
               }
               Date date = new Date(Long.valueOf(attr.getValue()));
               changes.setSoleAttributeValue(teamWf, attributeType, date);
            } else if (attributeType.isBoolean()) {
               String value = attr.getValue();
               boolean set = false;
               if (value.equals("true")) {
                  set = true;
               } else if (value.equals("false")) {
                  set = false;
               } else {
                  rd.errorf("Unexpected Boolean value [%s]", value);
               }
               changes.setSoleAttributeValue(teamWf, attributeType, set);
            } else {
               changes.setSoleAttributeValue(teamWf, attributeType, attr.getValue());
            }
         }
      }
   }

   private void addRelations() {
      for (NewActionRel rel : data.getRelations()) {
         RelationTypeToken relTypeTok = atsApi.tokenService().getRelationType(rel.getRelationType().getId());
         boolean aSide = rel.isSideA();
         RelationTypeSide relType = new RelationTypeSide(relTypeTok, aSide ? RelationSide.SIDE_A : RelationSide.SIDE_B);
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            for (ArtifactId art : rel.getArtifacts()) {
               changes.relate(teamWf, relType, art);
            }
         }
      }
   }

   private void setTeamSpecificData() {
      for (NewActionTeamData actionTeamData : data.getTeamData()) {
         AttributeTypeToken attrType = actionTeamData.getAttrType();
         AttributeTypeToken attributeType = atsApi.tokenService().getAttributeType(attrType);
         if (attributeType == null) {
            throw new OseeArgumentException("Invalid attribute type id %s", attrType);
         }
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            if (teamWf.getTeamDefinition().isInvalid() || teamWf.getTeamDefinition().getId().equals(
               actionTeamData.getTeamId().getId())) {
               for (Object obj : actionTeamData.getValues()) {
                  changes.addAttribute(teamWf, attributeType, obj);
               }
            }
         }

      }
   }

   private void setWorkPackage() {
      if (Strings.isValid(data.getWorkPackage())) {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            changes.addAttribute(teamWf, AtsAttributeTypes.WorkPackage, data.getWorkPackage());
         }
      }
   }

   private void setOriginator() {
      if (data.getOriginator().isValid()) {
         AtsUser originator = atsApi.getUserService().getUserById(data.getOriginator());
         if (originator != null) {
            for (IAtsTeamWorkflow teamWf : teamWfs) {
               changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedBy, originator.getUserId());
            }
         }
      }
   }

   private void setBacklog() {
      // NOTE: This may cause a problem if team already configured to add new items to backlog
      String agileTeamStr = data.getAgileTeam();
      if (Strings.isValid(agileTeamStr)) {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
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
   }

   private void setSprint() {
      String sprintStr = data.getSprint();
      if (Strings.isValid(sprintStr)) {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
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
   }

   private void setFeatureGroups() {
      String featureGroup = data.getFeatureGroup();
      if (Strings.isValid(featureGroup)) {
         IAgileFeatureGroup group = null;
         for (IAtsTeamWorkflow teamWf : teamWfs) {
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
   }

   private void setUnplanned() {
      if (data.isUnplanned()) {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.UnplannedWork, true);
         }
      }
   }

   private void setPoints() {
      if (Strings.isValid(data.getPoints())) {
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            IAgileTeam agileTeam = null;
            if (Strings.isNumeric(data.getAgileTeam())) {
               agileTeam = atsApi.getQueryService().getConfigItem(ArtifactId.valueOf(data.getAgileTeam()));
            }
            if (agileTeam == null) {
               IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
               agileTeam = atsApi.getAgileService().getAgileTeam(teamDef);
            }
            if (agileTeam == null) {
               rd.warning("Agile Team not found, could not set points.");
            } else {
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
      }
   }

}
