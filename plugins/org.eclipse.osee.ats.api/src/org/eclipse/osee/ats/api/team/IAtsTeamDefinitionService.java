/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamDefinitionService {

   IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDefHoldingVersions(IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program);

   IAtsTeamDefinition getTeamDefinition(String name);

   Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   TeamDefinition getTeamDefinitionById(ArtifactId teamDefId);

   TeamDefinition createTeamDefinition(String name, long id, IAtsChangeSet changes);

   TeamDefinition createTeamDefinition(String name, IAtsChangeSet changes);

   Collection<AtsUser> getLeads(IAtsTeamDefinition teamDef);

   Collection<AtsUser> getLeads(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems);

   Collection<AtsUser> getMembers(IAtsTeamDefinition teamDef);

   Collection<AtsUser> getMembersAndLeads(IAtsTeamDefinition teamDef);

   Collection<AtsUser> getSubscribed(IAtsTeamDefinition teamDef);

   boolean isAllowCommitBranch(IAtsTeamDefinition teamDef);

   Result isAllowCommitBranchInherited(IAtsTeamDefinition teamDef);

   Result isAllowCreateBranchInherited(IAtsTeamDefinition teamDef);

   BranchId getBaselineBranchId(IAtsTeamDefinition teamDef);

   BranchId getTeamBranchId(IAtsTeamDefinition teamDef);

   boolean isAllowCreateBranch(IAtsTeamDefinition teamDef);

   TeamDefinition getParentTeamDef(IAtsTeamDefinition teamDef);

   Collection<TeamDefinition> getChildrenTeamDefinitions(IAtsTeamDefinition teamDef);

   boolean isTeamUsesVersions(IAtsTeamDefinition teamDefinition);

   IAtsTeamDefinition getTeamDefinitionHoldingVersions(IAtsTeamDefinition teamDef);

   Collection<String> getRules(IAtsTeamDefinition teamDef);

   boolean hasRule(IAtsTeamDefinition teamDef, String rule);

   TeamDefinition createTeamDefinition(ArtifactToken teamDefArt);

   Collection<TeamDefinition> getTopLevelTeamDefinitions(Active active);

   List<IAtsTeamDefinition> getActive(Collection<IAtsTeamDefinition> teamDefs, Active active);

   Set<IAtsTeamDefinition> getChildren(IAtsTeamDefinition teamDef, boolean recurse);

   IAtsTeamDefinition getTopTeamDefinition();

   IAtsTeamDefinition getTopTeamDefinitionOrSentinel();

   Set<IAtsTeamDefinition> getTeamReleaseableDefinitions(Active active);

   Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem ai);

   Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsTeamDefinition teamDef);

   List<IAtsTeamDefinition> getTeamDefinitions(Active active);

   Collection<TeamDefinition> getTeamTopLevelDefinitions(Active active);

   void getTeamFromItemAndChildren(IAtsActionableItem ai, Set<IAtsTeamDefinition> aiTeams);

   Set<IAtsTeamDefinition> getTeamDefinitions(Collection<String> teamDefNames);

   Set<IAtsTeamDefinition> getTeamDefinitionsNameStartsWith(String prefix);

   Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais);

   IAtsTeamDefinition getImpactedTeamDef(IAtsActionableItem ai);

   Collection<IAtsTeamDefinition> getImpactedTeamDefInherited(IAtsActionableItem ai);

   Collection<TeamDefinition> getTeamTopLevelJaxDefinitions(Active active);

   Collection<IAtsTeamDefinition> getTeamDefs(Collection<TeamDefinition> jTeamDefs);

   Collection<IAtsTeamDefinition> getTeamDefHoldingVersions();

}
