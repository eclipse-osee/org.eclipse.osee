/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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