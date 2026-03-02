/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamService;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 */
public class AtsXWidgetsExampleBlamTest {

   private static final String NAME_OF_ATS_ITEM = "XWidgets Example Blam";

   /**
    * Load NAME_OF_ATS_ITEM blam, log any exceptions and report them as test failures. Purpose of this test is to serve
    * as a regression test to various widgets loaded in XWidgetExampleBlam.
    */
   @Test
   public void testXWidgetsExampleBlam() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      XNavigateItemBlam item = null;
      AbstractBlam foundBlam = null;
      for (AbstractBlam blam : BlamService.getBlams()) {
         if (blam.getName().equals(NAME_OF_ATS_ITEM)) {
            foundBlam = blam;
            break;
         }
      }

      if (foundBlam != null) {
         item = new XNavigateItemBlam(foundBlam, XNavigateItem.UTILITY_EXAMPLES);
      }

      Assert.assertTrue(String.format("%s not found from list of provided Blams.", NAME_OF_ATS_ITEM),
         foundBlam != null);
      Assert.assertNotNull(item);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);

      Assert.assertTrue(
         "Exceptions were thrown during AtsXWidgetsExampleBlamTest (gui test of XWidgetExampleBlam) should be none.",
         monitorLog.getSevereLogs().isEmpty());

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
