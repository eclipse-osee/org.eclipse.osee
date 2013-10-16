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
package org.eclipse.osee.ats.core.internal.state;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit Test for {@link StateManager}
 * 
 * @author Donald G. Dunne
 */
public class StateManagerUnitTest {

   @Test
   public void testIsHoursEqual() {
      IAtsWorkItem awa = Mockito.mock(IAtsWorkItem.class);
      StateManager mgr = new StateManager(awa);

      assertTrue(mgr.isHoursEqual(1.0, 1.0));
      assertTrue(mgr.isHoursEqual(01.0, 1.0));
      assertTrue(mgr.isHoursEqual(01.0, 1.000));
      assertTrue(mgr.isHoursEqual(1.0, 1.001));

      assertFalse(mgr.isHoursEqual(1.0, 1.01));
      assertFalse(mgr.isHoursEqual(1.0, -1.001));
      assertFalse(mgr.isHoursEqual(-1.0, 1.01));
      assertFalse(mgr.isHoursEqual(2, 4));
   }

   @Test
   public void testSetMetrics() throws OseeCoreException {
      IAtsWorkItem awa = mock(IAtsWorkItem.class);
      StateManager mgr = spy(new StateManager(awa));

      doNothing().when(mgr).load();

      WorkStateProvider stateProvider = mock(WorkStateProvider.class);
      when(mgr.getStateProvider()).thenReturn(stateProvider);

      IAtsStateDefinition state = mock(IAtsStateDefinition.class);

      when(state.getName()).thenReturn("Endorse");
      when(stateProvider.getHoursSpent("Endorse")).thenReturn(1.0);
      when(stateProvider.getPercentComplete("Endorse")).thenReturn(46);

      assertFalse(mgr.setMetricsIfChanged(state, 1.0, 46));
      verify(stateProvider, Mockito.never()).setHoursSpent(state.getName(), 1.0);

      assertFalse(mgr.setMetricsIfChanged(state, 1.001, 46));

      assertTrue(mgr.setMetricsIfChanged(state, 1.1, 46));
      assertTrue(mgr.setMetricsIfChanged(state, 1.0, 47));
   }
}
