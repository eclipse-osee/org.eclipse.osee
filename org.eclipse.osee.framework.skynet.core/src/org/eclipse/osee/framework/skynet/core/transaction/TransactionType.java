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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * MERGED will not be used the in database but will be replaced with the transaction_id of the change that was merged
 * 
 * @author Ryan D. Brooks
 */
public enum TransactionType {
   CREATED(-1), REVISED(-2), DELETED(-3), BRANCHED(-4), COMMITTED(-5), MERGED(-6);

   private int id;

   private TransactionType(int id) {
      this.id = id;
   }

   public int getId() {
      return id;
   }

   public static TransactionType getTransactionType(int id) {
      for (TransactionType transactionType : values()) {
         if (transactionType.getId() == id) {
            return transactionType;
         }
      }
      return null;
   }

   public static TransactionType convertModificationTypeToTransactionType(ModificationType modificationType) {
      TransactionType transactionType = null;

      switch (modificationType.getValue()) {
         case 1:
            transactionType = CREATED;
            break;
         case 2:
            transactionType = REVISED;
            break;
         case 3:
            transactionType = DELETED;
            break;
         default:
            break;
      }

      return transactionType;
   }
   
   public static ModificationType convertTransactionTypeToModificationType(TransactionType transactionType) {
	   ModificationType modificationType = null;

	      switch (transactionType.getId()) {
	         case -1:
	        	 modificationType = ModificationType.NEW;
	            break;
	         case -2:
	        	 modificationType = ModificationType.CHANGE;
	            break;
	         case -3:
	        	 modificationType = ModificationType.DELETE;
	            break;
	         case -4:
	        	 modificationType = ModificationType.CHANGE;
	            break;
	         case -5:
	        	 modificationType = ModificationType.CHANGE;
	            break;
	         default:
	            break;
	      }

	      return modificationType;
	   }
}