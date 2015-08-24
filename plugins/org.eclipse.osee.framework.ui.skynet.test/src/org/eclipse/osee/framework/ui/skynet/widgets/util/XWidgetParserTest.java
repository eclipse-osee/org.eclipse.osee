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

import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link XWidgetParser}
 *
 * @author Donald G. Dunne
 */
public class XWidgetParserTest {

   @Test
   public void testExtractlayoutData() {
      XWidgetRendererItem layoutData = XWidgetParser.extractlayoutData(null,
         "<XWidget displayName=\"My Name\" storageName=\"My Store Name\" fill=\"Vertically\" />");

      Assert.assertEquals("My Name", layoutData.getName());
      Assert.assertEquals("My Store Name", layoutData.getStoreName());
   }

   @Test
   public void testExtractlayoutDataNameOnly() {
      XWidgetRendererItem layoutData =
         XWidgetParser.extractlayoutData(null, "<XWidget displayName=\"My Name\" fill=\"Vertically\" />");

      Assert.assertEquals("My Name", layoutData.getName());
      Assert.assertEquals("My Name", layoutData.getStoreName());
   }

   public void testExtractlayoutDataStoreNameOnly() {
      XWidgetRendererItem layoutData =
         XWidgetParser.extractlayoutData(null, "<XWidget storageName=\"My Store Name\" fill=\"Vertically\" />");

      Assert.assertEquals("Unknown", layoutData.getName());
      Assert.assertEquals("My Store Name", layoutData.getStoreName());
   }

}