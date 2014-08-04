/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.model;

/**
 * @author Angel Avila
 */

public class DispoConfig {

   private String type;
   private String name;
   private String resolutionType;
   private String resolutionTitle;
   private String peerNotes;
   private String customerNotes;

   public DispoConfig() {

   }

   public String getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public String getResolutionType() {
      return resolutionType;
   }

   public String getResolutionTitle() {
      return resolutionTitle;
   }

   public String getPeerNotes() {
      return peerNotes;
   }

   public String getCustomerNotes() {
      return customerNotes;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setResolutionType(String resolutionType) {
      this.resolutionType = resolutionType;
   }

   public void setResolutionTitle(String resolutionTitle) {
      this.resolutionTitle = resolutionTitle;
   }

   public void setPeerNotes(String peerNotes) {
      this.peerNotes = peerNotes;
   }

   public void setCustomerNotes(String customerNotes) {
      this.customerNotes = customerNotes;
   }
}
