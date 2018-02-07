/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.ConvertAtsConfigGuidAttributesOperations;
import org.eclipse.osee.ats.dsl.BooleanDefUtil;
import org.eclipse.osee.ats.dsl.UserRefUtil;
import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDef;
import org.eclipse.osee.ats.dsl.atsDsl.AttrDefOptions;
import org.eclipse.osee.ats.dsl.atsDsl.AttrFullDef;
import org.eclipse.osee.ats.dsl.atsDsl.AttrValueDef;
import org.eclipse.osee.ats.dsl.atsDsl.ProgramDef;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Donald G. Dunne
 */
public class ImportAIsAndTeamDefinitionsToDb {

   private final AtsDsl atsDsl;
   private final IAtsChangeSet changes;
   private final Map<String, Artifact> newTeams = new HashMap<>();
   private final Map<String, Artifact> newAIs = new HashMap<>();
   private final Map<String, Artifact> newVersions = new HashMap<>();
   private final String modelName;
   private final Map<String, Artifact> teamNameToTeamDefArt = new HashMap<>();
   private final Map<String, ArtifactToken> sheetNameToArtifactIdMap;

   public ImportAIsAndTeamDefinitionsToDb(String modelName, AtsDsl atsDsl, Map<String, ArtifactToken> sheetNameToArtifactIdMap, IAtsChangeSet changes) {
      this.modelName = modelName;
      this.atsDsl = atsDsl;
      this.sheetNameToArtifactIdMap = sheetNameToArtifactIdMap;
      this.changes = changes;
   }

