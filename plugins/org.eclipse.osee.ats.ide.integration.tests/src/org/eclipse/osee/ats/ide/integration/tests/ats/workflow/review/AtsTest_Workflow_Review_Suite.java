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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review;

import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect.AtsXDefectValidatorTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect.ReviewDefectItemTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role.AtsXUserRoleValidatorTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role.UserRoleTest;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   GenerateReviewParticipationReportTest.class,
   AtsXDefectValidatorTest.class,
   ReviewDefectItemTest.class,
   AtsXUserRoleValidatorTest.class,
   UserRoleTest.class,
   DecisionReviewManagerTest.class,
   DecisionReviewDefinitionManagerTest.class,
   PeerToPeerReviewManagerTest.class,
   PeerReviewDefinitionManagerTest.class})

/**
 * This test suite contains tests that must be run against demo database
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Workflow_Review_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsTest_Workflow_Review_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Workflow_Review_Suite.class.getSimpleName());
   }
}
