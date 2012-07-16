/*
 * Created on May 30, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersion extends ICommitConfigArtifact, IAtsConfigObject {

   /*****************************
    * Name, Full Name, Description
    ******************************/

   void setHumanReadableId(String humanReadableId);

   void setName(String name);

   String getFullName();

   void setFullName(String fullName);

   @Override
   String getDescription();

   void setDescription(String description);

   @Override
   String toString();

   /*****************************
    * Parallel Versions
    ******************************/

   public List<IAtsVersion> getParallelVersions();

   void getParallelVersions(Set<ICommitConfigArtifact> configArts);

   public void setParallelVersions(List<IAtsVersion> parallelVersions);

   /*****************************
    * Branching Data
    ******************************/
   /**
    * @return directly configured baseline branch guid or parentTeamDefinition's branch guid
    */
   @Override
   String getBaslineBranchGuid();

   String getBaselineBranchGuidInherited();

   public void setBaselineBranchGuid(String parentBranchGuid);

   @Override
   String getCommitFullDisplayName();

   @Override
   Result isAllowCreateBranchInherited();

   boolean isAllowCreateBranch();

   void setAllowCreateBranch(boolean allow);

   boolean isAllowCommitBranch();

   void setAllowCommitBranch(boolean allow);

   @Override
   Result isAllowCommitBranchInherited();

   /*****************************
    * Misc
    ******************************/
   Date getReleaseDate();

   void setReleaseDate(Date date);

   void setReleasedDate(Date Date);

   Boolean isReleased();

   void setReleased(boolean released);

   Date getEstimatedReleaseDate();

   void setEstimatedReleasedDate(Date date);

   Collection<String> getStaticIds();

   boolean isLocked();

   Boolean isVersionLocked();

   void setLocked(boolean locked);

   void setVersionLocked(boolean locked);

   Boolean isNextVersion();

   void setNextVersion(boolean nextVersion);

}
