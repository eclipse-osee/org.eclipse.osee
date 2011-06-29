/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.hyper.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.hyper.HyperView;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

public class HyperRefreshAction extends Action {

   private final HyperView hyperView;

   public HyperRefreshAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setToolTipText("Refresh");
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
   }

   @Override
   public void run() {
      if (hyperView.homeSearchItem == null) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Refresh Error", "Viewer not loaded, nothing to refresh.");
         return;
      }
      hyperView.handleRefreshButton();
   }

}
