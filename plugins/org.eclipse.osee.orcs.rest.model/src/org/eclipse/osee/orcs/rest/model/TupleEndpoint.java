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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface TupleEndpoint {

   @Path("init")
   @POST
   public void addTuples();

   @Path("/tuple2")
   @GET
   <E1, E2> Long getTuple2(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1);

   @Path("/tuple2")
   @POST
   <E1, E2> Long addTuple2(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2);

   @Path("/tuple3")
   @POST
   <E1, E2> Long addTuple3(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2, @QueryParam("e3") String e3);

   @Path("/tuple4")
   @POST
   <E1, E2> Long addTuple4(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2, @QueryParam("e3") String e3, @QueryParam("e4") String e4);

}