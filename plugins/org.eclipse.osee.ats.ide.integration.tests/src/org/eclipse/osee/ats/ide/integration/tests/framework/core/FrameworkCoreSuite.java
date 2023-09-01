/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.framework.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for functionality under Framework Core.
 *
 * @author Loren K. Ashley
 */

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses
   (
      {
         FrameworkCoreAttributeTest.class
      }
   )
public class FrameworkCoreSuite {
   //no action
}
//@formatter:on

/* EOF */
