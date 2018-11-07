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

import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class BranchEvent implements FrameworkEvent, HasNetworkSender {

   private final BranchId sourceBranch;
   private final BranchId destinationBranch;
   private final BranchEventType eventType;
   private NetworkSender networkSender;

   public BranchEvent(BranchEventType branchEventType, BranchId sourceBranch) {
      this(branchEventType, sourceBranch, null);
   }

   public BranchEvent(BranchEventType branchEventType, BranchId sourceBranchUuid, BranchId destinationBranch) {
      this.sourceBranch = sourceBranchUuid;
      this.destinationBranch = destinationBranch;
      this.eventType = branchEventType;
   }

   public BranchId getSourceBranch() {
      return sourceBranch;
   }

   public BranchId getDestinationBranch() {
      return destinationBranch;
   }

   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   @Override
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public BranchEventType getEventType() {
      return eventType;
   }
}
