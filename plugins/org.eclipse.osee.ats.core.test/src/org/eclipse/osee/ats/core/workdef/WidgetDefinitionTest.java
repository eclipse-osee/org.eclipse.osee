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

package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WidgetDef} Test case for {@link WidgetDef}
 *
 * @author Donald G. Dunne
 */
public class WidgetDefinitionTest {

   @Test
   public void testToString() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertTrue(item.toString().contains("[review]"));
   }

   @Test
   public void testGetSetTooltip() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(null, item.getToolTip());
      item.setToolTip("desc");
      Assert.assertEquals("desc", item.getToolTip());
   }

   @Test
   public void testGetSetWidgetId() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(WidgetId.SENTINEL, item.getWidgetId());
      item.setWidgetId(WidgetId.XXArtEdAttrViewerWidget);
      Assert.assertEquals(WidgetId.XXArtEdAttrViewerWidget, item.getWidgetId());

      Assert.assertTrue(WidgetId.SENTINEL.isInvalid());
      Assert.assertFalse(WidgetId.SENTINEL.isValid());

      Assert.assertFalse(WidgetId.XXArtEdAttrViewerWidget.isInvalid());
      Assert.assertTrue(WidgetId.XXArtEdAttrViewerWidget.isValid());
   }

   @Test
   public void testGetSetDefaultName() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(null, item.getDefaultValue());
      item.setDefaultValue("desc");
      Assert.assertEquals("desc", item.getDefaultValue());
   }

   @Test
   public void testSet() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertFalse(item.is(XOption.ALIGN_CENTER));
      item.set(XOption.ALIGN_CENTER);
      Assert.assertTrue(item.is(XOption.ALIGN_CENTER));
   }

}
