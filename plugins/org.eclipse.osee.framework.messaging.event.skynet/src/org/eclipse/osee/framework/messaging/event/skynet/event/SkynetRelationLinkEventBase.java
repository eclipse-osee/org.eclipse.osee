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

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetRelationLinkEvent;

/**
 * @author Robert A. Fisher
 */
public class SkynetRelationLinkEventBase extends SkynetEventBase implements ISkynetRelationLinkEvent {
   private static final long serialVersionUID = 154870900652138769L;

   private final Integer relId;
   private final int relTypeId;
   private final int artAId;
   private final int artATypeId;
   private final int artBId;
   private final int artBTypeId;
   private final int gammaId;
   private final int branchId;

   public SkynetRelationLinkEventBase(int gammaId, int branchId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, int relTypeId, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;
      this.relId = relId;
      this.artAId = artAId;
      this.artATypeId = artATypeId;
      this.artBId = artBId;
      this.gammaId = gammaId;
      this.relTypeId = relTypeId;
      this.artBTypeId = artBTypeId;
   }

   public SkynetRelationLinkEventBase(SkynetRelationLinkEventBase base) {
      super(base.getNetworkSender());
      this.branchId = base.branchId;
      this.relId = base.relId;
      this.artAId = base.artAId;
      this.artATypeId = base.artATypeId;
      this.artBId = base.artBId;
      this.gammaId = base.gammaId;
      this.relTypeId = base.relTypeId;
      this.artBTypeId = base.artBTypeId;
   }

   @Override
   public int getId() {
      return branchId;
   }

   @Override
   public int getArtATypeId() {
      return artATypeId;
   }

   @Override
   public int getArtBTypeId() {
      return artBTypeId;
   }

   @Override
   public int getArtAId() {
      return artAId;
   }

   @Override
   public int getArtBId() {
      return artBId;
   }

   @Override
   public Integer getRelId() {
      return relId;
   }

   @Override
   public Integer getGammaId() {
      return gammaId;
   }

   @Override
   public int getRelTypeId() {
      return relTypeId;
   }
}
