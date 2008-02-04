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
package org.eclipse.osee.framework.messaging.event.skynet;

/**
 * Skynet messaging event; Should not be subscribed to by OSEE applications.
 * 
 * @author Donald G. Dunne
 */
public class NetworkRenameBranchEvent implements ISkynetEvent {
   private static final long serialVersionUID = 8339596149601997894L;
   private int branchId;
   private String branchName;
   private String shortName;
   private final int author;

   public NetworkRenameBranchEvent(int branchId, int author, String branchName, String shortName) {
      this.branchId = branchId;
      this.author = author;
      this.branchName = branchName;
      this.shortName = shortName;
   }

   public int getTransactionId() {
      return 0;
   }

   public int getBranchId() {
      return branchId;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object o) {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent#getAuthor()
    */
   public int getAuthor() {
      return author;
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
