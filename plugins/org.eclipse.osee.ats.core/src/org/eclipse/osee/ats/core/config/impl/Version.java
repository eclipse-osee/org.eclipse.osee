package org.eclipse.osee.ats.core.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.ats.core.model.ICommitConfigArtifact;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class Version extends AtsObject implements IAtsVersion {

   private boolean locked = false;
   private boolean next = false;
   private boolean active = false;
   private boolean released = false;

   private boolean createBranchAllowed = false;
   private boolean commitBranchAllowed = false;
   private String baselineBranchGuid = null;

   private String description = null;
   private String fullName = null;
   private Date releaseDate;
   private Date estimatedReleaseDate;

   private List<IAtsVersion> parallelVersions = new ArrayList<IAtsVersion>();
   private final Set<String> staticIds = new HashSet<String>();

   private IAtsTeamDefinition teamDefinition = null;

   public Version(String name, String guid, String humanReadableId) {
      super(name, guid, humanReadableId);
   }

   @Override
   public void getParallelVersions(Set<ICommitConfigArtifact> configArts) {
      configArts.add(this);
      for (IAtsVersion childArt : parallelVersions) {
         childArt.getParallelVersions(configArts);
      }
   }

   @Override
   public Boolean isVersionLocked() {
      return locked;
   }

   @Override
   public void setVersionLocked(boolean locked) {
      this.locked = locked;
   }

   @Override
   public Boolean isNextVersion() {
      return next;
   }

   @Override
   public void setNextVersion(boolean nextVersion) {
      this.next = nextVersion;
   }

   @Override
   public Boolean isReleased() {
      return released;
   }

   @Override
   public void setReleased(boolean released) {
      this.released = released;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      return teamDefinition;
   }

   public void setEstimatedReleaseDate(Date estimatedReleaseDate) {
      this.estimatedReleaseDate = estimatedReleaseDate;
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!createBranchAllowed) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!commitBranchAllowed) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
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
   public String getBaselineBranchGuidInherited() {
      if (Strings.isValid(baselineBranchGuid)) {
         return baselineBranchGuid;
      } else {
         return getTeamDefinition().getTeamBranchGuid();
      }
   }

   @Override
   public String getCommitFullDisplayName() {
      List<String> strs = new ArrayList<String>();
      strs.add(getName());
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public boolean isLocked() {
      return locked;
   }

   @Override
   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   @Override
   public void setTeamDefinition(IAtsTeamDefinition teamDefinition) {
      this.teamDefinition = teamDefinition;
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
   public List<IAtsVersion> getParallelVersions() {
      return parallelVersions;
   }

   @Override
   public void setParallelVersions(List<IAtsVersion> parallelVersions) {
      this.parallelVersions = parallelVersions;
   }

   @Override
   public void setAllowCreateBranch(boolean allow) {
      this.createBranchAllowed = allow;
   }

   @Override
   public void setAllowCommitBranch(boolean allow) {
      this.commitBranchAllowed = allow;
   }

   @Override
   public Date getReleaseDate() {
      return releaseDate;
   }

   @Override
   public Date getEstimatedReleaseDate() {
      return this.estimatedReleaseDate;
   }

   @Override
   public void setReleaseDate(Date date) {
      this.releaseDate = date;
   }

   @Override
   public Collection<String> getStaticIds() {
      return staticIds;
   }

   @Override
   public void setReleasedDate(Date date) {
      this.releaseDate = date;
   }

   @Override
   public void setEstimatedReleasedDate(Date date) {
      this.estimatedReleaseDate = date;
   }

   @Override
   public void setName(String name) {
      super.setName(name);
   }

   @Override
   public void setHumanReadableId(String humanReadableId) {
      super.setHumanReadableId(humanReadableId);
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public boolean isAllowCommitBranch() {
      return commitBranchAllowed;
   }

   @Override
   public boolean isAllowCreateBranch() {
      return createBranchAllowed;
   }

}
