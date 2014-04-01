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

   private long branchUuid;
   private long destinationBranchUuid;
   private BranchEventType eventType;
   private NetworkSender networkSender;

   public BranchEvent(BranchEventType branchEventType, long branchUuid) {
      this.branchUuid = branchUuid;
      this.eventType = branchEventType;
   }

   public BranchEvent(BranchEventType branchEventType, long sourceBranchUuid, long destinationBranchUuid) {
      this.branchUuid = sourceBranchUuid;
      this.destinationBranchUuid = destinationBranchUuid;
      this.eventType = branchEventType;
   }

   /**
    * Gets the value of the branchUuid property.
    * 
    * @return possible object is {@link String }
    */
   public long getBranchUuid() {
      return branchUuid;
   }

   // TODO: add comment to describe purpose of destinationBranch
   /**
    * Gets the value of the destinationBranchUuid property.
    * 
    * @return possible object is {@link String }
    */
   public long getDestinationBranchUuid() {
      return destinationBranchUuid;
   }

   /**
    * Sets the value of the BranchUuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setBranchUuid(long value) {
      this.branchUuid = value;
   }

   /**
    * Sets the value of the destinationBranchUuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setDestinationBranchUuid(long value) {
      this.destinationBranchUuid = value;
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
