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
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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

   String branchUuid;
   int txId;

   public TxResource(UriInfo uriInfo2, Request request2, String branchUuid, int txId) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
      this.txId = txId;
   }

   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() throws OseeCoreException {
      TransactionCache txCache = OrcsApplication.getOrcsApi().getTxsCache();
      TransactionRecord txRecord = txCache.getOrLoad(txId);
      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(Collections.singleton(txRecord));
   }

}
