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

package org.eclipse.osee.client.test.framework;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * If the database is in initialization mode, get out of initialization mode and re-authenticate as the test user. When
 * a JUnit test or group of tests is run by right clicking on the test or test group in the JUnit explorer instead of
 * running the entire test suite, the database will still be in initialization mode causing the test to fail.
 *
 * @author Loren K. Ashley
 */

public class ExitDatabaseInitializationRule implements TestRule {

   public ExitDatabaseInitializationRule() {
   }

   @Override
   public Statement apply(Statement base, Description desciption) {

      return new Statement() {
         @Override
         public void evaluate() throws Throwable {

            ExitDatabaseInitializationRule.exitDatabaseInit();

            base.evaluate();
         }
      };
   }

   /**
    * If the database is in initialization mode, get out of initialization mode and re-authenticate as the test user.
    */

   private static void exitDatabaseInit() {

      /*
       * When the test suite is run directly it will be in Database Initialization mode.
       */

      if (OseeProperties.isInDbInit()) {

         /*
          * Get out of database initialization mode and re-authenticate as the test user
          */

         OseeProperties.setInDbInit(false);
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
      }
   }
}

/* EOF */