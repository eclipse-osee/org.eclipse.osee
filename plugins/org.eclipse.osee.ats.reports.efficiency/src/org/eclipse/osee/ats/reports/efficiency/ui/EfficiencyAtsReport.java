/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.efficiency.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.reports.AtsReport;
import org.eclipse.osee.ats.reports.ReportTabFactory;
import org.eclipse.osee.ats.reports.efficiency.internal.Activator;
import org.eclipse.osee.ats.reports.efficiency.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Praveen Joseph
 */
public class EfficiencyAtsReport implements AtsReport<IAtsTeamDefinition, Object> {

   @Override
   public String getName() {
      return "Efficiency Report";
   }

   @Override
   public KeyedImage getKeyedImage() {
      return AtsImage.REPORT;
   }

   @Override
   public IAtsTeamDefinition getInputParameters() throws OseeCoreException {
      IAtsTeamDefinition atsTeamDefinition = null;
      TeamDefinitionDialog dialog =
         new TeamDefinitionDialog("Team Efficiency Report", "Please Select the Team Definition");
      dialog.setInput(TeamDefinitions.getTeamReleaseableDefinitions(Active.Both, AtsClientService.get().getConfig()));
      int open = dialog.open();
      if (open == 0) {
         atsTeamDefinition = dialog.getSelectedFirst();
      }
      return atsTeamDefinition;
   }

   @Override
   public IOperation createReportOperation(IAtsTeamDefinition input, Object output, TableLoadOption... tableLoadOptions) {
      return new LoadEfficiencyDataOperation(input);
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
            tabs.add(createTab("Team Efficiency", "reports/TeamEfficiencyBar.rptdesign"));
            tabs.add(createTab("Team Efficiency Trend", "reports/TeamEfficiencyLine.rptdesign"));
            return tabs;
         }
      });
   }

   private IResultsEditorTab createTab(String name, String rptDesingName) {
      return ReportTabFactory.createBirtReportTab(Activator.PLUGIN_ID, name, rptDesingName);
   }
}
