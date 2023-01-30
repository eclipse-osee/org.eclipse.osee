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

package org.eclipse.osee.ats.api.version;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class Version extends JaxAtsConfigObject implements IAtsVersion {

   public Version() {
      // for jax-rs
   }

   public Version(ArtifactToken artifact, AtsApi atsApi) {
      super(artifact.getId(), artifact.getName());
      setStoreObject(artifact);
      setAtsApi(atsApi);
   }

   @JsonSerialize(using = ToStringSerializer.class)
   Long teamDefId;
   boolean allowCreateBranch = false;
   boolean allowCommitBranch = false;
   boolean released = false;
   boolean locked = false;
   boolean nextVersion = false;
   BranchId baselineBranch = BranchId.SENTINEL;
   String closureState = "";

   public Long getTeamDefId() {
      return teamDefId;
   }

   public void setTeamDefId(Long teamDefId) {
      this.teamDefId = teamDefId;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.Version;
   }

   @Override
   public boolean isAllowCreateBranch() {
      return allowCreateBranch;
   }

   public void setAllowCreateBranch(boolean allowCreateBranch) {
      this.allowCreateBranch = allowCreateBranch;
   }

   @Override
   public boolean isAllowCommitBranch() {
      return allowCommitBranch;
   }

   public void setAllowCommitBranch(boolean allowCommitBranch) {
      this.allowCommitBranch = allowCommitBranch;
   }

   @Override
   public boolean isReleased() {
      return released;
   }

   public void setReleased(boolean released) {
      this.released = released;
   }

   @Override
   public boolean isLocked() {
      return locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   @Override
   public boolean isNextVersion() {
      return nextVersion;
   }

   public void setNextVersion(boolean nextVersion) {
      this.nextVersion = nextVersion;
   }

   @Override
   public BranchId getBaselineBranch() {
      return baselineBranch;
   }

   public void setBaselineBranch(BranchId baselineBranch) {
      this.baselineBranch = baselineBranch;
   }

   @Override
   public String getClosureState() {
      return closureState;
   }

   public void setClosureState(String closureState) {
      this.closureState = closureState;
   }

}
