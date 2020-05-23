/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.orcs.db.mock;

import org.junit.Assert;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabase implements TestRule {

   public static TestRule integrationRule(Object testObject) {
      return RuleChain.outerRule(new OseeDatabase()).around(new OsgiRule(testObject));
   }

   @Override
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("Description cannot be null", description);
            TestDatabase db = new TestDatabase(description.getClassName(), description.getMethodName());
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
