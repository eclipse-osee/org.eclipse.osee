/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;

public interface TupleEndpoint {

   @GET
   @Path("tuple2")
   <E1, E2> Long getTuple2(@QueryParam("tupleType") Tuple2Type<E1, E2> tupleType, @QueryParam("e1") E1 e1);

   @POST
   @Path("tuple2")
   @Produces(MediaType.APPLICATION_JSON)
   <E1, E2> GammaId addTuple2(@QueryParam("tupleType") Tuple2Type<E1, E2> tupleType, @QueryParam("e1") E1 e1, @QueryParam("e2") E2 e2);

   @POST
   @Path("tuple3")
   @Produces(MediaType.APPLICATION_JSON)
   <E1, E2, E3> GammaId addTuple3(@QueryParam("tupleType") Tuple3Type<E1, E2, E3> tupleType, @QueryParam("e1") E1 e1, @QueryParam("e2") E2 e2, @QueryParam("e3") E3 e3);

   @POST
   @Path("tuple4")
   @Produces(MediaType.APPLICATION_JSON)
   <E1, E2, E3, E4> GammaId addTuple4(@QueryParam("tupleType") Tuple4Type<E1, E2, E3, E4> tupleType, @QueryParam("e1") E1 e1, @QueryParam("e2") E2 e2, @QueryParam("e3") E3 e3, @QueryParam("e4") E4 e4);

   @DELETE
   @Path("{tupleTable}/{gammaId}")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId deleteTuple(@PathParam("tupleTable") int tupleTable, @PathParam("gammaId") GammaId gammaId);
}