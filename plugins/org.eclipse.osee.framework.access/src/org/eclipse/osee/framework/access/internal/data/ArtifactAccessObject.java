/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal.data;

import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactAccessObject extends AccessObject implements ArtifactId {

   private final ArtifactId artId;
   private final BranchId branch;
   private static final DoubleKeyHashMap<Long, BranchId, ArtifactAccessObject> cache = new DoubleKeyHashMap<>();

   public ArtifactAccessObject(ArtifactId artId, BranchId branch) {
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
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public void removeFromCache() {
      cache.remove(artId.getId(), branch);
   }

   @Override
   public void removeFromDatabase(int subjectId) {
      final String DELETE_ARTIFACT_ACL =
         "DELETE FROM OSEE_ARTIFACT_ACL WHERE privilege_entity_id = ? AND art_id =? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_ACL, subjectId, artId, branch);
   }

   public static ArtifactAccessObject getArtifactAccessObject(Artifact artifact) {
      return getArtifactAccessObject(artifact, artifact.getBranch());
   }

   public static ArtifactAccessObject getArtifactAccessObject(ArtifactId artifact, BranchId branch) {
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
}