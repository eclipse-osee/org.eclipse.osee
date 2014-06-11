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
package org.eclipse.osee.framework.core.message.test.mocks;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {

   private MockDataFactory() {
      // Utility Class
   }

   public static TransactionRecord createTransaction(int index, int branchUuid) {
      TransactionDetailsType type =
         TransactionDetailsType.values()[Math.abs(index % TransactionDetailsType.values().length)];
      int value = index;
      if (value == 0) {
         value++;
      }
      MockOseeDataAccessor<Long, Branch> accessor = new MockOseeDataAccessor<Long, Branch>();
      BranchCache cache = new BranchCache(accessor);
      return new TransactionRecord(value * 47, branchUuid, "comment_" + value, new Date(), value * 37, value * 42,
         type, cache);
   }

}
