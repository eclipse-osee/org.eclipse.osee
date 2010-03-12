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

/**
 * @author Robert A. Fisher
 */
public class NetworkArtifactAddedEvent extends SkynetArtifactEventBase {
   private static final long serialVersionUID = -4325821466558180270L;

   public NetworkArtifactAddedEvent(int branchId, int transactionId, int artId, int artTypeId, String factoryName, NetworkSender networkSender) {
      super(branchId, transactionId, artId, artTypeId, factoryName, networkSender);
   }

   public NetworkArtifactAddedEvent(SkynetArtifactEventBase base) {
      super(base);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkArtifactAddedEvent) {
         return getArtId() == ((NetworkArtifactAddedEvent) obj).getArtId();
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getArtId();
   }

   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

}
