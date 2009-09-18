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

import java.util.ArrayList;
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
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * This test will validate the change report data that is returned for the ATS configured actions with committed
 * branches. Upon first time run against a newly committed branch, it will generate a "General Data" artifact with the
 * change report data stored to xml in a "General String Data" attribute. <br>
 * <br>
 * Every additional time this validation is run against an already stored change report, the current xml and stored xml
 * change report will be compared. <br>
 * <br>
 * If errors are found, the developer will need to put a breakpoint below to see the was-is values of the change report
 * data.<br>
 * <br>
 * This test also ensures that all change reports can return their historical artifacts by loading and accessing the
 * descriptive name for each artifact.
 * 
 * @author Donald G. Dunne
 */
public class ValidateChangeReports extends XNavigateItemAction {

   static final String VCR_ROOT_ELEMENT_TAG = "ValidateChangeReport";
   static final String VCR_DB_GUID = "dbGuid";

   /**
    * @param parent
    */
   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData();
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      String currentDbGuid = OseeInfo.getDatabaseGuid();
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders = new String[] {"HRID", "PCR", "Results"};
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         sbFull.append(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         try {
            int x = 1;
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactListFromType(artifactTypeName, AtsUtil.getAtsBranch());
            for (Artifact artifact : artifacts) {
               String resultStr = "PASS";
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               try {
                  String str = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
                  OseeLog.log(AtsPlugin.class, Level.INFO, str);
                  if (monitor != null) {
                     monitor.subTask(str);
                  }

                  // Only validate committed branches cause working branches change too much
                  if (!teamArt.getSmaMgr().getBranchMgr().isCommittedBranchExists()) {
                     continue;
                  }
                  Result valid = changeReportValidated(currentDbGuid, teamArt, xResultData, false);
                  if (valid.isFalse()) {
                     resultStr = "Error: Not Valid: " + valid.getText();
                  }
               } catch (Exception ex) {
                  resultStr = "Error: Exception Validating: " + ex.getLocalizedMessage();
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
      List<IHealthStatus> stats = new ArrayList<IHealthStatus>(monitorLog.getAllLogs());
      for (IHealthStatus stat : stats) {
         Throwable tr = stat.getException();
         if (tr != null) {
            xResultData.logError("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

   /**
    * Return true if current change report is same as stored change report data
    * 
    * @param teamArt
    * @return Result.TrueResult if same, else Result.FalseResult with comparison in resultData
    * @throws ParserConfigurationException
    */
   static Result changeReportValidated(final String currentDbGuid, final TeamWorkFlowArtifact teamArt, XResultData resultData, boolean displayWasIs) throws OseeCoreException, ParserConfigurationException {
      String name = "VCR_" + teamArt.getHumanReadableId();
      List<Artifact> arts =
            ArtifactQuery.getArtifactListFromTypeAndName(GeneralData.ARTIFACT_TYPE, name, AtsUtil.getAtsBranch());
      String storedChangeReport = null;
      Artifact artifactForStore = null;
      if (arts.size() > 1) {
         throw new OseeStateException("Multiple artifacts found of name \"" + name + "\"");
      } else if (arts.size() == 1) {
         artifactForStore = arts.iterator().next();
         storedChangeReport =
               artifactForStore.getSoleAttributeValue(GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME, null);
      }
      // Retrieve current 
      ChangeData currentChangeData = teamArt.getSmaMgr().getBranchMgr().getChangeDataFromEarliestTransactionId();
      // Store 
      if (storedChangeReport == null) {
         // Reuse same artifact if already exists
         if (artifactForStore == null) {
            artifactForStore = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, AtsUtil.getAtsBranch(), name);
         }
         artifactForStore.setSoleAttributeValue(GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME, getReport(
               currentDbGuid, currentChangeData));
         artifactForStore.persist();
         resultData.log("Stored Change Report for " + teamArt.getHumanReadableId());
         return new Result(true, "Stored Change Report for " + teamArt.getHumanReadableId());
      }
      // Else, compare the two and report
      else {
         final String currentChangeReport = getReport(currentDbGuid, currentChangeData);
         final String fStoredChangeReport = storedChangeReport.replaceAll("\n", "");
         if (isXmlChangeDataEqual(currentChangeReport, fStoredChangeReport)) {
            resultData.log("Change Report Valid for " + teamArt.getHumanReadableId());
         } else {
            resultData.logError("Was/Is Change Report different for " + teamArt.getHumanReadableId());
            if (displayWasIs) {
               resultData.log("Was / Is reports displayed in Results View");
            }
            if (displayWasIs) {
               try {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           String prePage = AHTML.simplePageNoPageEncoding(AHTML.textToHtml(fStoredChangeReport));
                           ResultsEditor.open(new XResultPage("Was Change Report for " + teamArt.getHumanReadableId(),
                                 prePage));

                           String postPage = AHTML.simplePageNoPageEncoding(AHTML.textToHtml(currentChangeReport));
                           ResultsEditor.open(new XResultPage("Is Change Report for " + teamArt.getHumanReadableId(),
                                 postPage));

                        } catch (Exception ex) {
                           OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });

               } catch (Exception ex) {
                  System.err.println(ex.getLocalizedMessage());
               }
            }
            return new Result("FAIL");
         }
      }
      // As another test, ensure that all artifacts can be retrieved and display their name
      try {
         for (Artifact art : currentChangeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.NEW,
               ModificationType.DELETED, ModificationType.MERGED)) {
            art.getName();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception accessing name of change report artifacts: " + ex.getLocalizedMessage());
      }
      return new Result(true, "PASS");
   }

   private static String getReport(String dbGuid, ChangeData changeData) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      sb.append(String.format("<%s %s=\"%s\">", VCR_ROOT_ELEMENT_TAG, VCR_DB_GUID, dbGuid));
      for (Change change : changeData.getChanges()) {
         if (change instanceof RelationChange) {
            sb.append(toXml((RelationChange) change));
         } else if (change instanceof ArtifactChange) {
            sb.append(toXml((ArtifactChange) change));
         } else if (change instanceof AttributeChange) {
            sb.append(toXml((AttributeChange) change));
         }
      }
      sb.append(String.format("</%s>", VCR_ROOT_ELEMENT_TAG));
      String toReturn = sb.toString().replaceAll(">[\\s\\n\\r]+$", ">");
      return toReturn.replaceAll("\n", "");
   }

   private static String toXml(RelationChange change) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
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
      sb.append(AXml.addTagData("relTId", String.valueOf(change.getRelationType().getId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("RelChg", sb.toString());
   }

   private static String toXml(ArtifactChange change) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getToTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getFromTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("cType", String.valueOf(change.getChangeType().name())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("ArtChg", sb.toString());
   }

   private static String toXml(AttributeChange change) throws OseeCoreException, ParserConfigurationException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("brId", String.valueOf(change.getBranch().getBranchId())));
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getToTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getFromTransactionId().getTransactionNumber())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("cType", String.valueOf(change.getChangeType().name())));
      sb.append(AXml.addTagData("aModType", String.valueOf(change.getArtModType().name())));
      sb.append(AXml.addTagData("attrId", String.valueOf(change.getAttrId())));
      sb.append(AXml.addTagData("attrTId", String.valueOf(change.getTypeId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("AttrChg", sb.toString());
   }

   private static boolean isXmlChangeDataEqual(String data1, String data2) {
      int checkSum1 = getCheckSum(data1);
      int checkSum2 = getCheckSum(data2);

      boolean result = checkSum1 == checkSum2;
      if (!result) {
         ChangeReportComparer comparer = new ChangeReportComparer();
         comparer.compare(data1, data2);

         OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format("Checksums not equal - stored:[%s] current:[%s]",
               checkSum1, checkSum2));
      }
      return result;
   }

   private static int getCheckSum(String data) {
      int checksum = -1;
      for (int index = 0; index < data.length(); index++) {
         char character = data.charAt(index);
         if (character != '\n' && character != '\t' && character != '\r' && character != ' ') {
            checksum += character;
         }
      }
      return checksum;
   }
}
