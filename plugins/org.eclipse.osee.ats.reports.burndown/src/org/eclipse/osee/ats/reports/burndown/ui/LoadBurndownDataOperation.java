/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.ui;

import java.util.Collection;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.config.VersionsClient;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.reports.burndown.Activator;
import org.eclipse.osee.ats.reports.burndown.hours.HourBurndownLog;
import org.eclipse.osee.ats.reports.burndown.hours.HourBurndownModel;
import org.eclipse.osee.ats.reports.burndown.issues.IssueBurndownLog;
import org.eclipse.osee.ats.reports.burndown.issues.IssueBurndownModel;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class to extract data from workflows and fills the HourBurndownModel and IssueBurndownModel
 * 
 * @author Praveen Joseph
 */
public class LoadBurndownDataOperation extends AbstractOperation {

   private final Artifact version;
   private final Date startDate;
   private final Date endDate;

   public LoadBurndownDataOperation(Artifact version, Date startDate, Date endDate) {
      super("Load Burndown Data", Activator.PLUGIN_ID);
      this.version = version;
      this.startDate = startDate;
      this.endDate = endDate;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(version, "version");

      AtsConfigCache atsConfigCache = new AtsConfigCache();
      VersionArtifactStore artifactStore = new VersionArtifactStore(version, atsConfigCache);
      IAtsVersion version = artifactStore.getVersion();

      Collection<TeamWorkFlowArtifact> workflows = VersionsClient.getTargetedForTeamWorkflows(version);

      // HourBurndown
      HourBurndownLog log = new HourBurndownLog();
      log.getArtifacts().addAll(workflows);
      log.setStartDate(startDate);
      log.setEndDate(endDate);
      log.compute();
      HourBurndownModel.setLog(log);

      // Issue Burndown
      IssueBurndownLog issueLog = new IssueBurndownLog();
      issueLog.getArtifacts().addAll(workflows);
      issueLog.setStartDate(startDate);
      issueLog.setEndDate(endDate);
      issueLog.compute();
      IssueBurndownModel.setLog(issueLog);
   }

}
