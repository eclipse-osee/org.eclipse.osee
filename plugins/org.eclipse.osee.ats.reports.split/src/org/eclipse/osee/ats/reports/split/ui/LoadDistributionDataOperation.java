/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.reports.split.Activator;
import org.eclipse.osee.ats.reports.split.internal.AtsClientService;
import org.eclipse.osee.ats.reports.split.model.AIDistributionEntry;
import org.eclipse.osee.ats.reports.split.model.DistributionModel;
import org.eclipse.osee.ats.reports.split.model.StateDistributionEntry;
import org.eclipse.osee.ats.reports.split.model.TeamDistributionEntry;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class to extract data from workflows and fills the DistributionModel
 * 
 * @author Praveen Joseph
 */
public class LoadDistributionDataOperation extends AbstractOperation {

   private final IAtsVersion version;

   public LoadDistributionDataOperation(IAtsVersion version) {
      super("Load Work Distribution Data", Activator.PLUGIN_ID);
      this.version = version;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(version, "teamDefinition");
      Artifact artifact = AtsClientService.get().getConfigArtifact(version);

      AIDistributionEntry aiSplitEntry = new AIDistributionEntry(artifact);
      aiSplitEntry.computeAISplit();

      TeamDistributionEntry teamSplitEntry = new TeamDistributionEntry(artifact);
      teamSplitEntry.computeTeamSplit();

      StateDistributionEntry stateSplitEntry = new StateDistributionEntry(artifact);
      stateSplitEntry.computeStateSplit();

      DistributionModel.setAiSplitEntry(aiSplitEntry);
      DistributionModel.setTeamSplitEntry(teamSplitEntry);
      DistributionModel.setStateSplitEntry(stateSplitEntry);
   }

}
