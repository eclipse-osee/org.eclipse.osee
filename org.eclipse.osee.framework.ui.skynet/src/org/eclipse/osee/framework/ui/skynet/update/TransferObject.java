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
package org.eclipse.osee.framework.ui.skynet.update;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class TransferObject {
   private TransferStatus status;
   private Artifact artifact;

   public TransferObject(Artifact artifact, TransferStatus status) {
      super();
      this.status = status;
      this.artifact = artifact;
   }

   /**
    * @return the status
    */
   public TransferStatus getStatus() {
      return status;
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }
}
