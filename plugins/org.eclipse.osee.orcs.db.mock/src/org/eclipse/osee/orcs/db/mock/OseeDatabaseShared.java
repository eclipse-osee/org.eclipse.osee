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

import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.orcs.db.mock.internal.TestDatabase;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabaseShared implements TestRule {

   private final String connectionId;
   private final AtomicBoolean isFirstTime = new AtomicBoolean(true);

   private TestDatabase db;

   public OseeDatabaseShared(String connectionId) {
      this.connectionId = connectionId;
   }

   @Override
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {

         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("Connection Id cannot be null", connectionId);

            if (isFirstTime.compareAndSet(true, false)) {
               String className = description.getTestClass().getSimpleName();
               db = new TestDatabase(connectionId, className, className);
               db.initialize();
            }
            base.evaluate();
         }
      };
   }

   public void cleanup() {
      if (db != null) {
         db.cleanup();
      }
   }

}
