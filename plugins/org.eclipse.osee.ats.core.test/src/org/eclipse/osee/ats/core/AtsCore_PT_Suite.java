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
package org.eclipse.osee.ats.core;

import org.eclipse.osee.ats.core.notify.AtsCore_Notify_PT_Suite;
import org.eclipse.osee.ats.core.operation.AtsCore_Operation_PT_Suite;
import org.eclipse.osee.ats.core.review.AtsCore_Review_PT_Suite;
import org.eclipse.osee.ats.core.task.AtsCore_Task_PT_Suite;
import org.eclipse.osee.ats.core.util.AtsCore_Util_PT_Suite;
import org.eclipse.osee.ats.core.workflow.transition.AtsCore_Transition_PT_Suite;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTestUtilTest.class,
   AtsCore_Notify_PT_Suite.class,
   AtsCore_Operation_PT_Suite.class,
   AtsCore_Review_PT_Suite.class,
   AtsCore_Task_PT_Suite.class,
   AtsCore_Util_PT_Suite.class,
   AtsCore_Transition_PT_Suite.class})
/**
 * This test suite contains tests that must be run as PDE Junit (PT) through test launch config
 * 
 * @author Donald G. Dunne
 */
public class AtsCore_PT_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsCore_PT_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsCore_PT_Suite.class.getSimpleName());
   }
}
