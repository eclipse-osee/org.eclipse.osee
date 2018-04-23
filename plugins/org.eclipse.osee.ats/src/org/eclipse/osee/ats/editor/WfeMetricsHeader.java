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
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
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

   private final AbstractWorkflowArtifact awa;
   private Label percentLabel, hoursSpentLabel, remainHoursLabel;
   private WfePercentCompleteHeader totalPercentHeader;
   private WfeEstimatedHoursHeader estimatedHoursHeader;

   public WfeMetricsHeader(Composite parent, XFormToolkit toolkit, AbstractWorkflowArtifact awa, WorkflowEditor editor, IManagedForm managedForm) {
      super(parent, SWT.NONE);
      this.awa = awa;
      try {

         int numColumns = 8;
         if (!AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(awa.getWorkDefinition())) {
            numColumns = 10;
         }
         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(numColumns, false));
         setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         if (AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(awa.getWorkDefinition())) {
            percentLabel = FormsUtil.createLabelValue(toolkit, this, "Total Percent: ", "",
               "Calculation: Sum of percent for all states (including all tasks and reviews) / # statusable states (if configured)");
         } else {
            totalPercentHeader = new WfePercentCompleteHeader(this, 2, awa, editor);
         }
         estimatedHoursHeader = new WfeEstimatedHoursHeader(this, 2, awa, editor);
         hoursSpentLabel = FormsUtil.createLabelValue(toolkit, this, "Total Hours Spent: ", "",
            "Calculation: Sum of all hours spent for all tasks, reviews and in each state");
         remainHoursLabel = FormsUtil.createLabelValue(toolkit, this, "Remaining Hours: ", "",
            RemainingHoursColumn.getInstance().getDescription());

         refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void refresh() {
      if (!Widgets.isAccessible(hoursSpentLabel)) {
         return;
      }
      try {
         if (totalPercentHeader != null) {
            totalPercentHeader.refresh();
         }
         if (percentLabel != null && !percentLabel.isDisposed()) {
            percentLabel.setText(String.valueOf(
               PercentCompleteTotalUtil.getPercentCompleteTotal(awa, AtsClientService.get().getServices())));
         }
         if (estimatedHoursHeader != null) {
            estimatedHoursHeader.refresh();
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            hoursSpentLabel.setText(String.valueOf(AtsUtilCore.doubleToI18nString(
               HoursSpentUtil.getHoursSpentTotal(awa, AtsClientService.get().getServices()))));
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            Result result = RemainingHoursColumn.isRemainingHoursValid(awa);
            if (result.isFalse()) {
               remainHoursLabel.setText("Error" + result.getText());
            } else {
               remainHoursLabel.setText(
                  String.valueOf(AtsUtilCore.doubleToI18nString(RemainingHoursColumn.getRemainingHours(awa))));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      if (percentLabel != null) {
         percentLabel.update();
      }
      layout();
   }

}
