/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.test.db;

import org.eclipse.osee.ats.rest.test.db.internal.AtsTestDatabase;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Donald G. Dunne
 */
public class AtsMethodDatabase implements TestRule {

   @Override
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("Description cannot be null", description);
            AtsTestDatabase db = new AtsTestDatabase(description.getClassName(), description.getMethodName(), false);
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
