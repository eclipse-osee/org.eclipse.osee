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
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.reports.burndown.hours.HourBurndownLog;
import org.eclipse.osee.ats.reports.burndown.hours.HourBurndownModel;
import org.eclipse.osee.ats.reports.burndown.internal.Activator;
import org.eclipse.osee.ats.reports.burndown.internal.AtsClientService;
import org.eclipse.osee.ats.reports.burndown.issues.IssueBurndownLog;
import org.eclipse.osee.ats.reports.burndown.issues.IssueBurndownModel;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class to extract data from workflows and fills the HourBurndownModel and IssueBurndownModel
 * 
 * @author Praveen Joseph
 */
public class LoadBurndownDataOperation extends AbstractOperation {

   private final Artifact versionArt;
   private final Date startDate;
   private final Date endDate;

   public LoadBurndownDataOperation(Artifact versionArt, Date startDate, Date endDate) {
      super("Load Burndown Data", Activator.PLUGIN_ID);
      this.versionArt = versionArt;
      this.startDate = startDate;
      this.endDate = endDate;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(versionArt, "version");
      IAtsVersion version = AtsClientService.get().getConfigObject(versionArt);

      Collection<TeamWorkFlowArtifact> workflows =
         AtsClientService.get().getAtsVersionService().getTargetedForTeamWorkflowArtifacts(version);

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
