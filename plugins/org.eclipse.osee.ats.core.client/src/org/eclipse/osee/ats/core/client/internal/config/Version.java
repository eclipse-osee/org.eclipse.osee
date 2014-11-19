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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

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
   private long baselineBranchUuid = 0;

   private String description = null;
   private String fullName = null;
   private Date releaseDate;
   private Date estimatedReleaseDate;

   private List<IAtsVersion> parallelVersions = new ArrayList<IAtsVersion>();
   private final Set<String> staticIds = new HashSet<String>();

   private final IAtsVersionService versionService;

   public Version(IAtsVersionService versionService, String name, String guid, long uuid) {
      super(name, guid, uuid);
      this.versionService = versionService;
   }

   @Override
   public void getParallelVersions(Set<ICommitConfigItem> configArts) {
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

   public void setEstimatedReleaseDate(Date estimatedReleaseDate) {
      this.estimatedReleaseDate = estimatedReleaseDate;
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!createBranchAllowed) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (!AtsClientService.get().getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!commitBranchAllowed) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (!AtsClientService.get().getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public long getBaselineBranchUuid() {
      return baselineBranchUuid;
   }

   @Override
   public void setBaselineBranchUuid(long baselineBranchUuid) {
      this.baselineBranchUuid = baselineBranchUuid;
   }

   @Override
   public void setBaselineBranchUuid(String baselineBranchUuid) {
      if (Strings.isValid(baselineBranchUuid)) {
         this.baselineBranchUuid = Long.valueOf(baselineBranchUuid);
      }
   }

   @Override
   public long getBaselineBranchUuidInherited() {
      if (baselineBranchUuid > 0) {
         return baselineBranchUuid;
      } else {
         try {
            IAtsTeamDefinition teamDef = versionService.getTeamDefinition(this);
            if (teamDef != null) {
               return teamDef.getTeamBranchUuid();
            } else {
               return 0;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return 0;
         }
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
      try {
         super.setName(name);
      } catch (OseeCoreException ex) {
         throw new IllegalArgumentException("error setting name", ex);
      }
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

   @Override
   public String getTypeName() {
      return "Version";
   }

}
