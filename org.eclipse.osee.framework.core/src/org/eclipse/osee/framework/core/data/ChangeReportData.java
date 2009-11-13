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
package org.eclipse.osee.framework.core.data;



/**
 * @author Jeff C. Phillips
 */
public class ChangeReportData {
   private final TransactionRecord toTransactionRecord;
   private final TransactionRecord fromTransactionRecord;
   private final boolean isHistorical;

   public ChangeReportData(TransactionRecord toTransactionRecord,
         TransactionRecord fromTransactionRecord, boolean isHistorical) {
      super();
      this.toTransactionRecord = toTransactionRecord;
      this.fromTransactionRecord = fromTransactionRecord;
      
      this.isHistorical = isHistorical;
   }

   public TransactionRecord getToTransactionRecord() {
      return toTransactionRecord;
   }

   public TransactionRecord getFromTransactionRecord() {
      return fromTransactionRecord;
   }

   public boolean isHistorical() {
      return isHistorical;
   }
}
