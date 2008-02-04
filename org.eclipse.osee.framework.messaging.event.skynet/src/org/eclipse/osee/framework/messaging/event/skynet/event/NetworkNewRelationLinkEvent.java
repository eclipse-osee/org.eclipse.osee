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
 * @author Jeff C. Phillips
 */
public class NetworkNewRelationLinkEvent extends SkynetRelationLinkEventBase {

   /**
    * 
    */
   private static final long serialVersionUID = -519877422249674503L;

   private String rationale;
   private String aGuid;
   private String aHumanId;
   private String bHumanId;
   private String bGuid;
   private String descriptorName;
   private int aOrder;
   private int bOrder;
   private int relTypeId;

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artATypeId
    * @param artBId
    * @param artBTypeId
    * @param author TODO
    */
   public NetworkNewRelationLinkEvent(int gammaId, int branchId, int transactionId, Integer relId, int artAId, int artATypeId, int artBId, int artBTypeId, String rationale, int aOrder, int bOrder, int relTypeId, String aFactoryName, String bFactoryName, String aGuid, String bGuid, String aHuamnId, String bHuamnId, String descriptorName, int author) {
      super(gammaId, branchId, transactionId, relId, artAId, artATypeId, artBId, artBTypeId, aFactoryName,
            bFactoryName, author);

      this.rationale = rationale;
      this.aOrder = aOrder;
      this.bOrder = bOrder;
      this.relTypeId = relTypeId;
      this.aGuid = aGuid;
      this.bGuid = bGuid;
      this.aHumanId = aHuamnId;
      this.bHumanId = bHuamnId;
      this.descriptorName = descriptorName;
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

   /**
    * @return Returns the relTypeId.
    */
   public int getRelTypeId() {
      return relTypeId;
   }

   /**
    * @return Returns the aGuid.
    */
   public String getAGuid() {
      return aGuid;
   }

   /**
    * @return Returns the bGuid.
    */
   public String getBGuid() {
      return bGuid;
   }

   /**
    * @return Returns the aHuamnId.
    */
   public String getAHumanId() {
      return aHumanId;
   }

   /**
    * @return Returns the bHumanId.
    */
   public String getBHumanId() {
      return bHumanId;
   }

   public String getDescriptorName() {
      return descriptorName;
   }

}
