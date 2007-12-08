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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.actions.NewPeerToPeerReviewJob;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.widgets.dialog.StateListDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class AddPeerToPeerReviewService extends WorkPageService {

   private Hyperlink link;

   public AddPeerToPeerReviewService(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Add PeerToPeer Review", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY, Location.Global);
   }

   @Override
   public void create(Group workComp) {
      if (!(smaMgr.getSma() instanceof TeamWorkFlowArtifact)) return;
      link = toolkit.createHyperlink(workComp, name, SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            StateListDialog dialog = new StateListDialog("Related Review State",
                  "Select state to that review will be associated with.", smaMgr.getWorkFlow().getPageNames());
            dialog.setInitialSelections(new Object[] {smaMgr.getCurrentStateName()});
            if (dialog.open() == 0) {
               if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Add PeerToPeer Review",
                     "Create a PeerToPeer Review and attach it the \"" + dialog.getResult()[0] + "\" state?")) return;
               NewPeerToPeerReviewJob job = new NewPeerToPeerReviewJob((TeamWorkFlowArtifact) smaMgr.getSma(),
                     dialog.getSelectedState());
               job.setUser(true);
               job.setPriority(Job.LONG);
               job.schedule();
            }
         }
      });
      refresh();
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
}
