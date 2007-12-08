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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactPersistenceMemo implements PersistenceMemo {

   private TransactionId transactionId;
   private int artId;
   private int gammaId;

   /**
    * @param gammaId
    * @param artId
    */
   public ArtifactPersistenceMemo(TransactionId transactionId, int artId, int gammaId) {
      this.transactionId = transactionId;
      this.artId = artId;
      this.gammaId = gammaId;
   }

   /**
    * @return Returns the artId.
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return Returns the gammaId.
    */
   public int getTransactionNumber() {
      return transactionId.getTransactionNumber();
   }

   public TransactionId getTransactionId() {
      return transactionId;
   }

   /**
    * @param transactionId The transactionId to set.
    */
   public void setTransactionId(TransactionId transactionId) {
      this.transactionId = transactionId;
   }

   /**
    * @return the gammaId
    */
   public int getGammaId() {
      return gammaId;
   }

   /**
    * @param gamma
    */
   public void setGammaId(int gamma) {
      this.gammaId = gamma;
   }
}
