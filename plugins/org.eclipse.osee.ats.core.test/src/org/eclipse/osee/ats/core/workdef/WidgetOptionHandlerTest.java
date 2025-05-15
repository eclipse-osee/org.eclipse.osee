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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WidgetOptionHandlerTest {

   @Test
   public void testWidgetOptionHandler() {
      WidgetOptionHandler handler = new WidgetOptionHandler();

      handler.add(WidgetOption.ADD_DEFAULT_VALUE);
      Assert.assertTrue(handler.contains(WidgetOption.ADD_DEFAULT_VALUE));
      Assert.assertFalse(handler.contains(WidgetOption.FILL_HORZ));
   }

   @Test
   public void testAddWidgetOptionArray() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(Arrays.asList(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.FILL_HORZ));

      Assert.assertTrue(handler.contains(WidgetOption.ADD_DEFAULT_VALUE));
      Assert.assertTrue(handler.contains(WidgetOption.FILL_HORZ));

      handler = new WidgetOptionHandler(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.FILL_HORZ);

      Assert.assertTrue(handler.contains(WidgetOption.ADD_DEFAULT_VALUE));
      Assert.assertTrue(handler.contains(WidgetOption.FILL_HORZ));
   }

   @Test
   public void testGetCollection() {
      Collection<WidgetOption> collection =
         WidgetOptionHandler.getCollection(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.FILL_HORZ);
      Assert.assertTrue(collection.contains(WidgetOption.ADD_DEFAULT_VALUE));
      Assert.assertTrue(collection.contains(WidgetOption.FILL_HORZ));
      Assert.assertFalse(collection.contains(WidgetOption.NOT_RFC));
   }

   @Test
   public void testAdd_ENABLED() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.ENABLED);
      Assert.assertTrue(handler.contains(WidgetOption.ENABLED));
      handler.add(WidgetOption.NOT_ENABLED);
      Assert.assertFalse(handler.contains(WidgetOption.ENABLED));
      Assert.assertTrue(handler.contains(WidgetOption.NOT_ENABLED));
   }

   @Test
   public void testAdd_FUTURE_DATE_REQUIRED() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.FUTURE_DATE_REQUIRED);
      Assert.assertTrue(handler.contains(WidgetOption.FUTURE_DATE_REQUIRED));
      handler.add(WidgetOption.NOT_FUTURE_DATE_REQUIRED);
      Assert.assertFalse(handler.contains(WidgetOption.FUTURE_DATE_REQUIRED));
      Assert.assertTrue(handler.contains(WidgetOption.NOT_FUTURE_DATE_REQUIRED));
   }

   @Test
   public void testAdd_VERTICAL_LABEL() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.VERT_LABEL);
      Assert.assertTrue(handler.contains(WidgetOption.VERT_LABEL));
      handler.add(WidgetOption.HORZ_LABEL);
      Assert.assertFalse(handler.contains(WidgetOption.VERT_LABEL));
      Assert.assertTrue(handler.contains(WidgetOption.HORZ_LABEL));
   }

   @Test
   public void testAdd_REQUIRED_FOR_COMPLETION() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.RFC);
      Assert.assertTrue(handler.contains(WidgetOption.RFC));
      handler.add(WidgetOption.NOT_RFC);
      Assert.assertFalse(handler.contains(WidgetOption.RFC));
      Assert.assertTrue(handler.contains(WidgetOption.NOT_RFC));
   }

   @Test
   public void testAdd_EDITABLE() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.EDITABLE);
      Assert.assertTrue(handler.contains(WidgetOption.EDITABLE));
      handler.add(WidgetOption.NOT_EDITABLE);
      Assert.assertFalse(handler.contains(WidgetOption.EDITABLE));
      Assert.assertTrue(handler.contains(WidgetOption.NOT_EDITABLE));
   }

   @Test
   public void testAdd_FILL() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(WidgetOption.FILL_HORZ);
      handler.add(WidgetOption.FILL_VERT);
      Assert.assertTrue(handler.contains(WidgetOption.FILL_HORZ));
      Assert.assertTrue(handler.contains(WidgetOption.FILL_VERT));
      handler.add(WidgetOption.FILL_NONE);
      Assert.assertTrue(handler.contains(WidgetOption.FILL_NONE));
      Assert.assertFalse(handler.contains(WidgetOption.FILL_VERT));
      Assert.assertFalse(handler.contains(WidgetOption.FILL_HORZ));
   }

   @Test
   public void testSetWidgetOptionArray() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(Arrays.asList(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.FILL_HORZ));
      Assert.assertTrue(handler.getXOptions().contains(WidgetOption.ADD_DEFAULT_VALUE));
   }

   @Test
   public void testToString() {
      WidgetOptionHandler handler = new WidgetOptionHandler();
      handler.add(Arrays.asList(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.FILL_HORZ));
      String toString = handler.toString();
      Assert.assertTrue(
         toString.equals("[ADD_DEFAULT_VALUE, FILL_HORZ]") || toString.equals("[FILL_HORZ, ADD_DEFAULT_VALUE]"));
   }

}
