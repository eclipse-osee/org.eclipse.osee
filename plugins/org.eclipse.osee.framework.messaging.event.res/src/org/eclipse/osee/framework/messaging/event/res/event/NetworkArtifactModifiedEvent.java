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
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class NetworkArtifactModifiedEvent extends SkynetArtifactEventBase {
   private static final long serialVersionUID = -4325821466558180270L;
   private final Collection<AttributeChange> attributeValues;

   public NetworkArtifactModifiedEvent(int branchId, String branchGuid, int transactionId, int artId, String artGuid, int artTypeId, String artTypeGuid, String factoryName, NetworkSender networkSender, Collection<AttributeChange> attributeValues) {
      super(branchId, branchGuid, transactionId, artId, artGuid, artTypeId, artTypeGuid, factoryName, networkSender);

      this.attributeValues = attributeValues;
   }

   public NetworkArtifactModifiedEvent(SkynetArtifactEventBase base, Collection<AttributeChange> attributeValues) {
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

   public Collection<AttributeChange> getAttributeChanges() {
      return attributeValues;
   }
}
