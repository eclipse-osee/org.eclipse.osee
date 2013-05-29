/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.util;

import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;

/**
 * @author John Misinco
 */
public class IdUtil {

   private IdUtil() {
      // utility class
   }

   public static int getSourceTxId(CreateBranchData branchData, TransactionCache txCache) throws OseeCoreException {
      Conditions.checkNotNull(branchData, "branchData");
      Conditions.checkNotNull(txCache, "txCache");
      int sourceTransactionId = RelationalConstants.TRANSACTION_SENTINEL;

      if (BranchType.SYSTEM_ROOT != branchData.getBranchType()) {
         TransactionRecord sourceTx = txCache.getOrLoad(branchData.getFromTransaction().getGuid());
         sourceTransactionId = sourceTx.getId();
      }
      return sourceTransactionId;
   }

   public static int getParentBranchId(CreateBranchData newBranchData, TransactionCache txCache) throws OseeCoreException {
      int parentBranchId = RelationalConstants.BRANCH_SENTINEL;
      if (BranchType.SYSTEM_ROOT != newBranchData.getBranchType()) {
         TransactionRecord sourceTx = txCache.getOrLoad(newBranchData.getFromTransaction().getGuid());
         parentBranchId = sourceTx.getBranchId();
      }
      return parentBranchId;
   }

}
