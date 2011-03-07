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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkflowMetricsHeader extends Composite {

   private final AbstractWorkflowArtifact awa;
   private Label percentLabel, hoursSpentLabel, remainHoursLabel;
   private SMAPercentCompleteHeader totalPercentHeader;
   private SMAEstimatedHoursHeader estimatedHoursHeader;

   public SMAWorkflowMetricsHeader(Composite parent, XFormToolkit toolkit, AbstractWorkflowArtifact awa, SMAEditor editor, IManagedForm managedForm) {
      super(parent, SWT.NONE);
      this.awa = awa;
      try {

         int numColumns = 8;
         if (!awa.getWorkDefinition().isStateWeightingEnabled()) {
            numColumns = 10;
         }
         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(numColumns, false));
         setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         if (awa.getWorkDefinition().isStateWeightingEnabled()) {
            percentLabel =
               FormsUtil.createLabelValue(toolkit, this, "Total Percent: ", "",
                  "Calculation: Sum of percent for all states (including all tasks and reviews) / # statusable states (if configured)");
         } else {
            totalPercentHeader = new SMAPercentCompleteHeader(this, 2, awa, editor);
         }
         estimatedHoursHeader = new SMAEstimatedHoursHeader(this, 2, awa, editor);
         hoursSpentLabel =
            FormsUtil.createLabelValue(toolkit, this, "Total Hours Spent: ", "",
               "Calculation: Sum of all hours spent for all tasks, reviews and in each state");
         remainHoursLabel =
            FormsUtil.createLabelValue(toolkit, this, "Remaining Hours: ", "",
               RemainingHoursColumn.getInstance().getDescription());

         refresh();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void refresh() {
      if (hoursSpentLabel.isDisposed()) {
         return;
      }
      try {
         if (totalPercentHeader != null) {
            totalPercentHeader.refresh();
         }
         if (percentLabel != null && !percentLabel.isDisposed()) {
            percentLabel.setText(String.valueOf(PercentCompleteTotalColumn.getPercentCompleteTotal(awa)));
         }
         if (estimatedHoursHeader != null) {
            estimatedHoursHeader.refresh();
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            hoursSpentLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(HoursSpentTotalColumn.getHoursSpentTotal(awa))));
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            Result result = RemainingHoursColumn.isRemainingHoursValid(awa);
            if (result.isFalse()) {
               remainHoursLabel.setText("Error" + result.getText());
            } else {
               remainHoursLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(RemainingHoursColumn.getRemainingHours(awa))));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      if (percentLabel != null) {
         percentLabel.update();
      }
      layout();
   }

}
