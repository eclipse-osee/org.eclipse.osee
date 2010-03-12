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
package org.eclipse.osee.framework.core.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public class MockOseeTransactionDataAccessor implements ITransactionDataAccessor {

   private boolean wasLoadCalled = false;
   private boolean wasStoreCalled = false;

   public void setLoadCalled(boolean wasLoadCalled) {
      this.wasLoadCalled = wasLoadCalled;
   }

   public void setStoreCalled(boolean wasStoreCalled) {
      this.wasStoreCalled = wasStoreCalled;
   }

   public boolean wasLoaded() {
      return wasLoadCalled;
   }

   public boolean wasStoreCalled() {
      return wasStoreCalled;
   }

   //   @Override
   //   public void load(AbstractOseeCache<T> cache) throws OseeCoreException {
   //      Assert.assertNotNull(cache);
   //      setLoadCalled(true);
   //   }
   //
   //   @Override
   //   public void store(Collection<T> types) throws OseeCoreException {
   //      Assert.assertNotNull(types);
   //      setStoreCalled(true);
   //   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      wasLoadCalled = true;
   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException {
   }

   @Override
   public TransactionRecord loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      return null;
   }

   @Override
   public void load(TransactionCache transactionCache) throws OseeCoreException {
   }

}
