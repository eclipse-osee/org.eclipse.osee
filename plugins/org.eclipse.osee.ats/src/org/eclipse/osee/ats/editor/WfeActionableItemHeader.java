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

import java.util.logging.Level;
import org.eclipse.osee.ats.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
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
   private final AbstractWorkflowArtifact sma;

   public WfeActionableItemHeader(Composite parent, XFormToolkit toolkit, AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super(parent, SWT.NONE);
      this.sma = sma;
      try {
         final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) sma;

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

   private void refresh() {
      if (label.isDisposed()) {
         return;
      }
      final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) sma;
      ActionArtifact parentAction = teamWf.getParentActionArtifact();
      if (parentAction == null) {
         label.setText(" " + "Error: No Parent Action.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else if (parentAction.getActionableItems().isEmpty()) {
         label.setText(" " + "Error: No Actionable Items identified.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else {
         StringBuffer sb = new StringBuffer(
            AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItemsStr(teamWf));
         if (AtsClientService.get().getWorkItemService().getTeams(parentAction).size() > 1) {
            sb.append("         Other: ");
            for (IAtsTeamWorkflow workflow : AtsClientService.get().getWorkItemService().getTeams(parentAction)) {
               if (workflow.notEqual(teamWf)) {
                  sb.append(
                     AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItemsStr(
                        workflow));
                  sb.append(", ");
               }
            }
         }
         label.setText(sb.toString().replaceFirst(", $", ""));
         label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         label.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      }
      label.update();
      layout();
   }
}
