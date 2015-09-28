/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.reports.AtsReport;
import org.eclipse.osee.ats.reports.ReportTabFactory;
import org.eclipse.osee.ats.reports.split.Activator;
import org.eclipse.osee.ats.util.widgets.dialog.TeamVersionListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Praveen Joseph
 */
public class DistributionAtsReport implements AtsReport<IAtsVersion, Object> {

   @Override
   public String getName() {
      return "Distribution Reports";
   }

   @Override
   public KeyedImage getKeyedImage() {
      return AtsImage.REPORT;
   }

   @Override
   public IAtsVersion getInputParameters() {
      IAtsVersion param = null;
      TeamVersionListDialog dlg = new TeamVersionListDialog(Active.Both);
      int open = dlg.open();
      if (open == 0) {
         param = dlg.getSelectedVersion();
      }
      return param;
   }

   @Override
   public IOperation createReportOperation(IAtsVersion input, Object output, TableLoadOption... tableLoadOptions) {
      return new LoadDistributionDataOperation(input);
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
            tabs.add(createTab("Actionable Item", "reports/aiDistribution.rptdesign"));
            tabs.add(createTab("Team-Work", "reports/teamWorkDistribution.rptdesign"));
            tabs.add(createTab("Workflow State", "reports/stateWorkDistribution.rptdesign"));
            return tabs;
         }
      });
   }

   private IResultsEditorTab createTab(String name, String rptDesingName) {
      return ReportTabFactory.createBirtReportTab(Activator.PLUGIN_ID, name, rptDesingName);
   }
}
