/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

/**
 * @author Baily E. Roberts
 */
package org.eclipse.osee.ats.ide.integration.tests;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.ide.integration.tests.define.FixAttributeOperationTest;
import org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.OpenBlamsTest;
import org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic.ActionEndpointTest;
import org.eclipse.osee.ats.ide.integration.tests.ui.skynet.RelationIntegrityCheckTest;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ParallelTestSuite {

   @Test
   public void runSuite() {
      Result parallel = JUnitCore.runClasses(new ParallelComputer(true, false),
         new Class[] {
            OpenBlamsTest.class,
            FixAttributeOperationTest.class,
            ActionEndpointTest.class,
            RelationIntegrityCheckTest.class,});
      assertTrue(String.valueOf(parallel.getFailures()), parallel.wasSuccessful());

   }
}
