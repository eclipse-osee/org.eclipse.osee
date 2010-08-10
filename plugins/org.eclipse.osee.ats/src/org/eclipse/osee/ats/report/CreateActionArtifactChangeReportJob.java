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
package org.eclipse.osee.ats.report;

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
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateActionArtifactChangeReportJob extends Job {
   private final Set<TeamWorkFlowArtifact> teamArts;
   private final IAttributeType attributeType;

   public CreateActionArtifactChangeReportJob(String jobName, Set<TeamWorkFlowArtifact> teamArts, IAttributeType attributeType) {
      super(jobName);
      this.teamArts = teamArts;
      this.attributeType = attributeType;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      return runIt(monitor, getName(), teamArts, attributeType);
   }

   public static IStatus runIt(IProgressMonitor monitor, String jobName, Collection<TeamWorkFlowArtifact> teamArts, IAttributeType attributeType) {
      XResultData rd = new XResultData();
      try {
         if (teamArts.isEmpty()) {
            throw new OseeStateException("No Actions/Workflows Specified");
         }
         retrieveData(monitor, teamArts, attributeType, rd);
         if (rd.toString().equals("")) {
            rd.log("No Problems Found");
         }
         final String html = rd.getReport(jobName).getManipulatedHtml(Arrays.asList(Manipulations.NONE));
         final String title = jobName;
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Result result = new HtmlExportTable(title, html, true, false).exportCsv();
               if (result.isFalse()) {
                  result.popup();
                  return;
               }
               AWorkbench.popup(
                  title,
                  "Completed " + title + "\n\nFile saved to " + System.getProperty("user.home") + File.separator + "table.csv");
            }
         });
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, "Failed", ex);
      }
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    * 
    * @throws Exception
    */
   private static void retrieveData(IProgressMonitor monitor, Collection<TeamWorkFlowArtifact> teamArts, IAttributeType attributeType, XResultData rd) throws OseeCoreException {
      monitor.subTask("Retrieving Actions");

      int x = 1;
      rd.addRaw(AHTML.beginMultiColumnTable(95));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"HRID", "Bulld", "UI", attributeType.getName(),
         "RPCR", "Change"}));
      for (TeamWorkFlowArtifact teamArt : teamArts) {
         String rcprId = teamArt.getSoleAttributeValue(AtsAttributeTypes.ATS_LEGACY_PCR_ID, "");
         String result =
            String.format("Processing %s/%s RPCR %s for \"%s\"", x, teamArts.size(), rcprId,
               teamArt.getTeamDefinition().getName());
         monitor.subTask(result);
         rd.log("\nRPCR " + rcprId);
         for (ICommitConfigArtifact commitConfigArt : teamArt.getBranchMgr().getConfigArtifactsConfiguredToCommitTo()) {
            processTeam(teamArt, commitConfigArt.getParentBranch().getShortName(), attributeType, commitConfigArt, rd);
         }
         x++;

         //          System.err.println("Developmental purposes only, don't release with this");
         //          if (x >= 5)
         //          break;
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   private static void processTeam(TeamWorkFlowArtifact teamArt, String buildId, IAttributeType attributeType, ICommitConfigArtifact commitConfigArt, XResultData rd) throws OseeCoreException {
      String rpcrNum = teamArt.getSoleAttributeValue(AtsAttributeTypes.ATS_LEGACY_PCR_ID, "");
      ChangeData changeData = teamArt.getBranchMgr().getChangeData(commitConfigArt);
      for (Artifact modArt : changeData.getArtifacts(KindType.Artifact, ModificationType.NEW, ModificationType.MODIFIED)) {
         List<String> attrStrs = modArt.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
               modArt.getName(), attrStr, rpcrNum, "Content"}));
         }
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
               artChg.getName(), attrStr, rpcrNum, "Deleted"}));
         }
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.RelationOnly, ModificationType.NEW,
         ModificationType.MODIFIED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(attributeType);
         if (attrStrs.isEmpty()) {
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         }
         for (String attrStr : attrStrs) {
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
               artChg.getName(), attrStr, rpcrNum, "Relation"}));
         }
      }
   }

}
