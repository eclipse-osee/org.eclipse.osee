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
package org.eclipse.osee.ats.core.util.chart;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.ILineChart;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class LineChart implements ILineChart {

   private final Map<String, List<Double>> datasets = new HashMap<>();
   private final Map<String, String> rgbs = new HashMap<>();
   private String title;
   private String xAxisLabel;
   private String yAxisLabel;
   private List<String> xAxisLabels = new LinkedList<>();
   private String urlToGet;
   private final AtsApi atsApi;
   private final XResultData results = new XResultData();

   public LineChart(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @JsonIgnore
   public String getChart() {
      if (!Strings.isValid(title)) {
         results.error("Title must be specified");
      }
      if (!Strings.isValid(xAxisLabel)) {
         results.error("XAxisLabel must be specified");
      }
      if (!Strings.isValid(yAxisLabel)) {
         results.error("YAxisLabel must be specified");
      }
      if (datasets.isEmpty()) {
         results.error("DataSets can not be empty.");
      }

      try {
         // Fill LineChart.json
         String htmlChart = OseeInf.getResourceContents("web/agilebl/LineChart.html", getClass());
         htmlChart = htmlChart.replaceFirst("PUT_TITLE_HERE", title);
         htmlChart = htmlChart.replaceFirst("PUT_REPORT_DATE_HERE", DateUtil.getMMDDYYHHMM());
         htmlChart = htmlChart.replaceFirst("PUT_LABELS_HERE", Collections.toString(",", xAxisLabels));

         List<String> dataSetStrs = new LinkedList<>();
         for (Entry<String, List<Double>> entry : datasets.entrySet()) {

            String dataSetStr = OseeInf.getResourceContents("web/agilebl/LineDataset.json", getClass());
            dataSetStr = dataSetStr.replaceFirst("PUT_LABEL_HERE", entry.getKey());
            List<String> valueStrs = new LinkedList<>();
            for (Double value : entry.getValue()) {
               valueStrs.add("\"" + String.valueOf(value.intValue()) + "\"");
            }
            dataSetStr = dataSetStr.replaceFirst("PUT_DATA_HERE", Collections.toString(",", valueStrs));
            dataSetStr = dataSetStr.replaceAll("PUT_RGB_HERE", rgbs.get(entry.getKey()));
            dataSetStrs.add(dataSetStr);
         }
         String dataSetStr = Collections.toString(",", dataSetStrs);
         htmlChart = htmlChart.replaceFirst("PUT_DATASETS_HERE", dataSetStr);

         htmlChart = htmlChart.replaceFirst("PUT_XAXIS_LABEL_HERE", getxAxisLabel());
         htmlChart = htmlChart.replaceFirst("PUT_YAXIS_LABEL_HERE", getyAxisLabel());
         htmlChart = htmlChart.replaceFirst("PUT_TITLE_HERE", getTitle());
         htmlChart = AtsUtil.resolveAjaxToBaseApplicationServer(htmlChart, atsApi);
         return htmlChart;
      } catch (Exception ex) {
         results.errorf("Exception generating LineChart [%s]", Lib.exceptionToString(ex));
      }
      return null;
   }

   public void addLine(String label, List<Double> values, int red, int green, int blue) {
      datasets.put(label, values);
      rgbs.put(label, String.format("%s, %s, %s", red, green, blue));
   }

   public void addLine(String label, List<Double> values, String rgb) {
      datasets.put(label, values);
      rgbs.put(label, rgb);
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getxAxisLabel() {
      return xAxisLabel;
   }

   public void setxAxisLabel(String xAxisLabel) {
      this.xAxisLabel = xAxisLabel;
   }

   public String getyAxisLabel() {
      return yAxisLabel;
   }

   public void setyAxisLabel(String yAxisLabel) {
      this.yAxisLabel = yAxisLabel;
   }

   public Map<String, List<Double>> getDatasets() {
      return datasets;
   }

   public String getTitle() {
      return title;
   }

   public List<String> getxAxisLabels() {
      return xAxisLabels;
   }

   public void setxAxisLabels(List<String> xAxisLabels) {
      this.xAxisLabels = xAxisLabels;
   }

   public String getUrlToGet() {
      return urlToGet;
   }

   public void setUrlToGet(String urlToGet) {
      this.urlToGet = urlToGet;
   }

   public XResultData getResults() {
      return results;
   }

}
