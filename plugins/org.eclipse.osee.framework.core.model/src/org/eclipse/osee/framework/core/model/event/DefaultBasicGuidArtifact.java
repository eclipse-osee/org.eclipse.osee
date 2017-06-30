/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidArtifact extends BaseIdentity<String> implements HasBranch {
   private final BranchId branch;
   private ArtifactTypeId artifactType;

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeId artifactType, String artGuid) {
      super(artGuid);
      this.branch = branch;
      this.artifactType = artifactType;
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeId artifactType) {
      this(branch, artifactType, GUID.create());
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeId artifactType, ArtifactId artifact) {
      this(branch, artifactType, artifact.getGuid());
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public Long getArtTypeGuid() {
      return artifactType.getId();
   }

   public ArtifactTypeId getArtifactType() {
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
         return other.artifactType.equals(artifactType) & isOnSameBranch(other);
      }
      return equals;
   }

   public void setArtTypeGuid(ArtifactTypeId artifactType) {
      this.artifactType = artifactType;
   }
}