/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.core.workflow.state.AtsCore_Workflow_State_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtsCore_Workflow_State_Suite.class, StateTypeTest.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 *
 * @author Donald G. Dunne
 */
public class AtsCore_Workflow_JT_Suite {
   // Test Suite
}
