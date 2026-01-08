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

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Listens for test failures and will call to top test suite run if found.
 *
 * @author Donald G. Dunne
 */
public class TestFailureListener extends RunListener {

   private final RunNotifier runNotifier;

   public TestFailureListener(RunNotifier runNotifier) {
      super();
      this.runNotifier = runNotifier;
   }

   @Override
   public void testFailure(Failure failure) throws Exception {
      super.testFailure(failure);
      this.runNotifier.pleaseStop();
   }
}