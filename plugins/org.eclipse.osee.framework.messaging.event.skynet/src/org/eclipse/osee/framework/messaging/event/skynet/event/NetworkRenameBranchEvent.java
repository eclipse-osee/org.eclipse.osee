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
 * Skynet messaging event; Should not be subscribed to by OSEE applications.
 * 
 * @author Donald G. Dunne
 */
public class NetworkRenameBranchEvent extends SkynetEventBase {
   private static final long serialVersionUID = 8339596149601997894L;
   private int branchId;
   private String branchName;
   private String shortName;

   public NetworkRenameBranchEvent(int branchId, NetworkSender networkSender, String branchName, String shortName) {
      super(networkSender);
      this.branchId = branchId;
      this.branchName = branchName;
      this.shortName = shortName;
   }

   public int getTransactionId() {
      return 0;
   }

   public int getId() {
      return branchId;
   }

   /**
    * @return the branchName
    */
   public String getBranchName() {
      return branchName;
   }

   /**
    * @param branchName the branchName to set
    */
   public void setBranchName(String branchName) {
      this.branchName = branchName;
   }

   /**
    * @return the shortName
    */
   public String getShortName() {
      return shortName;
   }

   /**
    * @param shortName the shortName to set
    */
   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   /**
    * @param branchId the branchId to set
    */
   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

}
