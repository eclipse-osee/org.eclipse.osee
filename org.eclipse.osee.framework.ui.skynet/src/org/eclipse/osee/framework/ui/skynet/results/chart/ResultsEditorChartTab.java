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
package org.eclipse.osee.framework.ui.skynet.results.chart;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.util.ImageCapture;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorChartTab implements IResultsEditorChartTab {

   private final Chart chart;
   private final String tabName;
   private Composite chartComposite;
   private Canvas chartCanvas;

   public ResultsEditorChartTab(String tabName, Chart chart) {
      this.tabName = tabName;
      this.chart = chart;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorChartTab#getChart()
    */
   @Override
   public Chart getChart() throws OseeCoreException {
      return chart;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#createTab(org.eclipse.swt.widgets.Composite, org.eclipse.osee.framework.ui.skynet.results.ResultsEditor)
    */
   @Override
   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
      Chart chart = getChart();
      chartComposite = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(chartComposite);
      createToolbar(toolBar);

      GridData gd = new GridData(GridData.FILL_BOTH);
      if (chart == null) {
         Label label = new Label(chartComposite, SWT.BORDER);
         label.setText("\n   No Chart Provided");
      } else {
         chartCanvas = new Canvas(chartComposite, SWT.NONE);
         chartCanvas.setLayoutData(gd);
         chartCanvas.addPaintListener(new ChartViewerSWT(chart));
      }

      return chartComposite;
   }

   private void createToolbar(ToolBar toolBar) {
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.PRINT));
      item.setToolTipText("Print this tab");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            ImageCapture iCapture = new ImageCapture(chartCanvas);
            iCapture.popupDialog();
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);
   }
}
