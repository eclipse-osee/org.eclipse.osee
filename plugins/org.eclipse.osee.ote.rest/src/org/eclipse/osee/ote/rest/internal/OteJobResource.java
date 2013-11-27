/*******************************************************************************
9 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.internal;

import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteJobResource {

   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final String uuid;
   
   private final OteConfigurationStore store;
   

   public OteJobResource(UriInfo uriInfo, Request request, OteConfigurationStore store, String id) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.uuid = id;
      this.store = store;
   }
   
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OTEJobStatus getConfig() throws OseeCoreException, InterruptedException, ExecutionException {
	  return store.getJob(uuid);
   }
 
}
