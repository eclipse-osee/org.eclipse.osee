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

import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WidgetDefinition}
 *
 * @author Donald G. Dunne
 */
public class WidgetDefinitionTest {

   @Test
   public void testToString() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals("[review][]", item.toString());
   }

   @Test
   public void testGetSetTooltip() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getToolTip());
      item.setToolTip("desc");
      Assert.assertEquals("desc", item.getToolTip());
   }

   @Test
   public void testGetSetWidgetId() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(WidgetId.SENTINEL, item.getWidgetId());
      item.setWidgetId(WidgetId.XArtEdAttrViewerWidget);
      Assert.assertEquals(WidgetId.XArtEdAttrViewerWidget, item.getWidgetId());

      Assert.assertTrue(WidgetId.SENTINEL.isInvalid());
      Assert.assertFalse(WidgetId.SENTINEL.isValid());

      Assert.assertFalse(WidgetId.XArtEdAttrViewerWidget.isInvalid());
      Assert.assertTrue(WidgetId.XArtEdAttrViewerWidget.isValid());
   }

   @Test
   public void testGetSetDefaultName() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getDefaultValue());
      item.setDefaultValue("desc");
      Assert.assertEquals("desc", item.getDefaultValue());
   }

   @Test
   public void testSet() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertFalse(item.is(WidgetOption.ALIGN_CENTER));
      item.set(WidgetOption.ALIGN_CENTER);
      Assert.assertTrue(item.is(WidgetOption.ALIGN_CENTER));
   }

}
