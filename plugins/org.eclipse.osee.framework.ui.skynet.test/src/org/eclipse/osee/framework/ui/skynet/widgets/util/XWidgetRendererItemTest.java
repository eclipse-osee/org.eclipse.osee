/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link XWidgetRendererItem}
 *
 * @author Donald G. Dunne
 */
public class XWidgetRendererItemTest {

   @Test
   public void testSetGetName() {
      XWidgetRendererItem item = new XWidgetRendererItem(null);
      item.setName("My Name");

      Assert.assertEquals("My Name", item.getName());

      item.setName("this.that.one");

      Assert.assertEquals("this.that.one", item.getName());
   }

   @Test
   public void testSetGetStoreName() {
      XWidgetRendererItem item = new XWidgetRendererItem(null);
      item.setStoreName("My Name");

      Assert.assertEquals("My Name", item.getStoreName());

      item.setStoreName("this.that.one");

      Assert.assertEquals("this.that.one", item.getStoreName());
   }

}