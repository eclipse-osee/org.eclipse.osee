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

import java.util.Collections;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Roberto E. Escobar
 */
public class TxResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   Long branchUuid;
   int txId;

   public TxResource(UriInfo uriInfo2, Request request2, Long branchUuid, int txId) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
      this.txId = txId;
   }

   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() throws OseeCoreException {
      QueryFactory queryFactory = OrcsApplication.getOrcsApi().getQueryFactory(null);
      TransactionQuery query2 = queryFactory.transactionQuery();
      TransactionReadable baseTransaction = query2.andTxId(txId).getResults().getExactlyOne();
      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(Collections.singleton(baseTransaction));
   }

}
