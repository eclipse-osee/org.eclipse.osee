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

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Abstract for Action tests that tests getImageDescriptor and calls cleanup before/after class
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsActionTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Throwable {
      AtsTestUtil.cleanup();
   }

   @Test
   public void getImageDescriptor() throws Exception {
      Action action = createAction();
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
   }

   public abstract Action createAction();
}
