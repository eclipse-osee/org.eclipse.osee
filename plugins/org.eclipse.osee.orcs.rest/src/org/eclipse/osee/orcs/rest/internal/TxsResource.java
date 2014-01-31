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

import java.util.Arrays;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Roberto E. Escobar
 */
public class TxsResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   String branchUuid;

   public TxsResource(UriInfo uriInfo, Request request, String branchUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
   }

   @Path("{txId}")
   public TxResource getTransaction(@PathParam("txId") int txId) {
      return new TxResource(uriInfo, request, branchUuid, txId);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() throws OseeCoreException {

      QueryFactory queryFactory = OrcsApplication.getOrcsApi().getQueryFactory(null);
      BranchQuery query = queryFactory.branchQuery();
      ResultSet<BranchReadable> results = query.andUuids(branchUuid).getResults();
      BranchReadable branch = results.getExactlyOne();

      TransactionQuery query1 = queryFactory.transactionQuery();
      TransactionReadable headTransaction = query1.andIsHead(branch).getResults().getExactlyOne();

      TransactionQuery query2 = queryFactory.transactionQuery();
      TransactionReadable baseTransaction = query2.andTxId(branch.getBaseTransaction()).getResults().getExactlyOne();

      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(Arrays.asList(baseTransaction, headTransaction));
   }
}
