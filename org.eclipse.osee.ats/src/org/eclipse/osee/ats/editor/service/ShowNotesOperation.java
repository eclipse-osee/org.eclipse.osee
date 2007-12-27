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
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Group;

/**
 * @author Donald G. Dunne
 */
public class ShowNotesOperation extends WorkPageService implements IAtsEditorToolBarService {

   public ShowNotesOperation(final SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Show Notes", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY, Location.Global);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public ShowNotesOperation(final SMAManager smaMgr) {
      super("Show Note", smaMgr, null, null, null, null, null);
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

   private void performAddNote() {
      try {
         String title =
               "Notes for " + smaMgr.getSma().getHumanReadableId() + " - \"" + smaMgr.getSma().getDescriptiveName() + "\"";
         Overview logOver = new Overview();
         logOver.addHtml(AHTML.heading(3, title));
         logOver.addNotes(smaMgr.getSma(), "ALL");
         XResultView.getResultView().addResultPage(
               new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), logOver.getPage(),
                     Manipulations.HTML_MANIPULATIONS));
         AWorkbench.popup("Complete", "Notes in ATS Results");
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#getToolbarAction(org.eclipse.osee.ats.editor.SMAManager)
    */
   public Action getToolbarAction(SMAManager smaMgr) {
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performAddNote();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("note.gif"));
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
