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

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class AtsDbInitTest {

   @Before
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsUtil.isProductionDb());
   }

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      DatabaseInitializationOperation.executeWithoutPrompting("ATS - Developer");
   }

}
