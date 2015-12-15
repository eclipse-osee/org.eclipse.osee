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
package org.eclipse.osee.ats.rest.internal.workitem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class Version extends AtsConfigObject implements IAtsVersion {

   public Version(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   private ArtifactReadable getArtifact() {
      return (ArtifactReadable) artifact;
   }

   @Override
   public List<IAtsVersion> getParallelVersions() {
      List<IAtsVersion> parallelVersions = new ArrayList<>();
      for (ArtifactReadable parallelVerArt : getArtifact().getRelated(AtsRelationTypes.ParallelVersion_Child)) {
         IAtsVersion parallelVer = atsServices.getConfigItemFactory().getVersion(parallelVerArt);
         parallelVersions.add(parallelVer);
      }
      return parallelVersions;
   }

   @Override
   public void getParallelVersions(Set<ICommitConfigItem> configArts) {
      configArts.add(this);
      for (IAtsVersion childArt : getParallelVersions()) {
         childArt.getParallelVersions(configArts);
      }
   }

   @Override
   public void setParallelVersions(List<IAtsVersion> parallelVersions) {
      throw new UnsupportedOperationException("Version.setParallelVersions not implemented");
   }

   @Override
   public String getCommitFullDisplayName() {
      List<String> strs = new ArrayList<>();
      strs.add(getName());
      String fullName = getArtifact().getSoleAttributeValue(AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description = getArtifact().getSoleAttributeValue(AtsAttributeTypes.Description, "");
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!isAllowCreateBranch()) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (!atsServices.getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public boolean isAllowCreateBranch() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false);
   }

   @Override
   public void setAllowCreateBranch(boolean allow) {
      throw new UnsupportedOperationException("Version.setAllowCreateBranch not implemented");
   }

   @Override
   public boolean isAllowCommitBranch() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false);
   }

   @Override
   public void setAllowCommitBranch(boolean allow) {
      throw new UnsupportedOperationException("Version.setAllowCommitBranch not implemented");
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!isAllowCommitBranch()) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (!atsServices.getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Date getReleaseDate() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, null);
   }

   @Override
   public void setReleaseDate(Date date) {
      throw new UnsupportedOperationException("Version.setReleaseDate not implemented");
   }

   @Override
   public void setReleasedDate(Date Date) {
      throw new UnsupportedOperationException("Version.setReleasedDate not implemented");
   }

   @Override
   public Boolean isReleased() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.Released, false);
   }

   @Override
   public void setReleased(boolean released) {
      throw new UnsupportedOperationException("Version.setReleased not implemented");
   }

   @Override
   public Date getEstimatedReleaseDate() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, (Date) null);
   }

   @Override
   public void setEstimatedReleasedDate(Date date) {
      throw new UnsupportedOperationException("Version.setEstimatedReleasedDate not implemented");
   }

   @Override
   public boolean isLocked() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false);
   }

   @Override
   public Boolean isVersionLocked() {
      return isLocked();
   }

   @Override
   public void setLocked(boolean locked) {
      throw new UnsupportedOperationException("Version.setLocked not implemented");
   }

   @Override
   public void setVersionLocked(boolean locked) {
      throw new UnsupportedOperationException("Version.setVersionLocked not implemented");
   }

   @Override
   public Boolean isNextVersion() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.NextVersion, false);
   }

   @Override
   public void setNextVersion(boolean nextVersion) {
      throw new UnsupportedOperationException("Version.setNextVersion not implemented");
   }

   @Override
   public String getTypeName() {
      return "Version";
   }

   @Override
   public long getBaselineBranchUuid() {
      return Long.parseLong(getArtifact().getSoleAttributeAsString(AtsAttributeTypes.BaselineBranchUuid, "-1"));
   }

   @Override
   public long getBaselineBranchUuidInherited() {
      if (getBaselineBranchUuid() > 0) {
         return getBaselineBranchUuid();
      } else {
         try {
            IAtsTeamDefinition teamDef = atsServices.getVersionService().getTeamDefinition(this);
            if (teamDef != null) {
               return teamDef.getTeamBranchUuid();
            } else {
               return 0;
            }
         } catch (OseeCoreException ex) {
            return 0;
         }
      }
   }

   @Override
   public void setBaselineBranchUuid(long uuid) {
      throw new UnsupportedOperationException("Version.setBaselineBranchUuid not implemented");
   }

   @Override
   public void setBaselineBranchUuid(String uuid) {
      throw new UnsupportedOperationException("Version.setBaselineBranchUuid not implemented");
   }

}
