/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

public final class ArtifactKey {

   private Long artId;
   private BranchId branch;

   public ArtifactKey(ArtifactToken artifact) {
      this.artId = artifact.getId();
      this.branch = artifact.getBranch();
   }

   public ArtifactKey() {
   }

   public ArtifactKey setKey(ArtifactToken artifact) {
      this.artId = artifact.getId();
      this.branch = artifact.getBranch();
      return this;
   }

   public ArtifactKey setKey(Long artId, BranchId branch) {
      this.artId = artId;
      this.branch = branch;
      return this;
   }

   @Override
   public int hashCode() {
      return 31 * artId.hashCode() + branch.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ArtifactKey other = (ArtifactKey) obj;
      if (!artId.equals(other.artId)) {
         return false;
      }
      if (branch.notEqual(other.branch)) {
         return false;
      }
      return true;
   }
}