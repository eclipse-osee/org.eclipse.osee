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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.event.ArtifactEvent;

/**
 * @author Robert A. Fisher
 */
public abstract class ArtifactModifiedEvent extends ArtifactEvent {

   private final ArtifactModType modType;
   public enum ArtifactModType {
      Deleted, Added, Changed, Reverted, Purged, PrePurge, PurgedFromBranch;
   };

   public static ArtifactModType getModType(String type) {
      for (ArtifactModType e : ArtifactModType.values())
         if (e.name().equals(type)) return e;
      return null;
   }

   /**
    * @param artifact
    * @param type
    * @param sender
    */
   public ArtifactModifiedEvent(Artifact artifact, ArtifactModType type, Object sender) {
      super(artifact, sender);
      this.modType = type;
   }

   public ArtifactModType getType() {
      return modType;
   }
}