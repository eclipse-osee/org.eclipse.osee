/*********************************************************************
 * Copyright (c) 2015 Boeing
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Roberto E. Escobar
 */
@Path("datastore")
public interface DatastoreEndpoint {

   @GET
   @Path("info")
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   DatastoreInfo getInfo();

   @POST
   @Path("initialize")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId initialize(UserToken superUser);

   @POST
   @Path("synonyms")
   @Consumes(MediaType.TEXT_PLAIN)
   void synonyms();

   @POST
   @Path("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId createUsers(Iterable<UserToken> users);
}