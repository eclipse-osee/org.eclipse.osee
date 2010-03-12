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
 * @author Robert A. Fisher
 */
public class NetworkArtifactModifiedEvent extends SkynetArtifactEventBase {
   private static final long serialVersionUID = -4325821466558180270L;
   private final Collection<SkynetAttributeChange> attributeValues;

   public NetworkArtifactModifiedEvent(int branchId, int transactionId, int artId, int artTypeId, String factoryName, NetworkSender networkSender, Collection<SkynetAttributeChange> attributeValues) {
      super(branchId, transactionId, artId, artTypeId, factoryName, networkSender);

      this.attributeValues = attributeValues;
   }

   public NetworkArtifactModifiedEvent(SkynetArtifactEventBase base, Collection<SkynetAttributeChange> attributeValues) {
      super(base);

      this.attributeValues = attributeValues;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkArtifactModifiedEvent) {
         return getArtId() == ((NetworkArtifactModifiedEvent) obj).getArtId();
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

   public Collection<SkynetAttributeChange> getAttributeChanges() {
      return attributeValues;
   }
}
