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
package org.eclipse.osee.framework.resource.locator.attribute.test;

import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.resource.locator.attribute.HRIDCompatibility;

/**
 * @author Ryan Schmitt
 */
public class TestHRIDCompatibility extends TestCase {

   public void testHRIDfunctions() throws OseeDataStoreException {
      String[] tests = new String[6];
      tests[1] = "9HHVZ";
      tests[2] = "WCNG44";
      tests[3] = "EIEIO";
      tests[4] = "AAABGnVIZfEB8xmPJD8Aig";
      tests[5] = "AAABDBYmmqkBoNbJNWz2jg";
      HRIDCompatibility comp = new HRIDCompatibility();

      assertTrue(comp.isHRID(tests[1]));

      for (int i = 2; i <= 5; i++) {
         assertFalse(comp.isHRID(tests[i]));
      }

      assertEquals(comp.convertToGUID("1").length(), 22);
   }
}
