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

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class NetworkArtifactAddedEvent extends SkynetArtifactEventBase {
   private static final long serialVersionUID = -4325821466558180270L;

   public NetworkArtifactAddedEvent(int branchId, String branchGuid, int transactionId, int artId, String artGuid, int artTypeId, String artTypeGuid, String factoryName, NetworkSender networkSender) {
      super(branchId, branchGuid, transactionId, artId, artGuid, artTypeId, artTypeGuid, factoryName, networkSender);
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
