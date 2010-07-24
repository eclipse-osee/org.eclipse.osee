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
package org.eclipse.osee.framework.ui.plugin.event;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class UnloadedArtifact {
   private int artifactId;
   private int branchId;
   private int artifactTypeId;

   public UnloadedArtifact(int branchId, int artifactId, int artifactTypeId) {
      this.branchId = branchId;
      this.artifactId = artifactId;
      this.artifactTypeId = artifactTypeId;
   }

   public int getArtifactId() {
      return artifactId;
   }

   public String getArtTypeGuid() throws OseeCoreException {
      return ArtifactTypeManager.getType(artifactTypeId).getGuid();
   }

   public String getBranchGuid() throws OseeCoreException {
      return BranchManager.getBranch(branchId).getGuid();
   }

   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   public int getBranchId() {
      return branchId;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public int getArtifactTypeId() {
      return artifactTypeId;
   }

   public void setArtifactTypeId(int artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

}
