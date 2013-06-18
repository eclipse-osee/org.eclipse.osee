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
package org.eclipse.osee.orcs.db.mock;

import org.eclipse.osee.orcs.db.mock.internal.TestDatabase;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabase implements TestRule {

   private final String connectionId;

   public OseeDatabase(String connectionId) {
      this.connectionId = connectionId;
   }

   @Override
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("Connection Id cannot be null", connectionId);
            TestDatabase db = new TestDatabase(connectionId, description.getTestClass().getSimpleName(), description.getMethodName());
            try {
               db.initialize();
               base.evaluate();
            } finally {
               db.cleanup();
            }
         }
      };
   }
}
