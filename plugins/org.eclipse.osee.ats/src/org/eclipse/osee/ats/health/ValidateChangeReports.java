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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.health.change.DataChangeReportComparer;
import org.eclipse.osee.ats.health.change.ValidateChangeReportParser;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.Displays;

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

   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
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
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   @SuppressWarnings("unused")
   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      String currentDbGuid = OseeInfo.getDatabaseGuid();
      if (true) {
         validateSome(xResultData, currentDbGuid);
      } else {
         SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
         OseeLog.registerLoggerListener(monitorLog);
         StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
         String[] columnHeaders = new String[] {"HRID", "PCR", "Results"};
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         for (IArtifactType artifactType : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactTypes()) {
            sbFull.append(AHTML.addRowSpanMultiColumnTable(artifactType.getName(), columnHeaders.length));

            try {
               int x = 1;
               Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactListFromType(artifactType, AtsUtil.getAtsBranch());
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
                     if (!teamArt.getBranchMgr().isCommittedBranchExists()) {
                        continue;
                     }
                     Result valid = changeReportValidated(currentDbGuid, teamArt, xResultData, false);
                     if (valid.isFalse()) {
                        resultStr = "Error: " + valid.getText();
                     }
                  } catch (Exception ex) {
                     resultStr = "Error: Exception Validating: " + ex.getLocalizedMessage();
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
                  sbFull.append(AHTML.addRowMultiColumnTable(teamArt.getHumanReadableId(),
                     teamArt.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, ""), resultStr));
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
   }

   private void validateSome(XResultData rd, String currentDbGuid) throws OseeCoreException {
      Collection<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromIds(analyzeForMergeDifferences, AtsUtil.getAtsBranch());
      int x = 0;
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            rd.logError("Unexpected type for " + artifact.toStringWithId());
         }
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;

         String str = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
         OseeLog.log(AtsPlugin.class, Level.INFO, str);

         // Only validate committed branches cause working branches change too much
         if (!teamArt.getBranchMgr().isCommittedBranchExists()) {
            continue;
         }
         Result valid = changeReportValidated(currentDbGuid, teamArt, rd, false);
         if (valid.isFalse()) {
            rd.logError(valid.getText());
         }
      }
   }
   private final List<String> analyzeForMergeDifferences = Arrays.asList("D11QS", "15TT6", "9BJ55", "KJSXG", "W7C6Q",
      "QJN6U", "ABKL6", "57C55", "C7JVC", "FJ8QG", "PD2L9", "R544L", "H2ZWZ", "XZDFL", "ZQYNV", "2M3R1", "H3MPT",
      "DQWMJ", "G985U", "SC16V", "6HN4Y", "HMQZ0", "DHBYH", "B7NVG", "DNTLC", "C8C7G", "D96KZ", "L2VQY", "547KS",
      "GN5SD", "E02FA", "6H998", "9SRR7", "GBTY3", "C7HGV", "KVFFX", "GB0R4", "417PW", "9JK7Q", "Y84NK", "LRMZT",
      "BXH48", "L5554", "XJNLZ", "VNTWE", "5QG8E", "FC7PB", "BLN8W", "D9496", "QQKJS", "X808E", "YN188", "VHM3X",
      "2ZSZ7", "YFKYZ", "3Q50H", "646WD", "J6L48", "0P9YU", "89B4J", "ZVS1V", "XXFFH", "Y2NCG", "9K6WL", "8SX10",
      "BW2Q0", "B0JXG", "QQ1R9", "FR6SE", "WQZ9V", "JSWYS", "6W54P", "BDN3C", "533VS", "ECMZL", "PQN1T", "6SG3K",
      "WCZ3S", "J6G34", "99VFY", "GXWCE", "77BNS", "D5SBA", "AYV56", "ZTQ8U", "A1B4Q", "6K4C5", "6MDYJ", "NZBT2",
      "MR1LL", "98ZKQ", "DS6CQ");

   /**
    * Return true if current change report is same as stored change report data
    * 
    * @return Result.TrueResult if same, else Result.FalseResult with comparison in resultData
    */
   static Result changeReportValidated(final String currentDbGuid, final TeamWorkFlowArtifact teamArt, XResultData resultData, boolean displayWasIs) throws OseeCoreException {
      String name = "VCR_" + teamArt.getHumanReadableId();
      List<Artifact> arts =
         ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.GeneralData, name, AtsUtil.getAtsBranch());
      String storedChangeReport = null;
      Artifact artifactForStore = null;
      if (arts.size() > 1) {
         throw new OseeStateException("Multiple artifacts found of name \"" + name + "\"");
      } else if (arts.size() == 1) {
         artifactForStore = arts.iterator().next();
         storedChangeReport = artifactForStore.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData, null);
      }
      // Retrieve current
      ChangeData currentChangeData = teamArt.getBranchMgr().getChangeDataFromEarliestTransactionId();
      if (currentChangeData.isEmpty()) {
         return new Result(String.format("FAIL: Unexpected empty change report for %s", teamArt.toStringWithId()));
      }
      // Store
      if (storedChangeReport == null) {
         // Reuse same artifact if already exists
         if (artifactForStore == null) {
            artifactForStore =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtil.getAtsBranch(), name);
         }
         artifactForStore.setSoleAttributeValue(CoreAttributeTypes.GeneralStringData,
            getReport(currentDbGuid, currentChangeData));
         artifactForStore.persist();
         resultData.log("Stored Change Report for " + teamArt.getHumanReadableId());
         return new Result(true, "Stored Change Report for " + teamArt.getHumanReadableId());
      }
      // Else, compare the two and report
      else {
         final String currentChangeReport = getReport(currentDbGuid, currentChangeData);
         final String fStoredChangeReport = storedChangeReport.replaceAll("\n", "");
         if (!isXmlChangeDataEqual(currentChangeReport, fStoredChangeReport)) {
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
                           String storedChangeReportString = GetComparableString(fStoredChangeReport);
                           String currentChangeReportString = GetComparableString(currentChangeReport);

                           CompareHandler compareHandler =
                              new CompareHandler(storedChangeReportString, currentChangeReportString);
                           compareHandler.compare();

                        } catch (Exception ex) {
                           OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });

               } catch (Exception ex) {
                  System.err.println(ex.getLocalizedMessage());
               }
            }
            return new Result("FAIL: Was/Is Change Report different");
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
         return new Result("FAIL: Exception accessing name of change report artifacts: " + ex.getLocalizedMessage());
      }
      // As another test, allow ATS extensions add their own tests
      for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
         Result result = atsHealthCheck.validateChangeReports(currentChangeData, teamArt, resultData);
         if (result.isFalse()) {
            return result;
         }
      }

      return new Result(true, "PASS");
   }

   private static String GetComparableString(String changeReportString) {
      StringBuffer comparableString = new StringBuffer();
      ValidateChangeReportParser parser = new ValidateChangeReportParser();
      ArrayList<ArrayList<DataChangeReportComparer>> changeReport = parser.parse(changeReportString);

      for (int i = 0; i < changeReport.size(); i++) {
         for (int j = 0; j < changeReport.get(i).size(); j++) {
            comparableString.append(changeReport.get(i).get(j).getContent());
         }
      }
      return comparableString.toString().replaceAll("><", ">\n<");
   }

   private static String getReport(String dbGuid, ChangeData changeData) {
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

   private static void toXmlCommon(Change change, StringBuffer sb) {
      sb.append(AXml.addTagData(TxImportedValidateChangeReports.BRANCH_ID_ALIASES, change.getBranch().getGuid()));
   }

   private static String toXml(RelationChange change) {
      StringBuffer sb = new StringBuffer();
      toXmlCommon(change, sb);
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getTxDelta().getEndTx().getId())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getTxDelta().getStartTx().getId())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("bArtId", String.valueOf(change.getBArtId())));
      sb.append(AXml.addTagData("relId", String.valueOf(change.getRelLinkId())));
      sb.append(AXml.addTagData("rat", change.getRationale()));
      sb.append(AXml.addTagData("relTId", String.valueOf(change.getRelationType().getId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("RelChg", sb.toString());
   }

   private static String toXml(ArtifactChange change) {
      StringBuffer sb = new StringBuffer();
      toXmlCommon(change, sb);
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getTxDelta().getEndTx().getId())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getTxDelta().getStartTx().getId())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("ArtChg", sb.toString());
   }

   private static String toXml(AttributeChange change) {
      StringBuffer sb = new StringBuffer();
      toXmlCommon(change, sb);
      sb.append(AXml.addTagData("artTId", String.valueOf(change.getItemTypeId())));
      sb.append(AXml.addTagData("gamma", String.valueOf(change.getGamma())));
      sb.append(AXml.addTagData("artId", String.valueOf(change.getArtId())));
      sb.append(AXml.addTagData("tTranId", String.valueOf(change.getTxDelta().getEndTx().getId())));
      sb.append(AXml.addTagData("fTranId", String.valueOf(change.getTxDelta().getStartTx().getId())));
      sb.append(AXml.addTagData("mType", String.valueOf(change.getModificationType().name())));
      sb.append(AXml.addTagData("aModType", String.valueOf(change.getArtModType().name())));
      sb.append(AXml.addTagData("attrId", String.valueOf(change.getAttrId())));
      sb.append(AXml.addTagData("attrTId", String.valueOf(change.getAttributeType().getId())));
      sb.append(AXml.addTagData("hist", String.valueOf(change.isHistorical())));
      return AXml.addTagData("AttrChg", sb.toString());
   }

   private static boolean isXmlChangeDataEqual(String currentData, String storedData) {
      int checkSum1 = getCheckSum(currentData);
      int checkSum2 = getCheckSum(storedData);

      boolean result = checkSum1 == checkSum2;
      if (!result) {
         ChangeReportComparer comparer = new ChangeReportComparer();
         comparer.compare(currentData, storedData);

         OseeLog.log(AtsPlugin.class, Level.SEVERE,
            String.format("Checksums not equal - stored:[%s] current:[%s]", checkSum1, checkSum2));
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
