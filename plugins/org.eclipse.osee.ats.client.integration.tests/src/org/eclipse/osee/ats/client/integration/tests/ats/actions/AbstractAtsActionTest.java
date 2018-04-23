/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
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
