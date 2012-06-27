/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.WidgetOption;
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
