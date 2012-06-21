/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config.store;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.config.AtsObjectsClient;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.config.VersionFactory;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

public class TeamDefinitionArtifactStore extends ArtifactAtsObjectStore {

   public TeamDefinitionArtifactStore(IAtsTeamDefinition teamDef) {
      super(teamDef, AtsArtifactTypes.TeamDefinition, AtsUtilCore.getAtsBranchToken());
   }

   public TeamDefinitionArtifactStore(Artifact artifact) throws OseeCoreException {
      super(null, AtsArtifactTypes.TeamDefinition, AtsUtilCore.getAtsBranchToken());
      this.artifact = artifact;
      load();
   }

   @Override
   public Result saveToArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact teamDefArt = getArtifact();
      if (teamDefArt == null) {
         throw new OseeArgumentException("Team Definition must be created first before save");
      }
      IAtsTeamDefinition teamDef = getTeamDefinition();
      teamDefArt.setName(teamDef.getName());
      teamDefArt.setSoleAttributeValue(AtsAttributeTypes.Active, teamDef.isActive());
      boolean actionable = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, true);
      if (actionable != teamDef.isActionable()) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.Actionable, teamDef.isActionable());
      }
      teamDefArt.setSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, teamDef.isTeamUsesVersions());

      boolean allowCommitBranch = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true);
      if (allowCommitBranch != teamDef.isAllowCommitBranch()) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, teamDef.isAllowCommitBranch());
      }

      boolean allowCreateBranch = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true);
      if (allowCreateBranch != teamDef.isAllowCreateBranch()) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, teamDef.isAllowCreateBranch());
      }
      if (Strings.isValid(teamDef.getBaslineBranchGuid())) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, teamDef.getBaslineBranchGuid());
      }
      if (Strings.isValid(teamDef.getWorkflowDefinition())) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, teamDef.getWorkflowDefinition());
      }
      if (Strings.isValid(teamDef.getRelatedTaskWorkDefinition())) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition,
            teamDef.getRelatedTaskWorkDefinition());
      }
      if (Strings.isValid(teamDef.getDescription())) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.Description, teamDef.getDescription());
      }
      if (Strings.isValid(teamDef.getFullName())) {
         teamDefArt.setSoleAttributeValue(AtsAttributeTypes.FullName, teamDef.getFullName());
      }

      // set new actionable items if necessary
      for (IAtsActionableItem aia : teamDef.getActionableItems()) {
         Artifact aiaArt = new ActionableItemArtifactStore(aia).getArtifact();
         if (aiaArt != null && aiaArt.getRelatedArtifact(AtsRelationTypes.TeamActionableItem_Team) != null) {
            aiaArt.addRelation(AtsRelationTypes.TeamActionableItem_Team, teamDefArt);
         }
      }

      // set new children team defs if changed
      List<String> newGuids = AtsObjects.toGuids(teamDef.getChildrenTeamDefinitions());
      List<String> currGuids = Artifacts.toGuids(teamDefArt.getChildren());
      // remove curr children that are not part of new children
      for (Artifact child : teamDefArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            if (newGuids.contains(child.getGuid())) {
               teamDefArt.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, child);
            }
         }
      }
      // add new children that are not part of curr children
      for (String newGuid : newGuids) {
         if (!currGuids.contains(newGuid)) {
            Artifact newArt = null;
            IAtsTeamDefinition newTeamDef = AtsConfigCache.getSoleByGuid(newGuid, IAtsTeamDefinition.class);
            if (newTeamDef != null) {
               newArt = AtsObjectsClient.getSoleArtifact(newTeamDef);
            }
            // if not persisted yet, it should be in artifact cache
            if (newArt == null) {
               newArt = ArtifactCache.getActive(newGuid, AtsUtilCore.getAtsBranchToken());
            }
            teamDefArt.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
         }
      }

      // update relations for versions and users
      setRelationsOfType(teamDefArt, teamDef.getVersions(), AtsRelationTypes.TeamDefinitionToVersion_Version);
      setRelationsOfType(teamDefArt, teamDef.getSubscribed(), AtsRelationTypes.SubscribedUser_User);
      setRelationsOfType(teamDefArt, teamDef.getLeads(), AtsRelationTypes.TeamLead_Lead);
      setRelationsOfType(teamDefArt, teamDef.getMembers(), AtsRelationTypes.TeamMember_Member);
      setRelationsOfType(teamDefArt, teamDef.getPrivilegedMembers(), AtsRelationTypes.PrivilegedMember_Member);

      // update rules if changed
      teamDefArt.setAttributeValues(AtsAttributeTypes.RuleDefinition, teamDef.getRules());

      // update staticIds
      if (!teamDef.getStaticIds().isEmpty()) {
         teamDefArt.setAttributeValues(CoreAttributeTypes.StaticId, teamDef.getStaticIds());
      }

      // set parent artifact to top team def
      if (teamDef.getParentTeamDef() == null && !teamDef.getGuid().equals(
         TeamDefinitions.getTopTeamDefinition().getGuid())) {
         // if parent is null, add to top team definition
         Artifact topTeamDefArt = AtsObjectsClient.getSoleArtifact(TeamDefinitions.getTopTeamDefinition());
         topTeamDefArt.addChild(teamDefArt);
         topTeamDefArt.persist(transaction);
      } else {
         // else reset parent if necessary
         Artifact parentTeamDefArt = teamDefArt.getParent();
         if (parentTeamDefArt != null) {
            if (parentTeamDefArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
               if (!parentTeamDefArt.getGuid().equals(teamDef.getParentTeamDef().getGuid())) {
                  Artifact newParentTeamDefArt = AtsObjectsClient.getSoleArtifact(teamDef);
                  newParentTeamDefArt.addChild(teamDefArt);
                  newParentTeamDefArt.persist(transaction);
                  parentTeamDefArt.persist(transaction);
               }
            }
         }
      }
      teamDefArt.persist(transaction);
      return Result.TrueResult;
   }

   @Override
   public Artifact getArtifactOrCreate(SkynetTransaction transaction) throws OseeCoreException {
      Artifact artifact = super.getArtifactOrCreate(transaction);
      saveToArtifact(transaction);
      return artifact;
   }

   public void load() throws OseeCoreException {
      Artifact teamDefArt = getArtifact();
      if (teamDefArt != null) {
         IAtsTeamDefinition teamDef = TeamDefinitionFactory.getOrCreate(teamDefArt.getGuid(), teamDefArt.getName());
         teamDef.setHumanReadableId(teamDefArt.getHumanReadableId());
         teamDef.setName(teamDefArt.getName());
         atsObject = teamDef;
         teamDef.setActive(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Active, true));
         teamDef.setActionable(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, true));
         String actionDetailsFormat = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.ActionDetailsFormat, "");
         teamDef.setTeamUsesVersions(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, true));
         teamDef.setAllowCommitBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, true));
         teamDef.setAllowCreateBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true));
         String baselineBranchGuid = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
         if (Strings.isValid(baselineBranchGuid)) {
            teamDef.setBaselineBranchGuid(baselineBranchGuid);
         }
         String workflowDefinition = teamDefArt.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, "");
         if (Strings.isValid(workflowDefinition)) {
            teamDef.setWorkflowDefinition(workflowDefinition);
         }
         String relatedTaskWorkDefinition =
            teamDefArt.getSoleAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition, "");
         if (Strings.isValid(relatedTaskWorkDefinition)) {
            teamDef.setRelatedTaskWorkDefinition(relatedTaskWorkDefinition);
         }
         teamDef.setDescription(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         teamDef.setFullName(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));
         for (Artifact aiArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem)) {
            IAtsActionableItem ai = ActionableItemFactory.getOrCreate(aiArt.getGuid(), aiArt.getName());
            teamDef.getActionableItems().add(ai);
            ai.setTeamDefinition(teamDef);
         }
         for (Artifact child : teamDefArt.getChildren()) {
            if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
               IAtsTeamDefinition childTeamDef = TeamDefinitionFactory.getOrCreate(child.getGuid(), child.getName());
               teamDef.getChildrenTeamDefinitions().add(childTeamDef);
               childTeamDef.setParentTeamDef(teamDef);
            }
         }
         for (Artifact verArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
            IAtsVersion version = VersionFactory.getOrCreate(verArt.getGuid(), verArt.getName());
            teamDef.getVersions().add(version);
            version.setTeamDefinition(teamDef);
         }
         for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            teamDef.getSubscribed().add(user);
         }
         for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            teamDef.getLeads().add(user);
         }
         for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            teamDef.getMembers().add(user);
         }
         for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            teamDef.getPrivilegedMembers().add(user);
         }
         for (String ruleStr : teamDefArt.getAttributesToStringList(AtsAttributeTypes.RuleDefinition)) {
            teamDef.addRule(ruleStr);
         }
         for (String staticId : teamDefArt.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            teamDef.getStaticIds().add(staticId);
         }
         Artifact parentTeamDefArt = teamDefArt.getParent();
         if (parentTeamDefArt != null && parentTeamDefArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition parentTeamDef =
               TeamDefinitionFactory.getOrCreate(parentTeamDefArt.getGuid(), parentTeamDefArt.getName());
            teamDef.setParentTeamDef(parentTeamDef);
            parentTeamDef.getChildrenTeamDefinitions().add(teamDef);
         }
      }
   }

   public IAtsTeamDefinition getTeamDefinition() {
      return (IAtsTeamDefinition) atsObject;
   }

}
