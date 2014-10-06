/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("file")
public class OteFilesResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public StreamingOutput getFile(@QueryParam("path") String path ) throws OseeCoreException {
      final String myPath = path;
      return new StreamingOutput() {
         public void write(OutputStream output) throws IOException, WebApplicationException {
             try {
                File file = new File(myPath);
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[2048];
                int numRead = 0;
                while((numRead = is.read(bytes)) != -1){
                   output.write(bytes, 0, numRead);
                }
                is.close();
             } catch (Exception e) {
                 throw new WebApplicationException(e);
             }
         }
     };
   }
   
}
