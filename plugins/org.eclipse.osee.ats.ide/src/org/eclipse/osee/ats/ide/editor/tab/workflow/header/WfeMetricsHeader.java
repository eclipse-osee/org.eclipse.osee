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
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumn;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WfeMetricsHeader extends Composite {

   private final IAtsWorkItem workItem;
   private Label percentLabel, hoursSpentLabel, remainHoursLabel;
   private WfePercentCompleteHeader totalPercentHeader;
   private WfeEstimatedHoursHeader estimatedHoursHeader;

   public WfeMetricsHeader(Composite parent, XFormToolkit toolkit, IAtsWorkItem workItem, WorkflowEditor editor, IManagedForm managedForm) {
      super(parent, SWT.NONE);
      this.workItem = workItem;
      try {

         int numColumns = 8;
         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(numColumns, false));
         setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         totalPercentHeader = new WfePercentCompleteHeader(this, 2, workItem, editor);
         totalPercentHeader.setBackground(parent.getParent().getParent().getBackground());
         estimatedHoursHeader = new WfeEstimatedHoursHeader(this, 2, workItem, editor);
         estimatedHoursHeader.setBackground(parent.getParent().getParent().getBackground());
         hoursSpentLabel = FormsUtil.createLabelValue(toolkit, this, "Total Hours Spent: ", "",
            "Calculation: Sum of all hours spent for all tasks, reviews and in each state");
         remainHoursLabel = FormsUtil.createLabelValue(toolkit, this, "Remaining Hours: ", "",
            RemainingHoursColumn.getInstance().getDescription());

         refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      if (!Widgets.isAccessible(hoursSpentLabel)) {
         return;
      }
      try {
         if (totalPercentHeader != null) {
            totalPercentHeader.refresh();
         }
         if (percentLabel != null && !percentLabel.isDisposed()) {
            percentLabel.setText(
               String.valueOf(AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(workItem)));
         }
         if (estimatedHoursHeader != null) {
            estimatedHoursHeader.refresh();
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            hoursSpentLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(
               AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal(workItem))));
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            Result result = RemainingHoursColumn.isRemainingHoursValid(workItem);
            if (result.isFalse()) {
               remainHoursLabel.setText(result.getText());
            } else {
               remainHoursLabel.setText(
                  String.valueOf(AtsUtil.doubleToI18nString(RemainingHoursColumn.getRemainingHours(workItem))));
            }
         }
         getParent().layout();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      if (percentLabel != null) {
         percentLabel.update();
      }
      layout();
   }

}
