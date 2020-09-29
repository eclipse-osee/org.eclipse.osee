/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.access.object;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class ArtifactAccessObject extends AccessObject implements ArtifactId {

   private final ArtifactToken artifact;
   private static final Map<ArtifactToken, ArtifactAccessObject> cache = new HashMap<>();

   private ArtifactAccessObject(ArtifactToken artifact) {
      super(artifact);
      this.artifact = artifact;
   }

   @Override
   public BranchToken getBranch() {
      return artifact.getBranch();
   }

   @Override
   public void removeFromCache() {
      cache.remove(artifact);
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
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
      ArtifactAccessObject other = (ArtifactAccessObject) obj;
      if (artifact == null) {
         if (other.artifact != null) {
            return false;
         }
      } else if (!artifact.equals(other.artifact)) {
         return false;
      }
      return true;
   }

   public static ArtifactAccessObject valueOf(ArtifactToken artifact) {
      ArtifactAccessObject aao = cache.get(artifact);
      if (aao == null) {
         aao = new ArtifactAccessObject(artifact);
         cache.put(artifact, aao);
      }
      return aao;
   }

   @Override
   public String toString() {
      return "Artifact " + artifact.toStringWithId();
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }
}