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
package org.eclipse.osee.ats.test;

import org.eclipse.osee.ats.test.editor.stateItem.AtsDecisionReviewDecisionStateItemTest;
import org.eclipse.osee.ats.test.editor.stateItem.AtsDecisionReviewPrepareStateItemTest;
import org.eclipse.osee.ats.test.editor.stateItem.AtsForceAssigneesToTeamLeadsStateItemTest;
import org.eclipse.osee.ats.test.editor.stateItem.AtsHandleAddReviewRuleStateItemTest;
import org.eclipse.osee.ats.test.editor.stateItem.AtsPeerToPeerReviewPrepareStateItemTest;
import org.eclipse.osee.ats.test.editor.stateItem.AtsPeerToPeerReviewReviewStateItemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsForceAssigneesToTeamLeadsStateItemTest.class,
   AtsPeerToPeerReviewReviewStateItemTest.class,
   AtsPeerToPeerReviewPrepareStateItemTest.class,
   AtsDecisionReviewDecisionStateItemTest.class,
   AtsDecisionReviewPrepareStateItemTest.class,
   AtsHandleAddReviewRuleStateItemTest.class})
/**
 * This test suite contains tests that must be run against demo database
 * 
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_StateItem_Suite {
   // test provided above
}
