/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile.operations;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.AgileSprintDateData;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.core.agile.SprintUtil;
import org.eclipse.osee.ats.core.util.chart.LineChart;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class SprintBurnupOperations implements IAgileSprintHtmlOperation {

   private final AtsApi atsApi;

   public SprintBurnupOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getReportHtml(long teamId, long sprintId) {
      try {
         LineChart chart = getChartData(teamId, sprintId);
         if (chart.getResults().isErrors()) {
            return AHTML.simplePage(chart.getResults().toString().replaceAll("\n", "<br/>"));
         }
         return chart.getChart();
      } catch (Exception ex) {
         throw new OseeWrappedException(Lib.exceptionToString(ex).replaceAll("\n", "<br/>"));
      }
   }

   public LineChart getChartData(long teamId, long sprintId) {
      LineChart chart = new LineChart(atsApi);
      ArtifactToken team = atsApi.getQueryService().getArtifact(teamId);
      AgileSprintData data = SprintUtil.getAgileSprintData(atsApi, teamId, sprintId, chart.getResults());
      XResultData results = data.validate();
      if (results.isErrors()) {
         return chart;
      }
      try {
         chart.setTitle(team.getName() + " - " + data.getSprintName() + " - Burnup");
         chart.setyAxisLabel(SprintUtil.POINTS);
         chart.setxAxisLabel(SprintUtil.DATES);

         // Add labels
         List<String> xDateList = new LinkedList<>();
         for (AgileSprintDateData date : data.getDates()) {
            xDateList.add("\"" + DateUtil.getMMDDYY(date.getDate()) + "\"");
         }
         chart.setxAxisLabels(xDateList);

         boolean hasUnplanned = data.getUnPlannedPoints() > 0;

         Date today = new Date();
         List<Double> totals = new ArrayList<>();
         for (AgileSprintDateData date : data.getDates()) {
            if (today.after(date.getDate())) {
               totals.add(date.getTotalRealizedPoints());
            }
         }
         chart.addLine(hasUnplanned ? SprintUtil.TOTAL_REALIZED_POINTS : SprintUtil.TOTAL_POINTS, totals,
            SprintUtil.RGB_BLACK);

         List<Double> values = new ArrayList<>();
         for (AgileSprintDateData date : data.getDates()) {
            if (today.after(date.getDate())) {
               Double completed = date.getCompletedPlannedPoints() + date.getCompletedUnPlannedPoints();
               values.add(Double.valueOf(completed.intValue()));
            }
         }
         chart.addLine(SprintUtil.TOTAL_COMPLETED, values, SprintUtil.RGB_GREEN);

         if (hasUnplanned) {
            List<Double> values2 = new ArrayList<>();
            for (AgileSprintDateData date : data.getDates()) {
               if (today.after(date.getDate())) {
                  Double completed = date.getCompletedPlannedPoints();
                  values2.add(Double.valueOf(completed.intValue()));
               }
            }
            chart.addLine(SprintUtil.COMPLETED_PLANNED, values2, SprintUtil.RGB_BLUE);

            List<Double> values1 = new ArrayList<>();
            for (AgileSprintDateData date : data.getDates()) {
               if (today.after(date.getDate())) {
                  Double completedUnPlanned = date.getCompletedUnPlannedPoints();
                  values1.add(Double.valueOf(completedUnPlanned.intValue()));
               }
            }
            chart.addLine(SprintUtil.COMPLETED_UN_PLANNED, values1, SprintUtil.RGB_YELLOW);
         }

         return chart;
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Can't return LineChart");
      }

   }

   @Override
   public AgileReportType getReportType() {
      return AgileReportType.Burn_Up;
   }

}
