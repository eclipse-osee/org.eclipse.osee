/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ToggleXViewerColumnLoadingDebug extends XNavigateItemAction {

   public static final String DEBUG_COLUMN_LOADING = "DebugLoading";

   public ToggleXViewerColumnLoadingDebug() {
      super("Toggle XViewer Column Loading Debug", FrameworkImage.GEAR, XNavigateItem.UTILITY);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      boolean debugOn = "true".equals(System.getProperty(DEBUG_COLUMN_LOADING));
      boolean newDebugOn = !debugOn;
      if (MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         String.format("Toggle Column Loading Debug On to [%s]\n\n" //
            + "NOTE: This option should not be left on as it degrades loading performance in calculations\n\n" //
            + "NOTE: When true, a \"View Loading Report\" menu option will show in right-click\n" //
            + " of table after load and as report icon in toolbar.", newDebugOn))) {
         System.setProperty(DEBUG_COLUMN_LOADING, (debugOn ? "false" : "true"));
      }
   }

}
