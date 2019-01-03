/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import java.util.Map;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
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
      Map<String, AbstractBlam> blams = BlamContributionManager.getBlamMap();

      boolean foundBlam = blams.containsKey(NAME_OF_ATS_ITEM);
      if (foundBlam) {
         item = new XNavigateItemBlam(new XNavigateItem(null, "Blam Operations", FrameworkImage.BLAM),
            blams.get(NAME_OF_ATS_ITEM));
      }

      Assert.assertTrue(String.format("%s not found from list of provided Blams.", NAME_OF_ATS_ITEM), foundBlam);
      Assert.assertNotNull(item);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);

      Assert.assertTrue(
         "Exceptions were thrown during AtsXWidgetsExampleBlamTest (gui test of XWidgetExampleBlam) should be none.",
         monitorLog.getSevereLogs().isEmpty());

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
