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