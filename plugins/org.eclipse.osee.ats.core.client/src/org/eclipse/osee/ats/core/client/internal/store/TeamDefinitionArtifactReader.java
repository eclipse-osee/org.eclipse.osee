/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.store;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.core.client.IAtsUserAdmin;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifactReader extends AbstractAtsArtifactReader<IAtsTeamDefinition> {

   private final IAtsVersionService versionService;
   private final IAtsUserAdmin userAdmin;

   public TeamDefinitionArtifactReader(IActionableItemFactory actionableItemFactory, ITeamDefinitionFactory teamDefFactory, IVersionFactory versionFactory, IAtsVersionService versionService, IAtsUserAdmin userAdmin) {
      super(actionableItemFactory, teamDefFactory, versionFactory);
      this.versionService = versionService;
      this.userAdmin = userAdmin;
   }

   @Override
   public IAtsTeamDefinition load(AtsArtifactConfigCache cache, Artifact teamDefArt) throws OseeCoreException {
      IAtsTeamDefinition teamDef = getOrCreateTeamDefinition(cache, teamDefArt);

      teamDef.setName(teamDefArt.getName());
      teamDef.setActive(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
      teamDef.setActionable(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false));
      teamDef.setAllowCommitBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false));
      teamDef.setAllowCreateBranch(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false));
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
         IAtsActionableItem ai = getOrCreateActionableItem(cache, aiArt);
         teamDef.getActionableItems().add(ai);
         ai.setTeamDefinition(teamDef);
      }
      for (Artifact child : teamDefArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition childTeamDef = getOrCreateTeamDefinition(cache, child);
            teamDef.getChildrenTeamDefinitions().add(childTeamDef);
            childTeamDef.setParentTeamDef(teamDef);
         }
      }
      for (Artifact verArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         IAtsVersion version = getOrCreateVersion(cache, verArt);
         teamDef.getVersions().add(version);
         versionService.setTeamDefinition(version, teamDef);
      }
      for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
         teamDef.getSubscribed().add(user);
      }
      for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
         teamDef.getLeads().add(user);
      }
      for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
         teamDef.getMembers().add(user);
      }
      for (Artifact userArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
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
         IAtsTeamDefinition parentTeamDef = getOrCreateTeamDefinition(cache, parentTeamDefArt);
         teamDef.setParentTeamDef(parentTeamDef);
         parentTeamDef.getChildrenTeamDefinitions().add(teamDef);
      }
      return teamDef;
   }
}
