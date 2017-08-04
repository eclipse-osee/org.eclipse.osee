/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Payload for the Access Topic Event
 *
 * @author Donald G. Dunne
 */
public class AccessTopicEventPayload implements ISerializeableTopicPayload {
   BranchId branch;
   List<String> artifactUuids = new ArrayList<>();

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public List<String> getArtifactUuids() {
      return artifactUuids;
   }

   public void setArtifactUuids(List<String> artifactUuids) {
      this.artifactUuids = artifactUuids;
   }

   public void addArtifact(ArtifactId artifact) {
      addArtifact(artifact.getUuid());
   }

   public void addArtifact(Long artifactUuid) {
      getArtifactUuids().add(String.valueOf(artifactUuid));
   }

   public void addArtifact(Integer artifactId) {
      getArtifactUuids().add(String.valueOf(Long.valueOf(artifactId)));
   }

   /**
    * @return true if this artifact matches event branch and contains artifact
    */
   public boolean matches(ArtifactToken artifact) {
      if (!artifactUuids.isEmpty()) {
         return artifact.isOnBranch(branch) && artifactUuids.contains(artifact.getIdString());
      }
      return false;
   }
}