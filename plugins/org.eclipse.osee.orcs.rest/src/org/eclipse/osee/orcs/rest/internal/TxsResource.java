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
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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

      TransactionCache txCache = OrcsApplication.getOrcsApi().getTxsCache();
      Branch branch = OrcsApplication.getOrcsApi().getBranchCache().getByGuid(branchUuid);

      TransactionRecord txBase = txCache.getTransaction(branch, TransactionVersion.BASE);
      TransactionRecord txHead = txCache.getTransaction(branch, TransactionVersion.HEAD);

      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(Arrays.asList(txBase, txHead));
   }
}
