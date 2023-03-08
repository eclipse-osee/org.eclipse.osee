/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.rest.metrics;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public class MetricsReportOperations {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public MetricsReportOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public Response generateDevProgressReport(String targetVersion, Date startDate, Date endDate, boolean allTime) {
      StreamingOutput streamingOutput =
         new DevProgressMetrics(orcsApi, atsApi, targetVersion, startDate, endDate, allTime);
      String fileName = String.format("DevelopmentProgressMetrics_%s_%s.xml", targetVersion, Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM);
      builder.header("Content-Disposition", "attachment; filename=" + fileName).header("FileName", fileName);
      return builder.build();
   }

   public Response generateSoftwareReqVolatility(String targetVersion, Date startDate, Date endDate, boolean allTime, boolean implDetails) {
      StreamingOutput streamingOutput =
         new SoftwareReqVolatilityMetrics(orcsApi, atsApi, targetVersion, startDate, endDate, allTime, implDetails);
      String fileName =
         String.format("SoftwareRequirementsVolatilityMetrics_%s_%s.xml", targetVersion, Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM);
      builder.header("Content-Disposition", "attachment; filename=" + fileName).header("FileName", fileName);
      return builder.build();
   }

   public Response setBranchDiffData(String atsId) {
      try {
         IAtsWorkItem workItem = atsApi.getQueryService().getWorkItemByAtsId(atsId);
         if (!(workItem instanceof IAtsTeamWorkflow)) {
            return Response.status(406, "Provided atsId is not a Team Workflow").build();
         }
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         BranchToken branch = atsApi.getBranchService().getBranch(teamWf);
         if (!atsApi.getBranchService().branchExists(branch)) {
            return Response.ok().build();
         } else if (branch == null || branch.isInvalid()) {
            return Response.status(406, "Branch Value is invalid").build();
         }
         List<ChangeItem> changeItems = orcsApi.getBranchOps().compareBranch(branch);
         if (changeItems.isEmpty()) {
            return Response.ok().build();
         }
         String changeItemJson = JsonUtil.toJson(changeItems);
         if (atsApi.getAttributeResolver().getAttributeCount(teamWf, CoreAttributeTypes.BranchDiffData) == 0) {
            atsApi.getAttributeResolver().addAttribute(workItem, CoreAttributeTypes.BranchDiffData, changeItemJson);
         } else if (!atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, CoreAttributeTypes.BranchDiffData,
            "").equals(changeItemJson)) {
            atsApi.getAttributeResolver().setSoleAttributeValue(workItem, CoreAttributeTypes.BranchDiffData,
               changeItemJson);
         }
      } catch (Exception ex) {
         OseeLog.log(MetricsReportOperations.class, Level.WARNING, "Error setting Branch Diff Data JSON", ex);
      }
      return Response.ok().build();
   }
}