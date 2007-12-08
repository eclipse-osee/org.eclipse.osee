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

/**
 * @author Jeff C. Phillips
 */
public class CacheArtifactModifiedEvent extends ArtifactModifiedEvent {

   /**
    * @param artifact
    * @param type
    * @param sender
    */
   public CacheArtifactModifiedEvent(Artifact artifact, ModType type, Object sender) {
      super(artifact, type, sender);
   }

   /**
    * @param guid
    * @param branch TODO
    * @param type
    * @param sender
    */
   public CacheArtifactModifiedEvent(String guid, Branch branch, ModType type, Object sender) {
      super(guid, branch, type, sender);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CacheArtifactModifiedEvent) {
         return (getGuid().equals(((CacheArtifactModifiedEvent) obj).getGuid()));
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

}
