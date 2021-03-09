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

package org.eclipse.osee.framework.access.internal.data;

import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactAccessObject extends AccessObject implements ArtifactId {

   private final ArtifactId artId;
   private final BranchToken branch;
   private static final DoubleKeyHashMap<Long, BranchToken, ArtifactAccessObject> cache = new DoubleKeyHashMap<>();

   public ArtifactAccessObject(ArtifactId artId, BranchToken branch) {
      this.artId = artId;
      this.branch = branch;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + artId.hashCode();
      result = 31 * result + branch.hashCode();
      return result;
   }

   @Override
   public BranchToken getBranch() {
      return branch;
   }

   @Override
   public void removeFromCache() {
      cache.remove(artId.getId(), branch);
   }

   @Override
   public void removeFromDatabase(ArtifactId subjectId) {
      final String DELETE_ARTIFACT_ACL =
         "DELETE FROM OSEE_ARTIFACT_ACL WHERE privilege_entity_id = ? AND art_id =? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_ACL, subjectId, artId, branch);
   }

   public static ArtifactAccessObject getArtifactAccessObject(Artifact artifact) {
      return getArtifactAccessObject(artifact, artifact.getBranch());
   }

   public static ArtifactAccessObject getArtifactAccessObject(ArtifactId artifact, BranchToken branch) {
      ArtifactAccessObject accessObject = cache.get(artifact.getId(), branch);

      if (accessObject == null) {
         accessObject = new ArtifactAccessObject(artifact, branch);
         cache.put(artifact.getId(), branch, accessObject);
      }
      return accessObject;
   }

   public static AccessObject getArtifactAccessObjectFromCache(Artifact artifact) {
      return cache.get(artifact.getId(), artifact.getBranch());
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ArtifactAccessObject)) {
         return false;
      }
      ArtifactAccessObject ao = (ArtifactAccessObject) obj;
      return ao.artId.equals(this.artId) && ao.branch.equals(this.branch);
   }

   @Override
   public Long getId() {
      return artId.getId();
   }

}