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
package org.eclipse.osee.disposition.rest.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Angel Avila
 */
public class DispoSourceFileResource {
   private final DispoApi dispoApi;
   private final BranchId branch;
   private final String setId;

   public DispoSourceFileResource(DispoApi dispoApi, BranchId branch, String setId) {
      this.dispoApi = dispoApi;
      this.branch = branch;
      this.setId = setId;
   }

   /**
    * Get a specific Source File given a file name
    *
    * @param fileName The name of the Source File to search for
    * @return The found Source File if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Source File
    * @response.representation.404.doc Not Found, Could not the Source File
    */
   @Path("{fileName}/{fileNumber}")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getDispoItemsById(@PathParam("fileName") String fileName, @PathParam("fileNumber") String fileNumber) {
      if (!fileName.endsWith(".LIS")) {
         fileName = fileName.replaceAll(dispoApi.getConfig().getFileExtRegex(), ".LIS");
      }

      DispoSet set = dispoApi.getDispoSetById(branch, setId);
      String fullPath = set.getImportPath() + File.separator + "vcast" + File.separator + fileName;
      if (!(new File(fullPath).exists())) {
         String regex = "\\.2\\.";
         fullPath = fullPath.replaceAll(regex, String.format(".%s.2.", fileNumber));
      }
      final File result = new File(fullPath);

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException {
            FileInputStream inputStream = null;
            Writer writer = null;
            try {
               inputStream = new FileInputStream(result);
               writer = new OutputStreamWriter(outputStream, "UTF-8");
               int c;
               while ((c = inputStream.read()) != -1) {
                  writer.write(c);
               }
               outputStream.flush();
            } catch (IOException ex) {
               throw new OseeCoreException(ex);
            } finally {
               Lib.close(inputStream);
               Lib.close(writer);
            }

         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/text").build();
   }
}
