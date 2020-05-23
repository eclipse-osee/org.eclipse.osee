/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.branch.graph.core;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.jface.action.IMenuManager;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphEditorContextMenuProvider extends ContextMenuProvider {

   private final BranchGraphEditor editor;

   public BranchGraphEditorContextMenuProvider(EditPartViewer viewer, BranchGraphEditor editor) {
      super(viewer);
      this.editor = editor;
   }

   @Override
   public void buildContextMenu(IMenuManager menu) {
      GEFActionConstants.addStandardActionGroups(menu);
      menu.appendToGroup(GEFActionConstants.GROUP_PRINT, new PrintAction(editor));
   }

}
