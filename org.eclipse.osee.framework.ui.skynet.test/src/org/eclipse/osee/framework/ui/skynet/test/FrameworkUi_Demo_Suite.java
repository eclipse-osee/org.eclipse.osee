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
import org.eclipse.osee.framework.ui.skynet.test.cases.WordEditTest;
import org.eclipse.osee.framework.ui.skynet.test.nonproduction.InterArtifactDropTest;

/**
 * @author Donald G. Dunne
 */
public class FrameworkUi_Demo_Suite extends TestSuite {

   public static Test suite() {
      TestSuite suite = new TestSuite("FrameworkUi_Demo_Suite");
      //$JUnit-BEGIN$
      suite.addTestSuite(InterArtifactDropTest.class);
      suite.addTestSuite(WordEditTest.class);
      //      suite.addTestSuite(RebaselineArtifactTest.class);
      //$JUnit-END$
      return suite;
   }

}
