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
   private final int artBId;
   private int gammaId;

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artBId
    * @param author TODO
    */
   public SkynetRelationLinkEventBase(int relTypeId, int gammaId, int branchId, int transactionId, Integer relId, int artAId, int artBId, int author) {
      super(branchId, transactionId, author);
      this.relId = relId;
      this.artAId = artAId;
      this.artBId = artBId;
      this.gammaId = gammaId;
      this.relTypeId = relTypeId;
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
