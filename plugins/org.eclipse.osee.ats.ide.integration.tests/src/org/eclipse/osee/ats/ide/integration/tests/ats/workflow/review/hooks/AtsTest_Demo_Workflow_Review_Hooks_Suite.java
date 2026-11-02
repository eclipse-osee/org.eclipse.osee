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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.hooks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsDecisionReviewDecisionStateHookTest.class,
   AtsDecisionReviewPrepareStateHookTest.class,
   AtsForceAssigneesToTeamLeadsStateHookTest.class,
   AtsPeerToPeerReviewPrepareStateHookTest.class,
   AtsPeerToPeerReviewReviewStateHookTest.class})
/**
 * This test suite contains tests that must be run against demo database
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Workflow_Review_Hooks_Suite {
   // do nothing
}
