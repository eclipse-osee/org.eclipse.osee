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
public class NetworkRelationLinkModifiedEvent extends SkynetRelationLinkEventBase {
   private static final long serialVersionUID = 548299278567054333L;

   private String rationale;
   int aOrder;
   int bOrder;

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artBId
    * @param author
    * @param relTypeId
    */
   public NetworkRelationLinkModifiedEvent(int gammaId, int branchId, int transactionId, int relId, int artAId, int artBId, String rationale, int aOrder, int bOrder, int author, int relTypeId) {
      super(relTypeId, gammaId, branchId, transactionId, relId, artAId, artBId, author);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
   }

   /**
    * @return Returns the aOrder.
    */
   public int getAOrder() {
      return aOrder;
   }

   /**
    * @return Returns the bOrder.
    */
   public int getBOrder() {
      return bOrder;
   }

   /**
    * @return Returns the rationale.
    */
   public String getRationale() {
      return rationale;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkRelationLinkModifiedEvent) {
         return (getRelId() == (((NetworkRelationLinkModifiedEvent) obj).getRelId()));
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getRelId();
   }

}
