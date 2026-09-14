/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.demo.reqts.icd;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * Navigate item that tests the full client-server round-trip for signal checking. Creates a minimal SCD with dummy
 * inputs, calls checkSignalsTest on the server, and displays the populated results as an HTML report.
 *
 * @author Donald G. Dunne
 */
public class CheckSignalsRoundTripTestNavigateItem extends XNavigateItemAction {

   public CheckSignalsRoundTripTestNavigateItem(XNavItemCat xNavItemCat) {
      super("Check Signals Round-Trip Test", FrameworkImage.GEAR, xNavItemCat);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      AtsApi atsApi = AtsApiService.get();
      SignalCheckerData scd = new SignalCheckerData();
      scd.setBranch(DemoBranches.SAW_Bld_2);
      scd.setSigDbSystem("DEMO_V1");
      scd.setRecurse(false);

      SignalCheckerData result = atsApi.getAtsIcdService().checkSignalsTest(scd);
      String html = atsApi.getAtsIcdService().getHtmlReport(result);
      ResultsEditor.open("Results", getName(), html);
   }

}
