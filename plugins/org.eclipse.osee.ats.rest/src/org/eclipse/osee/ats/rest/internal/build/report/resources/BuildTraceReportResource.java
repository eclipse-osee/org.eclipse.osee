/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsWorkflowData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.ArtIdParser;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsWorkflowDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.table.BuildTraceTable;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import com.sun.jersey.core.header.ContentDisposition;

/**
 * @author John Misinco
 * @author Megumi Telles
 */
@Path("buildTraceReport")
public class BuildTraceReportResource {

   private static final String CHANGE_REPORTS_PATH = "/atsData/changeReports/";

   private final Log logger;
   private final OrcsApi orcsApi;

   public BuildTraceReportResource(Log logger, OrcsApi orcsApi) {
      this.logger = logger;
      this.orcsApi = orcsApi;
   }

   @GET
   @Path("archive/{programId}/{buildId}")
   @Produces("application/zip")
   public Response getBuildArchive(@PathParam("programId") String programId, @PathParam("buildId") final String buildId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName, @DefaultValue("UNKNOWN") @QueryParam("build") final String buildName) {
      final String fileName = programName + "_" + buildName;

      ContentDisposition contentDisposition =
         ContentDisposition.type("attachment").fileName(fileName + ".zip").creationDate(new Date()).build();

      return Response.ok(new StreamingOutput() {
         @Override
         public void write(OutputStream output) throws WebApplicationException {
            ByteArrayOutputStream tableStream = new ByteArrayOutputStream();
            final ZipOutputStream zout = new ZipOutputStream(output);
            final String serverData = OseeServerProperties.getOseeApplicationServerData(null);

            Set<String> pcrIds = new HashSet<String>();
            createTraceReport(buildId, programName, buildName, AtsElementData.ARCHIVE_URL_TEMPLATE, pcrIds, tableStream);
            try {
               zout.putNextEntry(new ZipEntry(fileName + ".html"));
               tableStream.writeTo(zout);
               zout.closeEntry();

               for (String pcrId : pcrIds) {
                  String pcrFileName = pcrId + ".xml";
                  File file = new File(serverData + CHANGE_REPORTS_PATH + pcrFileName);
                  if (file.exists()) {
                     FileInputStream fis = null;
                     try {
                        fis = new FileInputStream(file);
                        zout.putNextEntry(new ZipEntry("changeReports/" + pcrFileName));
                        Lib.inputStreamToOutputStream(fis, zout);
                        zout.closeEntry();
                     } catch (IOException ex) {
                        OseeExceptions.wrapAndThrow(ex);
                     } finally {
                        Lib.close(fis);
                     }
                  }
               }

            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(zout);
            }
         }
      }).header("Content-Disposition", contentDisposition).build();
   }

   @GET
   @Path("{programId}/{buildId}")
   @Produces(MediaType.TEXT_HTML)
   public StreamingOutput getBuildReport(@PathParam("programId") String programId, @PathParam("buildId") final String buildId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName, @DefaultValue("UNKNOWN") @QueryParam("build") final String buildName) {
      return new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            createTraceReport(buildId, programName, buildName, AtsElementData.CHANGE_REPORT_URL_TEMPLATE, null, output);
         }
      };
   }

   private void createTraceReport(final String buildId, final String programName, final String buildName, String urlTemplate, final Collection<String> pcrIds, OutputStream output) {
      final QueryFactory queryFactory = orcsApi.getQueryFactory(null);
      final IOseeBranch branch = getBaselineBranch(buildId, queryFactory);

      final BuildTraceTable buildTraceTable = new BuildTraceTable(output, urlTemplate);
      buildTraceTable.initializeTraceReportTable(programName, buildName);
      AtsDataHandler<AtsWorkflowData> handler = new AtsDataHandler<AtsWorkflowData>() {

         @Override
         public void handleData(AtsWorkflowData data) {
            if (data.getWorkflowBuildId().equals(buildId)) {
               String pcrId = data.getWorkflowPcrId();
               if (pcrIds != null) {
                  pcrIds.add(pcrId);
               }
               try {
                  Collection<Integer> artIds = ArtIdParser.getArtIds(pcrId);
                  Map<ArtifactReadable, Iterable<ArtifactReadable>> requirementsToTests =
                     new LinkedHashMap<ArtifactReadable, Iterable<ArtifactReadable>>();

                  if (Conditions.hasValues(artIds)) {
                     ResultSet<ArtifactReadable> requirements =
                        queryFactory.fromBranch(branch).andLocalIds(artIds).getResults();

                     for (ArtifactReadable requirement : requirements) {
                        ResultSet<ArtifactReadable> verifiers =
                           requirement.getRelated(CoreRelationTypes.Verification__Verifier);
                        requirementsToTests.put(requirement, verifiers);
                     }

                  }
                  buildTraceTable.addRpcrToTable(pcrId, requirementsToTests);

               } catch (OseeCoreException ex) {
                  logger.error(ex, "Error handling AtsWorkflowData");
               }
            }
         }
      };

      AtsWorkflowDataParser parser = new AtsWorkflowDataParser(InputFilesUtil.getWorkflowFile(), handler);
      parser.parseDocument();
      buildTraceTable.close();
   }

   private IOseeBranch getBaselineBranch(String buildId, QueryFactory queryFactory) {
      QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON);
      ArtifactReadable buildArt = builder.andGuid(buildId).getResults().getExactlyOne();
      ResultSet<? extends AttributeReadable<String>> branchGuids =
         buildArt.getAttributes(AtsAttributeTypes.BaselineBranchGuid);
      Conditions.checkNotNull(branchGuids, "branchGuid");
      String baselineBranchGuid = branchGuids.getExactlyOne().getValue();
      IOseeBranch branch = TokenFactory.createBranch(baselineBranchGuid, "TraceReport Branch");
      return branch;
   }

}
