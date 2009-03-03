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
package org.eclipse.osee.framework.ui.data.model.editor.core;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorContextMenuProvider extends ContextMenuProvider {

   private final ODMEditor editor;

   public ODMEditorContextMenuProvider(EditPartViewer viewer, ODMEditor editor) {
      super(viewer);
      this.editor = editor;
   }

   @Override
   public void buildContextMenu(IMenuManager menu) {
      GEFActionConstants.addStandardActionGroups(menu);

      //      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(EditAction.ID));
      menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.UNDO.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.REDO.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.DELETE.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_PRINT, getAction(ActionFactory.PRINT.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.IMPORT.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.EXPORT.getId()));
   }

   private IAction getAction(String actionId) {
      return editor.getActionRegistry().getAction(actionId);
   }
}
