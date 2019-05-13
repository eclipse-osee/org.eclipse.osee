/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeTeamAndIdsHeader extends Composite implements IWfeEventHandle {

   private final IAtsWorkItem workItem;
   Label teamWfIdValueLabel, parentIdValueLabel, idValueLabel, actionIdValueLabel;

   public WfeTeamAndIdsHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(3, true));
      editor.getToolkit().adapt(this);

      try {

         if (workItem.isTeamWorkflow()) {
            teamWfIdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Team: ", "");
         } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
            parentIdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Parent Id: ", "");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         idValueLabel =
            FormsUtil.createLabelValue(editor.getToolkit(), this, workItem.getArtifactTypeName() + " Id: ", "");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         IAtsAction action = workItem.getParentAction();
         if (action != null) {
            actionIdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Action Id: ", "");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      refresh();
      if (workItem.getStoreObject() instanceof Artifact) {
         editor.registerEvent(this, (Artifact) workItem.getStoreObject());
      }

   }

   @Override
   public void refresh() {
      idValueLabel.setText(workItem.getAtsId());
      if (workItem.isTeamWorkflow()) {
         teamWfIdValueLabel.setText(((TeamWorkFlowArtifact) workItem).getTeamName());
      } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
         IAtsTeamWorkflow parentTeamWorkflow = workItem.getParentTeamWorkflow();
         parentIdValueLabel.setText(AtsClientService.get().getWorkItemService().getCombinedPcrId(parentTeamWorkflow));
      }
      IAtsAction action = workItem.getParentAction();
      if (action != null) {
         actionIdValueLabel.setText(action.getAtsId());
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }
}
