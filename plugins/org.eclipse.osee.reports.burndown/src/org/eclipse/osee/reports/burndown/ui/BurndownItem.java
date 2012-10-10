/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.config.VersionsClient;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.reports.burndown.hours.HourBurndownLog;
import org.eclipse.osee.reports.burndown.hours.HourBurndownModel;
import org.eclipse.osee.reports.burndown.hours.HourBurndownTab;
import org.eclipse.osee.reports.burndown.issues.IssueBurndownLog;
import org.eclipse.osee.reports.burndown.issues.IssueBurndownModel;
import org.eclipse.osee.reports.burndown.issues.IssueBurndownTab;


/**
 * Class to extract data from workflows and fills the HourBurndownModel and IssueBurndownModel
 * 
 * @author Praveen Joseph
 */
public class BurndownItem extends XNavigateItemAction {

  /**
   * Constructor which calls its parent constructor
   * 
   * @param parent :
   */
  public BurndownItem(final XNavigateItem parent) {
    super(parent, "Burndown Reports", AtsImage.REPORT);
  }

  @Override
  public void run(final TableLoadOption... tableLoadOptions) throws Exception {
    // Get input from the user.
    BurndownSelectionDialog dlg = new BurndownSelectionDialog(Active.Both);
    int open = dlg.open();
    if (open == 0) {
      populateModel(dlg);
      ResultsEditor.open(new IResultsEditorProvider() {

        @Override
        public String getEditorName() {
          return "Burndown Reports";
        }

        @Override
        public List<IResultsEditorTab> getResultsEditorTabs() {
          List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
          tabs.add(new HourBurndownTab());
          tabs.add(new IssueBurndownTab());
          return tabs;
        }

      });
    }
  }

  private boolean populateModel(final BurndownSelectionDialog dlg) {
    try {
      Artifact art = (Artifact) dlg.getSelectedVersion();
      AtsConfigCache atsConfigCache = new AtsConfigCache();
      VersionArtifactStore artifactStore = new VersionArtifactStore(art, atsConfigCache);
      IAtsVersion version = artifactStore.getVersion();
    	
    	// HourBurndown
      HourBurndownLog log = new HourBurndownLog();
      log.getArtifacts().addAll(VersionsClient.getTargetedForTeamWorkflows(version));
      log.setStartDate(dlg.getStartDate());
      log.setEndDate(dlg.getEndDate());
      log.compute();
      HourBurndownModel.setLog(log);

      // Issue Burndown
      IssueBurndownLog issueLog = new IssueBurndownLog();
      issueLog.getArtifacts().addAll(VersionsClient.getTargetedForTeamWorkflows(version));
      issueLog.setStartDate(dlg.getStartDate());
      issueLog.setEndDate(dlg.getEndDate());
      issueLog.compute();
      IssueBurndownModel.setLog(issueLog);

    }
    catch (OseeCoreException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

}
