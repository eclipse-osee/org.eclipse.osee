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

package org.eclipse.osee.ats.core.transition;

import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link TransitionResult}
 *
 * @author Donald G. Dunne
 */
public class TransitionResultTest {

   @Test
   public void testGetDetails() {
      TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER.getDetails();
   }

   @Test
   public void testToString() {
      TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER.toString();
   }

   @Test
   public void testGetException() {
      TransitionResult result = new TransitionResult("details", new OseeStateException("hello"));
      Assert.assertNotNull(result);
      Assert.assertNotNull(result.getException());
      Assert.assertNotNull(result.getDetails());
   }
}
