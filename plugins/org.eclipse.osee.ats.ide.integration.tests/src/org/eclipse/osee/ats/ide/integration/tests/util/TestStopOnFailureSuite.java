/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.util;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * Will check for test failures and stop full test run. This can be used for test suites that must pass, eg: DemoDbInit,
 * before other tests can run.
 *
 * @author Donald G. Dunne
 */
public class TestStopOnFailureSuite extends Suite {

   public TestStopOnFailureSuite(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
      super(klass, suiteClasses);
   }

   public TestStopOnFailureSuite(Class<?> klass) throws InitializationError {
      super(klass, klass.getAnnotation(SuiteClasses.class).value());
   }

   @Override
   public void run(RunNotifier runNotifier) {
      runNotifier.addListener(new TestFailureListener(runNotifier));
      super.run(runNotifier);
   }
}
