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
package org.eclipse.osee.framework.core.model;

import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TransactionRecordFactory implements IOseeTypeFactory {

   public TransactionRecord create(int transactionNumber, BranchId branch, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      return new TransactionRecord(transactionNumber, branch, comment, timestamp, authorArtId, commitArtId, txType);
   }

   public TransactionRecord createOrUpdate(TransactionCache txCache, int transactionNumber, BranchId branch, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      Conditions.checkNotNull(txCache, "txCache");
      TransactionRecord record = txCache.getById(transactionNumber);
      if (record == null) {
         record = create(transactionNumber, branch, comment, timestamp, authorArtId, commitArtId, txType);
      } else {
         txCache.decache(record);
         record.setAuthor(authorArtId);
         record.setComment(comment);
         record.setCommit(commitArtId);
         record.setTimeStamp(timestamp);
      }
      txCache.cache(record);
      return record;
   }

   public TransactionRecord getOrCreate(TransactionCache txCache, int transactionNumber) throws OseeCoreException {
      Conditions.checkNotNull(txCache, "txCache");
      TransactionRecord record = txCache.getById(transactionNumber);
      if (record == null) {
         record = new TransactionRecord(transactionNumber);
         txCache.cache(record);
      }
      return record;
   }
}