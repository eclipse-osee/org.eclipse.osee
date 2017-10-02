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
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
@Path("branch")
public class BranchesResource {

   // Allows to insert contextual objects into the class,
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @Path("{uuid}")
   public BranchResource getBranch(@PathParam("uuid") BranchId id) {
      return new BranchResource(uriInfo, request, id, OrcsApplication.getOrcsApi());
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() {
      BranchQuery query = OrcsApplication.getOrcsApi().getQueryFactory().branchQuery();
      ResultSet<BranchReadable> results = query.andIsOfType(BranchType.BASELINE, BranchType.WORKING).getResults();

      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(results);
   }

   @Path("{branch}/tuples")
   public TupleEndpoint getTuples(@PathParam("branch") BranchId branch, @HeaderParam("osee.account.id") UserId accountId) {
      return new TupleEndpointImpl(OrcsApplication.getOrcsApi(), branch, accountId);
   }

   @Path("{branch}/applic")
   public ApplicabilityEndpoint getApplicability(@PathParam("branch") BranchId branch, @HeaderParam("osee.account.id") UserId accountId) {
      return new ApplicabilityEndpointImpl(OrcsApplication.getOrcsApi(), branch, accountId);
   }
}
