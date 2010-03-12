/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCacheUpdateResponse {

   private final List<TransactionRecord> rows;

   public TransactionCacheUpdateResponse(List<TransactionRecord> rows) {
      this.rows = rows;
   }

   public List<TransactionRecord> getTxRows() {
      return rows;
   }

   public static TransactionCacheUpdateResponse fromCache(TransactionRecordFactory factory, Collection<TransactionRecord> types) throws OseeCoreException {
      List<TransactionRecord> rows = new ArrayList<TransactionRecord>();
      for (TransactionRecord tx : types) {
         rows.add(factory.create(tx.getId(), tx.getBranchId(), tx.getComment(), tx.getTimeStamp(), tx.getAuthor(),
               tx.getCommit(), tx.getTxType()));
      }
      return new TransactionCacheUpdateResponse(rows);
   }
}
