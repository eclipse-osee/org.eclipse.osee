/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import java.util.Date;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TaskEstLabelProvider extends XViewerLabelProvider {

   private final TaskEstXViewer xTaskEstViewer;
   private final AtsApi atsApi;

   public TaskEstLabelProvider(TaskEstXViewer xTaskEstViewer) {
      super(xTaskEstViewer);
      this.xTaskEstViewer = xTaskEstViewer;
      atsApi = AtsApiService.get();
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      TaskEstDefinition taskDef = (TaskEstDefinition) element;
      if (xCol.equals(TaskEstFactory.Check_Col)) {
         if (taskDef.isManual()) {
            return ImageManager.getImage(AtsImage.TASK);
         }
         if (taskDef.hasTask()) {
            return null;
         }
         if (taskDef.isChecked()) {
            return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_TRUE);
         } else {
            return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_UNSET);
         }
      }
      if (xCol.equals(TaskEstFactory.Assignee_Col)) {
         if (taskDef.hasTask()) {
            return AssigneeColumnUI.instance.getColumnImage(taskDef.getTask(), xCol, columnIndex);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      TaskEstDefinition ted = (TaskEstDefinition) element;
      if (xCol.equals(TaskEstFactory.Check_Col)) {
         if (ted.isManual()) {
            return "Manual";
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Name_Col)) {
         if (ted.hasTask()) {
            return ted.getTask().getName();
         }
         return ted.getName();
      } else if (xCol.equals(TaskEstFactory.Status_Col)) {
         if (ted.hasTask()) {
            return atsApi.getColumnService().getColumnText(AtsColumnId.State.getId(), ted.getTask());
         }
         return "No Task";
      } else if (xCol.equals(TaskEstFactory.Assignee_Col)) {
         if (ted.hasTask()) {
            return atsApi.getColumnService().getColumnText(AtsColumnId.Assignees.getId(), ted.getTask());
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Description_Col)) {
         if (ted.hasTask()) {
            return atsApi.getAttributeResolver().getSoleAttributeValue(ted.getTask(), AtsAttributeTypes.Description,
               "");
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Assumptions_Col)) {
         if (ted.hasTask()) {
            return atsApi.getAttributeResolver().getSoleAttributeValue(ted.getTask(), AtsAttributeTypes.Assumptions,
               "");
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Notes_Col)) {
         if (ted.hasTask()) {
            return atsApi.getAttributeResolver().getSoleAttributeValue(ted.getTask(), AtsAttributeTypes.WorkflowNotes,
               "");
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Estimated_Points_Col)) {
         if (ted.hasTask()) {
            return String.valueOf(atsApi.getAttributeResolver().getSoleAttributeValueAsString(ted.getTask(),
               xTaskEstViewer.getPointsAttrType(), ""));
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Attachments_Col)) {
         if (ted.hasTask()) {
            int count = atsApi.getRelationResolver().getRelated(ted.getTask(),
               CoreRelationTypes.SupportingInfo_SupportingInfo).size();
            if (count > 0) {
               return String.valueOf(count);
            }
         }
         return "";
      } else if (xCol.equals(TaskEstFactory.Estimated_Completion_Date_Col)) {
         if (ted.hasTask()) {
            Date date = atsApi.getAttributeResolver().getSoleAttributeValue(ted.getTask(),
               AtsAttributeTypes.EstimatedCompletionDate, null);
            if (date != null) {
               return DateUtil.getMMDDYY(date);
            }
         }
         return "";
      }
      return "";
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      TaskEstDefinition taskDef = (TaskEstDefinition) element;
      if (xCol.equals(TaskEstFactory.Status_Col)) {
         if (taskDef.hasTask()) {
            IAtsTask task = taskDef.getTask();
            if (task.isInWork()) {
               return Displays.getSystemColor(SWT.COLOR_BLUE);
            } else if (task.isCompleted()) {
               return Displays.getSystemColor(SWT.COLOR_DARK_GREEN);
            } else if (task.isCancelled()) {
               return Displays.getSystemColor(SWT.COLOR_DARK_RED);
            }
         }
      }
      return null;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}