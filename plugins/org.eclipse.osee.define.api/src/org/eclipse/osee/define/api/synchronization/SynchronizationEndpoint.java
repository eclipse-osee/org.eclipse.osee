/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.api.synchronization;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

/**
 * This interface defines the REST API end points for importing and exporting synchronization artifacts.
 *
 * @author Loren K. Ashley
 */

@Path("synchronization")
public interface SynchronizationEndpoint {

   /**
    * Makes a request to export a Synchronization Artifact.
    *
    * @param exportRequest an {@link ExportRequest} object containing the export parameters.
    * @return an {@link InputStream} containing the synchronization artifact.
    */

   @POST
   @Path("export")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_OCTET_STREAM})
   InputStream exporter(ExportRequest exportRequest);

   /**
    * Makes a request to import a Synchronization Artifact.
    *
    * @param importRequest an {@link ImportRequest} object containing the import parameters.
    * @param inputStream an {@link InputStream} containing the synchronization artifact.
    */

   //@formatter:off
   @POST
   @Path("import")
   @Consumes({MediaType.MULTIPART_FORM_DATA})
   void
      importer
         (
            @Multipart( value = "importRequest", type = MediaType.APPLICATION_JSON )         ImportRequest importRequest,
            @Multipart( value = "file",          type = MediaType.APPLICATION_OCTET_STREAM ) InputStream   inputStream
         );
   //@formatter:on

}

/* EOF */