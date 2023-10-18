/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.internal.ScriptDefToken;

/**
 * @author Ryan T. Baldwin
 */
@Path("import")
@Swagger
public interface ScriptImportEndpoint {

   @POST()
   @Path("file/{ciSetId}")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   TransactionResult importFile(@Multipart("file") InputStream stream, @PathParam("ciSetId") ArtifactId ciSetId);

   @POST()
   @Path("batch/{ciSetId}")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   TransactionResult importBatch(@Multipart("file") InputStream stream, @PathParam("ciSetId") ArtifactId ciSetId);

   @POST()
   @Path("builderdata/{ciSetId}")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   TransactionBuilderData getTxBuilderData(@Multipart("file") InputStream stream,
      @PathParam("ciSetId") ArtifactId ciSetId);

   @POST()
   @Path("token")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   ScriptDefToken getScriptDefinition(@Multipart("file") InputStream stream);

}
