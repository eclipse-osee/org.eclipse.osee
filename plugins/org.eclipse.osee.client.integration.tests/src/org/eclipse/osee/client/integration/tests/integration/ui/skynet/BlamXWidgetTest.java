/*******************************************************************************
 * Copyright (c) 2004, 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Will instantiate all Blams and test<br>
 * 1) blam.getXWidgetXml() string is parse-able<br>
 * 2) each XWidget declared in XWidgetXml is resolved to an XWidget class
 * 
 * @author Donald G. Dunne
 */
public class BlamXWidgetTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Test
   public void testXWidgetsResolved() throws Exception {
      Collection<AbstractBlam> blams = BlamContributionManager.getBlamOperations();
      for (AbstractBlam blam : blams) {
         List<XWidgetRendererItem> datas = blam.getLayoutDatas();
         for (XWidgetRendererItem xWidgetLayoutData : datas) {
            XWidget xWidget = xWidgetLayoutData.getXWidget();
            Assert.assertNotNull(xWidget);

            /**
             * Test that widget gets resolved. If widget is unresolved, the resolver will resolve it as an XLabel with
             * an error string so the widget creation doesn't exception and fail. Check for this condition.
             */
            String errorMessage = String.format("%s - %s", blam.getClass().getSimpleName(), xWidget.getLabel());
            Assert.assertFalse(errorMessage, xWidget.getLabel().contains("Unhandled XWidget"));
         }
      }
   }
}
