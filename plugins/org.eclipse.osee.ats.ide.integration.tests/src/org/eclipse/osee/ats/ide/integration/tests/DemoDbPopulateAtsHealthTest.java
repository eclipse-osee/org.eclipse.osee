/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests;

import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.health.AtsHealthTestTest;
import org.junit.BeforeClass;

/**
 * Extension to validate ats database so test will run once after populate and once at end of the tests. Without this,
 * JUnit only runs test once.
 *
 * @author Donald G Dunne
 */
public class DemoDbPopulateAtsHealthTest extends AtsHealthTestTest {

   @BeforeClass
   public static void cleanup() {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }
}
