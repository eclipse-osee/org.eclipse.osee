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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WidgetDefinition}
 *
 * @author Donald G. Dunne
 */
public class WidgetDefinitionTest {

   @Test
   public void testGetSetDescription() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testToString() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals("[review][null]", item.toString());
   }

   @Test
   public void testGetSetAttribute() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getAtrributeName());
      item.setAttributeName("desc");
      Assert.assertEquals("desc", item.getAtrributeName());
   }

   @Test
   public void testGetSetTooltip() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getToolTip());
      item.setToolTip("desc");
      Assert.assertEquals("desc", item.getToolTip());
   }

   @Test
   public void testGetSetWidgetname() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getXWidgetName());
      item.setXWidgetName("desc");
      Assert.assertEquals("desc", item.getXWidgetName());
   }

   @Test
   public void testGetSetDefaultName() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(null, item.getDefaultValue());
      item.setDefaultValue("desc");
      Assert.assertEquals("desc", item.getDefaultValue());
   }

   @Test
   public void testGetSetHeight() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertEquals(0, item.getHeight());
      item.setHeight(4);
      Assert.assertEquals(4, item.getHeight());
   }

   @Test
   public void testSet() {
      WidgetDefinition item = new WidgetDefinition("review");
      Assert.assertFalse(item.is(WidgetOption.ALIGN_CENTER));
      item.set(WidgetOption.ALIGN_CENTER);
      Assert.assertTrue(item.is(WidgetOption.ALIGN_CENTER));
   }

}
