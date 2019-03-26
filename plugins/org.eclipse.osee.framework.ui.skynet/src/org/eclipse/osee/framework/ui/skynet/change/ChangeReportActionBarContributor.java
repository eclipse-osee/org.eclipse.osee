/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.change.actions.OpenAssociatedArtifact;
import org.eclipse.osee.framework.ui.skynet.change.actions.OpenQuickSearchAction;
import org.eclipse.osee.framework.ui.skynet.change.actions.ReloadChangeReportAction;
import org.eclipse.osee.framework.ui.skynet.change.view.ChangeReportEditor;

/**
 * @author Roberto E. Escobar
 */
public class ChangeReportActionBarContributor implements IActionContributor {

   private final ChangeReportEditor editor;
   private Action reloadAction;
   private OpenAssociatedArtifact openAssocAction;

   public ChangeReportActionBarContributor(ChangeReportEditor editor) {
      this.editor = editor;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      ChangeUiData uiData = editor.getEditorInput().getChangeData();
      if (UserGroupService.getOseeAdmin().isCurrentUserMember()) {
         manager.add(new OpenChangeReportByTransactionIdAction());
      }
      manager.add(getReloadAction());
      manager.add(new Separator());
      //      manager.add(createCompareMenu());
      manager.add(getOpenAssociatedArtifactAction());
      manager.add(new OpenQuickSearchAction(new UiSelectBetweenDeltasBranchProvider(uiData)));
   }

   //   public Action createCompareMenu() {
   //      if (compareMenu == null) {
   //         ChangeUiData uiData = editor.getEditorInput().getChangeData();
   //
   //         compareMenu = new CompareDropDown();
   //         compareMenu.add(new CompareAction(CompareType.COMPARE_BASE_TO_HEAD, editor, uiData));
   //         compareMenu.add(new CompareAction(CompareType.COMPARE_CURRENTS_AGAINST_PARENT, editor, uiData));
   //         // TODO FUTURE -- Not Supported at this time on the server side
   //         // compareMenu.add(new CompareAction(CompareType.COMPARE_CURRENTS_AGAINST_OTHER_BRANCH, editor, uiData));
   //      }
   //      return compareMenu;
   //   }

   public OpenAssociatedArtifact getOpenAssociatedArtifactAction() {
      if (openAssocAction == null) {
         openAssocAction = new OpenAssociatedArtifact(editor.getEditorInput().getChangeData());
      }
      return openAssocAction;
   }

   public Action getReloadAction() {
      if (reloadAction == null) {
         reloadAction = new ReloadChangeReportAction(editor);
      }
      return reloadAction;
   }

}
