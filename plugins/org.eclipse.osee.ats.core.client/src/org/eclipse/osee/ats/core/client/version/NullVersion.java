/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.version;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.util.Result;

public class NullVersion implements IAtsVersion {

   public static NullVersion instance = new NullVersion();

   private NullVersion() {
   }

   @Override
   public String getName() {
      return "Null Version";
   }

   @Override
   public String getTypeName() {
      return "Version";
   }

   @Override
   public String toStringWithId() {
      return getName();
   }

   @Override
   public String getGuid() {
      return getName();
   }

   @Override
   public String getHumanReadableId() {
      return getName();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return false;
   }

   @Override
   public void setHumanReadableId(String humanReadableId) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public void setName(String name) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public String getFullName() {
      return getName();
   }

   @Override
   public void setFullName(String fullName) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public String getDescription() {
      return getName();
   }

   @Override
   public void setDescription(String description) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public List<IAtsVersion> getParallelVersions() {
      return null;
   }

   @Override
   public void getParallelVersions(Set<ICommitConfigArtifact> configArts) {
   }

   @Override
   public void setParallelVersions(List<IAtsVersion> parallelVersions) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public String getBaslineBranchGuid() {
      return null;
   }

   @Override
   public String getBaselineBranchGuidInherited() {
      return null;
   }

   @Override
   public void setBaselineBranchGuid(String parentBranchGuid) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public String getCommitFullDisplayName() {
      return null;
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      return null;
   }

   @Override
   public boolean isAllowCreateBranch() {
      return false;
   }

   @Override
   public void setAllowCreateBranch(boolean allow) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public boolean isAllowCommitBranch() {
      return false;
   }

   @Override
   public void setAllowCommitBranch(boolean allow) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      return null;
   }

   @Override
   public Date getReleaseDate() {
      return null;
   }

   @Override
   public void setReleaseDate(Date date) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public void setReleasedDate(Date Date) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public Boolean isReleased() {
      return false;
   }

   @Override
   public void setReleased(boolean released) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public Date getEstimatedReleaseDate() {
      return null;
   }

   @Override
   public void setEstimatedReleasedDate(Date date) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public Collection<String> getStaticIds() {
      return Collections.emptyList();
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public Boolean isVersionLocked() {
      return false;
   }

   @Override
   public void setLocked(boolean locked) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public void setVersionLocked(boolean locked) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

   @Override
   public Boolean isNextVersion() {
      return false;
   }

   @Override
   public void setNextVersion(boolean nextVersion) {
      throw new IllegalArgumentException("not valid for " + getName());
   }

}
