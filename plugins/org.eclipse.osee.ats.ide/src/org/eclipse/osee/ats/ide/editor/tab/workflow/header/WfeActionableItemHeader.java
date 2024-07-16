/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.ats.ide.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeActionableItemHeader extends Composite {

   private Label label;
   private final IAtsWorkItem workItem;

   public WfeActionableItemHeader(Composite parent, XFormToolkit toolkit, IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, SWT.NONE);
      this.workItem = workItem;
      try {
         final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) workItem;

         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(2, false));
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = 4;
         setLayoutData(gd);

         Hyperlink link = toolkit.createHyperlink(this, "Actionable Items: ", SWT.NONE);
         link.setToolTipText("Edit Actionable Items for the parent Action (this may add Team Workflows)");
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  EditActionableItemsAction.editActionableItems(teamWf);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         label = toolkit.createLabel(this, " ");
         refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      if (label.isDisposed()) {
         return;
      }
      final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) workItem;
      if (teamWf.getParentAction() == null) {
         label.setText("Error: No Parent Action");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
      else {
      IAtsAction parentAction = (IAtsAction) teamWf.getParentAction().getStoreObject();
      if (parentAction == null) {
         label.setText(" " + "Error: No Parent Action.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else if (AtsApiService.get().getActionableItemService().getActionableItems(parentAction).isEmpty()) {
         label.setText(" " + "Error: No Actionable Items identified.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else {
         StringBuffer sb =
            new StringBuffer(AtsApiService.get().getActionableItemService().getActionableItemsStr(teamWf));
         if (AtsApiService.get().getWorkItemService().getTeams(parentAction).size() > 1) {
            sb.append("         Other: ");
            for (IAtsTeamWorkflow workflow : AtsApiService.get().getWorkItemService().getTeams(parentAction)) {
               if (workflow.notEqual(teamWf)) {
                  sb.append(AtsApiService.get().getActionableItemService().getActionableItemsStr(workflow));
                  sb.append(", ");
               }
            }
         }
         label.setText(sb.toString().replaceFirst(", $", ""));
         label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         label.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      }
      }
      label.update();
      layout();
   }

}
