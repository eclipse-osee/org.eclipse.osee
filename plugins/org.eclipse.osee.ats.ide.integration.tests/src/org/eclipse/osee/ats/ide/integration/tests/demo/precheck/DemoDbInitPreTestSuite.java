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

package org.eclipse.osee.ats.ide.integration.tests.demo.precheck;

import org.eclipse.osee.ats.ide.integration.tests.util.TestStopOnFailureSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(TestStopOnFailureSuite.class)
@Suite.SuiteClasses({ManifestTest.class})

/**
 * Tests that can be performed prior to the full demo test suite. Example: Checking that all *.MF files are correct.
 *
 * @author Donald G. Dunne
 */
public class DemoDbInitPreTestSuite {
   // do nothing
}
