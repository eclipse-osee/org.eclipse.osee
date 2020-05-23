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

package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   WorkPackageIdUtilityTest.class,
   WorkPackageNameUtilityTest.class,
   WorkPackageProgramUtilityTest.class,
   WorkPackageArtIdUtilityTest.class,
   WorkPackageTypeUtilityTest.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 *
 * @author Donald G. Dunne
 */
public class AtsCore_InternalColumnEv_JT_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsCore_InternalColumnEv_JT_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsCore_InternalColumnEv_JT_Suite.class.getSimpleName());
   }
}
