/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.UserTokens;

/**
 * @author Donald G. Dunne
 */
@Path("user")
public interface UserEndpoint {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public UserTokens get();

   /**
    * Upload or replace the user's public certificate. Only PEM format certificates accepted.
    */
   @PUT
   @Path("public-certificate")
   @Consumes({MediaType.TEXT_PLAIN})
   void uploadPublicCertificate(String certificatePem);

   /**
    * Retrieve the user's public certificate.
    */
   @GET
   @Path("public-certificate")
   @Produces({MediaType.TEXT_PLAIN})
   Response getPublicCertificate();

   /**
    * Delete the user's public certificate.
    */
   @DELETE
   @Path("public-certificate")
   void deletePublicCertificate();
}
