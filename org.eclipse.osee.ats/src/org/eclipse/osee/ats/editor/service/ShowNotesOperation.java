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

import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class ShowNotesOperation extends WorkPageService {

   private final SMAManager smaMgr;
   private Hyperlink link;

   public ShowNotesOperation(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Show Notes", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY, Location.Global);
      this.smaMgr = smaMgr;
   }

   @Override
   public void create(Group workComp) {
      link = toolkit.createHyperlink(workComp, name, SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            String title = "Notes for " + smaMgr.getSma().getHumanReadableId() + " - \"" + smaMgr.getSma().getDescriptiveName() + "\"";
            Overview logOver = new Overview();
            logOver.addHtml(AHTML.heading(3, title));
            logOver.addNotes(smaMgr.getSma(), "ALL");
            XResultView.getResultView().addResultPage(
                  new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), logOver.getPage(),
                        Manipulations.HTML_MANIPULATIONS));
            AWorkbench.popup("Complete", "Notes in ATS Results");
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
