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
   private final int artAId;
   private final int artATypeId;
   private final int artBId;
   private final int artBTypeId;
   private final String aFactoryName;
   private final String bFactoryName;
   private int gammaId;

   /**
    * @param branchId
    * @param transactionId
    * @param relId
    * @param artAId
    * @param artATypeId
    * @param artBId
    * @param artBTypeId
    * @param author TODO
    */
   public SkynetRelationLinkEventBase(int gammaId, int branchId, int transactionId, Integer relId, int artAId, int artATypeId, int artBId, int artBTypeId, String aFactoryName, String bFactoryName, int author) {
      super(branchId, transactionId, author);
      this.relId = relId;
      this.artAId = artAId;
      this.artATypeId = artATypeId;
      this.artBId = artBId;
      this.artBTypeId = artBTypeId;
      this.aFactoryName = aFactoryName;
      this.bFactoryName = bFactoryName;
      this.gammaId = gammaId;
   }

   /**
    * @return Returns the artAId.
    */
   public int getArtAId() {
      return artAId;
   }

   /**
    * @return Returns the artATypeId.
    */
   public int getArtATypeId() {
      return artATypeId;
   }

   /**
    * @return Returns the artBId.
    */
   public int getArtBId() {
      return artBId;
   }

   /**
    * @return Returns the artBTypeId.
    */
   public int getArtBTypeId() {
      return artBTypeId;
   }

   /**
    * @return Returns the relId.
    */
   public Integer getRelId() {
      return relId;
   }

   /**
    * @return Returns the aFactoryName.
    */
   public String getAFactoryName() {
      return aFactoryName;
   }

   /**
    * @return Returns the bFactoryName.
    */
   public String getBFactoryName() {
      return bFactoryName;
   }

   public Integer getGammaId() {
      return gammaId;
   }
}
