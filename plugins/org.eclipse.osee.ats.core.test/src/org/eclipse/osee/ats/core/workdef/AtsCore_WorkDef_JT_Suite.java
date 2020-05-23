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

package org.eclipse.osee.ats.core.workdef;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsWorkDefinitionServiceImplTest.class,
   StateEventTypeTest.class,
   StateColorTest.class,
   RuleDefinitionOptionTest.class,
   ReviewBlockTypeTest.class,
   AbstractWorkDefItemTest.class,
   CompositeStateItemTest.class,
   DecisionReviewDefinitionTest.class,
   DecisionReviewOptionTest.class,
   PeerReviewDefinitionTest.class,
   StateDefinitionTest.class,
   WidgetOptionHandlerTest.class,
   WidgetOptionTest.class,
   WidgetDefinitionTest.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 *
 * @author Donald G. Dunne
 */
public class AtsCore_WorkDef_JT_Suite {
   // Test Suite
}
