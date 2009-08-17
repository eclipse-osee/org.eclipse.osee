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

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artBId
    * @param networkSender
    */
   public SkynetRelationLinkEventBase(int relTypeId, int gammaId, int branchId, Integer relId, int artAId, int artATypeId, int artBId, int artBTypeId, NetworkSender networkSender) {
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

   /**
    * @return the branchId
    */
   public int getBranchId() {
      return branchId;
   }

   /**
    * @return the artATypeId
    */
   public int getArtATypeId() {
      return artATypeId;
   }

   /**
    * @return the artBTypeId
    */
   public int getArtBTypeId() {
      return artBTypeId;
   }

   /**
    * @return Returns the artAId.
    */
   public int getArtAId() {
      return artAId;
   }

   /**
    * @return Returns the artBId.
    */
   public int getArtBId() {
      return artBId;
   }

   /**
    * @return Returns the relId.
    */
   public Integer getRelId() {
      return relId;
   }

   public Integer getGammaId() {
      return gammaId;
   }

   /**
    * @return the relTypeId
    */
   public int getRelTypeId() {
      return relTypeId;
   }
}
