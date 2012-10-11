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
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.reports.split.model.AIDistributionEntry;
import org.eclipse.osee.ats.reports.split.model.DistributionModel;
import org.eclipse.osee.ats.reports.split.model.StateDistributionEntry;
import org.eclipse.osee.ats.reports.split.model.TeamDistributionEntry;
import org.eclipse.osee.ats.reports.split.ui.ai.AITab;
import org.eclipse.osee.ats.reports.split.ui.state.StateTab;
import org.eclipse.osee.ats.reports.split.ui.team.TeamTab;
import org.eclipse.osee.ats.util.widgets.dialog.TeamVersionListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * Class to create the navigate item in the UI and to populate the model
 * 
 * @author Chandan Bandemutt
 */
public class DistributionItem extends XNavigateItemAction {

   /**
    * Constructor that calls super
    * 
    * @param parent :
    */
   public DistributionItem(final XNavigateItem parent) {
      super(parent, "Distribution Reports", AtsImage.REPORT);
   }

   @Override
   public void run(final TableLoadOption... tableLoadOptions) throws Exception {
      // Get input from the user.
      TeamVersionListDialog dlg = new TeamVersionListDialog(Active.Both);
      int open = dlg.open();
      if (open == 0) {
         populateModel(dlg);
         ResultsEditor.open(new IResultsEditorProvider() {

            @Override
            public String getEditorName() {
               return "Distribution Reports";
            }

            @Override
            public List<IResultsEditorTab> getResultsEditorTabs() {
               List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
               tabs.add(new AITab());
               tabs.add(new TeamTab());
               tabs.add(new StateTab());
               return tabs;
            }

         });
      }
   }

   private boolean populateModel(final TeamVersionListDialog dlg) {
      try {
         // AI Split
         @SuppressWarnings("cast")
         IAtsVersion version = (IAtsVersion) dlg.getSelectedVersion();
         VersionArtifactStore artifactStore = new VersionArtifactStore(version);
         Artifact artifact = artifactStore.getArtifact();

         AIDistributionEntry aiSplitEntry = new AIDistributionEntry(artifact);
         aiSplitEntry.computeAISplit();

         TeamDistributionEntry teamSplitEntry = new TeamDistributionEntry(artifact);
         teamSplitEntry.computeTeamSplit();

         StateDistributionEntry stateSplitEntry = new StateDistributionEntry(artifact);
         stateSplitEntry.computeStateSplit();

         DistributionModel.setAiSplitEntry(aiSplitEntry);
         DistributionModel.setTeamSplitEntry(teamSplitEntry);
         DistributionModel.setStateSplitEntry(stateSplitEntry);

      } catch (OseeCoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }

      return true;
   }

}
