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
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorContextMenuProvider extends ContextMenuProvider {

   /** The editor's action registry. */
   private final ActionRegistry actionRegistry;

   /**
    * Instantiate a new menu context provider for the specified EditPartViewer and ActionRegistry.
    * 
    * @param viewer the editor's graphical viewer
    * @param registry the editor's action registry
    * @throws IllegalArgumentException if registry is <tt>null</tt>.
    */
   public ODMEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
      super(viewer);
      if (registry == null) {
         throw new IllegalArgumentException();
      }
      actionRegistry = registry;
   }

   /**
    * Called when the context menu is about to show. Actions, whose state is enabled, will appear in the context menu.
    * 
    * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
    */
   @Override
   public void buildContextMenu(IMenuManager menu) {
      // Add standard action groups to the menu
      GEFActionConstants.addStandardActionGroups(menu);

      // Add actions to the menu
      //      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(EditAction.ID));
      menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.UNDO.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.REDO.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.DELETE.getId()));
      menu.appendToGroup(GEFActionConstants.GROUP_PRINT, getAction(ActionFactory.PRINT.getId()));
   }

   private IAction getAction(String actionId) {
      return actionRegistry.getAction(actionId);
   }

}
