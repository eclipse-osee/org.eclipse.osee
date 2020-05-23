/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.mocks;

import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class MockOseeDataAccessor<T> implements IOseeDataAccessor<T> {

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

   @Override
   public void load(IOseeCache<T> cache) {
      Assert.assertNotNull(cache);
      setLoadCalled(true);
   }
}