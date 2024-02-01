/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ClearAtsConfigCacheAllServers extends XNavigateItemAction {

   public ClearAtsConfigCacheAllServers() {
      super("Clear ATS Config Cache - All Servers", FrameworkImage.GEAR, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      if (MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName() + "\n\nAre you sure?")) {
         XResultData rd = AtsApiService.get().getStoreService().clearAtsCachesAllServers();
         XResultDataUI.report(rd, getName());
         AtsApiService.get().clearCaches();
      }

   }

   @Override
   public String getDescription() {
      return "Clear all servers registered in OseeInfo \"osee.health.servers\" and clear this client cache.";
   }
}
