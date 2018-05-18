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
package org.eclipse.osee.framework.core.dsl.integration.mocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;

/**
 * @author Roberto E. Escobar
 */
public class MockDslProvider implements OseeDslProvider {

   private final OseeDsl dslToReturn;

   private boolean wasLoadDslCalled;
   private boolean wasGetDslCalled;
   private OseeDsl dslToStore;

   public MockDslProvider(OseeDsl dslToReturn) {
      this.dslToReturn = dslToReturn;
   }

   @Override
   public void loadDsl() {
      wasLoadDslCalled = true;
   }

   @Override
   public OseeDsl getDsl() {
      wasGetDslCalled = true;
      return dslToReturn;
   }

   @Override
   public void storeDsl(OseeDsl dsl) {
      dslToStore = dsl;
   }

   public OseeDsl getDslToStore() {
      return dslToStore;
   }

   public boolean wasLoadDslCalled() {
      return wasLoadDslCalled;
   }

   public boolean wasGetDslCalled() {
      return wasGetDslCalled;
   }

   @Override
   public Map<String, Long> getContextGuidToIdMap() {
      return new ConcurrentHashMap<>();
   }

}