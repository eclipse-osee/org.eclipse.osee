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
 * @author Donald G. Dunne
 * @author Robert A. Fisher
 */
public class NetworkRelationLinkOrderModifiedEvent extends SkynetRelationLinkEventBase {
   private static final long serialVersionUID = 548299278567054333L;

   private final String rationale;
   int aOrder;
   int bOrder;

   public NetworkRelationLinkOrderModifiedEvent(int gammaId, int branchId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, int relTypeId, NetworkSender networkSender, String rationale, int aOrder, int bOrder) {
      super(gammaId, branchId, relId, artAId, artATypeId, artBId, artBTypeId, relTypeId, networkSender);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
   }

   public NetworkRelationLinkOrderModifiedEvent(SkynetRelationLinkEventBase base, String rationale, int aOrder, int bOrder) {
      super(base);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
   }

   public int getAOrder() {
      return aOrder;
   }

   public int getBOrder() {
      return bOrder;
   }

   public String getRationale() {
      return rationale;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkRelationLinkOrderModifiedEvent) {
         return getRelId() == ((NetworkRelationLinkOrderModifiedEvent) obj).getRelId();
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getRelId();
   }

}
