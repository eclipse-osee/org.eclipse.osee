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
package org.eclipse.osee.coverage.test;

import org.eclipse.osee.coverage.test.model.CoverageItemTest;
import org.eclipse.osee.coverage.test.model.CoverageUnitTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {CoverageItemTest.class, CoverageUnitTest.class, MatchTypeTest.class})
/**
 * @author Donald G. Dunne
 */
public class Coverage_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
   }
}
