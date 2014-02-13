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
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersion extends ICommitConfigItem, IAtsConfigObject {

   /*****************************
    * Name, Full Name, Description
    ******************************/

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

   void getParallelVersions(Set<ICommitConfigItem> configArts);

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
