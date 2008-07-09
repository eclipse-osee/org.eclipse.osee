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

package org.eclipse.osee.ats.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Donald G. Dunne
 */
public class SMAPrint extends Action {

   private final SMAManager smaMgr;
   private final SMATaskComposite taskComposite;
   private final SMAWorkFlowTab workFlowTab;

   public SMAPrint(SMAManager smaMgr, SMAWorkFlowTab workFlowTab, SMATaskComposite taskComposite) {
      super();
      this.smaMgr = smaMgr;
      this.workFlowTab = workFlowTab;
      this.taskComposite = taskComposite;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      try {
         StringBuffer sb = new StringBuffer();
         sb.append(AHTML.beginMultiColumnTable(100));
         sb.append(AHTML.addRowMultiColumnTable(new String[] {AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Title: ",
               smaMgr.getSma().getDescriptiveName())}));
         sb.append(AHTML.endMultiColumnTable());
         sb.append(AHTML.beginMultiColumnTable(100));
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
         //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Current State: ",
                     ((IWorldViewArtifact) smaMgr.getSma()).getWorldViewState()),
               //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Team: ",
                     ((IWorldViewArtifact) smaMgr.getSma()).getWorldViewTeam()),
               //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Assignees: ",
                     ((IWorldViewArtifact) smaMgr.getSma()).getWorldViewActivePoc()),
               //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Originator: ",
                     ((IWorldViewArtifact) smaMgr.getSma()).getWorldViewOriginator()),
               //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Created: ", XDate.getDateStr(
                     smaMgr.getLog().getCreationDate(), XDate.MMDDYYHHMM))

         }));
         sb.append(AHTML.endMultiColumnTable());
         sb.append(AHTML.beginMultiColumnTable(100));
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
               //
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Workflow: ", smaMgr.getSma().getArtifactTypeName()),
               AHTML.getLabelValueStr(AHTML.LABEL_FONT, "HRID: ", smaMgr.getSma().getHumanReadableId()),
               (smaMgr.getSma().getParentActionArtifact() == null ? "" : AHTML.getLabelValueStr(AHTML.LABEL_FONT,
                     "Action HRID: ", smaMgr.getSma().getParentActionArtifact().getHumanReadableId()))}));
         sb.append(AHTML.endMultiColumnTable());
         if (workFlowTab != null) sb.append(workFlowTab.getHtml());
         if (taskComposite != null) sb.append(taskComposite.getHtml());
         sb.append(AHTML.newline());
         sb.append(smaMgr.getLog().getHtml());
         XResultData resultData = new XResultData(AtsPlugin.getLogger());
         resultData.addRaw(sb.toString());
         resultData.report(smaMgr.getSma().getDescriptiveName(), Manipulations.RAW_HTML);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

   }

}
