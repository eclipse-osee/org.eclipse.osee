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
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Donald G. Dunne
 */
public abstract class WorkPageService {

   protected final SMAManager smaMgr;
   protected final boolean showSidebarService = true;

   public WorkPageService(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   public boolean isCurrentState(AtsWorkPage page) throws OseeCoreException {
      return smaMgr.isCurrentState(page.getName());
   }

   public boolean isCurrentNonCompleteCancelledState(AtsWorkPage page) throws OseeCoreException {
      return smaMgr.isCurrentState(page.getName()) && !isCompleteCancelledState(page);
   }

   public boolean isCompleteCancelledState(AtsWorkPage page) {
      return page.isCancelledPage() || page.isCompletePage();
   }

   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) throws OseeCoreException {
   }

   public Action createToolbarService() {
      return null;
   }

   public void dispose() {
   }

   public void refresh() {
   }

   public String getSidebarCategory() {
      return null;
   }

   protected IHyperlinkListener readOnlyHyperlinkListener = new IHyperlinkListener() {
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
         if (smaMgr.isHistoricalVersion())
            AWorkbench.popup(
                  "Historical Error",
                  "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());

         else
            AWorkbench.popup(
                  "Authentication Error",
                  "You do not have permissions to edit " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
      };

      public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
      };

      public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
      };
   };

   public abstract String getName();

   /**
    * By default, all sidebar services will be have their createSidebarService method called. This method can be
    * overridden to determine if it should be called.
    * 
    * @return the showSidebarService
    * @throws OseeCoreException TODO
    */
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return showSidebarService;
   }

}
