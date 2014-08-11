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
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.dsl.BooleanDefUtil;
import org.eclipse.osee.ats.dsl.UserRefUtil;
import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class ImportAIsAndTeamDefinitionsToDb {

   private final AtsDsl atsDsl;
   private final IAtsChangeSet changes;
   private final Map<String, Artifact> newTeams = new HashMap<String, Artifact>();
   private final Map<String, Artifact> newAIs = new HashMap<String, Artifact>();
   private final Map<String, Artifact> newVersions = new HashMap<String, Artifact>();
   private final String modelName;

   public ImportAIsAndTeamDefinitionsToDb(String modelName, AtsDsl atsDsl, IAtsChangeSet changes) {
      this.modelName = modelName;
      this.atsDsl = atsDsl;
      this.changes = changes;
   }

   public void execute() throws OseeCoreException {
      importUserDefinitions(atsDsl.getUserDef());
      importTeamDefinitions(
         atsDsl.getTeamDef(),
         AtsClientService.get().getConfigArtifact(
            TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getConfig())));
      importActionableItems(
         atsDsl.getActionableItemDef(),
         AtsClientService.get().getConfigArtifact(
            ActionableItems.getTopActionableItem(AtsClientService.get().getConfig())));
   }

   private void importUserDefinitions(EList<UserDef> userDefs) throws OseeCoreException {
      for (UserDef dslUserDef : userDefs) {
         String dslUserName = Strings.unquote(dslUserDef.getName());
         Artifact userArt = null;
         if (dslUserDef.getUserDefOption().contains("GetOrCreate")) {
            userArt = UserManager.createUser(getOseeUser(dslUserDef), null);
            changes.add(userArt);
         }
         if (userArt == null) {
            userArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, AtsUtilCore.getAtsBranch(), dslUserName);
         }
         if (userArt == null) {
            throw new OseeStateException(String.format("No user found in datbase with name [%s] from [%s]",
               dslUserName, modelName), modelName);
         }
      }
   }

   private IUserToken getOseeUser(final UserDef dslUserDef) {
      return TokenFactory.createUserToken(GUID.create(), Strings.unquote(dslUserDef.getName()),
         Strings.isValid(dslUserDef.getEmail()) ? dslUserDef.getEmail() : Strings.unquote(dslUserDef.getName()),
         Strings.isValid(dslUserDef.getUserId()) ? dslUserDef.getUserId() : Strings.unquote(dslUserDef.getName()),
         BooleanDefUtil.get(dslUserDef.getActive(), true), false, true);
   }

   private void importTeamDefinitions(EList<TeamDef> teamDefs, Artifact parentArtifact) throws OseeCoreException {
      for (TeamDef dslTeamDef : teamDefs) {
         String dslTeamName = Strings.unquote(dslTeamDef.getName());
         //         System.out.println("   - Importing Team " + dslTeamName);
         Artifact newTeam = null;
         if (dslTeamDef.getTeamDefOption().contains("GetOrCreate")) {
            newTeam = getOrCreate(dslTeamName, true, parentArtifact);
         }
         if (newTeam == null) {
            String guid = dslTeamDef.getGuid();
            if (guid != null && !GUID.isValid(guid)) {
               throw new OseeArgumentException("Invalid guid [%s] specified for DSL Team Definition [%s]", guid,
                  dslTeamDef);
            }
            newTeam =
               ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamDefinition, AtsUtilCore.getAtsBranch(),
                  dslTeamName, guid);
         }
         if (parentArtifact != null && !parentArtifact.equals(newTeam)) {
            parentArtifact.addChild(newTeam);
         }
         newTeams.put(newTeam.getName(), newTeam);

         newTeam.getAttributes(AtsAttributeTypes.Active).iterator().next().setValue(
            BooleanDefUtil.get(dslTeamDef.getActive(), true));
         //         newTeam.setSoleAttributeValue(CoreAttributeTypes.Active, BooleanDefUtil.get(dslTeamDef.getActive(), true));
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
            newTeam.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, dslTeamDef.getWorkDefinition());
         }
         if (Strings.isValid(dslTeamDef.getTeamWorkflowArtifactType())) {
            newTeam.setSoleAttributeValue(AtsAttributeTypes.TeamWorkflowArtifactType,
               dslTeamDef.getTeamWorkflowArtifactType());
         }
         if (Strings.isValid(dslTeamDef.getRelatedTaskWorkDefinition())) {
            newTeam.setSoleAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition,
               dslTeamDef.getRelatedTaskWorkDefinition());
         }
         importAccessContextIds(newTeam, dslTeamDef.getAccessContextId());
         importVersionDefinitions(dslTeamDef.getVersion(), newTeam);
         // process children
         importTeamDefinitions(dslTeamDef.getChildren(), newTeam);
         changes.add(newTeam);
      }

   }

   private Set<Artifact> getUsers(EList<UserRef> userRefs) throws OseeCoreException {
      Set<Artifact> users = new HashSet<Artifact>();
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

   private void importAccessContextIds(Artifact teamOrAi, EList<String> contextIds) throws OseeCoreException {
      for (String accessContextId : contextIds) {
         teamOrAi.addAttribute(CoreAttributeTypes.AccessContextId, accessContextId);
      }
   }

   private void importVersionDefinitions(EList<VersionDef> versionDefs, Artifact teamDef) throws OseeCoreException {

      Map<String, Artifact> nameToVerArt = new HashMap<String, Artifact>();
      for (VersionDef dslVersionDef : versionDefs) {
         String dslVerName = Strings.unquote(dslVersionDef.getName());
         // System.out.println("   - Importing Version " + dslVerName);

         String guid = dslVersionDef.getGuid();
         if (guid != null && !GUID.isValid(guid)) {
            throw new OseeArgumentException("Invalid guid [%s] specified for DSL Version Definition [%s]", guid,
               dslVersionDef);
         }
         Artifact newVer =
            ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtilCore.getAtsBranch(), dslVerName, guid);

         teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, newVer);
         nameToVerArt.put(newVer.getName(), newVer);
         newVersions.put(newVer.getName(), newVer);
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCommitBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCreateBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.NextVersion, BooleanDefUtil.get(dslVersionDef.getNext(), false));
         newVer.setSoleAttributeValue(AtsAttributeTypes.Released,
            BooleanDefUtil.get(dslVersionDef.getReleased(), false));
         if (Strings.isValid(dslVersionDef.getBaselineBranchUuid())) {
            newVer.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, dslVersionDef.getBaselineBranchUuid());
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

   private void importActionableItems(EList<ActionableItemDef> aiDefs, Artifact parentArtifact) throws OseeCoreException {
      for (ActionableItemDef dslAIDef : aiDefs) {
         String dslAIName = Strings.unquote(dslAIDef.getName());
         // System.out.println("   - Importing Actionable Item " + dslAIName);
         Artifact newAi = null;
         if (dslAIDef.getAiDefOption().contains("GetOrCreate")) {
            newAi = getOrCreate(dslAIName, false, parentArtifact);
         }
         if (newAi == null) {
            String guid = dslAIDef.getGuid();
            if (guid != null && !GUID.isValid(guid)) {
               throw new OseeArgumentException("Invalid guid [%s] specified for DSL Actionable Item Definition [%s]",
                  guid, dslAIDef);
            }
            newAi =
               ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem, AtsUtilCore.getAtsBranch(), dslAIName,
                  guid);
         }
         if (parentArtifact != null && !parentArtifact.equals(newAi)) {
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
         if (dslAIDef.getTeamDef() != null) {
            if (dslAIDef.getTeamDef() == null) {
               throw new OseeStateException(String.format("No Team Definition defined for Actionable Item [%s]",
                  dslAIName));
            }
            newAi.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeams.get(dslAIDef.getTeamDef()));
         }
         importAccessContextIds(newAi, dslAIDef.getAccessContextId());
         importActionableItems(dslAIDef.getChildren(), newAi);
         changes.add(newAi);
      }
   }

   private Artifact getOrCreate(String artifactName, boolean isTeamDef, Artifact parentArtifact) throws OseeCoreException {
      Artifact parent = parentArtifact;
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      if (parent == null) {
         if (isTeamDef) {
            parent =
               AtsClientService.get().storeConfigObject(
                  TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getConfig()), changes);
         } else {
            parent =
               AtsClientService.get().storeConfigObject(
                  ActionableItems.getTopActionableItem(AtsClientService.get().getConfig()), changes);
         }
         changes.execute();
      }

      if (parent.getName().equals(artifactName)) {
         return parent;
      }
      for (Artifact child : parent.getChildren()) {
         if (isTeamDef && !(child.isOfType(AtsArtifactTypes.TeamDefinition))) {
            continue;
         }
         if (!isTeamDef && !(child.isOfType(AtsArtifactTypes.ActionableItem))) {
            continue;
         }
         if (child.getName().equals(artifactName)) {
            return child;
         }
      }
      return null;
   }

}
