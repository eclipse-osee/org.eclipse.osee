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
package org.eclipse.osee.ats.rest.internal.build.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsWorkflowData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.ArtIdParser;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsWorkflowDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.resources.BuildTraceReportResource;
import org.eclipse.osee.ats.rest.internal.build.report.table.BuildTraceTable;
import org.eclipse.osee.ats.rest.internal.build.report.table.BuildTraceTable.VerifierUriProvider;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John Misinco
 * @author Megumi Telles
 */
public class BuildTraceReport {

   private static final Pair<String, String> EMPTY_PAIR = new Pair<String, String>("", "");

   public void getBuildArchive(OutputStream output, OrcsApi orcsApi, Log logger, String fileName, String programId, String buildId, String programName, String buildName, UriInfo uriInfo) {
      ByteArrayOutputStream tableStream = new ByteArrayOutputStream();
      final ZipOutputStream zout = new ZipOutputStream(output);

      String supportFilesUrl =
         uriInfo.getBaseUriBuilder().path(BuildTraceReportResource.RESOURCE_BASE).host("localhost").build().toString();

      ArchiveCollector dataCollector = new ArchiveCollector(supportFilesUrl, logger);
      createTraceReport(buildId, programName, buildName, AtsElementData.ARCHIVE_REPORT_TEMPLATE, tableStream,
         dataCollector, orcsApi, logger);
      try {
         zout.putNextEntry(new ZipEntry(fileName + ".html"));
         tableStream.writeTo(zout);
         zout.closeEntry();
         dataCollector.writeArchive(zout);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(zout);
      }
   }

   public void getBuildReport(OutputStream output, OrcsApi orcsApi, Log logger, String programId, String buildId, String programName, String buildName) {
      createTraceReport(buildId, programName, buildName, AtsElementData.CHANGE_REPORT_URL_TEMPLATE, output, null,
         orcsApi, logger);
   }

   private void createTraceReport(final String buildId, final String programName, final String buildName, String urlTemplate, OutputStream output, final ArchiveCollector dataCollector, OrcsApi orcsApi, final Log logger) {
      final QueryFactory queryFactory = orcsApi.getQueryFactory(null);
      final IOseeBranch branch = getBaselineBranch(buildId, queryFactory);
      final List<Pair<String, String>> buildNamesToUrls = getBuildNameUrlPairs(buildId, queryFactory);

      final BuildTraceTable buildTraceTable = new BuildTraceTable(output, urlTemplate, new VerifierUriProvider() {

         @Override
         public List<Pair<String, String>> getBuildToUrlPairs(String verifierName) {
            List<Pair<String, String>> toReturn = new LinkedList<Pair<String, String>>();
            String fullPath = convertVerifierNameToPath(verifierName);
            if (Strings.isValid(fullPath)) {
               for (Pair<String, String> buildToUrl : buildNamesToUrls) {
                  String url = String.format(buildToUrl.getSecond(), fullPath);
                  URI loadSourceResource =
                     UriBuilder.fromPath("..").path("sourceFile").queryParam("url", url).queryParam("offline",
                        dataCollector != null).build();
                  toReturn.add(new Pair<String, String>(buildToUrl.getFirst(), loadSourceResource.toString()));
               }
            } else {
               for (int i = 0; i < getColumnCount(); i++) {
                  toReturn.add(EMPTY_PAIR);
               }
            }
            if (dataCollector != null) {
               dataCollector.onBuildToUrlPairs(verifierName, toReturn);
            }
            return toReturn;
         }

         @Override
         public int getColumnCount() {
            return buildNamesToUrls.size();
         }
      });
      buildTraceTable.initializeTraceReportTable(programName, buildName);
      AtsDataHandler<AtsWorkflowData> handler = new AtsDataHandler<AtsWorkflowData>() {

         @Override
         public void handleData(AtsWorkflowData data) {
            if (data.getWorkflowBuildId().equals(buildId)) {
               String pcrId = data.getWorkflowPcrId();
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
               if (dataCollector != null) {
                  dataCollector.onPcrId(pcrId);
               }
            }
         }
      };

      AtsWorkflowDataParser parser = new AtsWorkflowDataParser(InputFilesUtil.getWorkflowFile(), handler);
      parser.parseDocument();
      buildTraceTable.close();
   }

   private IOseeBranch getBaselineBranch(String buildId, QueryFactory queryFactory) {
      QueryBuilder builder = queryFactory.fromBranch(AtsUtilCore.getAtsBranch());
      ArtifactReadable buildArt = builder.andGuid(buildId).getResults().getExactlyOne();
      ResultSet<? extends AttributeReadable<String>> branchUuids =
         buildArt.getAttributes(AtsAttributeTypes.BaselineBranchUuid);
      Conditions.checkNotNull(branchUuids, "branchUuids");
      String baselineBranchUuid = branchUuids.getExactlyOne().getValue();
      IOseeBranch branch = TokenFactory.createBranch(Long.valueOf(baselineBranchUuid), "TraceReport Branch");
      return branch;
   }

   private List<Pair<String, String>> getBuildNameUrlPairs(String buildId, QueryFactory queryFactory) {
      List<Pair<String, String>> toReturn = new LinkedList<Pair<String, String>>();
      QueryBuilder builder = queryFactory.fromBranch(AtsUtilCore.getAtsBranch());
      ArtifactReadable buildArt = builder.andGuid(buildId).getResults().getExactlyOne();
      String buildToUrlPairs = buildArt.getSoleAttributeAsString(AtsAttributeTypes.TestToSourceLocator, "");
      if (Strings.isValid(buildToUrlPairs)) {
         for (String pair : buildToUrlPairs.split("\n")) {
            String[] tokens = pair.trim().split("@");
            String build = tokens[0];
            String url = tokens[1];
            toReturn.add(new Pair<String, String>(build, url));
         }
      }
      return toReturn;
   }

   private String convertVerifierNameToPath(String verifierName) {
      int lastDot = verifierName.lastIndexOf(".");
      if (lastDot == -1) {
         return Strings.EMPTY_STRING;
      }
      String packageName = verifierName.substring(0, lastDot);
      List<String> paths = new LinkedList<String>();
      paths.add(packageName);
      paths.add("src");
      for (String token : packageName.split("\\.")) {
         paths.add(token);
      }
      paths.add(verifierName.substring(lastDot + 1) + ".java");
      return Collections.toString("/", paths);
   }

}
