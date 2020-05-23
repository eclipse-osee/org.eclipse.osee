/*********************************************************************
 * Copyright (c) 2019 Boeing
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
