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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @author Karol M. Wilk
 */
public class AtsXWidgetsExampleBlamTest {

   private static final String NAME_OF_ATS_ITEM = "XWidgets Example";

   /**
    * Load NAME_OF_ATS_ITEM blam, log any exceptions and report them as test failures. Purpose of this test is to serve
    * as a regression test to various widgets loaded in XWidgetExampleBlam.
    */
   @org.junit.Test
   public void testXWidgetsExampleBlam() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      XNavigateItemBlam item = null;
      Collection<AbstractBlam> blams = BlamContributionManager.getBlamOperations();
      boolean foundBlam = false;
      for (AbstractBlam blam : blams) {
         if (blam.getName().equals(NAME_OF_ATS_ITEM)) {
            item = new XNavigateItemBlam(new XNavigateItem(null, "Blam Operations", FrameworkImage.BLAM), blam);
            foundBlam = true;
            break;
         }
      }

      Assert.assertTrue(String.format("%s not found from list of provided Blams.", NAME_OF_ATS_ITEM), foundBlam);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);

      Assert.assertTrue(
         "Exceptions were thrown during AtsXWidgetsExampleBlamTest (gui test of XWidgetExampleBlam) should be none.",
         monitorLog.getSevereLogs().isEmpty());

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
