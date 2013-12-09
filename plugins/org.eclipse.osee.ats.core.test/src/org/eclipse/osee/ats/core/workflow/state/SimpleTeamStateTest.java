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
