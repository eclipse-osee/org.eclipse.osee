/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

public class BranchEvent implements FrameworkEvent, HasNetworkSender {

   private String branchGuid;
   private String destinationBranchGuid;
   private BranchEventType eventType;
   private NetworkSender networkSender;

   public BranchEvent(BranchEventType branchEventType, String branchGuid) {
      this.branchGuid = branchGuid;
      this.eventType = branchEventType;
   }

   public BranchEvent(BranchEventType branchEventType, String sourceBranchGuid, String destinationBranchGuid) {
      this.branchGuid = sourceBranchGuid;
      this.destinationBranchGuid = destinationBranchGuid;
      this.eventType = branchEventType;
   }

   /**
    * Gets the value of the branchGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getBranchGuid() {
      return branchGuid;
   }

   // TODO: add comment to describe purpose of destinationBranch
   /**
    * Gets the value of the destinationBranchGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getDestinationBranchGuid() {
      return destinationBranchGuid;
   }

   /**
    * Sets the value of the BranchGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setBranchGuid(String value) {
      this.branchGuid = value;
   }

   /**
    * Sets the value of the destinationBranchGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setDestinationBranchGuid(String value) {
      this.destinationBranchGuid = value;
   }

   /**
    * Gets the value of the networkSender property.
    * 
    * @return possible object is {@link NetworkSender }
    */
   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    * 
    * @param value allowed object is {@link NetworkSender }
    */
   @Override
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public BranchEventType getEventType() {
      return eventType;
   }

   public void setEventType(BranchEventType eventType) {
      this.eventType = eventType;
   }

}
