/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.util;

import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Donald G. Dunne
 */
public class WfeContributor extends MultiPageEditorActionBarContributor {
   public WfeContributor() {
      super();
   }

   protected IAction getAction(ITextEditor editor, String actionID) {
      return editor == null ? null : editor.getAction(actionID);
   }

   @Override
   public void setActivePage(IEditorPart part) {
      if (part != null && part instanceof WorkflowEditor) {
         IActionBars actionBars = getActionBars();
         if (actionBars != null) {

            WorkflowEditor editor = (WorkflowEditor) part;
            actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), new WfePrint(editor.getWorkItem()));
            actionBars.updateActionBars();
         }
      }
   }
}
