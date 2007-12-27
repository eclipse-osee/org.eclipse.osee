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
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.swt.widgets.Group;

/**
 * @author Donald G. Dunne
 */
public class OpenInSkyWalkerOperation extends WorkPageService implements IAtsEditorToolBarService {

   public OpenInSkyWalkerOperation(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Open Sky Walker", smaMgr, page, toolkit, section, null, Location.None);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public OpenInSkyWalkerOperation(SMAManager smaMgr) {
      super("Open Sky Walker", smaMgr, null, null, null, null, null);
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
      SkyWalkerView.exploreArtifact(smaMgr.getSma());
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
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("skywalker.gif"));
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
      return true;
   }
}
