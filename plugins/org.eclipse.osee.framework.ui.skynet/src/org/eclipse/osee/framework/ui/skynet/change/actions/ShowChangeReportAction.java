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

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.ChangeReportHandler;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportAction extends Action {

   private final ITransactionRecordSelectionProvider selectionProvider;

   public ShowChangeReportAction(ITransactionRecordSelectionProvider selectionProvider) {
      super("Show Change Report");
      this.selectionProvider = selectionProvider;
      setToolTipText(getText());
   }

   @Override
   public void run() {
      List<TransactionId> selectedTransactionRecords = selectionProvider.getSelectedTransactionRecords();
      if (selectedTransactionRecords.size() == 0) {
         AWorkbench.popup("Must select transaction(s) to show Change Report");
      }
      ChangeReportHandler handler = new ChangeReportHandler();
      handler.executeWithException(null, new StructuredSelection(selectedTransactionRecords));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

}
