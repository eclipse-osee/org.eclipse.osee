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

package org.eclipse.osee.framework.ui.skynet.explorer;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
class ArtifactData {
   private Artifact artifact;

   ArtifactData(Artifact artifact) {
      this.artifact = artifact;
   }

   Artifact getArtifact() {
      return artifact;
   }

   void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }
}
