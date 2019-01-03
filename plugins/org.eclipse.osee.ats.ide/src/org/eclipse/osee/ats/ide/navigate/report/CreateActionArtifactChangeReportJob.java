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
package org.eclipse.osee.ats.ide.navigate.report;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateActionArtifactChangeReportJob extends Job {
   private final Set<IAtsTeamWorkflow> teamWfs;
   private final AttributeTypeToken attributeType;

   public CreateActionArtifactChangeReportJob(String jobName, Set<IAtsTeamWorkflow> teamWfs, AttributeTypeToken attributeType) {
      super(jobName);
      this.teamWfs = teamWfs;
      this.attributeType = attributeType;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      return runIt(monitor, getName(), teamWfs, attributeType);
   }

   public static IStatus runIt(IProgressMonitor monitor, String jobName, Collection<IAtsTeamWorkflow> teamWfs, AttributeTypeToken attributeType) {
      XResultData rd = new XResultData();
      try {
         if (teamWfs.isEmpty()) {
            throw new OseeStateException("No Actions/Workflows Specified");
         }
         retrieveData(monitor, teamWfs, attributeType, rd);
         if (rd.toString().equals("")) {
            rd.log("No Problems Found");
         }
         final String html = XResultDataUI.getReport(rd, jobName).getManipulatedHtml(Arrays.asList(Manipulations.NONE));
         final String title = jobName;
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Result result = new HtmlExportTable(title, html, true, false).exportCsv();
               if (result.isFalse()) {
                  AWorkbench.popup(result);
                  return;
               }
               AWorkbench.popup(title, "Completed " + title + "\n\nFile saved to " + System.getProperty(
                  "user.home") + File.separator + "table.csv");
            }
         });
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Failed", ex);
      }
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    */
   private static void retrieveData(IProgressMonitor monitor, Collection<IAtsTeamWorkflow> teamWfs, AttributeTypeToken attributeType, XResultData rd) {
      monitor.subTask("Retrieving Actions");

      int x = 1;
      rd.addRaw(AHTML.beginMultiColumnTable(95));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(
         new String[] {"ID", "Bulld", "UI", attributeType.getName(), "RPCR", "Change"}));
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         String rcprId = teamArt.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");
         String result = String.format("Processing %s/%s RPCR %s for \"%s\"", x, teamWfs.size(), rcprId,
            teamArt.getTeamDefinition().getName());
         monitor.subTask(result);
         rd.log("\nRPCR " + rcprId);
         for (ICommitConfigItem commitConfigArt : AtsClientService.get().getBranchService().getConfigArtifactsConfiguredToCommitTo(
            teamArt)) {
            processTeam(teamArt, AtsClientService.get().getBranchService().getBranchShortName(commitConfigArt),
               attributeType, commitConfigArt, rd);
         }
         x++;

         //          System.err.println("Developmental purposes only, don't release with this");
         //          if (x >= 5)
         //          break;
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   private static void processTeam(TeamWorkFlowArtifact teamArt, String buildId, AttributeTypeId attributeType, ICommitConfigItem commitConfigArt, XResultData rd) {
      String rpcrNum = teamArt.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");
      ChangeData changeData = AtsBranchManager.getChangeData(teamArt, commitConfigArt);
      for (Artifact modArt : changeData.getArtifacts(KindType.Artifact, ModificationType.NEW,
         ModificationType.MODIFIED)) {
         List<String> attrStrs = modArt.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(AttributeId.UNSPECIFIED);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(
               new String[] {teamArt.getAtsId(), buildId, modArt.getName(), attrStr, rpcrNum, "Content"}));
         }
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(AttributeId.UNSPECIFIED);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(
               new String[] {teamArt.getAtsId(), buildId, artChg.getName(), attrStr, rpcrNum, "Deleted"}));
         }
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.RelationOnly, ModificationType.NEW,
         ModificationType.MODIFIED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(AttributeId.UNSPECIFIED);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(
               new String[] {teamArt.getAtsId(), buildId, artChg.getName(), attrStr, rpcrNum, "Relation"}));
         }
      }
   }

}
