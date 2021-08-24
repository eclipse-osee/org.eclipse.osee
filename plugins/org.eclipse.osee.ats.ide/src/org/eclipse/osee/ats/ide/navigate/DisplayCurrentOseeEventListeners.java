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

import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;

/**
 * @author Donald G. Dunne
 */
public class DisplayCurrentOseeEventListeners extends XNavigateItemAction {

   public DisplayCurrentOseeEventListeners() {
      super("Display Current OSEE Event Listeners", PluginUiImage.ADMIN, XNavigateItem.TOP_ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      String str = OseeEventManager.getListenerReport();
      ResultsEditor.open(new XResultPage(getName(), str));
   }

}
