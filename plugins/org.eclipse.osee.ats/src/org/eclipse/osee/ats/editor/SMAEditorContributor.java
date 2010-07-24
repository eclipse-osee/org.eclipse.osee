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
package org.eclipse.osee.ats.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Donald G. Dunne
 */
public class SMAEditorContributor extends MultiPageEditorActionBarContributor {
   private IEditorPart activeEditorPart;

   public SMAEditorContributor() {
      super();
   }

   protected IAction getAction(ITextEditor editor, String actionID) {
      return editor == null ? null : editor.getAction(actionID);
   }

   @Override
   public void setActivePage(IEditorPart part) {
      if (activeEditorPart == part) {
         return;
      }

      activeEditorPart = part;

      IActionBars actionBars = getActionBars();
      if (actionBars != null) {

         SMAEditor editor = part instanceof SMAEditor ? (SMAEditor) part : null;

         if (editor != null) {
            actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), editor.getPrintAction());
            actionBars.updateActionBars();
         }
      }
   }
}
