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

   /**
    * @return the artifactId
    */
   public int getArtifactId() {
      return artifactId;
   }

   /**
    * @param artifactId the artifactId to set
    */
   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   /**
    * @return the branchId
    */
   public int getId() {
      return branchId;
   }

   /**
    * @param branchId the branchId to set
    */
   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   /**
    * @return the artifactTypeId
    */
   public int getArtifactTypeId() {
      return artifactTypeId;
   }

   /**
    * @param artifactTypeId the artifactTypeId to set
    */
   public void setArtifactTypeId(int artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

}
