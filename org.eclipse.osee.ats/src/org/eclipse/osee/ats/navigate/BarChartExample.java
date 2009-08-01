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
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.chart.ResultsEditorChartTab;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

public class BarChartExample extends XNavigateItemAction {

   /**
    * @param parent
    * @param action
    */
   public BarChartExample(XNavigateItem parent) {
      super(parent, "Bar Chart Example", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return "Example Bar Chart";
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            tabs.add(new ResultsEditorChartTab("Chart", createMyChart()));
            tabs.add(getReportHtmlTab());
            return tabs;
         }

      });
   }

   private IResultsEditorTab getReportHtmlTab() {
      StringBuffer sb = new StringBuffer();
      sb.append("Example Bar Chart Data");
      sb.append(AHTML.beginMultiColumnTable(95, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Title", "Status"}));
      for (int x = 0; x < 3; x++)
         sb.append(AHTML.addRowMultiColumnTable(new String[] {"Type " + x, "Title " + x, x + ""}));
      sb.append(AHTML.endMultiColumnTable());
      return new ResultsEditorHtmlTab("Example Bar Chart Data", "Report", AHTML.simplePage(sb.toString()));
   }

   @SuppressWarnings( {"deprecation", "unchecked"})
   public static Chart createMyChart() {
      // bart charts are based on charts that contain axes
      ChartWithAxes cwaBar = ChartWithAxesImpl.create();
      cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
      cwaBar.getBlock().getOutline().setVisible(true);
      cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

      // customize the plot
      Plot p = cwaBar.getPlot();
      p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
      p.getOutline().setVisible(false);

      cwaBar.getTitle().getLabel().getCaption().setValue("Example Bar Chart");

      // customize the legend
      Legend lg = cwaBar.getLegend();
      lg.getText().getFont().setSize(16);
      lg.getInsets().set(10, 5, 0, 0);
      lg.setAnchor(Anchor.NORTH_LITERAL);

      // customize the X-axis
      Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
      xAxisPrimary.setType(AxisType.TEXT_LITERAL);
      xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
      xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
      xAxisPrimary.getTitle().setVisible(false);

      // customize the Y-axis
      Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
      yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
      yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
      yAxisPrimary.getLabel().getCaption().getFont().setRotation(90);

      // initialize a collection with the X-series data
      java.util.Vector<String> vs = new java.util.Vector<String>();
      vs.add("zero");
      vs.add("one");
      vs.add("two");

      TextDataSet categoryValues = TextDataSetImpl.create(vs);

      // initialize a collection with the Y-series data
      ArrayList<Double> vn1 = new ArrayList<Double>();
      vn1.add(new Double(25));
      vn1.add(new Double(35));
      vn1.add(new Double(-45));

      NumberDataSet orthoValues1 = NumberDataSetImpl.create(vn1);

      // create the category base series
      Series seCategory = SeriesImpl.create();
      seCategory.setDataSet(categoryValues);

      // create the value orthogonal series
      BarSeries bs1 = (BarSeries) BarSeriesImpl.create();
      bs1.setSeriesIdentifier("My Bar Series");
      bs1.setDataSet(orthoValues1);
      bs1.setRiserOutline(null);
      bs1.getLabel().setVisible(true);
      bs1.setLabelPosition(Position.INSIDE_LITERAL);

      // wrap the base series in the X-axis series definition
      SeriesDefinition sdX = SeriesDefinitionImpl.create();
      sdX.getSeriesPalette().update(0); // set the colors in the palette
      xAxisPrimary.getSeriesDefinitions().add(sdX);
      sdX.getSeries().add(seCategory);

      // wrap the orthogonal series in the X-axis series definition
      SeriesDefinition sdY = SeriesDefinitionImpl.create();
      sdY.getSeriesPalette().update(1); // set the color in the palette
      yAxisPrimary.getSeriesDefinitions().add(sdY);
      sdY.getSeries().add(bs1);

      return cwaBar;
   }

}
