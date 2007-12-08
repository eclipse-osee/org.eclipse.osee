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
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.GuidEvent;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactVersionIncrementedEvent extends GuidEvent {
   private Artifact oldVersion;
   private Artifact newVersion;

   public ArtifactVersionIncrementedEvent(Artifact oldVersion, Artifact newVersion, Object sender) {
      super(sender);

      this.newVersion = newVersion;
      this.oldVersion = oldVersion;

      if (!oldVersion.getGuid().equals(newVersion.getGuid())) throw new IllegalArgumentException(
            "The old version artifact must have the same GUID as the new version artifact.");

      setGuid(newVersion.getGuid(), newVersion.getBranch());
   }

   /**
    * @return Returns the newVersion.
    */
   public Artifact getNewVersion() {
      return newVersion;
   }

   /**
    * @return Returns the oldVersion.
    */
   public Artifact getOldVersion() {
      return oldVersion;
   }

}
