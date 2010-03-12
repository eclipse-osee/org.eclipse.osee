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
import org.eclipse.osee.framework.core.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TransactionRecordFactory implements IOseeTypeFactory {

   public TransactionRecordFactory() {
   }

   public TransactionRecord create(int transactionNumber, int branchId, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(transactionNumber < 1, "[%s] is not a valid transaction number",
            transactionNumber);
      Conditions.checkNotNull(timestamp, "timestamp");
      Conditions.checkNotNull(txType, "transaction type");
      return new TransactionRecord(transactionNumber, branchId, comment, timestamp, authorArtId, commitArtId, txType);
   }

   public TransactionRecord createOrUpdate(TransactionCache cache, int transactionNumber, int branchId, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      TransactionRecord record = cache.getById(transactionNumber);
      if (record == null) {
         record = create(transactionNumber, branchId, comment, timestamp, authorArtId, commitArtId, txType);
      } else {
         cache.decache(record);
         record.setAuthor(authorArtId);
         record.setComment(comment);
         record.setCommit(commitArtId);
         record.setTimeStamp(timestamp);
      }
      cache.cache(record);
      return record;
   }
}