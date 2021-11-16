/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.Collection;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * @author Donald G. Dunne
 */
public class UpdateWorkDefValidStateNameConfig extends XNavigateItemAction {

   public UpdateWorkDefValidStateNameConfig() {
      super("Update Work Def validStateName Config", FrameworkImage.GEAR, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      Collection<String> stateNames = AtsApiService.get().getWorkDefinitionService().updateAllValidStateNames();
      StringBuilder sb = new StringBuilder();
      sb.append(getName() + "<br/><br/>");
      for (String state : stateNames) {
         sb.append(state + "<br/>");
      }
      ResultsEditor.open("States", getName(), AHTML.simplePage(sb.toString()));
   }

   @Override
   public String getDescription() {
      return "This will read all Work Definitions, retrieve all state names and set the\n" //
         + "validStateNames in ATS Config artifact.  ATS Server caches will need to be\n" //
         + "cleared and client restarted to see affects.";
   }
}
