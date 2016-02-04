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
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TransactionRecordFactory implements IOseeTypeFactory {

   public TransactionRecord create(int transactionNumber, BranchId branch, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      return this.create(transactionNumber, branch.getUuid(), comment, timestamp, authorArtId, commitArtId, txType,
         null);
   }

   public TransactionRecord create(int transactionNumber, long branchUuid, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType, BranchCache branchCache) throws OseeCoreException {
      return new TransactionRecord(transactionNumber, branchUuid, comment, timestamp, authorArtId, commitArtId, txType,
         branchCache);
   }

   private TransactionRecord create(int transactionNumber, BranchCache branchCache) throws OseeCoreException {
      return new TransactionRecord(transactionNumber, branchCache);
   }

   public TransactionRecord createOrUpdate(TransactionCache txCache, int transactionNumber, long branchUuid, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType, BranchCache branchCache) throws OseeCoreException {
      Conditions.checkNotNull(txCache, "txCache");
      TransactionRecord record = txCache.getById(transactionNumber);
      if (record == null) {
         record =
            create(transactionNumber, branchUuid, comment, timestamp, authorArtId, commitArtId, txType, branchCache);
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

   public TransactionRecord getOrCreate(TransactionCache txCache, int transactionNumber, BranchCache branchCache) throws OseeCoreException {
      Conditions.checkNotNull(txCache, "txCache");
      TransactionRecord record = txCache.getById(transactionNumber);
      if (record == null) {
         record = create(transactionNumber, branchCache);
         txCache.cache(record);
      }
      return record;
   }
}