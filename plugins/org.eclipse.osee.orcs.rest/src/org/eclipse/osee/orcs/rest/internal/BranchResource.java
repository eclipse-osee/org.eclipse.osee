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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchResource {

   private final UriInfo uriInfo;
   private final BranchId branchId;
   private final OrcsApi orcsApi;

   public BranchResource(UriInfo uriInfo, BranchId branchId, OrcsApi orcsApi) {
      this.uriInfo = uriInfo;
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
      ResultSet<Branch> results = query.andId(branchId).getResults();
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(results);
   }
}