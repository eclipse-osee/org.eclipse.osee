/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityData {

   ArtifactId artifact = ArtifactId.SENTINEL;
   List<ApplicabilityToken> applIds = new LinkedList<ApplicabilityToken>();

   public ApplicabilityData() {
      // for jax-rs
   }

   public ArtifactId getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactId artifact) {
      this.artifact = artifact;
   }

   public List<ApplicabilityToken> getApplIds() {
      return applIds;
   }

   public void setApplIds(List<ApplicabilityToken> applIds) {
      this.applIds = applIds;
   }

}
