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

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetArtifactEvent;

/**
 * @author Robert A. Fisher
 */
public class SkynetArtifactEventBase extends SkynetEventBase implements ISkynetArtifactEvent {
   private static final long serialVersionUID = 7923550763258313718L;

   private final int artId;
   private final int artTypeId;
   private final String factoryName;
   private final int transactionId;
   private final int branchId;

   /**
    * @param branchId
    * @param transactionId
    * @param artId
    * @param artTypeId
    * @param author
    */
   public SkynetArtifactEventBase(int branchId, int transactionId, int artId, int artTypeId, String factoryName, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;

      this.artId = artId;
      this.artTypeId = artTypeId;
      this.factoryName = factoryName;
      this.transactionId = transactionId;
   }

   /**
    * @return the branchId
    */
   public int getBranchId() {
      return branchId;
   }

   /**
    * @return Returns the transactionId.
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * @return Returns the artId.
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return Returns the artTypeId.
    */
   public int getArtTypeId() {
      return artTypeId;
   }

   public String getFactoryName() {
      return factoryName;
   }
}
