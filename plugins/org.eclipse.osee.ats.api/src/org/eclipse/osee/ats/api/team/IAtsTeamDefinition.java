/*
 * Created on May 30, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.api.rule.IAtsRules;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamDefinition extends IAtsConfigObject, IAtsRules, ICommitConfigArtifact {

   /*****************************
    * Name, Full Name, Description
    ******************************/

   void setName(String name) throws OseeCoreException;

   void setDescription(String description);

   void setHumanReadableId(String hrid);

   @Override
   String getDescription();

   // Name
   void setFullName(String fullName);

   String getFullName();

   /*****************************
    * Misc
    ******************************/

   void setActionable(boolean actionable);

   boolean isActionable();

   void setActive(boolean active);

   boolean isActive();

   Collection<String> getStaticIds();

   /*****************************
    * Related Actionable Items
    ******************************/

   Collection<IAtsActionableItem> getActionableItems();

   /*****************************
    * Parent and Children Team Definitions
    ******************************/

   void setParentTeamDef(IAtsTeamDefinition parentTeamDef);

   IAtsTeamDefinition getParentTeamDef();

   Collection<IAtsTeamDefinition> getChildrenTeamDefinitions();

   /*****************************************************
    * Team Leads, Members, Priviledged Members
    ******************************************************/

   Collection<IAtsUser> getLeads();

   Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) throws OseeCoreException;

   Collection<IAtsUser> getMembers() throws OseeCoreException;

   Collection<IAtsUser> getMembersAndLeads() throws OseeCoreException;

   Collection<IAtsUser> getSubscribed();

   Collection<IAtsUser> getPrivilegedMembers();

   /*****************************
    * Branching Data
    ******************************/

   void setAllowCommitBranch(boolean allowCommitBranch);

   boolean isAllowCommitBranch();

   @Override
   Result isAllowCommitBranchInherited();

   void setAllowCreateBranch(boolean allowCreateBranch);

   boolean isAllowCreateBranch();

   @Override
   Result isAllowCreateBranchInherited();

   void setBaselineBranchGuid(String parentBranchGuid);

   @Override
   String getBaslineBranchGuid();

   String getTeamBranchGuid();

   @Override
   String getCommitFullDisplayName();

   /*****************************
    * Versions
    ******************************/

   void setTeamUsesVersions(boolean teamUsesVersions);

   boolean isTeamUsesVersions();

   IAtsVersion getNextReleaseVersion();

   IAtsTeamDefinition getTeamDefinitionHoldingVersions() throws OseeCoreException;

   IAtsVersion getVersion(String name) throws OseeCoreException;

   Collection<IAtsVersion> getVersions();

   Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException;

   Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException;

   Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType);

   Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType);

   /*****************************
    * Work Definitions
    ******************************/

   void setWorkflowDefinition(String workflowDefinitionName);

   String getWorkflowDefinition();

   String getRelatedTaskWorkDefinition();

   void setRelatedTaskWorkDefinition(String name);

   void initialize(String fullname, String description, Collection<IAtsUser> leads, Collection<IAtsUser> members, Collection<IAtsActionableItem> actionableItems, TeamDefinitionOptions... teamDefinitionOptions);

   /*****************************
    * Rules
    ******************************/
   @Override
   void addRule(String rule);

   @Override
   Collection<String> getRules();

   @Override
   boolean hasRule(String rule);

   @Override
   void removeRule(String rule);

}
