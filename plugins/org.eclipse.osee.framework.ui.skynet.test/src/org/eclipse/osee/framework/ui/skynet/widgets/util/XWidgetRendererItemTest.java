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

import java.util.Arrays;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link XWidgetData}
 *
 * @author Donald G. Dunne
 */
public class XWidgetRendererItemTest {

   @Test
   public void testSetGetName() {
      XWidgetData item = new XWidgetData();
      item.setLabel("My Name");

      Assert.assertEquals("My Name", item.getLabel());

      item.setLabel("this.that.one");

      Assert.assertEquals("this.that.one", item.getLabel());
   }

   @Test
   public void testSelectableCollectionIsExpanded() {
      XWidgetService.get().addWidget(new XXTextWidget());
      XWidgetBuilder builder = new XWidgetBuilder();

      builder.andTextWidget("Selectable").andSelectable(Arrays.asList("one", "two"));

      Assert.assertEquals(Arrays.asList("one", "two"), builder.getXWidgetDatas().get(0).getSelectable());
   }

}
