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
package org.eclipse.osee.framework.ui.skynet.results.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Marker;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.BaseSampleData;
import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.OrthogonalSampleData;
import org.eclipse.birt.chart.model.data.SampleData;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.chart.ResultsEditorChartTab;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorExample extends XNavigateItemAction {

   public static String TITLE = "Results Editor Example";
   private static enum Columns {
      Date, Priority_123_Open_Bugs, Goal;
   };
   List<String> chartDateStrs =
         Arrays.asList("09/07/2008", "09/21/2008", "10/05/2008", "10/19/2008", "11/02/2008", "11/16/2008",
               "11/30/2008", "12/14/2008", "12/28/2008", "01/11/2009", "01/25/2009", "02/08/2009", "02/22/2009",
               "03/08/2009", "03/22/2009", "04/05/2009", "04/19/2009");
   List<Double> chartValueStrs =
         Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0, 165.0, 171.0, 179.0, 178.0, 177.0, 164.0,
               159.0, 159.0, 157.0, 157.0);
   List<Double> chartValueStrsGoal =
         Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0, 165.0, 171.0, 179.0, 177.0, 175.0, 173.0,
               171.0, 169.0, 167.0, 165.0);

   /**
    * @param parent
    */
   public ResultsEditorExample(XNavigateItem parent) {
      super(parent, TITLE, FrameworkImage.ADMIN);
   }

   public String getStatusReport() {

      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, TITLE));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {Columns.Date.name(),
            Columns.Priority_123_Open_Bugs.name(), Columns.Goal.name()}));
      for (int x = 0; x < chartDateStrs.size(); x++) {
         sb.append(AHTML.addRowMultiColumnTable(chartDateStrs.get(x), "" + chartValueStrs.get(x),
               "" + chartValueStrsGoal.get(x)));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   public Chart createChart() {
      ChartWithAxes cwaLine = ChartWithAxesImpl.create();
      cwaLine.setType("Line Chart"); //$NON-NLS-1$
      cwaLine.setSubType("Overlay"); //$NON-NLS-1$

      // Plot
      cwaLine.getBlock().setBackground(ColorDefinitionImpl.WHITE());
      Plot p = cwaLine.getPlot();
      p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));

      // Title
      cwaLine.getTitle().getLabel().getCaption().setValue(
            "Action Item Backlog - Priority 1-3 Bugs\nGoal: 50% Reduction - Baseline: YE 2008");//$NON-NLS-1$

      // Legend
      Legend lg = cwaLine.getLegend();
      lg.setItemType(LegendItemType.SERIES_LITERAL);

      // X-Axis
      Axis xAxisPrimary = cwaLine.getPrimaryBaseAxes()[0];
      xAxisPrimary.setType(AxisType.TEXT_LITERAL);
      xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
      xAxisPrimary.getOrigin().setType(IntersectionType.MIN_LITERAL);

      // Y-Axis
      Axis yAxisPrimary = cwaLine.getPrimaryOrthogonalAxis(xAxisPrimary);
      yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);

      // Data Set
      TextDataSet categoryValues = TextDataSetImpl.create(chartDateStrs.toArray(new String[chartDateStrs.size()]));
      NumberDataSet orthoValues1 = NumberDataSetImpl.create(chartValueStrs.toArray(new Double[chartValueStrs.size()]));
      NumberDataSet orthoValuesGoal =
            NumberDataSetImpl.create(chartValueStrsGoal.toArray(new Double[chartValueStrsGoal.size()]));

      SampleData sd = DataFactory.eINSTANCE.createSampleData();
      BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData();
      sdBase.setDataSetRepresentation("");//$NON-NLS-1$
      sd.getBaseSampleData().add(sdBase);

      OrthogonalSampleData sdOrthogonal1 = DataFactory.eINSTANCE.createOrthogonalSampleData();
      sdOrthogonal1.setDataSetRepresentation("");//$NON-NLS-1$
      sdOrthogonal1.setSeriesDefinitionIndex(0);
      sd.getOrthogonalSampleData().add(sdOrthogonal1);

      OrthogonalSampleData sdOrthogonal2 = DataFactory.eINSTANCE.createOrthogonalSampleData();
      sdOrthogonal2.setDataSetRepresentation("");//$NON-NLS-1$
      sdOrthogonal2.setSeriesDefinitionIndex(1);
      sd.getOrthogonalSampleData().add(sdOrthogonal2);

      cwaLine.setSampleData(sd);

      // X-Series
      Series seCategory = SeriesImpl.create();
      seCategory.setDataSet(categoryValues);
      SeriesDefinition sdX = SeriesDefinitionImpl.create();

      xAxisPrimary.getSeriesDefinitions().add(sdX);
      sdX.getSeries().add(seCategory);

      // Y-Series
      LineSeries ls1 = (LineSeries) LineSeriesImpl.create();
      ls1.setDataSet(orthoValues1);
      ls1.setSeriesIdentifier("Count");
      ls1.getLineAttributes().setColor(ColorDefinitionImpl.BLUE());
      for (int i = 0; i < ls1.getMarkers().size(); i++) {
         ((Marker) ls1.getMarkers().get(i)).setType(MarkerType.TRIANGLE_LITERAL);
      }
      ls1.getLabel().setVisible(true);

      LineSeries ls2 = (LineSeries) LineSeriesImpl.create();
      ls2.setDataSet(orthoValuesGoal);
      ls2.setSeriesIdentifier("Goal (100)");
      ls2.getLineAttributes().setColor(ColorDefinitionImpl.GREEN());
      for (int i = 0; i < ls2.getMarkers().size(); i++) {
         ((Marker) ls2.getMarkers().get(i)).setType(MarkerType.TRIANGLE_LITERAL);
      }
      ls2.getLabel().setVisible(true);

      SeriesDefinition sdY = SeriesDefinitionImpl.create();
      sdY.getSeriesPalette().shift(-2);
      yAxisPrimary.getSeriesDefinitions().add(sdY);
      sdY.getSeries().add(ls2);
      sdY.getSeries().add(ls1);

      return cwaLine;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      final String html = AHTML.simplePage(getStatusReport());
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
            for (int x = 0; x < chartDateStrs.size(); x++) {
               rows.add(new ResultsXViewerRow(new String[] {chartDateStrs.get(x),
                     String.valueOf(chartValueStrs.get(x)), String.valueOf(chartValueStrsGoal.get(x))}));
            }
            List<XViewerColumn> columns =
                  Arrays.asList(new XViewerColumn(Columns.Date.name(), Columns.Date.name(), 80, SWT.LEFT, true,
                        SortDataType.Date, false, ""), new XViewerColumn(Columns.Priority_123_Open_Bugs.name(),
                        Columns.Priority_123_Open_Bugs.name(), 80, SWT.LEFT, true, SortDataType.Integer, false, ""),
                        new XViewerColumn(Columns.Goal.name(), Columns.Goal.name(), 80, SWT.LEFT, true,
                              SortDataType.Integer, false, ""));

            return Arrays.asList(new ResultsEditorChartTab("Chart", createChart()), new ResultsEditorTableTab("Data",
                  columns, rows), new ResultsEditorHtmlTab(TITLE, "Report", html));
         }

      });
   }

}
