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

import java.sql.SQLException;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Group;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifact extends WorkPageService implements IAtsEditorToolBarService {

   Action action;

   public OpenVersionArtifact(final SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Open Targeted for Version", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY,
            Location.CurrentState);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public OpenVersionArtifact(final SMAManager smaMgr) {
      super("Open Targeted for Version", smaMgr, null, null, null, null, null);
   }

   @Override
   public void create(Group workComp) {
   }

   /*
    * (non-Javadoc)
    * 
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
      try {
         if (((TeamWorkFlowArtifact) smaMgr.getSma()).getTargetedForVersion() != null) ArtifactEditor.editArtifact(((TeamWorkFlowArtifact) smaMgr.getSma()).getTargetedForVersion());
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#getToolbarAction(org.eclipse.osee.ats.editor.SMAManager)
    */
   public Action getToolbarAction(SMAManager smaMgr) {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performOpen();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("version.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#refreshToolbarAction()
    */
   public void refreshToolbarAction() {
      boolean enabled = false;
      try {
         enabled = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTargetedForVersion() != null;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      action.setEnabled(enabled);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#showInToolbar(org.eclipse.osee.ats.editor.SMAManager)
    */
   public boolean showInToolbar(SMAManager smaMgr) {
      return (smaMgr.getSma() instanceof TeamWorkFlowArtifact);
   }
}
