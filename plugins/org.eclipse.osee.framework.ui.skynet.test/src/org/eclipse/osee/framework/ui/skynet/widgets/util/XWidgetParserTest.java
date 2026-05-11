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

import org.eclipse.osee.framework.core.widget.XWidgetData;
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
   public void testExtractWidData() {
      XWidgetData widData = XWidgetParser.extractWidgetData("<XWidget displayName=\"My Name\" storageName=\"My Store Name\" fill=\"Vertically\" />");

      Assert.assertEquals("My Name", widData.getName());
      Assert.assertEquals("My Store Name", widData.getStoreName());
   }

   @Test
   public void testExtractWidDataNameOnly() {
      XWidgetData widData =
         XWidgetParser.extractWidgetData("<XWidget displayName=\"My Name\" fill=\"Vertically\" />");

      Assert.assertEquals("My Name", widData.getName());
      Assert.assertEquals("My Name", widData.getStoreName());
   }

   public void testExtractWidDataStoreNameOnly() {
      XWidgetData widData =
         XWidgetParser.extractWidgetData("<XWidget storageName=\"My Store Name\" fill=\"Vertically\" />");

      Assert.assertEquals("Unknown", widData.getName());
      Assert.assertEquals("My Store Name", widData.getStoreName());
   }

}