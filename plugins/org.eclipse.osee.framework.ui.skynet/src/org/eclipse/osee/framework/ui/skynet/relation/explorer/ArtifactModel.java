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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ArtifactModel {

   private boolean add;
   private final boolean artifactFound;
   private final Artifact artifact;
   private String name;
   private ArtifactTypeToken descriptor;
   private String rationale;

   public ArtifactModel(String name, ArtifactTypeToken descriptor) {
      this(false, null, name, descriptor, "");
   }

   public ArtifactModel(Artifact artifact) {
      this(true, artifact, artifact.getName(), artifact.getArtifactType(), "");
   }

   private ArtifactModel(boolean artifactFound, Artifact artifact, String name, ArtifactTypeToken descriptor, String rationale) {
      this.add = true;
      this.artifactFound = artifactFound;
      this.artifact = artifact;
      this.name = name;
      this.descriptor = descriptor;
      this.rationale = rationale;
   }

   /**
    * @return Returns the artifact.
    */
   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return Returns the add.
    */
   public boolean isAdd() {
      return add;
   }

   /**
    * @param add The add to set.
    */
   public void setAdd(boolean add) {
      this.add = add;
   }

   /**
    * @return Returns the artifact.
    */
   public String getName() {
      return name;
   }

   /**
    * @param name - The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return Returns the descriptor.
    */
   public ArtifactTypeToken getDescriptor() {
      return descriptor;
   }

   /**
    * @param descriptor The descriptor to set.
    */
   public void setDescriptor(ArtifactTypeToken descriptor) {
      this.descriptor = descriptor;
   }

   /**
    * @return Returns the rationale.
    */
   public String getRationale() {
      return rationale;
   }

   /**
    * @param rationale The rationale to set.
    */
   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   /**
    * @return Returns the artifactFound.
    */
   public boolean isArtifactFound() {
      return artifactFound;
   }
}
