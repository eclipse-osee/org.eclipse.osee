/*
 * Created on Aug 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.blam;

import java.util.Arrays;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * Will instantiate all Blams and test<br>
 * 1) blam.getXWidgetXml() string is parse-able<br>
 * 2) each XWidget declared in XWidgetXml is resolved to an XWidget class
 * 
 * @author Donald G. Dunne
 */
public class BlamXWidgetTest {

   @org.junit.Test
   public void testXWidgetsResolved() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      for (AbstractBlam blam : BlamContributionManager.getBlamOperations()) {
         for (DynamicXWidgetLayoutData xWidgetLayoutData : blam.getLayoutDatas()) {
            XWidget xWidget = xWidgetLayoutData.getXWidget();
            Assert.assertNotNull(xWidget);
            /**
             * Test that widget gets resolved. If widget is unresolved, the resolver will resolve it as an XLabel with
             * an error string so the widget creation doesn't exception and fail. Check for this condition.
             */
            Assert.assertFalse(blam.getClass().getSimpleName() + " - " + xWidget.getLabel(),
               xWidget.getLabel().contains("Unhandled XWidget"));
         }
      }
      TestUtil.severeLoggingEnd(monitorLog, Arrays.asList(""));
   }
}
