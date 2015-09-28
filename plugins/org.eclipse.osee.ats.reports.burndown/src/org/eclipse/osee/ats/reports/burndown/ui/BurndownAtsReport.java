/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.reports.AtsReport;
import org.eclipse.osee.ats.reports.ReportTabFactory;
import org.eclipse.osee.ats.reports.burndown.internal.Activator;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Praveen Joseph
 */
public class BurndownAtsReport implements AtsReport<BurdownInputParameters, Object> {

   @Override
   public String getName() {
      return "Burndown Reports";
   }

   @Override
   public KeyedImage getKeyedImage() {
      return AtsImage.REPORT;
   }

   @Override
   public BurdownInputParameters getInputParameters() {
      BurdownInputParameters params = null;
      BurndownSelectionDialog dlg = new BurndownSelectionDialog(Active.Both);
      int open = dlg.open();
      if (open == 0) {
         Artifact version = dlg.getSelectedVersion();
         Date startDate = dlg.getStartDate();
         Date endDate = dlg.getEndDate();
         params = new BurdownInputParameters(version, startDate, endDate);
      }
      return params;
   }

   @Override
   public IOperation createReportOperation(BurdownInputParameters input, Object output, TableLoadOption... tableLoadOptions) {
      return new LoadBurndownDataOperation(input.getVersion(), input.getStartDate(), input.getEndDate());
   }

   @Override
   public Object createOutputParameters() {
      // None using a static shared object
      return null;
   }

   @Override
   public void displayResults(Object output) {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return getName();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            List<IResultsEditorTab> tabs = new ArrayList<>();
            tabs.add(createTab("Hour Burndown", "reports/HourBurndown.rptdesign"));
            tabs.add(createTab("Issue Burndown", "reports/IssueBurndown.rptdesign"));
            return tabs;
         }
      });
   }

   private IResultsEditorTab createTab(String name, String rptDesingName) {
      return ReportTabFactory.createBirtReportTab(Activator.PLUGIN_ID, name, rptDesingName);
   }
}
