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
package org.eclipse.osee.ats.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurationTestSuite {

   public static Test suite() {
      TestSuite suite =
            new TestSuite(
                  "Test for org.eclipse.osee.ats.test.config - All ATS Tests - Can be run on either production or test databases");
      //$JUnit-BEGIN$
      suite.addTestSuite(AtsWorkItemDefinitionTest.class);
      suite.addTestSuite(AtsActionableItemToTeamDefinitionTest.class);
      suite.addTestSuite(AtsTeamDefintionToWorkflowTest.class);
      //$JUnit-END$
      return suite;
   }

}
