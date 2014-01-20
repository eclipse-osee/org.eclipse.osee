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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsProgramData;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsAbstractSAXParser.AtsDataHandler;
import org.eclipse.osee.ats.rest.internal.build.report.parser.AtsProgramDataParser;
import org.eclipse.osee.ats.rest.internal.build.report.table.UrlListTable;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;

/**
 * @author John Misinco
 */
@Path("programs")
public class ProgramsResource {

   private static final String PROGRAM_URI_TEMPLATE = "program/%s?program=%s";

   private final Log logger;

   public ProgramsResource(Log logger) {
      this.logger = logger;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public StreamingOutput getPrograms() {
      return new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            final UrlListTable table = new UrlListTable(output);
            try {
               table.initializeTable("Programs", "Programs");
               AtsDataHandler<AtsProgramData> handler = new AtsDataHandler<AtsProgramData>() {

                  @Override
                  public void handleData(AtsProgramData data) {
                     String uri = String.format(PROGRAM_URI_TEMPLATE, data.getProgramId(), data.getProgramName());
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
      };

   }
}
