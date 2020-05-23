/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest;

import org.eclipse.osee.ats.rest.internal.util.AtsRest_Internal_Util_IntegrationSuite;
import org.eclipse.osee.ats.rest.util.AtsDabaseInitializedTest;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsServer_JUnit_TestSuite.class,
   AtsDabaseInitializedTest.class,
   AtsRest_Internal_Util_IntegrationSuite.class})
public class AtsServer_Integration_TestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsServer_Integration_TestSuite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsServer_Integration_TestSuite.class.getSimpleName());
   }
}
