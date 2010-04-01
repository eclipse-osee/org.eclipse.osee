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
package org.eclipse.osee.framework.messaging.event.res.event;

import org.eclipse.osee.framework.messaging.event.res.IFrameworkArtifactEvent;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class SkynetArtifactEventBase extends FrameworkEventBase implements IFrameworkArtifactEvent {
   private static final long serialVersionUID = 7923550763258313718L;

   private final int artId;
   private final int artTypeId;
   private final String factoryName;
   private final int transactionId;
   private final int branchId;
   private final String branchGuid;
   private final String artGuid;
   private final String artTypeGuid;

   public SkynetArtifactEventBase(int branchId, String branchGuid, int transactionId, int artId, String artGuid, int artTypeId, String artTypeGuid, String factoryName, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;
      this.branchGuid = branchGuid;
      this.artId = artId;
      this.artGuid = artGuid;
      this.artTypeId = artTypeId;
      this.artTypeGuid = artTypeGuid;
      this.factoryName = factoryName;
      this.transactionId = transactionId;
   }

   public SkynetArtifactEventBase(SkynetArtifactEventBase base) {
      super(base.getNetworkSender());
      this.branchId = base.branchId;
      this.branchGuid = base.branchGuid;
      this.artId = base.artId;
      this.artGuid = base.artGuid;
      this.artTypeId = base.artTypeId;
      this.artTypeGuid = base.artTypeGuid;
      this.factoryName = base.factoryName;
      this.transactionId = base.transactionId;
   }

   public int getBranchId() {
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

   @Override
   public String getArtGuid() {
      return artGuid;
   }

   @Override
   public String getArtTypeGuid() {
      return artTypeGuid;
   }

   @Override
   public String getBranchGuid() {
      return branchGuid;
   }

}
