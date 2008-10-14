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
package org.eclipse.osee.ats.health;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChanged;
import org.eclipse.osee.framework.skynet.core.change.AttributeChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChanged;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateChangeReports extends XNavigateItemAction {

   /**
    * @param parent
    */
   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData();
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders = new String[] {"HRID", "PCR", "Results"};
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      //            for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {

      for (String artifactTypeName : new String[] {"Lba V13 Req Team Workflow"}) {
         sbFull.append(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         try {
            int x = 1;
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch());
            for (Artifact artifact : artifacts) {
               String resultStr = "PASS";
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               try {
                  String str = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
                  OSEELog.logInfo(AtsPlugin.class, str, false);
                  if (monitor != null) {
                     monitor.subTask(str);
                  }

                  // Only validate committed branches cause working branches change too much
                  if (!teamArt.getSmaMgr().getBranchMgr().isCommittedBranch()) continue;
                  Result valid = changeReportValidated(teamArt, null);
                  if (valid.isFalse()) {
                     resultStr = "Error: Not Valid: " + valid.getText();
                  }
               } catch (Exception ex) {
                  resultStr = "Error: Exception Validating: " + ex.getLocalizedMessage();
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex.getLocalizedMessage(), ex);
               }
               sbFull.append(AHTML.addRowMultiColumnTable(teamArt.getHumanReadableId(), teamArt.getSoleAttributeValue(
                     ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), ""), resultStr));
            }
         } catch (Exception ex) {
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Exception: " + ex.getLocalizedMessage(),
                  columnHeaders.length));
         }
      }
      sbFull.append(AHTML.endMultiColumnTable());
      xResultData.addRaw(sbFull.toString().replaceAll("\n", ""));
      if (monitorLog.getSevereLogs().size() > 0) {
         xResultData.logError(String.format("%d SevereLogs during test.\n", monitorLog.getSevereLogs().size()));
      }
   }

   /**
    * Return true if current change report is same as stored change report data
    * 
    * @param teamArt
    * @return Result.TrueResult if same, else Result.FalseResult with comparison in resultData
    * @throws ParserConfigurationException
    */
   public static Result changeReportValidated(TeamWorkFlowArtifact teamArt, XResultData resultData) throws OseeCoreException, ParserConfigurationException {
      String name = "VCR_" + teamArt.getHumanReadableId();
      List<Artifact> arts =
            ArtifactQuery.getArtifactsFromTypeAndName(GeneralData.ARTIFACT_TYPE, name, AtsPlugin.getAtsBranch());
      String storedChangeReport = null;
      if (arts.size() > 1) {
         throw new OseeStateException("Multiple artifacts found of name \"" + name + "\"");
      } else if (arts.size() == 1) {
         storedChangeReport =
               arts.iterator().next().getSoleAttributeValue(GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME, null);
      }
      // Retrieve current 
      ChangeData currentChangeData = teamArt.getSmaMgr().getBranchMgr().getChangeData();
      // Store 
      if (storedChangeReport == null) {
         Artifact artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, AtsPlugin.getAtsBranch(), name);
         artifact.setSoleAttributeValue(GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME, getReport(currentChangeData));
         artifact.persistAttributes();
         resultData.log("Stored Change Report for " + teamArt.getHumanReadableId());
         return new Result(true, "Stored Change Report for " + teamArt.getHumanReadableId());
      }
      // Else, compare the two and report
      else {
         String currentChangeReport = getReport(currentChangeData);
         if (storedChangeReport.equals(currentChangeReport)) {
            resultData.log("Change Report Valid for " + teamArt.getHumanReadableId());
            return Result.TrueResult;
         }
         resultData.logError("Was/Is Change Report different for " + teamArt.getHumanReadableId());
         return new Result("FAIL");
      }
   }

   private static String getReport(ChangeData changeData) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      for (Change change : changeData.getChanges()) {
         if (change instanceof RelationChanged) {
            sb.append(toXml((RelationChanged) change) + "\n");
         } else if (change instanceof ArtifactChanged) {
            sb.append(toXml((ArtifactChanged) change) + "\n");
         } else if (change instanceof AttributeChanged) {
            sb.append(toXml((AttributeChanged) change) + "\n");
         }
      }
      return sb.toString().replaceAll(">[\\s\\n\\r]+$", ">");
   }

   private static String toXml(RelationChanged change) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getArtTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getToTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getFromTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("cType", String.valueOf(change.getChangeType().name())));
      sb.append(AXml.addTagData("bArtId", String.valueOf(change.getBArtId())));
      sb.append(AXml.addTagData("relId", String.valueOf(change.getRelLinkId())));
      sb.append(AXml.addTagData("rat", change.getRationale()));
      sb.append(AXml.addTagData("aOrdr", String.valueOf(change.getLinkOrder())));
      sb.append(AXml.addTagData("bOrdr", String.valueOf(change.getBLinkOrder())));
      sb.append(AXml.addTagData("relTId", String.valueOf(change.getRelationType().getRelationTypeId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("RelChg", sb.toString());
   }

   private static String toXml(ArtifactChanged change) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getArtTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getToTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getFromTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("cType", String.valueOf(change.getChangeType().name())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("ArtChg", sb.toString());
   }

   private static String toXml(AttributeChanged change) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getArtTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getToTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getFromTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("cType", String.valueOf(change.getChangeType().name())));
      sb.append(AXml.addTagData("aModType", String.valueOf(change.getArtModType().name())));
      sb.append(AXml.addTagData("attrId", String.valueOf(change.getAttrId())));
      sb.append(AXml.addTagData("attrTId", String.valueOf(change.getAttrTypeId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("AttrChg", sb.toString());
   }

}
