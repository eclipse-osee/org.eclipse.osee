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

package org.eclipse.osee.ats.core.workflow.state;

import org.eclipse.osee.ats.api.workdef.StateType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Unit for {@link SimpleTeamState}
 * 
 * @author Donald G. Dunne
 */
public class SimpleTeamStateTest {

   @Test
   public void testGetName() {
      SimpleTeamState state = new SimpleTeamState("Analyze", StateType.Working);
      Assert.assertEquals(StateType.Working, state.getStateType());
      Assert.assertEquals("Analyze", state.getName());
   }
}
