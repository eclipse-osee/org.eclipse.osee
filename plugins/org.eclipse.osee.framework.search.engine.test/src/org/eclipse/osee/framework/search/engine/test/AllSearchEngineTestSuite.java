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
package org.eclipse.osee.framework.search.engine.test;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.search.engine.test.attribute.AttributeTestSuite;
import org.eclipse.osee.framework.search.engine.test.internal.InternalTestSuite;
import org.eclipse.osee.framework.search.engine.test.language.LanguageTestSuite;
import org.eclipse.osee.framework.search.engine.test.utility.UtilityTestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
//@formatter:off   
   AttributeTestSuite.class,
   InternalTestSuite.class,
   LanguageTestSuite.class, 
   UtilityTestSuite.class, 
//@formatter:on   
})
public class AllSearchEngineTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
   }
}
