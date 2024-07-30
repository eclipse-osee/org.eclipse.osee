/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class PostgresOnlyRule implements TestRule {

   @Override
   public Statement apply(Statement base, Description description) {
      if (description.getAnnotation(PostgresOnly.class) != null || description.getTestClass().getAnnotation(
         PostgresOnly.class) != null) {
         if (!"true".equalsIgnoreCase(System.getProperty("postgresqlDB"))) {
            return new Statement() {
               @Override
               public void evaluate() {
                  System.out.println(
                     "Skipping " + description.getClassName() + " beacuse it is not running in PostgreSQL");
               }
            };
         }
      }
      return base;
   }
}