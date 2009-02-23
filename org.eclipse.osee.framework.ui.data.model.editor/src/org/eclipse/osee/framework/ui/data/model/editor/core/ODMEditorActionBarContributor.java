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

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorActionBarContributor extends ActionBarContributor {

   /**
    * Create actions managed by this contributor.
    * 
    * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
    */
   @Override
   protected void buildActions() {
      addRetargetAction(new UndoRetargetAction());
      addRetargetAction(new RedoRetargetAction());
      addRetargetAction(new DeleteRetargetAction());
      addRetargetAction(new ZoomInRetargetAction());
      addRetargetAction(new ZoomOutRetargetAction());
      RetargetAction action =
            new RetargetAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, null, IAction.AS_CHECK_BOX);
      action.setImageDescriptor(ODMImages.getImageDescriptor(ODMImages.SNAP_TO_GRID));
      action.setDisabledImageDescriptor(ODMImages.getImageDescriptor(ODMImages.SNAP_TO_GRID_DISABLED));
      action.setToolTipText("Enable Snap To Geometry");
      addRetargetAction(action);
   }

   /**
    * Add actions to the given toolbar.
    * 
    * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
    */
   @Override
   public void contributeToToolBar(IToolBarManager toolBarManager) {
      super.contributeToToolBar(toolBarManager);
      toolBarManager.add(new Separator());
      toolBarManager.add(new ZoomComboContributionItem(getPage()));
      toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
      toolBarManager.add(getAction(ActionFactory.REDO.getId()));
      toolBarManager.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
      //      toolBarManager.add(getAction(ActionFactory.REFRESH.getId()));
      toolBarManager.add(new Separator());
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
    */
   @Override
   protected void declareGlobalActionKeys() {
      addGlobalActionKey(ActionFactory.PRINT.getId());
      addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
      addGlobalActionKey(ActionFactory.DELETE.getId());
   }
}