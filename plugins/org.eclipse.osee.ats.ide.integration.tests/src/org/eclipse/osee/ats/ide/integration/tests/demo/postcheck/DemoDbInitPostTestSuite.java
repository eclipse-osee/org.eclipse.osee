/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.demo.postcheck;

import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.AtsWorkDefinitionXWidgetTest;
import org.eclipse.osee.ats.ide.integration.tests.util.TestStopOnFailureSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Tests that require the populated demo database. */
@RunWith(TestStopOnFailureSuite.class)
@Suite.SuiteClasses({AtsWorkDefinitionXWidgetTest.class, WorkDefXWidgetFingerprintTest.class})
public class DemoDbInitPostTestSuite {
   // Suite marker
}
