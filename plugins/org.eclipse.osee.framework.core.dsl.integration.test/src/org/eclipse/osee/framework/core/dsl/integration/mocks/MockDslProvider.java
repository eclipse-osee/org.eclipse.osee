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