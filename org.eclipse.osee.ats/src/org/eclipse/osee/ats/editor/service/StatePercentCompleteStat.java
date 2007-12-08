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
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.DefaultTeamState;
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
public class StatePercentCompleteStat extends WorkPageService {

   private Hyperlink link;

   public StatePercentCompleteStat(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Percent Complete", smaMgr, page, toolkit, section, ServicesArea.STATISTIC_CATEGORY,
            Location.NonCompleteCurrentState);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#create()
    */
   @Override
   public void create(Group workComp) {
      if (smaMgr.getCurrentStateName().equals(page.getName())) {
         link = toolkit.createHyperlink(workComp, "", SWT.NONE);
         if (smaMgr.getSma().isReadOnly())
            link.addHyperlinkListener(readOnlyHyperlinkListener);
         else
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (smaMgr.getCurrentStateName().equals(DefaultTeamState.Implement.name()) && smaMgr.getSma().isMetricsFromTasks()) {
                        AWorkbench.popup("ERROR",
                              "Percent Complete is rollup of task(s) percent complete " + "and can not be edited here.");
                        return;
                     } else if (smaMgr.promptChangeStatus(false)) section.refreshStateServices();
                  } catch (SQLException ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            });
      }
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (link != null && !link.isDisposed()) link.setText("State Percent: " + smaMgr.getSma().getStatePercentComplete(
            page.getName()));
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
