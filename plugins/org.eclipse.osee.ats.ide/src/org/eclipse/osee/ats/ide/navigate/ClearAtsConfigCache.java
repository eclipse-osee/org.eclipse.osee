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

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class ClearAtsConfigCache extends XNavigateItemAction {

   public ClearAtsConfigCache() {
      super("Clear ATS Config Cache", FrameworkImage.GEAR, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      AtsApiService.get().reloadServerAndClientCaches();
      AtsApiService.get().clearCaches();

   }

   @Override
   public String getDescription() {
      return "Clear a singleton server of its cached data and then requests cache from client.\n" //
         + "This will not work in a multiple server production environment";
   }
}
