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
package org.eclipse.osee.ats.core.model.impl;

import java.util.Arrays;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AtsActionGroup}
 *
 * @author Donald G. Dunne
 */
public class AtsActionGroupTest {

   @Test
   public void testGetFirstAction() {
      AtsActionGroup group = new AtsActionGroup(GUID.create(), "do this", Lib.generateId());
      Assert.assertEquals(0, group.getActions().size());
      Assert.assertEquals(null, group.getFirstAction());

      MockWorkItem item1 = new MockWorkItem("item 1", "Endorse", StateType.Working);
      MockWorkItem item2 = new MockWorkItem("item 2", "Endorse", StateType.Working);
      group.addAction(item1);
      group.addAction(item2);
      Assert.assertEquals(2, group.getActions().size());
      Assert.assertEquals(item1, group.getFirstAction());
   }

   @Test
   public void testSetActions() {
      AtsActionGroup group = new AtsActionGroup(GUID.create(), "do this", Lib.generateId());

      MockWorkItem item1 = new MockWorkItem("item 1", "Endorse", StateType.Working);
      MockWorkItem item2 = new MockWorkItem("item 2", "Endorse", StateType.Working);
      group.setActions(Arrays.asList(item1, item2));
      Assert.assertEquals(2, group.getActions().size());

      group.setActions(Arrays.asList(item1));
      Assert.assertEquals(1, group.getActions().size());

   }

}
