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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.stateItem.AtsDebugWorkPage;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts.OpenView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class DebugOperations extends WorkPageService {

   public DebugOperations(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Debug Operations", smaMgr, page, toolkit, section, ServicesArea.DEBUG_PAGE_CATEGORY,
            Location.SpecifiedPageId);
   }

   @Override
   public boolean displayService() {
      return AtsPlugin.isAtsAdmin();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isSpecifiedPageId(java.lang.String)
    */
   @Override
   public boolean isSpecifiedPageId(String pageId) {
      return pageId.equals(AtsDebugWorkPage.PAGE_ID);
   }

   @Override
   public void create(Group workComp) {
      Hyperlink link = toolkit.createHyperlink(workComp, "Dirty Report", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            StringBuilder info = new StringBuilder();
            info.append(AHTML.heading(2, "State Machine Artifact - Dirty Report"));
            smaMgr.getSma().isSMADirty(info);
            String title = "State Machine Artifact - Dirty Report";
            XResultView.getResultView().addResultPage(
                  new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), AHTML.simplePage(info.toString())));
            AWorkbench.popup("Complete", title + " Complete...Results in ATS Results");
         }

      });
      link = toolkit.createHyperlink(workComp, "Refresh Dirty", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            smaMgr.getEditor().onDirtied();
         }

      });
      link = toolkit.createHyperlink(workComp, "Open VUE Workflow", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            String hrid = smaMgr.getWorkFlow().getId().replaceFirst("^.* - ", "");
            if (hrid.length() != 5)
               AWorkbench.popup("Open Workflow", "Workflow is NOT an artifact\n\n" + smaMgr.getWorkFlow().getId());
            else
               AtsLib.open(hrid, OpenView.ArtifactEditor);
         }
      });
      link = toolkit.createHyperlink(workComp, "Open Team Definition", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            if (!(smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
               AWorkbench.popup("Open Workflow", "Workflow is NOT TeamWorkflowArtifact");
            } else {
               try {
                  AtsLib.open(((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getGuid(),
                        OpenView.ArtifactEditor);
               } catch (SQLException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         }
      });
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