   public void execute() {
      importUserDefinitions(atsDsl.getUserDef());
      importTeamDefinitions(atsDsl.getTeamDef(), (Artifact) AtsClientService.get().getQueryService().getArtifact(
         TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService())));
      importActionableItems(atsDsl.getActionableItemDef(),
         (Artifact) AtsClientService.get().getQueryService().getArtifact(
            ActionableItems.getTopActionableItem(AtsClientService.get())));
      importProgram(atsDsl.getProgram());
   }

   private void importUserDefinitions(EList<UserDef> userDefs) {
      for (UserDef dslUserDef : userDefs) {
         String dslUserName = Strings.unquote(dslUserDef.getName());
         Artifact userArt = null;
         try {
            userArt = UserManager.getUserByName(dslUserName);
         } catch (UserNotInDatabase ex) {
            // do nothing
         }
         if (userArt == null && dslUserDef.getUserDefOption().contains("GetOrCreate")) {
            userArt = UserManager.createUser(getOseeUser(dslUserDef), null);
            changes.add(userArt);
         }
         if (userArt == null) {
            userArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, AtsClientService.get().getAtsBranch(),
               dslUserName);
         }
         if (userArt == null) {
            throw new OseeStateException(
               String.format("No user found in datbase with name [%s] from [%s]", dslUserName, modelName), modelName);
         }
      }
   }

   private UserToken getOseeUser(final UserDef dslUserDef) {
      return UserToken.create(Lib.generateArtifactIdAsInt(), Strings.unquote(dslUserDef.getName()),
         Strings.isValid(dslUserDef.getEmail()) ? dslUserDef.getEmail() : Strings.unquote(dslUserDef.getName()),
         Strings.isValid(dslUserDef.getUserId()) ? dslUserDef.getUserId() : Strings.unquote(dslUserDef.getName()),
         BooleanDefUtil.get(dslUserDef.getActive(), true), false, true);
   }

   @SuppressWarnings("deprecation")
   private void importTeamDefinitions(EList<TeamDef> teamDefs, Artifact parentArtifact) {
      for (TeamDef dslTeamDef : teamDefs) {
         String dslTeamName = Strings.unquote(dslTeamDef.getName());
         //         System.out.println("   - Importing Team " + dslTeamName);
         Artifact newTeam = null;
         if (dslTeamDef.getTeamDefOption().contains("GetOrCreate")) {
            newTeam = getOrCreate(dslTeamName, true, parentArtifact);
         }
         if (newTeam == null) {
            long id = dslTeamDef.getUuid() > 0 ? dslTeamDef.getUuid() : Lib.generateArtifactIdAsInt();
            newTeam = ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamDefinition,
               AtsClientService.get().getAtsBranch(), dslTeamName, id);
         }
         if (parentArtifact != null && parentArtifact.notEqual(newTeam)) {
            parentArtifact.addChild(newTeam);
         }
         newTeams.put(newTeam.getName(), newTeam);
         teamNameToTeamDefArt.put(newTeam.getName(), newTeam);

         newTeam.getAttributes(AtsAttributeTypes.Active).iterator().next().setValue(
            BooleanDefUtil.get(dslTeamDef.getActive(), true));
         for (String staticId : dslTeamDef.getStaticId()) {
            newTeam.setSingletonAttributeValue(CoreAttributeTypes.StaticId, staticId);
         }
         for (Artifact user : getUsers(dslTeamDef.getLead())) {
            newTeam.addRelation(AtsRelationTypes.TeamLead_Lead, user);
         }
         for (Artifact user : getUsers(dslTeamDef.getMember())) {
            newTeam.addRelation(AtsRelationTypes.TeamMember_Member, user);
         }
         for (Artifact user : getUsers(dslTeamDef.getPrivileged())) {
            newTeam.addRelation(AtsRelationTypes.PrivilegedMember_Member, user);
         }
         if (Strings.isValid(dslTeamDef.getWorkDefinition())) {
            ArtifactToken workDefArt = sheetNameToArtifactIdMap.get(dslTeamDef.getWorkDefinition());
            IAtsTeamDefinition newTeamDef = AtsClientService.get().getConfigItemFactory().getTeamDef(newTeam);
            AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(newTeamDef, workDefArt, changes);
         }
         if (Strings.isValid(dslTeamDef.getTeamWorkflowArtifactType())) {
            newTeam.setSoleAttributeValue(AtsAttributeTypes.TeamWorkflowArtifactType,
               dslTeamDef.getTeamWorkflowArtifactType());
         }
         if (Strings.isValid(dslTeamDef.getRelatedTaskWorkDefinition())) {
            newTeam.setSoleAttributeValue(ConvertAtsConfigGuidAttributesOperations.RelatedTaskWorkDefinition,
               dslTeamDef.getRelatedTaskWorkDefinition());

            ArtifactId workDefArt = sheetNameToArtifactIdMap.get(dslTeamDef.getRelatedTaskWorkDefinition());
            newTeam.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinitionReference, workDefArt);
         }
         if (dslTeamDef.getRules().size() > 0) {
            newTeam.setAttributeValues(AtsAttributeTypes.RuleDefinition, dslTeamDef.getRules());
         }
         importAccessContextIds(newTeam, dslTeamDef.getAccessContextId());
         importVersionDefinitions(dslTeamDef.getVersion(), newTeam);
         // process children
         importTeamDefinitions(dslTeamDef.getChildren(), newTeam);
         changes.add(newTeam);
      }

   }

   private Set<Artifact> getUsers(EList<UserRef> userRefs) {
      Set<Artifact> users = new HashSet<>();
      if (userRefs != null) {
         IAtsUserServiceClient userServiceClient = AtsClientService.get().getUserServiceClient();
         for (String userId : UserRefUtil.getUserIds(userRefs)) {
            User user = userServiceClient.getOseeUserById(userId);
            users.add(user);
         }
         for (String userName : UserRefUtil.getUserNames(userRefs)) {
            User user = UserManager.getUserByName(Strings.unquote(userName));
            users.add(user);
         }
      }
      return users;
   }

   private void importAccessContextIds(Artifact teamOrAi, EList<String> contextIds) {
      for (String accessContextId : contextIds) {
         teamOrAi.addAttribute(CoreAttributeTypes.AccessContextId, accessContextId);
      }
   }

   private void importVersionDefinitions(EList<VersionDef> versionDefs, Artifact teamDef) {

      Map<String, Artifact> nameToVerArt = new HashMap<>();
      for (VersionDef dslVersionDef : versionDefs) {
         String dslVerName = Strings.unquote(dslVersionDef.getName());
         // System.out.println("   - Importing Version " + dslVerName);
         long id = dslVersionDef.getUuid() > 0 ? dslVersionDef.getUuid() : Lib.generateArtifactIdAsInt();
         Artifact newVer = ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version,
            AtsClientService.get().getAtsBranch(), dslVerName, id);

         teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, newVer);
         nameToVerArt.put(newVer.getName(), newVer);
         newVersions.put(newVer.getName(), newVer);
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCommitBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCreateBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.NextVersion,
            BooleanDefUtil.get(dslVersionDef.getNext(), false));
         newVer.setSoleAttributeValue(AtsAttributeTypes.Released,
            BooleanDefUtil.get(dslVersionDef.getReleased(), false));
         if (Strings.isValid(dslVersionDef.getBaselineBranchUuid())) {
            newVer.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchId, dslVersionDef.getBaselineBranchUuid());
         }
         for (String staticId : dslVersionDef.getStaticId()) {
            newVer.setSingletonAttributeValue(CoreAttributeTypes.StaticId, staticId);
         }
      }
      // Handle parallel versions
      for (VersionDef dslVersionDef : versionDefs) {
         String aiName = Strings.unquote(dslVersionDef.getName());
         Artifact verArt = nameToVerArt.get(aiName);
         for (String parallelVerStr : dslVersionDef.getParallelVersion()) {
            // System.out.println(String.format("   - Importing Parallel Version [%s] -> Child [%s]", aiName, parallelVerStr));
            Artifact childArt = nameToVerArt.get(parallelVerStr);
            verArt.addRelation(AtsRelationTypes.ParallelVersion_Child, childArt);
         }
      }
   }

   @SuppressWarnings("deprecation")
   private void importActionableItems(EList<ActionableItemDef> aiDefs, Artifact parentArtifact) {
      for (ActionableItemDef dslAIDef : aiDefs) {
         String dslAIName = Strings.unquote(dslAIDef.getName());
         // System.out.println("   - Importing Actionable Item " + dslAIName);
         Artifact newAi = null;
         if (dslAIDef.getAiDefOption().contains("GetOrCreate")) {
            newAi = getOrCreate(dslAIName, false, parentArtifact);
         }
         if (newAi == null) {
            long id = dslAIDef.getUuid() > 0 ? dslAIDef.getUuid() : Lib.generateArtifactIdAsInt();
            newAi = ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem,
               AtsClientService.get().getAtsBranch(), dslAIName, id);
         }
         if (parentArtifact != null && parentArtifact.notEqual(newAi)) {
            parentArtifact.addChild(newAi);
         }
         newAIs.put(newAi.getName(), newAi);
         newAi.getAttributes(AtsAttributeTypes.Active).iterator().next().setValue(
            BooleanDefUtil.get(dslAIDef.getActive(), true));
         //         newAi.setSoleAttributeValue(CoreAttributeTypes.Active, BooleanDefUtil.get(dslAIDef.getActive(), true));
         newAi.setSoleAttributeValue(AtsAttributeTypes.Actionable, BooleanDefUtil.get(dslAIDef.getActionable(), true));
         for (String staticId : dslAIDef.getStaticId()) {
            newAi.setSingletonAttributeValue(CoreAttributeTypes.StaticId, staticId);
         }
         for (Artifact user : getUsers(dslAIDef.getLead())) {
            newAi.addRelation(AtsRelationTypes.TeamLead_Lead, user);
         }
         for (Artifact user : getUsers(dslAIDef.getOwner())) {
            newAi.addRelation(AtsRelationTypes.ActionableItem_User, user);
         }
         if (dslAIDef.getRules().size() > 0) {
            newAi.setAttributeValues(AtsAttributeTypes.RuleDefinition, dslAIDef.getRules());
         }

         if (dslAIDef.getTeamDef() != null) {
            if (dslAIDef.getTeamDef() == null) {
               throw new OseeStateException(
                  String.format("No Team Definition defined for Actionable Item [%s]", dslAIName));
            }
            newAi.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeams.get(dslAIDef.getTeamDef()));
         }
         importAccessContextIds(newAi, dslAIDef.getAccessContextId());
         importActionableItems(dslAIDef.getChildren(), newAi);
         changes.add(newAi);
      }
   }

   @SuppressWarnings("deprecation")
   private void importProgram(EList<ProgramDef> programDefs) {
      for (ProgramDef dslProgramDef : programDefs) {
         String dslProgramName = Strings.unquote(dslProgramDef.getName());
         Artifact newProgramArt = null;
         IArtifactType programArtifactType = AtsArtifactTypes.Program;
         String artifactTypeName = dslProgramDef.getArtifactTypeName();
         if (Strings.isValid(artifactTypeName)) {
            programArtifactType = ArtifactTypeManager.getType(artifactTypeName);
         }
         long id = dslProgramDef.getUuid() > 0 ? dslProgramDef.getUuid() : Lib.generateArtifactIdAsInt();
         newProgramArt = ArtifactTypeManager.addArtifact(programArtifactType, AtsClientService.get().getAtsBranch(),
            dslProgramName, id);
         changes.add(newProgramArt);
         newProgramArt.getAttributes(AtsAttributeTypes.Active).iterator().next().setValue(
            BooleanDefUtil.get(dslProgramDef.getActive(), true));
         importProgramTeamDef(dslProgramDef, newProgramArt);
         if (Strings.isValid(dslProgramDef.getNamespace())) {
            newProgramArt.setSoleAttributeValue(AtsAttributeTypes.Namespace, dslProgramDef.getNamespace());
         }
         importProgramAttributes(dslProgramDef, newProgramArt);
      }
   }

   private void importProgramAttributes(ProgramDef dslProgramDef, Artifact newProgramArt) {
      for (AttrDef attrDef : dslProgramDef.getAttributes()) {
         String attrName = Strings.unquote(attrDef.getName());
         AttrDefOptions attrDefOption = attrDef.getOption();
         if (attrDefOption instanceof AttrValueDef) {
            AttributeType attrType = AttributeTypeManager.getType(attrName);
            newProgramArt.addAttributeFromString(attrType, Strings.unquote(((AttrValueDef) attrDefOption).getValue()));
         } else if (attrDefOption instanceof AttrFullDef) {
            AttrFullDef attrFullDef = (AttrFullDef) attrDefOption;
            if (Strings.isValid(attrFullDef.getUuid())) {
               Long id = Long.valueOf(attrFullDef.getUuid());
               AttributeType attrType = AttributeTypeManager.getTypeById(id);
               for (String value : attrFullDef.getValues()) {
                  newProgramArt.addAttribute(attrType, Strings.unquote(value));
               }
            }

         }
      }
   }

   private void importProgramTeamDef(ProgramDef dslProgramDef, Artifact newProgramArt) {
      if (Strings.isValid(dslProgramDef.getTeamDefinition())) {
         String teamDefIdOrName = Strings.unquote(dslProgramDef.getTeamDefinition());
         if (Strings.isNumeric(teamDefIdOrName)) {
            newProgramArt.addAttribute(AtsAttributeTypes.TeamDefinitionReference, ArtifactId.valueOf(teamDefIdOrName));
         } else {
            Artifact teamDefArt = teamNameToTeamDefArt.get(teamDefIdOrName);
            if (teamDefArt == null) {
               throw new OseeStateException("No Team Definition found with name [%s] from program definition [%s]",
                  teamDefIdOrName, dslProgramDef.getName());
            }
            newProgramArt.addAttribute(AtsAttributeTypes.TeamDefinitionReference, teamDefArt);
         }
      }
   }

   private Artifact getOrCreate(String artifactName, boolean isTeamDef, Artifact parentArtifact) {
      Artifact parent = parentArtifact;
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      if (parent == null) {
         if (isTeamDef) {
            parent = (Artifact) AtsClientService.get().getQueryService().getArtifact(
               TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()));
         } else {
            parent = (Artifact) ActionableItems.getTopActionableItem(AtsClientService.get());
         }
         changes.execute();
      }

      if (parent.getName().equals(artifactName)) {
         return parent;
      }
      for (Artifact child : parent.getChildren()) {
         if (isTeamDef && !child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            continue;
         }
         if (!isTeamDef && !child.isOfType(AtsArtifactTypes.ActionableItem)) {
            continue;
         }
         if (child.getName().equals(artifactName)) {
            return child;
         }
      }
      return null;
   }

}
