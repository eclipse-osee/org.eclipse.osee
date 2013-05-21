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
package org.eclipse.osee.ats.rest.internal.resources;

import java.io.OutputStream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.rest.internal.AtsApplication;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsBuildData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsBuildDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.table.UrlListTable;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author John Misinco
 */
@Path("program")
public class ProgramResource {

   private static final String TRACE_URI_TEMPLATE = "%sbuildTraceReport/%s/%s?program=%s&build=%s";

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Path("{programId}")
   @Produces(MediaType.TEXT_HTML)
   public StreamingOutput getBuilds(@PathParam("programId") final String programId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName) {
      return new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            final UrlListTable table = new UrlListTable(output);
            try {
               table.initializeTable("Builds", programName + " Builds");

               AtsDataHandler<AtsBuildData> handler = new AtsDataHandler<AtsBuildData>() {

                  @Override
                  public void handleData(AtsBuildData data) {
                     if (data.getBuildProgramId().equals(programId)) {
                        String uri =
                           String.format(TRACE_URI_TEMPLATE, uriInfo.getBaseUri(), programId, data.getBuildId(),
                              programName, data.getBuildName());
                        try {
                           table.addUrl(data.getBuildName(), uri);
                        } catch (OseeCoreException ex) {
                           AtsApplication.getLogger().error(ex, "Error handling AtsBuildData");
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
      };
   }
}
