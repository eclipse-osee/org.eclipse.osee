/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 */
@Path("/traceability/datarights")
public final class DataRightsSwReqAndCodeResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry resourceRegistry;
   private final Map<String, Object> properties;
   private final Log logger;
   private final QueryFactory queryFactory;

   public DataRightsSwReqAndCodeResource(Log logger, Map<String, Object> properties, IResourceRegistry resourceRegistry, OrcsApi orcsApi) {
      this.properties = properties;
      this.resourceRegistry = resourceRegistry;
      this.orcsApi = orcsApi;
      queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
   }

   /**
    * Provides the Data Rights Report
    *
    * @return Returns an excel spreadsheet containing the Data Rights Report
    */
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getDataRightspReport(@QueryParam("branch") BranchId branch, @QueryParam("code_root") String codeRoot) {
      TraceMatch match = new TraceMatch("\\^SRS\\s*([^;]+);?", null);
      TraceAccumulator traceAccumulator = new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", match);
      StreamingOutput streamingOutput =
         new DataRightsStreamingOutput(orcsApi, branch, codeRoot, traceAccumulator, logger);

      ResponseBuilder builder = Response.ok(streamingOutput);
      String fileName = "Req_Code_Data_Rights_Trace_Report.xml";
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   /**
    * Provides the user interface for the Data Rights Report
    *
    * @return Returns the html page for the Data Rights Report
    */
   @Path("ui")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(queryFactory.branchQuery());
      return pageUtil.realizeApplet(resourceRegistry, "dataRightsReport.html", getClass());
   }

   private static final IArtifactType WCAFE = TokenFactory.createArtifactType(204509162766367L, "WCAFE");

   /**
    * Checks the Data Rights on the provided branch in the important subsystems: Controls and Displays, Mission System
    * Management, Data Management and Unmanned Systems Management to see if each of the requirements has a
    * classification and a subject matter expert.
    *
    * @return Returns a list of all of the requirements that don't have the correct settings
    */
   @Path("validate")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String check(@QueryParam("branchId") BranchId branchId) {
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(branchId).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      for (ArtifactReadable art : results) {
         if (art.isOfType(WCAFE)) {
            continue;
         }
         String classification = art.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
         String subsystem = art.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
         String sme = art.getSoleAttributeValue(CoreAttributeTypes.SubjectMatterExpert, "");
         if (subsystem.equals("Controls and Displays") || subsystem.equals(
            "Mission System Management") || subsystem.equals(
               "Data Management") || subsystem.equals("Unmanned Systems Management")) {
            if (classification.isEmpty()) {
               appendDetails("missing classification", strb, art, subsystem, classification);
            }
            if (sme.equals("")) {
               appendDetails("missing sme", strb, art, subsystem, classification);
            }
            count++;
         }
      }
      strb.append("done" + count);
      return strb.toString();
   }

   /**
    * Copies the data rights settings for artifacts of type Abstract Software Requirement on a given branch to the same
    * artifacts on the destination branch. The destination branch should be appropriate to edit data rights on.
    *
    * @return Returns a list of the artifacts that do not have a source or destination or data rights
    * @throws Exception
    */
   @Path("software")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.TEXT_PLAIN)
   public Response copySWReqDataRights(@Context HttpHeaders httpHeaders, //
      @FormParam("sourceBranch") BranchId sourceBranch, //
      @FormParam("destinationBranch") BranchId destinationBranch) throws Exception {

      return copyDataRights(sourceBranch, destinationBranch, CoreArtifactTypes.AbstractSoftwareRequirement);
   }

   /**
    * Copies the data rights settings for artifacts of type Abstract Software Requirement on a given branch to the same
    * artifacts on the destination branch. The destination branch should be appropriate to edit data rights on.
    *
    * @return Returns a list of the artifacts that do not have a source or destination or data rights
    * @throws Exception
    */
   @Path("ssd")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.TEXT_PLAIN)
   public Response copySSDDataRights(@Context HttpHeaders httpHeaders, //
      @FormParam("sourceBranch") BranchId sourceBranch, //
      @FormParam("destinationBranch") BranchId destinationBranch) throws Exception {
      return copyDataRights(sourceBranch, destinationBranch, CoreArtifactTypes.SubsystemDesign);
   }

   private Response copyDataRights(BranchId sourceBranch, BranchId destinationBranch, IArtifactType artifactType) throws Exception {
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(destinationBranch).andIsOfType(artifactType).getResults();

      String branchName =
         orcsApi.getQueryFactory().branchQuery().andId(sourceBranch).getResults().getExactlyOne().getName();

      String txMsg = "Copy data rights for " + artifactType + " from " + branchName;
      TransactionBuilder txBuilder = createTxBuilder(txMsg, destinationBranch);

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      for (ArtifactReadable dest : results) {
         if (dest.isOfType(WCAFE)) {
            continue;
         }

         ArtifactReadable source = queryFactory.fromBranch(sourceBranch).andId(dest).getResults().getAtMostOneOrNull();
         if (source == null) {
            String classification = dest.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
            String subsystem = dest.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
            appendDetails("missing source", strb, dest, subsystem, classification);
         } else {
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.DataRightsClassification);
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.SubjectMatterExpert);
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.DataRightsBasis);
            count++;
         }
      }
      txBuilder.commit();

      strb.append("done: " + count);

      ResponseBuilder builder = Response.ok(strb.toString());
      builder.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);

      return builder.build();
   }

   private void appendDetails(String msg, StringBuilder strb, ArtifactReadable art, String subsystem, String classification) {
      strb.append(
         msg + "|" + subsystem + "|" + classification + " |" + art.getArtifactType() + "| " + art.getName() + "| " + art.getId() + "| " + art.getLastModifiedTransaction() + "<br />");
   }

   private TransactionBuilder createTxBuilder(String comment, BranchId branchId) {
      TransactionFactory txFactory = orcsApi.getTransactionFactory();
      ArtifactReadable userArtifact =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(50).getResults().getExactlyOne();

      return txFactory.createTransaction(branchId, userArtifact, comment);
   }

   private void setBestValue(TransactionBuilder txBuilder, ArtifactReadable source, ArtifactReadable dest, AttributeTypeId attributeType) throws Exception {
      String value = dest.getSoleAttributeValue(attributeType, "");
      String sourceValue = source.getSoleAttributeValue(attributeType, AttributeId.UNSPECIFIED);
      if (sourceValue.equals(AttributeId.UNSPECIFIED)) {
         if (value.isEmpty() && attributeType.equals(CoreAttributeTypes.DataRightsClassification)) {
            txBuilder.setSoleAttributeValue(dest, attributeType, AttributeId.UNSPECIFIED);
         }
      } else {
         if (value.isEmpty()) {
            txBuilder.setSoleAttributeValue(dest, attributeType, sourceValue);
         } else {
            if (!value.equals(sourceValue)) {
               logger.warn("%s", dest.getName() + " conflict with " + attributeType);
            }
         }
      }
   }
}