package org.eclipse.osee.ats.rest.internal.util.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheckProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthCheckOperation {

   private static String SELECT_INWORK_WORKFLOWS =
      "SELECT distinct art.art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr " //
         + "WHERE attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = 570 and " //
         + "attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = 1152921504606847147 and attr.VALUE = 'Working'";
   private final IAtsServices services;
   private final JdbcService jdbcService;
   private final MailService mailService;
   boolean inTest = false;
   private List<IAtsHealthCheck> healthChecks;

   public AtsHealthCheckOperation(IAtsServices services, JdbcService jdbcService, MailService mailService) {
      this.services = services;
      this.jdbcService = jdbcService;
      this.mailService = mailService;
   }

   public XResultData run() {
      XResultData rd = new XResultData();
      try {
         ElapsedTime time = new ElapsedTime("ATS Health Check");
         runIt(rd);
         String elapsedStr = time.end(Units.SEC);
         rd.log("\n\n" + elapsedStr);
         time.end();
         emailResults(rd);
      } catch (Exception ex) {
         rd.errorf("Exception running reports [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private void emailResults(XResultData rd) {
      if (mailService != null) {
         MailMessage msg = MailMessage.newBuilder() //
            .from("noop@osee.com") //
            .recipients(Arrays.asList("donald.g.dunne@boeing.com")) //
            .subject("ATS Health Check") //
            .addHtml(AHTML.simplePage(rd.toString().replaceAll("\n", "</br>")))//
            .build();

         List<MailStatus> sendMessages = mailService.sendMessages(msg);
         System.out.println(sendMessages);
      }
   }

   public void runIt(XResultData rd) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      HealthCheckResults vResults = new HealthCheckResults();
      if (inTest) {
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap2", "blah blah");
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap3", "blah blah");
      } else {
         List<IAtsHealthCheck> checks = getHealthChecks();

         // Break artifacts into blocks so don't run out of memory
         List<Collection<Long>> artIdLists = loadWorkingWorkItemIds(rd);
         int numblocks = artIdLists.size();
         int x = 1;
         for (Collection<Long> artIdList : artIdLists) {

            System.err.println(String.format("processing %s / %s", x++, numblocks));
            Collection<ArtifactToken> allArtifacts = services.getArtifacts(artIdList);

            // remove all deleted/purged artifacts first
            List<ArtifactId> artifacts = new ArrayList<>(allArtifacts.size());
            for (ArtifactId artifact : allArtifacts) {
               if (!services.getStoreService().isDeleted(artifact)) {
                  artifacts.add(artifact);
               }
            }
            count += artifacts.size();

            for (ArtifactId artifact : artifacts) {
               for (IAtsHealthCheck check : checks) {
                  if (services.getStoreService().isDeleted(artifact)) {
                     continue;
                  }
                  IAtsWorkItem workItem = services.getWorkItemFactory().getWorkItem((ArtifactToken) artifact);
                  Date date = new Date();
                  try {
                     check.check(artifact, workItem, vResults, services);
                  } catch (Exception ex) {
                     vResults.log(artifact, check.getName(), "Error: Exception: " + Lib.exceptionToString(ex));
                  }
                  vResults.logTestTimeSpent(date, check.getName());
               }
            }
            System.gc();
         }
      }
      // Log resultMap data into xResultData
      vResults.addResultsMapToResultData(rd);
      vResults.addTestTimeMapToResultData(rd);

      rd.logf("Completed processing %s work items.", count);
   }

   private List<IAtsHealthCheck> getHealthChecks() {
      if (healthChecks == null) {
         healthChecks = new LinkedList<>();
         healthChecks.add(new TestWorkflowTeamDefinition());
         healthChecks.add(new TestWorkflowVersions());
         for (IAtsHealthCheckProvider provider : AtsHealthCheckProviderService.getHealthCheckProviders()) {
            healthChecks.addAll(provider.getHealthChecks());
         }
      }
      return healthChecks;
   }

   private static class TestWorkflowTeamDefinition implements IAtsHealthCheck {

      @Override
      public void check(ArtifactId artifact, IAtsWorkItem workItem, HealthCheckResults results, IAtsServices services) {
         if (workItem.isTeamWorkflow()) {
            if (workItem.getParentTeamWorkflow().getTeamDefinition() == null) {
               error(results, workItem, "Team workflow has no Team Definition (re-run conversion?)");
            }
         }
      }
   }

   private static class TestWorkflowVersions implements IAtsHealthCheck {

      @Override
      public void check(ArtifactId artifact, IAtsWorkItem workItem, HealthCheckResults results, IAtsServices services) {
         if (workItem.isTeamWorkflow()) {
            IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
            Collection<ArtifactToken> versions = services.getRelationResolver().getRelated(teamWf,
               AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
            if (versions.size() > 1) {
               error(results, workItem, "Team workflow has " + versions.size() + " versions; should only be 0 or 1");
            } else {
               IAtsVersion version = services.getVersionService().getTargetedVersion(teamWf);
               if (version != null && teamWf.getTeamDefinition() != null && teamWf.getTeamDefinition().getTeamDefinitionHoldingVersions() != null) {
                  if (!teamWf.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersions().contains(version)) {
                     error(results, workItem,
                        "Team workflow " + teamWf.getAtsId() + " has version" + version.toStringWithId() + " that does not belong to teamDefHoldingVersions ");
                  }
               }
            }
         }
      }
   }

   private List<Collection<Long>> loadWorkingWorkItemIds(XResultData rd) throws OseeCoreException {
      rd.log("testLoadAllCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      List<Long> artIds = getCommonArtifactIds(rd);
      if (artIds.isEmpty()) {
         rd.error("Error: Artifact load returned 0 artifacts to check");
      }
      rd.log("testLoadAllCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      return Collections.subDivide(artIds, 4000);
   }

   private List<Long> getCommonArtifactIds(XResultData rd) throws OseeCoreException {
      List<Long> artIds = new ArrayList<>();
      // For single or re-runs of subset
      //      artIds.addAll(Arrays.asList(8381138L, 600305128L, 9115994L, 8003310L, 8646243L, 8660113L, 9036993L, 8646249L));
      // OR
      // Load all inwork workflows
      rd.log("getCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_INWORK_WORKFLOWS);
         while (chStmt.next()) {
            artIds.add(Long.valueOf(chStmt.getInt(1)));
         }
      } finally {
         chStmt.close();
         rd.log("getCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      }
      return artIds;
   }

}
