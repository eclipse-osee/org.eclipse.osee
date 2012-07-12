/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test.store;

import org.eclipse.osee.coverage.store.ITestUnitStore;
import org.eclipse.osee.coverage.store.TestUnitCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author John R. Misinco
 */
public class MockTestUnitStore implements ITestUnitStore {

   @SuppressWarnings("unused")
   @Override
   public void load(TestUnitCache cache) throws OseeCoreException {
      // do nothing
   }

   @SuppressWarnings("unused")
   @Override
   public void store(TestUnitCache cache) throws OseeCoreException {
      // do nothing
   }

}
