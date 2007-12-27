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
import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldOperation extends WorkPageService implements IAtsEditorToolBarService {

   public OpenInAtsWorldOperation(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Open in ATS World", smaMgr, page, toolkit, section, null, Location.None);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public OpenInAtsWorldOperation(SMAManager smaMgr) {
      super("Open in ATS World", smaMgr, null, null, null, null, null);
   }

   @Override
   public void create(Group workComp) {
      Hyperlink link = toolkit.createHyperlink(workComp, name, SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            performOpen();
         }

      });
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.IAtsEditorToolBarService#showInToolbar()
    */
   public boolean showInToolbar(SMAManager smaMgr) {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.IAtsEditorToolBarService#run()
    */
   public void performOpen() {
      try {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            ActionArtifact actionArt = ((TeamWorkFlowArtifact) smaMgr.getSma()).getParentActionArtifact();
            WorldView.loadIt("Action " + actionArt.getHumanReadableId(), Arrays.asList(new Artifact[] {actionArt}));
            return;
         } else if (smaMgr.getSma() instanceof StateMachineArtifact) {
            WorldView.loadIt(smaMgr.getSma().getArtifactTypeName() + ": " + smaMgr.getSma().getHumanReadableId(),
                  Arrays.asList(new Artifact[] {smaMgr.getSma()}));
            return;
         }
         OSEELog.logSevere(AtsPlugin.class, "Unhandled artifact type " + smaMgr.getSma().getArtifactTypeName(), true);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
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
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("world.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#refreshToolbarAction()
    */
   public void refreshToolbarAction() {
   }
}
