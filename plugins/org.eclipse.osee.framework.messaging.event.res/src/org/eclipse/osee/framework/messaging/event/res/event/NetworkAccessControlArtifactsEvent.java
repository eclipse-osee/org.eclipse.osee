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
package org.eclipse.osee.framework.messaging.event.res.event;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public class NetworkAccessControlArtifactsEvent extends SkynetArtifactsEventBase {
   private static final long serialVersionUID = -4325821466558180270L;
   private final String accessControlModType;

   public String getAccessControlModTypeName() {
      return accessControlModType;
   }

   public NetworkAccessControlArtifactsEvent(String accessControlModType, int branchId, Collection<Integer> artifactIds, Collection<String> artifactGuids, Collection<Integer> artifactTypeIds, NetworkSender networkSender) {
      super(branchId, artifactIds, artifactGuids, artifactTypeIds, networkSender);
      this.accessControlModType = accessControlModType;
   }

   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

}
