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
package org.eclipse.osee.ats.impl.internal.model;

import org.eclipse.osee.ats.impl.internal.convert.ConvertWorkDefinitionToAtsDslTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ConvertWorkDefinitionToAtsDslTest.class,
   ModelUtilTest.class,
   WidgetOptionTest.class,
   StateDefinitionTest.class,
   WorkDefinitionTest.class,
   WidgetOptionHandlerTest.class,
   WidgetDefinitionTest.class,
   PeerReviewDefinitionTest.class,
   DecisionReviewOptionTest.class,
   DecisionReviewDefinitionTest.class,
   AbstractWorkDefItemTest.class,
   CompositeStateItemTest.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 *
 * @author Donald G. Dunne
 */
public class AtsCore_WorkDef_JT_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsCore_WorkDef_JT_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsCore_WorkDef_JT_Suite.class.getSimpleName());
   }
}
