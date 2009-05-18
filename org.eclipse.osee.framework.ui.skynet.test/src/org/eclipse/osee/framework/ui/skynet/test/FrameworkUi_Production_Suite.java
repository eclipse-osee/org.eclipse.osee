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
package org.eclipse.osee.framework.ui.skynet.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.framework.ui.skynet.test.cases.OseeEmailTest;

/**
 * @author Donald G. Dunne
 */
public class FrameworkUi_Production_Suite extends TestSuite {

   public static Test suite() {
      TestSuite suite = new TestSuite("FrameworkUi_Production_Suite");
      //$JUnit-BEGIN$
      suite.addTestSuite(OseeEmailTest.class);
      //$JUnit-END$
      return suite;
   }

}
