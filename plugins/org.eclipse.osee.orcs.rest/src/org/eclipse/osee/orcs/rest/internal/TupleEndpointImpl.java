/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.ProductLineFamily;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Angel Avila
 */
public class TupleEndpointImpl implements TupleEndpoint {

   private final OrcsApi orcsApi;

   @Context
   private UriInfo uriInfo;
   private final BranchId branch;
   private final UserId account;

   public TupleEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.account = account;
   }

   @Override
   @Path("init")
   @POST
   public void addTuples() {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Init Tuples");

      Tuple3Type<Long, Long, Long> ViewApplicability22 = Tuple3Type.valueOf(ProductLineFamily, 20L);
      Tuple4Type<Long, Long, Long, Long> OseeTypeDef22 = Tuple4Type.valueOf(DefaultFamily, 40L);

      tx.addTuple3(ViewApplicability22, 412215L, 466L, 4L);
      tx.addTuple4(OseeTypeDef22, 222112L, 44L, 54445L, 66L);

      tx.commit();
   }

   @Override
   @Path("/tuple2")
   @GET
   public <E1, E2> Long getTuple2(@QueryParam("tupleType") Tuple2Type<E1, E2> tupleType, @QueryParam("e1") E1 e1) {
      Iterable<E2> iterable = orcsApi.getQueryFactory().tupleQuery().getTuple2(tupleType, branch, e1);
      if (iterable.iterator().hasNext()) {
         return (Long) iterable.iterator().next();
      }
      return -1L;
   }

   @Override
   public <E1, E2> GammaId addTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Add Tuple 2");
      GammaId gammaId = tx.addTuple2(tupleType, e1, e2);
      tx.commit();
      return gammaId;
   }

   @Path("/tuple3")
   @POST
   @Override
   public <E1, E2, E3> GammaId addTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Add Tuple 3");
      GammaId gammaId = tx.addTuple3(tupleType, e1, e2, e3);
      tx.commit();
      return gammaId;
   }

   @Path("/tuple4")
   @POST
   @Override
   public <E1, E2, E3, E4> GammaId addTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Add Tuple 4");
      GammaId gammaId = tx.addTuple4(tupleType, e1, e2, e3, e4);
      tx.commit();
      return gammaId;
   }
}