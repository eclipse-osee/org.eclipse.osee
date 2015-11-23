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
