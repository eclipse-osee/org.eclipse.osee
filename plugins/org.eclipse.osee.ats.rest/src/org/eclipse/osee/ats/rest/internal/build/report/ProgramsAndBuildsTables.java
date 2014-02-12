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

import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsBuildData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsProgramData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsBuildDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsProgramDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.resources.BuildTraceReportResource;
import org.eclipse.osee.ats.rest.internal.build.report.table.UrlListTable;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;

/**
 * @author John Misinco
 */
public class ProgramsAndBuildsTables {

   public void getProgramsTable(final Log logger, OutputStream output, final UriInfo uriInfo) {
      final UrlListTable table = new UrlListTable(output);
      try {
         table.initializeTable("Programs", "Programs");
         AtsDataHandler<AtsProgramData> handler = new AtsDataHandler<AtsProgramData>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleData(AtsProgramData data) {
               String uri =
                  uriInfo.getRequestUriBuilder().path("program").path(data.getProgramId()).queryParam("program",
                     data.getProgramName()).build().toString();
               try {
                  Pair<String, String> pair = new Pair<String, String>(data.getProgramName(), uri);
                  table.addUrl(pair);
               } catch (OseeCoreException ex) {
                  logger.error(ex, "Error handling AtsProgramData");
               }

            }
         };
         AtsProgramDataParser parser = new AtsProgramDataParser(InputFilesUtil.getProgramFile(), handler);
         parser.parseDocument();
         table.close();
      } catch (OseeCoreException ex) {
         throw new WebApplicationException(ex);
      }
   }

   public void getBuildsTable(final Log logger, OutputStream output, final String programName, final String programId, final UriInfo uriInfo) {
      final UrlListTable table = new UrlListTable(output);
      try {
         table.initializeTable("Builds", programName + " Builds", "Offline Download");

         AtsDataHandler<AtsBuildData> handler = new AtsDataHandler<AtsBuildData>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleData(AtsBuildData data) {
               if (data.getBuildProgramId().equals(programId)) {
                  String buildUri =
                     uriInfo.getBaseUriBuilder().path(BuildTraceReportResource.RESOURCE_BASE).path(programId).queryParam(
                        "program", programName).path(data.getBuildId()).queryParam("build", data.getBuildName()).build().toString();

                  String archiveUri =
                     uriInfo.getBaseUriBuilder().path(BuildTraceReportResource.RESOURCE_BASE).path("archive").path(
                        programId).queryParam("program", programName).path(data.getBuildId()).queryParam("build",
                        data.getBuildName()).build().toString();
                  try {
                     Pair<String, String> build = new Pair<String, String>(data.getBuildName(), buildUri);
                     Pair<String, String> offline = new Pair<String, String>("download", archiveUri);
                     table.addUrl(build, offline);
                  } catch (OseeCoreException ex) {
                     logger.error(ex, "Error handling AtsBuildData");
                  }

               }
            }
         };
         AtsBuildDataParser parser = new AtsBuildDataParser(InputFilesUtil.getBuildFile(), handler);
         parser.parseDocument();
         table.close();
      } catch (OseeCoreException ex) {
         throw new WebApplicationException(ex);
      }
   }

}
