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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.ats.test.AtsTest_TestDb_Suite;

/**
 * This Test Suite is to run against a postgres database with ATS Developer as the DbInit.<br>
 * <br>
 * Example test cases would be a test that purges or corrupts databaes data
 * 
 * @author Donald G. Dunne
 */
public class MasterTestSuite_TestDbTests extends TestSuite {

   public static Test suite() throws ClassNotFoundException {
      TestSuite suite = new TestSuite("OSEE Master Production TestDb Suite");

      suite.addTest(AtsTest_TestDb_Suite.suite());

      return suite;
   }

}
