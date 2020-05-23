/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerRefreshAction extends Action {

   private final ActionWalkerView view;

   public ActionWalkerRefreshAction(ActionWalkerView view) {
      super("Refresh", ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      this.view = view;
   }

   @Override
   public void run() {
      view.refresh();
   }

}
