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
package org.eclipse.osee.support.test;

import java.util.logging.Level;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class OseeMasterProductionTestSuite {

   public static Test suite() throws ClassNotFoundException {
      TestSuite suite = new TestSuite("OSEE Master Test Suite.");

      for (Test test : OseeTests.getOseeTests(OseeTestType.Production)) {
         OseeLog.log(Activator.class, Level.INFO, "Adding Production OseeTest [" + test + "]");
         suite.addTest(test);
      }
      return suite;
   }

}
