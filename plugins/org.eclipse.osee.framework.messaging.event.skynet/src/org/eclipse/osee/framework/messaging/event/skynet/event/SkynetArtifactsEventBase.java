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
package org.eclipse.osee.framework.messaging.event.skynet.event;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class SkynetArtifactsEventBase extends SkynetEventBase implements ISkynetEvent {
   private static final long serialVersionUID = 7923550763258313718L;

   private final Collection<Integer> artifactTypeIds;
   private final Collection<Integer> artifactIds;
   private final int branchId;

   /**
    * @param branchId
    * @param artifactIds
    * @param artifactTypeIds
    * @param author
    */
   public SkynetArtifactsEventBase(int branchId, Collection<Integer> artifactIds, Collection<Integer> artifactTypeIds, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;
      this.artifactIds = artifactIds;
      this.artifactTypeIds = artifactTypeIds;
   }

   /**
    * @return the branchId
    */
   public int getId() {
      return branchId;
   }

   /**
    * @return the artifactTypeIds
    */
   public Collection<Integer> getArtifactTypeIds() {
      return artifactTypeIds;
   }

   /**
    * @return the artifactIds
    */
   public Collection<Integer> getArtifactIds() {
      return artifactIds;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkArtifactPurgeEvent) {
         return artifactIds.hashCode() == ((NetworkArtifactPurgeEvent) obj).getArtifactIds().hashCode() && artifactTypeIds.hashCode() == ((NetworkArtifactPurgeEvent) obj).getArtifactTypeIds().hashCode();
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return artifactIds.hashCode() + artifactTypeIds.hashCode();
   }

}
