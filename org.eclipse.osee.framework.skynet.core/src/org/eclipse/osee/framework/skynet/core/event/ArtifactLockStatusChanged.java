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
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactLockStatusChanged extends Event {

   private Artifact artifact;

   public ArtifactLockStatusChanged(Object sender, Artifact artifact) {
      super(sender);

      this.artifact = artifact;
   }

   /**
    * @return Returns the artifact.
    */
   public Artifact getArtifact() {
      return artifact;
   }
}
