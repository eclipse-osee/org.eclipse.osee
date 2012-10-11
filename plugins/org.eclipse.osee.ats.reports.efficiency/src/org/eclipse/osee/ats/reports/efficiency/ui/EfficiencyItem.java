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
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.reports.efficiency.team.TeamEfficiencyModel;
import org.eclipse.osee.ats.reports.efficiency.team.VersionEfficiency;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * Class to take inputs from user and populate TeamEfficiencyModel
 * 
 * @author Praveen Joseph
 */
public class EfficiencyItem extends XNavigateItemAction {

   /**
    * Constructor that calls its parent constructor
    * 
    * @param parent :
    */
   public EfficiencyItem(final XNavigateItem parent) {
      super(parent, "Efficiency Report", AtsImage.REPORT);
   }

   @Override
   public void run(final TableLoadOption... tableLoadOptions) throws Exception {
      // Get input from the user.
      TeamDefinitionDialog dlg =
         new TeamDefinitionDialog("Team Efficiency Report", "Please Select the Team Definition");
      dlg.setInput(TeamDefinitions.getTeamReleaseableDefinitions(Active.Both));
      int open = dlg.open();
      if (open == 0) {
         populateModel(dlg);
         ResultsEditor.open(new IResultsEditorProvider() {

            @Override
            public String getEditorName() {
               return "Efficiency Report";
            }

            @Override
            public List<IResultsEditorTab> getResultsEditorTabs() {
               List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
               tabs.add(new TeamEfficiencyTab());
               tabs.add(new TeamEfficiencyLineTab());
               return tabs;
            }

         });
      }
   }

   private boolean populateModel(final TeamDefinitionDialog dlg) {
      try {
         Object[] result = dlg.getResult();
         if ((result == null) || (result.length == 0)) {
            return false;
         }
         Object res = result[0];
         if (res instanceof IAtsTeamDefinition) {
            IAtsTeamDefinition teamDef = (IAtsTeamDefinition) res;
            List<VersionEfficiency> verEffs = new ArrayList<VersionEfficiency>();
            for (IAtsVersion version : teamDef.getVersions()) {
               VersionEfficiency eff = new VersionEfficiency(version);
               eff.compute();
               verEffs.add(eff);
            }
            TeamEfficiencyModel.setVersionEfficiency(verEffs);
         }
      } catch (OseeCoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }

      return true;
   }

}
