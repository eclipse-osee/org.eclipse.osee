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
package org.eclipse.osee.ats.editor.service;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.swt.widgets.Group;

/**
 * @author Donald G. Dunne
 */
public class OpenInArtifactEditorOperation extends WorkPageService implements IAtsEditorToolBarService {

   public OpenInArtifactEditorOperation(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Open Artifact Editor", smaMgr, page, toolkit, section, null, Location.None);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public OpenInArtifactEditorOperation(SMAManager smaMgr) {
      super("Open Artifact Editor", smaMgr, null, null, null, null, null);
   }

   @Override
   public boolean displayService() {
      return false;
   }

   @Override
   public void create(Group workComp) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.operation.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
   }

   private void performOpen() {
      ArtifactEditor.editArtifact(smaMgr.getSma());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#getToolbarAction(org.eclipse.osee.ats.editor.SMAManager)
    */
   public Action getToolbarAction(SMAManager smaMgr) {
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performOpen();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("laser_16_16.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#refreshToolbarAction()
    */
   public void refreshToolbarAction() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#showInToolbar(org.eclipse.osee.ats.editor.SMAManager)
    */
   public boolean showInToolbar(SMAManager smaMgr) {
      return AtsPlugin.isAtsAdmin();
   }

}
