/*
 * Created on Feb 2, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

public class ImportAIsAndTeamDefinitionsToDb {

   private final AtsDsl atsDsl;
   private final SkynetTransaction transaction;
   private final Map<String, Artifact> newTeams = new HashMap<String, Artifact>();
   private final Map<String, Artifact> newAIs = new HashMap<String, Artifact>();
   private final Map<String, Artifact> newVersions = new HashMap<String, Artifact>();
   private final String modelName;

   public ImportAIsAndTeamDefinitionsToDb(String modelName, AtsDsl atsDsl, SkynetTransaction transaction) {
      this.modelName = modelName;
      this.atsDsl = atsDsl;
      this.transaction = transaction;
   }

   public void execute() throws OseeCoreException {
      importUserDefinitions(atsDsl.getUserDef());
      importTeamDefinitions(atsDsl.getTeamDef(), TeamDefinitionArtifact.getTopTeamDefinition());
      importActionableItems(atsDsl.getActionableItemDef(), ActionableItemArtifact.getTopActionableItem());
   }

   public void importUserDefinitions(EList<UserDef> userDefs) throws OseeCoreException {
      for (UserDef dslUserDef : userDefs) {
         System.out.println("   - Importing User " + dslUserDef.getName());
         Artifact userArt = null;
         if (dslUserDef.getUserDefOption().contains("GetOrCreate")) {
            userArt = UserManager.createUser(getOseeUser(dslUserDef), transaction);
         }
         if (userArt == null) {
            userArt =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, AtsUtil.getAtsBranch(), dslUserDef.getName());
         }
         if (userArt == null) {
            throw new OseeStateException(String.format("No user found in datbase with name [%s] from [%s]",
               dslUserDef.getName(), modelName));
         }
      }
   }

   private IOseeUser getOseeUser(final UserDef dslUserDef) {
      return new IOseeUser() {

         private static final long serialVersionUID = 1L;

         @Override
         public boolean isActive() {
            return BooleanDefUtil.get(dslUserDef.getActive(), true);
         }

         @Override
         public String getUserID() {
            return Strings.isValid(dslUserDef.getUserId()) ? dslUserDef.getUserId() : dslUserDef.getName();
         }

         @Override
         public String getName() {
            return dslUserDef.getName();
         }

         @Override
         public String getEmail() {
            return Strings.isValid(dslUserDef.getEmail()) ? dslUserDef.getEmail() : dslUserDef.getName();
         }
      };
   }

   public void importTeamDefinitions(EList<TeamDef> teamDefs, Artifact parentArtifact) throws OseeCoreException {
      for (TeamDef dslTeamDef : teamDefs) {
         System.out.println("   - Importing Team " + dslTeamDef.getName());
         Artifact newTeam = null;
         if (dslTeamDef.getTeamDefOption().contains("GetOrCreate")) {
            newTeam = getOrCreate(dslTeamDef.getName(), true, parentArtifact);
         }
         if (newTeam == null) {
            newTeam =
               ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamDefinition, AtsUtil.getAtsBranch(),
                  dslTeamDef.getName());
         }
         if (parentArtifact != null && !parentArtifact.equals(newTeam)) {
            parentArtifact.addChild(newTeam);
         }
         newTeams.put(newTeam.getName(), newTeam);

         newTeam.getAttributes(AtsAttributeTypes.Active).iterator().next().setValue(
            BooleanDefUtil.get(dslTeamDef.getActive(), true));
         //         newTeam.setSoleAttributeValue(CoreAttributeTypes.Active, BooleanDefUtil.get(dslTeamDef.getActive(), true));
         boolean configuredForTeamUsesVersions = BooleanDefUtil.get(dslTeamDef.getUsesVersions(), false);
         boolean hasVersions = dslTeamDef.getVersion().size() > 0;
         newTeam.setSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, configuredForTeamUsesVersions | hasVersions);
         for (String staticId : dslTeamDef.getStaticId()) {
            StaticIdManager.setSingletonAttributeValue(newTeam, staticId);
         }
         for (Artifact user : UserRefUtil.getUsers(dslTeamDef.getLead())) {
            newTeam.addRelation(AtsRelationTypes.TeamLead_Lead, user);
         }
         for (Artifact user : UserRefUtil.getUsers(dslTeamDef.getMember())) {
            newTeam.addRelation(AtsRelationTypes.TeamMember_Member, user);
         }
         for (Artifact user : UserRefUtil.getUsers(dslTeamDef.getPriviledged())) {
            newTeam.addRelation(AtsRelationTypes.PrivilegedMember_Member, user);
         }
         if (Strings.isValid(dslTeamDef.getWorkDefinition())) {
            newTeam.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, dslTeamDef.getWorkDefinition());
         }
         importVersionDefinitions(dslTeamDef.getVersion(), (TeamDefinitionArtifact) newTeam);
         // process children
         importTeamDefinitions(dslTeamDef.getChildren(), newTeam);
         newTeam.persist(transaction);
      }

   }

   public void importVersionDefinitions(EList<VersionDef> versionDefs, TeamDefinitionArtifact teamDef) throws OseeCoreException {
      for (VersionDef dslVersionDef : versionDefs) {
         System.out.println("   - Importing Version " + dslVersionDef.getName());
         Artifact newVer =
            ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(), dslVersionDef.getName());
         teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, newVer);
         newVersions.put(newVer.getName(), newVer);
         newVer.setSoleAttributeValue(CoreAttributeTypes.Active, BooleanDefUtil.get(dslVersionDef.getActive(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCommitBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch,
            BooleanDefUtil.get(dslVersionDef.getAllowCreateBranch(), true));
         newVer.setSoleAttributeValue(AtsAttributeTypes.NextVersion, BooleanDefUtil.get(dslVersionDef.getNext(), false));
         if (Strings.isValid(dslVersionDef.getBaselineBranchGuid())) {
            newVer.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, dslVersionDef.getBaselineBranchGuid());
         }
         for (String staticId : dslVersionDef.getStaticId()) {
            StaticIdManager.setSingletonAttributeValue(newVer, staticId);
         }
      }
   }

   public void importActionableItems(EList<ActionableItemDef> aiDefs, Artifact parentArtifact) throws OseeCoreException {
      for (ActionableItemDef dslAIDef : aiDefs) {
         System.out.println("   - Importing Actionable Item" + dslAIDef.getName());
         Artifact newAi = null;
         if (dslAIDef.getAiDefOption().contains("GetOrCreate")) {
            newAi = getOrCreate(dslAIDef.getName(), false, parentArtifact);
         }
         if (newAi == null) {
            newAi =
               ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem, AtsUtil.getAtsBranch(),
                  dslAIDef.getName());
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
            StaticIdManager.setSingletonAttributeValue(newAi, staticId);
         }
         for (Artifact user : UserRefUtil.getUsers(dslAIDef.getLead())) {
            newAi.addRelation(AtsRelationTypes.TeamLead_Lead, user);
         }
         if (dslAIDef.getTeamDef() != null) {
            if (dslAIDef.getTeamDef() == null) {
               throw new OseeStateException(String.format("No Team Definition defined for Actionable Item [%s]",
                  dslAIDef.getName()));
            }
            newAi.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeams.get(dslAIDef.getTeamDef()));
         }
         importActionableItems(dslAIDef.getChildren(), newAi);
         newAi.persist(transaction);
      }
   }

   private Artifact getOrCreate(String artifactName, boolean isTeamDef, Artifact parentArtifact) throws OseeCoreException {
      Artifact parent = parentArtifact;
      if (parent == null) {
         if (isTeamDef) {
            parent = TeamDefinitionArtifact.getTopTeamDefinition();
         } else {
            parent = ActionableItemArtifact.getTopActionableItem();
         }
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
