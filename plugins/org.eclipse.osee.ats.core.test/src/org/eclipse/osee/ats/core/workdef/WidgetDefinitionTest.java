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
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WidgetDef}
 *
 * @author Donald G. Dunne
 */
public class WidgetDefinitionTest {

   @Test
   public void testGetSetDescription() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(null, item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testToString() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals("[review][]", item.toString());
   }

   @Test
   public void testGetSetTooltip() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(null, item.getToolTip());
      item.setToolTip("desc");
      Assert.assertEquals("desc", item.getToolTip());
   }

   @Test
   public void testGetSetWidgetname() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals("", item.getXWidgetName());
      item.setXWidgetName("desc");
      Assert.assertEquals("desc", item.getXWidgetName());
   }

   @Test
   public void testGetSetDefaultName() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(null, item.getDefaultValue());
      item.setDefaultValue("desc");
      Assert.assertEquals("desc", item.getDefaultValue());
   }

   @Test
   public void testGetSetHeight() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertEquals(0, item.getHeight());
      item.setHeight(4);
      Assert.assertEquals(4, item.getHeight());
   }

   @Test
   public void testSet() {
      WidgetDef item = new WidgetDef("review");
      Assert.assertFalse(item.is(WidgetOption.ALIGN_CENTER));
      item.set(WidgetOption.ALIGN_CENTER);
      Assert.assertTrue(item.is(WidgetOption.ALIGN_CENTER));
   }

}
