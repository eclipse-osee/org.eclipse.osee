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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewPeerToPeerReviewJob;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.widgets.dialog.StateListAndTitleDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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
public class AddPeerToPeerReviewService extends WorkPageService {

   private Hyperlink link;

   public AddPeerToPeerReviewService(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) {
      return ((smaMgr.getSma() instanceof TeamWorkFlowArtifact) && isCurrentState(page));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      link = toolkit.createHyperlink(workGroup, getName(), SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               StateListAndTitleDialog dialog =
                     new StateListAndTitleDialog("Related Review State",
                           "Select state to that review will be associated with.",
                           smaMgr.getWorkFlowDefinition().getPageNames());
               dialog.setInitialSelections(new Object[] {smaMgr.getStateMgr().getCurrentStateName()});
               dialog.setReviewTitle("Review \"" + smaMgr.getSma().getArtifactTypeName() + "\" titled \"" + smaMgr.getSma().getDescriptiveName() + "\"");
               if (dialog.open() == 0) {
                  if (dialog.getReviewTitle() == null || dialog.getReviewTitle().equals("")) {
                     AWorkbench.popup("ERROR", "Must enter review title");
                     return;
                  }
                  NewPeerToPeerReviewJob job =
                        new NewPeerToPeerReviewJob((TeamWorkFlowArtifact) smaMgr.getSma(), dialog.getReviewTitle(),
                              dialog.getSelectedState());
                  job.setUser(true);
                  job.setPriority(Job.LONG);
                  job.schedule();
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Add PeerToPeer Review";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return ServicesArea.OPERATION_CATEGORY;
   }

}
