/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsDecisionReviewDecisionStateItemTest.class,
   AtsDecisionReviewPrepareStateItemTest.class,
   AtsForceAssigneesToTeamLeadsStateItemTest.class,
   AtsPeerToPeerReviewPrepareStateItemTest.class,
   AtsPeerToPeerReviewReviewStateItemTest.class})
/**
 * This test suite contains tests that must be run against demo database
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_StateItem_Suite {
   // do nothing
}
