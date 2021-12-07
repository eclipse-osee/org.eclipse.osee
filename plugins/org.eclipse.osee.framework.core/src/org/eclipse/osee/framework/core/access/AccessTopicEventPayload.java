/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.core.access;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Payload for the Access Topic Event
 *
 * @author Donald G. Dunne
 */
public class AccessTopicEventPayload {
   BranchId branch;
   List<ArtifactToken> artifactTokens = new ArrayList<>();

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public List<ArtifactToken> getArtifactTokens() {
      return artifactTokens;
   }

   public void setArtifactTokens(List<ArtifactToken> artifactTokens) {
      this.artifactTokens = artifactTokens;
   }

   public void addArtifact(ArtifactToken artifactToken) {
      this.artifactTokens.add(artifactToken);
   }

   /**
    * @return true if this artifact matches event branch and contains artifact
    */
   public boolean matches(ArtifactToken artifactToken) {
      if (!artifactTokens.isEmpty()) {
         return artifactToken.isOnBranch(branch) && artifactTokens.contains(artifactToken);
      }
      return false;
   }
}