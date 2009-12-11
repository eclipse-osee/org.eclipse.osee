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
import junit.framework.Assert;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IOseeStorable;

/**
 * @author Roberto E. Escobar
 */
public class MockOseeDataAccessor<T extends IOseeStorable> implements IOseeDataAccessor<T> {

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
   public void load(IOseeCache<T> cache) throws OseeCoreException {
      Assert.assertNotNull(cache);
      setLoadCalled(true);
   }

   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      Assert.assertNotNull(types);
      setStoreCalled(true);
   }

}
