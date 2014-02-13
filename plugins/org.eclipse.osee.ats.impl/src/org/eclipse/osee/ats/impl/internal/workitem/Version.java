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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class Version extends AtsConfigObject implements IAtsVersion {

   public Version(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public List<IAtsVersion> getParallelVersions() {
      return null;
   }

   @Override
   public void getParallelVersions(Set<ICommitConfigItem> configArts) {
   }

   @Override
   public void setParallelVersions(List<IAtsVersion> parallelVersions) {
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
   }

   @Override
   public boolean isAllowCommitBranch() {
      return false;
   }

   @Override
   public void setAllowCommitBranch(boolean allow) {
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
   }

   @Override
   public void setReleasedDate(Date Date) {
   }

   @Override
   public Boolean isReleased() {
      return null;
   }

   @Override
   public void setReleased(boolean released) {
   }

   @Override
   public Date getEstimatedReleaseDate() {
      return null;
   }

   @Override
   public void setEstimatedReleasedDate(Date date) {
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public Boolean isVersionLocked() {
      return null;
   }

   @Override
   public void setLocked(boolean locked) {
   }

   @Override
   public void setVersionLocked(boolean locked) {
   }

   @Override
   public Boolean isNextVersion() {
      return null;
   }

   @Override
   public void setNextVersion(boolean nextVersion) {
   }

   @Override
   public String getTypeName() {
      return null;
   }

}
