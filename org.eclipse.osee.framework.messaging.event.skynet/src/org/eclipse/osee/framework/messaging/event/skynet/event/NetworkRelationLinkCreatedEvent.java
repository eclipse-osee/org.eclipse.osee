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
 * @author Jeff C. Phillips
 */
public class NetworkRelationLinkCreatedEvent extends SkynetRelationLinkEventBase {

   private static final long serialVersionUID = -519877422249674503L;

   private final String rationale;
   private final String descriptorName;
   private final int aOrder;
   private final int bOrder;

   public NetworkRelationLinkCreatedEvent(int gammaId, int branchId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, int relTypeId, NetworkSender networkSender, String rationale, int aOrder, int bOrder, String descriptorName) {
      super(gammaId, branchId, relId, artAId, artATypeId, artBId, artBTypeId, relTypeId, networkSender);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
      this.descriptorName = descriptorName;
   }

   public NetworkRelationLinkCreatedEvent(SkynetRelationLinkEventBase base, String rationale, int aOrder, int bOrder, String descriptorName) {
      super(base);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
      this.descriptorName = descriptorName;
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

   public String getDescriptorName() {
      return descriptorName;
   }

}
