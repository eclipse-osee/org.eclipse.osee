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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.util.logging.Level;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for the redirection of
 * global actions to the active editor. Multi-page contributor replaces the contributors for the individual editors in
 * the multi-page editor.
 */
public class TestManagerEditorContributor extends MultiPageEditorActionBarContributor {
   private IEditorPart activeEditorPart;

   /**
    * Creates a multi-page contributor.
    */
   public TestManagerEditorContributor() {
      super();
      createActions();
   }

   public void setActivePage(IEditorPart part) {
      if (activeEditorPart == part) return;

      activeEditorPart = part;

      IActionBars actionBars = getActionBars();
      if (actionBars != null) {

         ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

         actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor,
               ITextEditorActionConstants.DELETE));
         actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor,
               ITextEditorActionConstants.UNDO));
         actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor,
               ITextEditorActionConstants.REDO));
         actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor, ITextEditorActionConstants.CUT));
         actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor,
               ITextEditorActionConstants.COPY));
         actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor,
               ITextEditorActionConstants.PASTE));
         actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor,
               ITextEditorActionConstants.SELECT_ALL));
         actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor,
               ITextEditorActionConstants.FIND));
         actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor,
               IDEActionFactory.BOOKMARK.getId()));
         actionBars.updateActionBars();
      }
   }

   /**
    * Returns the action registered with the given text editor.
    * 
    * @return IAction or null if editor is null.
    */
   protected IAction getAction(ITextEditor editor, String actionID) {
      return (editor == null ? null : editor.getAction(actionID));
   }

   private void createActions() {
      //Left here for future actions that might want to be contributed to the status 
      //line for Test Manager.
   }

   @Override
   public void setActiveEditor(IEditorPart part) {
      super.setActiveEditor(part);

      if (part instanceof TestManagerEditor) {

         //Left here for future actions that might want to be contributed to the status 
         //line for Test Manager.
      }

   }

   @Override
   public void contributeToStatusLine(final IStatusLineManager statusLineManager) {
      super.contributeToStatusLine(statusLineManager);
      try {
         /*Job addStatusLineManager = new Job("TM contributing to the status line.") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  OseeContributionItem.addTo(statusLineManager);
               } catch (Throwable th) {
                  OseeLog.log(TestManagerPlugin.class, Level.SEVERE, th.getMessage(), th);
                  return Status.CANCEL_STATUS;
               }
               return Status.OK_STATUS;
            }
         };
         addStatusLineManager.schedule();*/

      } catch (Throwable th) {
         OseeLog.log(TestManagerPlugin.class, Level.WARNING, "Unable to contribute to the status line.", th);
      }
   }

}
