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
package org.eclipse.osee.framework.ui.data.model.editor.model.helper;

import java.util.List;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;

/**
 * @author Roberto E. Escobar
 */
public class PackageModel {
   private List<ArtifactDataType> artifacts;

   public PackageModel(List<ArtifactDataType> artifacts) {
      this.artifacts = artifacts;
   }

   /**
    * @return the artifacts
    */
   public List<ArtifactDataType> getArtifacts() {
      return artifacts;
   }

   /**
    * @param artifacts the artifacts to set
    */
   public void setArtifacts(List<ArtifactDataType> artifacts) {
      this.artifacts = artifacts;
   }

   public boolean hasArtifactTypes() {
      return getArtifacts().isEmpty();
   }

}
