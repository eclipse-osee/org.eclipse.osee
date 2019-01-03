/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.ide.workdef.viewer;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Contributes actions to a toolbar. This class is tied to the editor in the definition of editor-extension (see
 * plugin.xml).
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefConfigEditorActionBarContributor extends ActionBarContributor {

   /**
    * Create actions managed by this contributor.
    * 
    * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
    */
   @Override
   protected void buildActions() {
      addRetargetAction(new DeleteRetargetAction());
      addRetargetAction(new UndoRetargetAction());
      addRetargetAction(new RedoRetargetAction());
   }

   /**
    * Add actions to the given toolbar.
    * 
    * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
    */
   @Override
   public void contributeToToolBar(IToolBarManager toolBarManager) {
      toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
      toolBarManager.add(getAction(ActionFactory.REDO.getId()));
   }

   @Override
   protected void declareGlobalActionKeys() {
      // currently none
   }

}