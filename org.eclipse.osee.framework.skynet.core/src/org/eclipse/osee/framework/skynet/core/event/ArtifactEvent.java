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
 * @author Donald G. Dunne
 */
public abstract class ArtifactEvent extends Event {
   private final Artifact artifact;

   public ArtifactEvent(Artifact artifact, Object sender) {
      super(sender);
      this.artifact = artifact;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ArtifactEvent) {
         return artifact.equals(((ArtifactEvent) obj).artifact);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return artifact.hashCode();
   }
}