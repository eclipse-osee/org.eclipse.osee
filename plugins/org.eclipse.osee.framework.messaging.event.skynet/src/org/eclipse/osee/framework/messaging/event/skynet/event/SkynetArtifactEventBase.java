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

   public SkynetArtifactEventBase(int branchId, int transactionId, int artId, int artTypeId, String factoryName, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;

      this.artId = artId;
      this.artTypeId = artTypeId;
      this.factoryName = factoryName;
      this.transactionId = transactionId;
   }

   public SkynetArtifactEventBase(SkynetArtifactEventBase base) {
      super(base.getNetworkSender());

      this.branchId = base.branchId;

      this.artId = base.artId;
      this.artTypeId = base.artTypeId;
      this.factoryName = base.factoryName;
      this.transactionId = base.transactionId;

   }

   public int getId() {
      return branchId;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public int getArtId() {
      return artId;
   }

   public int getArtTypeId() {
      return artTypeId;
   }

   public String getFactoryName() {
      return factoryName;
   }
}
