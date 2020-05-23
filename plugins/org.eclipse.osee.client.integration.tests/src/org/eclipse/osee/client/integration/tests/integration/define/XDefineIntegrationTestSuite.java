/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.define;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   FixAttributeOperationTest.class,
   ImportTraceUnitsOperationTest.class,
   TestPlanComplianceReportTest.class})
/**
 * @author Ryan D. Brooks
 */
public class XDefineIntegrationTestSuite {
   // Test Suite
}
