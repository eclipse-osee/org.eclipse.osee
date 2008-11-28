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
package org.eclipse.osee.ats.editor.service.branch;

import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SetAsDefaultBranchService extends WorkPageService implements IBranchEventListener {

   private Hyperlink link;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public SetAsDefaultBranchService(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return isCurrentState(page);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) throws OseeCoreException {
      link = toolkit.createHyperlink(workGroup, getName(), SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            performService();
         }
      });

      OseeEventManager.addListener(this);
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Set As Default Branch";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return AtsBranchManager.BRANCH_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (link != null && !link.isDisposed()) {
         boolean enabled = isEnabled();
         link.setEnabled(enabled);
         link.setUnderlined(enabled);
      }
   }

   private boolean isEnabled() {
      boolean enabled = false;
      try {
         return smaMgr.getBranchMgr().isWorkingBranch();
      } catch (Exception ex) {
         // do nothing
      }
      return enabled;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            refresh();
         }
      });

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            refresh();
         }
      });

   }

   private void performService() {
      smaMgr.getBranchMgr().setAsDefaultBranch();
   }

}
