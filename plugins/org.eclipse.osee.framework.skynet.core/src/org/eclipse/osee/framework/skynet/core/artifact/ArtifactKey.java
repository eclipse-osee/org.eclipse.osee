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

import org.eclipse.osee.framework.skynet.core.types.IArtifact;

public final class ArtifactKey {

   private int artId;
   private int branchId;

   public ArtifactKey(IArtifact artifact) {
      this.artId = artifact.getArtId();
      this.branchId = artifact.getBranch().getId();
   }

   public ArtifactKey(int artId, int branchId) {
      this.artId = artId;
      this.branchId = branchId;
   }

   public ArtifactKey setKey(IArtifact artifact) {
      this.artId = artifact.getArtId();
      this.branchId = artifact.getBranch().getId();
      return this;
   }

   public ArtifactKey setKey(int artId, int branchId) {
      this.artId = artId;
      this.branchId = branchId;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + artId;
      result = prime * result + branchId;
      return result;
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
      if (artId != other.artId) {
         return false;
      }
      if (branchId != other.branchId) {
         return false;
      }
      return true;
   }
}