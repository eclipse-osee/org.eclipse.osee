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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   BranchId branchId;
   OrcsApi orcsApi;

   public BranchResource(UriInfo uriInfo, Request request, BranchId branchId, OrcsApi orcsApi) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.orcsApi = orcsApi;
   }

   /**
    * @return Html representation of branche(s) that match UUID. Although it's not expected, no exception thrown if
    * multiple branches found.
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      ResultSet<BranchReadable> results = query.andId(branchId).getResults();
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(results);
   }

}
