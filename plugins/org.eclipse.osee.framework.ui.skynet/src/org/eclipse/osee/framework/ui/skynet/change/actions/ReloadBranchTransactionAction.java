/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ReloadBranchTransactionAction extends Action {

   private final IChangeReportView view;

   public ReloadBranchTransactionAction(IChangeReportView view) {
      super("Reload Branch Transaction", IAction.AS_PUSH_BUTTON);
      setId("reload.branch.transaction");
      setToolTipText("Reloads the Branch Transactions");
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      this.view = view;
   }

   @Override
   public void run() {
      view.recomputeBranchTransactions();
   }
}