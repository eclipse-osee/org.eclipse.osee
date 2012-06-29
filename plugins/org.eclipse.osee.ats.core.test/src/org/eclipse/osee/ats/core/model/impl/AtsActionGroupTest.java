/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import java.util.Arrays;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.mock.MockWorkItem;
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
      AtsActionGroup group = new AtsActionGroup("do this");
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
      AtsActionGroup group = new AtsActionGroup("do this");

      MockWorkItem item1 = new MockWorkItem("item 1", "Endorse", StateType.Working);
      MockWorkItem item2 = new MockWorkItem("item 2", "Endorse", StateType.Working);
      group.setActions(Arrays.asList(item1, item2));
      Assert.assertEquals(2, group.getActions().size());

      group.setActions(Arrays.asList(item1));
      Assert.assertEquals(1, group.getActions().size());

   }

}
