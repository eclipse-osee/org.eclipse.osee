package org.eclipse.osee.ats.core.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.ats.core.model.TeamDefinitionOptions;
import org.eclipse.osee.ats.core.model.VersionLockedType;
import org.eclipse.osee.ats.core.model.VersionReleaseType;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.ats.core.util.AtsUtilCoreCore;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.RuleManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinition extends AtsObject implements IAtsTeamDefinition {
   private boolean active = true;
   private boolean actionable = false;
   private boolean allowCreateBranch = false;
   private boolean allowCommitBranch = false;
   private boolean teamUsesVersions = false;

   private String baselineBranchGuid = null;
   private String description = null;
   private String fullName = null;
   private String workflowDefinitionName;
   private String actionDetailsFormat;
   private String relatedTaskWorkDefinition;
   private Double manDayHours = 0.0;

   private IAtsTeamDefinition parentTeamDef;

   private final Set<String> staticIds = new HashSet<String>();
   private final RuleManager ruleMgr = new RuleManager();

   private final Set<IAtsUser> leads = new HashSet<IAtsUser>();
   private final Set<IAtsUser> members = new HashSet<IAtsUser>();
   private final Set<IAtsUser> priviledgedMembers = new HashSet<IAtsUser>();
   private final Set<IAtsUser> subscribed = new HashSet<IAtsUser>();

   private final Set<IAtsTeamDefinition> childrenTeamDefinitions = new HashSet<IAtsTeamDefinition>();
   private final Set<IAtsActionableItem> actionableItems = new HashSet<IAtsActionableItem>();
   private final Set<IAtsVersion> versions = new HashSet<IAtsVersion>();

   @Override
   public String getRelatedTaskWorkDefinition() {
      return relatedTaskWorkDefinition;
   }

   public TeamDefinition(String name, String guid, String humanReadableId) {
      super(name, guid, humanReadableId);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!allowCreateBranch) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!allowCommitBranch) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public void initialize(String fullname, String description, Collection<IAtsUser> leads, Collection<IAtsUser> members, Collection<IAtsActionableItem> actionableItems, TeamDefinitionOptions... teamDefinitionOptions) {
      List<Object> teamDefOptions = Collections.getAggregate((Object[]) teamDefinitionOptions);

      setDescription(description);
      setFullName(fullname);
      leads.addAll(leads);
      members.addAll(members);

      if (teamDefOptions.contains(TeamDefinitionOptions.TeamUsesVersions)) {
         setTeamUsesVersions(true);
      }
      if (teamDefOptions.contains(TeamDefinitionOptions.RequireTargetedVersion)) {
         addRule(RuleDefinitionOption.RequireTargetedVersion);
      }
      actionableItems.addAll(actionableItems);
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with versions. This allows
    * multiple TeamDefinitions to be versioned/released together by having the parent hold the versions. It is not
    * required that a product configured in ATS uses the versions option. If no parent with versions is found, null is
    * returned. If boolean "Team Uses Versions" is false, just return cause this team doesn't use versions
    * 
    * @return parent TeamDefinition that holds the version definitions
    */

   @Override
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions() throws OseeCoreException {
      if (!isTeamUsesVersions()) {
         return null;
      }
      if (getVersions().size() > 0) {
         return this;
      }
      IAtsTeamDefinition parentTda = getParentTeamDef();
      if (parentTda != null) {
         return parentTda.getTeamDefinitionHoldingVersions();
      }
      return null;
   }

   @Override
   public IAtsVersion getNextReleaseVersion() {
      for (IAtsVersion verArt : getVersions()) {
         if (verArt.isNextVersion()) {
            return verArt;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException {
      IAtsTeamDefinition teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) {
         return new ArrayList<IAtsVersion>();
      }
      return teamDef.getVersions(releaseType, lockedType);
   }

   @Override
   public double getManDayHrsFromItemAndChildren() {
      return getHoursPerWorkDayFromItemAndChildren(this);
   }

   /**
    * If hours per work day attribute is set, use it, otherwise, walk up the Team Definition tree. Value used in
    * calculations.
    */

   @Override
   public double getHoursPerWorkDayFromItemAndChildren(IAtsTeamDefinition teamDef) {
      try {
         if (manDayHours != null && manDayHours != 0) {
            return manDayHours;
         }
         if (teamDef.getParentTeamDef() != null) {
            return teamDef.getHoursPerWorkDayFromItemAndChildren(teamDef.getParentTeamDef());
         }
         return AtsUtilCoreCore.DEFAULT_HOURS_PER_WORK_DAY;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0.0;
   }

   /**
    * Return ONLY leads configured for this Depending on the use, like creating new actions, the assignees (or Leads)
    * are determined first from users configured as leads of individual actionable items and only if that returns no
    * leads, THEN default to using the leads configured for the TeamDefinition. In these cases, use
    * getLeads(Collection<IAtsActionableItem>) instead.
    * 
    * @return users configured as leads for this IAtsTeamDefinition
    */

   @Override
   public Collection<IAtsUser> getLeads() {
      return leads;
   }

   @Override
   public Collection<IAtsUser> getPrivilegedMembers() {
      return priviledgedMembers;
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this
    * 
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    */

   @Override
   public Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
      Set<IAtsUser> leads = new HashSet<IAtsUser>();
      for (IAtsActionableItem aia : actionableItems) {
         if (this.equals(aia.getTeamDefinitionInherited())) {
            // If leads are specified for this aia, add them
            if (aia.getLeads().size() > 0) {
               leads.addAll(aia.getLeads());
            } else {
               if (aia.getTeamDefinitionInherited() != null) {
                  leads.addAll(aia.getTeamDefinitionInherited().getLeads());
               }
            }
         }
      }
      if (leads.isEmpty()) {
         leads.addAll(getLeads());
      }
      return leads;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Collection<IAtsUser> getMembersAndLeads() {
      return Collections.setUnion(getMembers(), getLeads());
   }

   @Override
   public Collection<IAtsUser> getMembers() {
      return members;
   }

   @Override
   public IAtsVersion getVersion(String name) {
      for (IAtsVersion verArt : getVersions()) {
         if (verArt.getName().equals(name)) {
            return verArt;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsVersion> getVersions() {
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockType) {
      return Collections.setIntersection(getVersionsReleased(releaseType), getVersionsLocked(lockType));
   }

   @Override
   public Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType) {
      ArrayList<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (IAtsVersion version : getVersions()) {
         if (version.isReleased() && (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both)) {
            versions.add(version);
         } else if ((!version.isReleased() && releaseType == VersionReleaseType.UnReleased) || releaseType == VersionReleaseType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType) {
      ArrayList<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (IAtsVersion version : getVersions()) {
         if (version.isVersionLocked() && (lockType == VersionLockedType.Locked || lockType == VersionLockedType.Both)) {
            versions.add(version);
         } else if ((!version.isVersionLocked() && lockType == VersionLockedType.UnLocked) || lockType == VersionLockedType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public boolean isTeamUsesVersions() {
      return teamUsesVersions;
   }

   @Override
   public boolean isActionable() {
      return actionable;
   }

   @Override
   public Collection<RuleDefinition> getRules() {
      return ruleMgr.getRules();
   }

   @Override
   public RuleDefinition addRule(RuleDefinitionOption option) {
      return ruleMgr.addRule(option);
   }

   @Override
   public RuleDefinition addRule(String ruleId) {
      return ruleMgr.addRule(ruleId);
   }

   @Override
   public boolean hasRule(RuleDefinitionOption option) {
      return ruleMgr.hasRule(option);
   }

   @Override
   public boolean hasRule(String ruleId) {
      return ruleMgr.hasRule(ruleId);
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/>
    * <br/>
    * If no branch is associated then null will be returned.
    */
   @Override
   public String getTeamBranchGuid() {
      String guid = getBaslineBranchGuid();
      if (GUID.isValid(guid)) {
         return guid;
      } else {
         IAtsTeamDefinition parentTeamDef = getParentTeamDef();
         if (parentTeamDef instanceof TeamDefinition) {
            return parentTeamDef.getTeamBranchGuid();
         }
      }
      return null;
   }

   @Override
   public String getCommitFullDisplayName() {
      return getName();
   }

   @Override
   public boolean isAllowCreateBranch() {
      return allowCreateBranch;
   }

   @Override
   public void setAllowCreateBranch(boolean allowCreateBranch) {
      this.allowCreateBranch = allowCreateBranch;
   }

   @Override
   public String getBaslineBranchGuid() {
      return baselineBranchGuid;
   }

   @Override
   public void setBaselineBranchGuid(String baselineBranchGuid) {
      this.baselineBranchGuid = baselineBranchGuid;
   }

   @Override
   public boolean isAllowCommitBranch() {
      return allowCommitBranch;
   }

   @Override
   public void setAllowCommitBranch(boolean allowCommitBranch) {
      this.allowCommitBranch = allowCommitBranch;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String getFullName() {
      return fullName;
   }

   @Override
   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   @Override
   public void setTeamUsesVersions(boolean teamUsesVersions) {
      this.teamUsesVersions = teamUsesVersions;
   }

   @Override
   public IAtsTeamDefinition getParentTeamDef() {
      return parentTeamDef;
   }

   @Override
   public Double getManDayHours() {
      return manDayHours;
   }

   @Override
   public void setManDayHours(Double manDayHours) {
      this.manDayHours = manDayHours;
   }

   @Override
   public void setActionable(boolean actionable) {
      this.actionable = actionable;
   }

   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions() {
      return childrenTeamDefinitions;
   }

   @Override
   public void setWorkflowDefinition(String workDefinitionName) {
      workflowDefinitionName = workDefinitionName;
   }

   @Override
   public String getWorkflowDefinition() {
      return workflowDefinitionName;
   }

   @Override
   public Collection<String> getStaticIds() {
      return staticIds;
   }

   @Override
   public String getActionDetailsFormat() {
      return actionDetailsFormat;
   }

   @Override
   public void setActionDetailsFormat(String actionDetailsFormat) {
      this.actionDetailsFormat = actionDetailsFormat;
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   @Override
   public void setRelatedTaskWorkDefinition(String name) {
      this.relatedTaskWorkDefinition = name;
   }

   @Override
   public void setParentTeamDef(IAtsTeamDefinition parentTeamDef) {
      if (parentTeamDef.getGuid().equals(getGuid())) {
         throw new IllegalArgumentException("Can't set parent to self");
      }
      this.parentTeamDef = parentTeamDef;
   }

   @Override
   public Collection<IAtsUser> getSubscribed() {
      return subscribed;
   }

}
