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

import org.junit.Assert;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabase implements TestRule {
   private final String[] osgiBindings;

   public OseeDatabase(String... osgiBindings) {
      this.osgiBindings = osgiBindings;
   }

   public static TestRule integrationRule(Object testObject) {
      return RuleChain.outerRule(new OseeDatabase("orcs.jdbc.service")).around(new OsgiRule(testObject));
   }

   @Override
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("Osgi Binding cannot be null", osgiBindings);
            Assert.assertNotNull("Description cannot be null", description);
            Assert.assertTrue("Osgi Binding cannot be empty", osgiBindings.length > 0);
            TestDatabase db = new TestDatabase(description.getClassName(), description.getMethodName(), osgiBindings);
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
