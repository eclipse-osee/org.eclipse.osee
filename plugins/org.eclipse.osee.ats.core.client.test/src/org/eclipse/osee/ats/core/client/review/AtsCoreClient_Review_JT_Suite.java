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
package org.eclipse.osee.ats.core.client.review;

import org.eclipse.osee.ats.core.client.review.defect.AtsXDefectValidatorTest;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItemTest;
import org.eclipse.osee.ats.core.client.review.role.AtsXUserRoleValidatorTest;
import org.eclipse.osee.ats.core.client.review.role.UserRoleTest;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ReviewDefectItemTest.class,
   AtsXDefectValidatorTest.class,
   UserRoleTest.class,
   AtsXUserRoleValidatorTest.class})
/**
 * This test suite contains tests that must be run against demo database as Plugin JUnit (PT)
 *
 * @author Donald G. Dunne
 */
public class AtsCoreClient_Review_JT_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsCoreClient_Review_JT_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsCoreClient_Review_JT_Suite.class.getSimpleName());
   }
}
