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
package org.eclipse.osee.ats.impl.internal;

import org.eclipse.osee.ats.impl.internal.model.AtsCore_WorkDef_JT_Suite;
import org.eclipse.osee.ats.impl.internal.query.AtsImpl_Query_Suite;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtsCore_WorkDef_JT_Suite.class, AtsImpl_Query_Suite.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 * 
 * @author Donald G. Dunne
 */
public class AtsImpl_JT_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsImpl_JT_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsImpl_JT_Suite.class.getSimpleName());
   }
}
