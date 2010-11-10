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
import org.eclipse.osee.ats.field.RemainingHoursColumn;
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

/**
 * @author Donald G. Dunne
 */
public class SMAWorkflowMetricsHeader extends Composite {

   private final AbstractWorkflowArtifact sma;
   private Label percentLabel, estHoursLabel, hoursSpentLabel, remainHoursLabel;

   public SMAWorkflowMetricsHeader(Composite parent, XFormToolkit toolkit, AbstractWorkflowArtifact sma) {
      super(parent, SWT.NONE);
      this.sma = sma;
      try {

         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(8, false));
         setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         percentLabel =
            FormsUtil.createLabelValue(toolkit, this, "Total Percent: ", "",
               "Calculation: sum of percent for all states (including all tasks and reviews) / # statusable states");
         estHoursLabel =
            FormsUtil.createLabelValue(toolkit, this, "Total Estimated Hours: ", "",
               "Calculation: sum estimated hours for workflow and all tasks and reviews");
         hoursSpentLabel =
            FormsUtil.createLabelValue(toolkit, this, "Total Hours Spent: ", "",
               "Calculation: sum of all hours spent for all tasks, reviews and in each state");
         remainHoursLabel =
            FormsUtil.createLabelValue(toolkit, this, "Remaining Hours: ", "",
               RemainingHoursColumn.getInstance().getDescription());

         refresh();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void refresh() {
      if (percentLabel.isDisposed()) {
         return;
      }
      try {
         if (!percentLabel.isDisposed()) {
            percentLabel.setText(String.valueOf(sma.getPercentCompleteSMATotal()));
         }
         if (estHoursLabel != null && !estHoursLabel.isDisposed()) {
            estHoursLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(sma.getEstimatedHoursTotal())));
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            hoursSpentLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(sma.getHoursSpentSMATotal())));
         }
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) {
            Result result = RemainingHoursColumn.isRemainingHoursValid(sma);
            if (result.isFalse()) {
               remainHoursLabel.setText("Error" + result.getText());
            } else {
               remainHoursLabel.setText(String.valueOf(AtsUtil.doubleToI18nString(RemainingHoursColumn.getRemainingHours(sma))));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      percentLabel.update();
      layout();
   }

}
