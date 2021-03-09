/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranchId;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidArtifact extends BaseIdentity<String> implements HasBranchId {
   private final BranchId branch;
   private ArtifactTypeToken artifactType;

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeToken artifactType, String artGuid) {
      super(artGuid);
      this.branch = branch;
      this.artifactType = artifactType;
   }

   public boolean isTypeEqual(ArtifactTypeToken artifactType) {
      return artifactType.equals(this.artifactType);
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeToken artifactType) {
      this(branch, artifactType, GUID.create());
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactToken artifact) {
      this(branch, artifact.getArtifactType(), artifact.getGuid());
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public Long getArtTypeGuid() {
      return artifactType.getId();
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public String toString() {
      return String.format("branchId = %s; artType = %s; guid = %s", branch.getId(), artifactType.getId(), getGuid());
   }

   /**
    * Note: DefaultBasicGuidArtifact class does not implement the hashCode, but instead uses the one implemented by
    * Identity. It can not use the branch uuid due to the need for IArtifactTokens to match Artifact instances. In
    * addition, the event system requires that the DefaultBasicGuidArtifact and Artifact hashcode matches.
    */
   @Override
   public boolean equals(Object obj) {
      boolean equals = super.equals(obj);
      if (equals && obj instanceof DefaultBasicGuidArtifact) {
         DefaultBasicGuidArtifact other = (DefaultBasicGuidArtifact) obj;
         return other.artifactType.equals(artifactType) && isOnSameBranch(other);
      }
      return equals;
   }

   public void setArtTypeGuid(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }
}