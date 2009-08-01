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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
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
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;

/**
 * @author Donald G. Dunne
 */
public class CreateActionArtifactChangeReportJob extends Job {
   private final Set<TeamWorkFlowArtifact> teamArts;
   private final String byAttribute;

   public CreateActionArtifactChangeReportJob(String jobName, Set<TeamWorkFlowArtifact> teamArts, String byAttribute) {
      super(jobName);
      this.teamArts = teamArts;
      this.byAttribute = byAttribute;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      return runIt(monitor, getName(), teamArts, byAttribute);
   }

   public static IStatus runIt(IProgressMonitor monitor, String jobName, Collection<TeamWorkFlowArtifact> teamArts, String byAttribute) {
      XResultData rd = new XResultData();
      try {
         if (teamArts.size() == 0)
            throw new OseeStateException("No Actions/Workflows Specified");
         retrieveData(monitor, teamArts, byAttribute, rd);
         if (rd.toString().equals(""))
            rd.log("No Problems Found");
         final String html = rd.getReport(jobName).getManipulatedHtml(Arrays.asList(Manipulations.NONE));
         final String title = jobName;
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               Result result = (new HtmlExportTable(title, html, true, false)).exportCsv();
               if (result.isFalse()) {
                  result.popup();
                  return;
               }
               AWorkbench.popup(
                     title,
                     "Completed " + title + "\n\nFile saved to " + System.getProperty("user.home") + File.separator + "table.csv");
            }
         }, true);
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Failed", ex);
      }
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    * 
    * @throws Exception
    */
   private static void retrieveData(IProgressMonitor monitor, Collection<TeamWorkFlowArtifact> teamArts, String byAttribute, XResultData rd) throws OseeCoreException {
      monitor.subTask("Retrieving Actions");

      int x = 1;
      rd.addRaw(AHTML.beginMultiColumnTable(95));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"HRID", "Bulld", "UI", byAttribute, "RPCR", "Change"}));
      for (TeamWorkFlowArtifact teamArt : teamArts) {
         String rcprId = teamArt.getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), "");
         String result =
               (String.format("Processing %s/%s RPCR %s for \"%s\"", x, teamArts.size(), rcprId,
                     teamArt.getTeamDefinition().getName()));
         monitor.subTask(result);
         rd.log("\nRPCR " + rcprId);
         for (ICommitConfigArtifact commitConfigArt : teamArt.getSmaMgr().getBranchMgr().getConfigArtifactsConfiguredToCommitTo()) {
            processTeam(teamArt, commitConfigArt.getParentBranch().getShortName(), byAttribute, commitConfigArt, rd);
         }
         x++;

         //          System.err.println("Developmental purposes only, don't release with this");
         //          if (x >= 5)
         //          break;
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   private static void processTeam(TeamWorkFlowArtifact teamArt, String buildId, String byAttribute, ICommitConfigArtifact commitConfigArt, XResultData rd) throws OseeCoreException {
      String rpcrNum = teamArt.getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), "");
      ChangeData changeData = teamArt.getSmaMgr().getBranchMgr().getChangeData(commitConfigArt);
      for (Artifact modArt : changeData.getArtifacts(KindType.Artifact, ModificationType.NEW, ModificationType.MODIFIED)) {
         List<String> attrStrs = modArt.getAttributesToStringList(byAttribute);
         if (attrStrs.size() == 0)
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         for (String attrStr : attrStrs)
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
                  modArt.getName(), attrStr, rpcrNum, "Content"}));
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(byAttribute);
         if (attrStrs.size() == 0)
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         for (String attrStr : attrStrs)
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
                  artChg.getName(), attrStr, rpcrNum, "Deleted"}));
      }
      for (Artifact artChg : changeData.getArtifacts(KindType.RelationOnly, ModificationType.NEW,
            ModificationType.MODIFIED)) {
         List<String> attrStrs = artChg.getAttributesToStringList(byAttribute);
         if (attrStrs.size() == 0)
            attrStrs.add(EnumeratedAttribute.UNSPECIFIED_VALUE);
         for (String attrStr : attrStrs)
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(), buildId,
                  artChg.getName(), attrStr, rpcrNum, "Relation"}));
      }
   }

}
