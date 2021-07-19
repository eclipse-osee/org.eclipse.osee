/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.taskest;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.ats.ide.world.WorldLabelProvider;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstLabelProvider extends WorldLabelProvider {

   private final AtsApi atsApi;

   public XTaskEstLabelProvider(TaskXViewer xTaskViewer) {
      super(xTaskViewer);
      this.atsApi = AtsApiService.get();
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof TaskEstDefinition) {
         return null;
      }
      return super.getForeground(element, xCol, columnIndex);
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof TaskEstDefinition) {
         return null;
      }
      return super.getBackground(element, xCol, columnIndex);
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof TaskEstDefinition) {
         TaskEstDefinition ted = (TaskEstDefinition) element;
         if (xViewerColumn.getName().equals("Title")) {
            return ted.getName();
         } else if (xViewerColumn.getName().equals("Select")) {
            return ted.isChecked() ? "Selected" : "";
         }
      } else if (element instanceof IAtsTask) {
         IAtsTask task = (IAtsTask) element;
         if (xViewerColumn.getName().equals("Select")) {
            if (task.getTags().contains(TaskEstUtil.TASK_EST_CANNED)) {
               return "Canned";
            } else if (task.getTags().contains(TaskEstUtil.TASK_EST_MANUAL)) {
               return "Manual";
            } else {
               return "Other";
            }
         } else if (xViewerColumn.getName().equals("Attachments")) {
            int count =
               atsApi.getRelationResolver().getRelatedCount(task, CoreRelationTypes.SupportingInfo_SupportingInfo);
            if (count > 0) {
               return String.valueOf(count);
            } else {
               return "";
            }
         } else if (xViewerColumn.getName().equals("TLE Reviewed")) {
            return XAbstractSignDateAndByButton.getText((Artifact) task.getStoreObject(),
               AtsAttributeTypes.TleReviewedDate, AtsAttributeTypes.TleReviewedBy);
         } else if (xViewerColumn.getName().equals("Related Workflow")) {
            if (task.isCancelled()) {
               return "";
            }
            IAtsTeamWorkflow teamWf = TaskEstUtil.getWorkflow(task.getParentTeamWorkflow(), task, atsApi);
            if (teamWf != null) {
               return teamWf.toStringWithId();
            }
            return "Select to Create Workflow";
         }
      }
      String str = super.getColumnText(element, columnIndex);
      return str;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof TaskEstDefinition) {
         TaskEstDefinition ted = (TaskEstDefinition) element;
         if (xViewerColumn.getName().equals("Select")) {
            if (ted.isChecked()) {
               return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_TRUE);
            } else {
               return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_UNSET);
            }
         }
      } else if (element instanceof IAtsTask) {
         IAtsTask task = (IAtsTask) element;
         if (xViewerColumn.getName().equals("Select")) {
            return ImageManager.getImage(AtsImage.TASK);
         } else if (xViewerColumn.getName().equals("TLE Reviewed")) {
            if (atsApi.getAttributeResolver().getAttributeCount(task, AtsAttributeTypes.TleReviewedBy) > 0) {
               return ImageManager.getImage(AtsImage.CHECK_BLUE);
            }
         } else if (xViewerColumn.getName().equals("Related Workflow")) {
            if (task.isCancelled()) {
               return null;
            }
            IAtsTeamWorkflow teamWf = TaskEstUtil.getWorkflow(task.getParentTeamWorkflow(), task, atsApi);
            if (teamWf != null) {
               return ImageManager.getImage(AtsImage.WORKFLOW);
            }
            return ImageManager.getImage(FrameworkImage.ADD_GREEN);
         }
      }
      return null;
   }

}
