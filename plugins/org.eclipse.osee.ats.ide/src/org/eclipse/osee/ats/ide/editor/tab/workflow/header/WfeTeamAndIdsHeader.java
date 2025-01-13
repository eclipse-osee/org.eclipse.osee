/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class WfeTeamAndIdsHeader extends Composite {

   private final IAtsWorkItem workItem;
   Text teamWfIdValue, parentIdValue, atsIdValue, pcrIdsValue, actionIdValue;

   public WfeTeamAndIdsHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(4, true));
      editor.getToolkit().adapt(this);

      try {

         if (workItem.isTeamWorkflow()) {
            teamWfIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Team: ", "").getSecond();
            teamWfIdValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
            parentIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Parent Id: ", "").getSecond();
            parentIdValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         atsIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "ATS Id: ", "").getSecond();
         atsIdValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         atsIdValue.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
               Clipboard clipboard = new Clipboard(null);
               try {
                  clipboard.setContents(new Object[] {workItem.getAtsId()},
                     new Transfer[] {TextTransfer.getInstance()});
               } finally {
                  clipboard.dispose();
               }
            }

         });
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         if (!workItem.getPcrIdsAll().isEmpty()) {
            pcrIdsValue = FormsUtil.createLabelText(editor.getToolkit(), this, "PCR Id(s): ", "").getSecond();
            pcrIdsValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         IAtsAction action = workItem.getParentAction();
         if (action != null) {
            actionIdValue = FormsUtil.createLabelText(editor.getToolkit(), this, "Action Id: ", "").getSecond();
            actionIdValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      refresh();
   }

   public void refresh() {
      if (Widgets.isAccessible(atsIdValue)) {
         Thread refreshThread = new Thread("Refresh Workflow Editor") {

            @Override
            public void run() {
               super.run();
               String pcrIdsValueStr = Collections.toString(", ", workItem.getPcrIdsAll());
               String teamWfIdValueStr = "";
               String parentIdValueStr = "";
               if (workItem.isTeamWorkflow()) {
                  teamWfIdValueStr = ((TeamWorkFlowArtifact) workItem).getTeamName();
               } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
                  IAtsTeamWorkflow parentTeamWorkflow = workItem.getParentTeamWorkflow();
                  parentIdValueStr = AtsApiService.get().getWorkItemService().getCombinedPcrId(parentTeamWorkflow);
               }
               IAtsAction action = workItem.getParentAction();
               String actionIdValueStr = "";
               if (action != null) {
                  actionIdValueStr = action.getAtsId();
               }

               final String fTeamWfIdValueStr = teamWfIdValueStr;
               final String fPcrIdsValueStr = pcrIdsValueStr;
               final String fParentIdValueStr = parentIdValueStr;
               final String fActionIdValueStrr = actionIdValueStr;
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     if (Widgets.isAccessible(atsIdValue)) {
                        atsIdValue.setText(workItem.getAtsId());
                     }
                     if (Widgets.isAccessible(pcrIdsValue)) {
                        pcrIdsValue.setText(fPcrIdsValueStr);
                     }
                     if (Widgets.isAccessible(teamWfIdValue)) {
                        teamWfIdValue.setText(fTeamWfIdValueStr);
                     }
                     if (Widgets.isAccessible(parentIdValue)) {
                        parentIdValue.setText(fParentIdValueStr);
                     }
                     if (Widgets.isAccessible(actionIdValue)) {
                        actionIdValue.setText(fActionIdValueStrr);
                     }
                  }
               });
            }

         };
         refreshThread.start();
      }
   }

}
