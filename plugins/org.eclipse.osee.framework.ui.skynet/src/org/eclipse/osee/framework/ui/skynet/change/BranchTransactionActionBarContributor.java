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

package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.change.actions.OpenAssociatedArtifact;
import org.eclipse.osee.framework.ui.skynet.change.actions.OpenQuickSearchAction;
import org.eclipse.osee.framework.ui.skynet.change.actions.ReloadBranchTransactionAction;
import org.eclipse.osee.framework.ui.skynet.change.view.ChangeReportEditor;

/**
 * @author Donald G. Dunne
 */
public class BranchTransactionActionBarContributor implements IActionContributor {

   private final ChangeReportEditor editor;
   private Action reloadAction;
   private OpenAssociatedArtifact openAssocAction;

   public BranchTransactionActionBarContributor(ChangeReportEditor editor) {
      this.editor = editor;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      ChangeUiData uiData = editor.getEditorInput().getChangeData();
      if (UserManager.getUser().isOseeAdmin()) {
         manager.add(new OpenChangeReportByTransactionIdAction());
      }
      manager.add(getReloadAction());
      manager.add(new Separator());
      manager.add(getOpenAssociatedArtifactAction());
      manager.add(new OpenQuickSearchAction(new UiSelectBetweenDeltasBranchProvider(uiData)));
   }

   public OpenAssociatedArtifact getOpenAssociatedArtifactAction() {
      if (openAssocAction == null) {
         openAssocAction = new OpenAssociatedArtifact(editor.getEditorInput().getChangeData());
      }
      return openAssocAction;
   }

   public Action getReloadAction() {
      if (reloadAction == null) {
         reloadAction = new ReloadBranchTransactionAction(editor);
      }
      return reloadAction;
   }

}
