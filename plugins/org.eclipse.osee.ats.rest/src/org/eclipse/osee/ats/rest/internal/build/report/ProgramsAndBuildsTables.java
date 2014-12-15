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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsBuildData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsProgramData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsBuildDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsProgramDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.table.UrlListTable;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author John Misinco
 */
public class ProgramsAndBuildsTables {
   private static final String LAST_SUCCESSFUL_RUN = "lastSuccessfulRun";

   public void getProgramsTable(OrcsApi orcsApi, final Log logger, OutputStream output) {
      final UrlListTable table = new UrlListTable(output);
      try {
         table.initializeTable("Programs", getLastSuccessfulRun(orcsApi), "Programs");
         AtsDataHandler<AtsProgramData> handler = new AtsDataHandler<AtsProgramData>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleData(AtsProgramData data) {
               String uri =
                  UriBuilder.fromPath("program").path(data.getProgramId()).queryParam("program", data.getProgramName()).build().toString();
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

   public void getBuildsTable(OrcsApi orcsApi, final Log logger, OutputStream output, final String programName, final String programId) {
      final UrlListTable table = new UrlListTable(output);
      try {
         table.initializeTable("Builds", getLastSuccessfulRun(orcsApi), programName + " Builds", "Offline Download");

         AtsDataHandler<AtsBuildData> handler = new AtsDataHandler<AtsBuildData>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleData(AtsBuildData data) {
               if (data.getBuildProgramId().equals(programId)) {
                  String buildUri =
                     UriBuilder.fromPath("..").path(programId).queryParam("program", programName).path(
                        data.getBuildId()).queryParam("build", data.getBuildName()).build().toString();

                  String archiveUri =
                     UriBuilder.fromPath("..").path("archive").path(programId).queryParam("program", programName).path(
                        data.getBuildId()).queryParam("build", data.getBuildName()).build().toString();
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

   private String getLastSuccessfulRun(OrcsApi orcsApi) {
      String toReturn = "Last run time not set";
      ArtifactReadable webGroup =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(AtsArtifactToken.WebPrograms).getResults().getExactlyOne();
      if (webGroup != null) {
         List<String> staticIds = webGroup.getAttributeValues(CoreAttributeTypes.StaticId);
         for (String staticId : staticIds) {
            if (staticId.startsWith(LAST_SUCCESSFUL_RUN)) {
               String[] tokens = staticId.split("=");
               if (tokens.length == 2) {
                  Date date = new Date(Long.parseLong(tokens[1]));
                  SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                  toReturn = String.format("Last successful synchronization:\n%s", dateFormat.format(date));
                  break;
               }
            }
         }
      }
      return toReturn;
   }

}
