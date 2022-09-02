/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.synchronization;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Class of static methods for creating JUnit {@link TestRule}s for the test user.
 *
 * @author Loren K. Ashley
 */

public class TestUserRules {

   /**
    * Creates a {@link TestRule} that adds the test user to the OSEE Publishing group before the {@link Statement} is
    * executed and removes the test user from the OSEE Publishing group after the {@link Statement} has completed.
    *
    * @return a {@link TestRule} that ensures the test user is a member of the OSEE Publishing group.
    */

   public static TestRule createInPublishingGroupTestRule() {
      return new TestRule() {
         @Override
         public Statement apply(Statement base, Description description) {
            return new Statement() {
               @Override
               public void evaluate() throws Throwable {

                  TestUser.addTestUserToPublishingGroup();
                  TestUser.clearServerCache();

                  base.evaluate();

                  TestUser.removeTestUserFromPublishingGroup();
                  TestUser.clearServerCache();
               }
            };
         }
      };
   }

}

/* EOF */
