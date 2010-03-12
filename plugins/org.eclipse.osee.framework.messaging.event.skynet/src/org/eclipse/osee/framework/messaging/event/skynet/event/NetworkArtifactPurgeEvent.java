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

/**
 * @author Donald G. Dunne
 */
public class NetworkArtifactPurgeEvent extends SkynetArtifactsEventBase {
   private static final long serialVersionUID = -4325821466558180270L;

   /**
    * @param branchId
    * @param artifactIds
    * @param toArtifactTypeId
    * @param author
    */
   public NetworkArtifactPurgeEvent(int branchId, Collection<Integer> artifactIds, Collection<Integer> artifactTypeIds, NetworkSender networkSender) {
      super(branchId, artifactIds, artifactTypeIds, networkSender);
   }

   /**
    * @return Returns the serialVersionUID.
    */
   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

}
