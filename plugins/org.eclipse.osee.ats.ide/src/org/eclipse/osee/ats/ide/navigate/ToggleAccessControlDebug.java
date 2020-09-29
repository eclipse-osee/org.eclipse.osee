/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.access.AccessControlUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ToggleAccessControlDebug extends XNavigateItemAction {

   public ToggleAccessControlDebug(XNavigateItem parent) {
      super(parent, "Toggle Access Control Debug", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      boolean debugOn = AccessControlUtil.isDebugOn();
      boolean newDebugOn = !debugOn;
      if (MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         String.format("Toggle Access Debug On to [%s]", newDebugOn))) {
         AccessControlUtil.setDebugOn(newDebugOn);
         for (ArtifactExplorer artExp : ArtifactExplorer.getEditors()) {
            artExp.resetMenu();
         }
      }
      AccessControlUtil.errorf("Access Control Debug On [%s] ", newDebugOn);
   }
}
