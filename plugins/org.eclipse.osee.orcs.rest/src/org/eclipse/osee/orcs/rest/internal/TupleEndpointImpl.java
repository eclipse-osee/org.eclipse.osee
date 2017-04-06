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
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Angel Avila
 */
public class TupleEndpointImpl implements TupleEndpoint {

   private final OrcsApi orcsApi;

   @Context
   private UriInfo uriInfo;
   private final BranchId branch;

   public TupleEndpointImpl(OrcsApi orcsApi, BranchId branch) {
      this.orcsApi = orcsApi;
      this.branch = branch;
   }

   @Override
   @Path("init")
   @POST
   public void addTuples() {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, getUser(), "Init Tuples");

      Tuple3Type<Long, Long, Long> ViewApplicability22 = Tuple3Type.valueOf(ProductLineFamily, 20L);
      Tuple4Type<Long, Long, Long, Long> OseeTypeDef22 = Tuple4Type.valueOf(DefaultFamily, 40L);

      tx.addTuple3(ViewApplicability22, 412215L, 466L, 4L);
      tx.addTuple4(OseeTypeDef22, 222112L, 44L, 54445L, 66L);

      tx.commit();
   }

   @Override
   @Path("/tuple2")
   @GET
   public <E1, E2> Long getTuple2(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1) {
      Iterable<Object> iterable =
         orcsApi.getQueryFactory().tupleQuery().getTuple2(Tuple2Type.valueOf(tupleType), branch, e1);
      if (iterable.iterator().hasNext()) {
         return (Long) iterable.iterator().next();
      }
      return -1L;
   }

   @Override
   @Path("/tuple2")
   @POST
   public <E1, E2> Long addTuple2(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2) {
      Long toReturn;
      Object element1 = e1;
      Object element2 = e2;
      if (Strings.isNumeric(e1)) {
         element1 = Long.parseLong(e1);
      }
      if (Strings.isNumeric(e2)) {
         element2 = Long.parseLong(e2);
      }

      TupleTypeId typleTypeId = TupleTypeId.valueOf(tupleType);

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, getUser(), "Add Tuple 2");
      toReturn = tx.addTuple(typleTypeId, element1, element2);
      tx.commit();
      return toReturn;
   }

   @Override
   @Path("/tuple3")
   @POST
   public <E1, E2> Long addTuple3(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2, @QueryParam("e3") String e3) {
      Long toReturn;
      Object element1 = e1;
      Object element2 = e2;
      Object element3 = e3;
      if (Strings.isNumeric(e1)) {
         element1 = Long.parseLong(e1);
      }
      if (Strings.isNumeric(e2)) {
         element2 = Long.parseLong(e2);
      }
      if (Strings.isNumeric(e3)) {
         element3 = Long.parseLong(e3);
      }
      TupleTypeId typleTypeId = TupleTypeId.valueOf(tupleType);

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, getUser(), "Add Tuple 3");
      toReturn = tx.addTuple(typleTypeId, element1, element2, element3);
      tx.commit();
      return toReturn;
   }

   @Override
   @Path("/tuple4")
   @POST
   public <E1, E2> Long addTuple4(@QueryParam("tupleType") Long tupleType, @QueryParam("e1") String e1, @QueryParam("e2") String e2, @QueryParam("e3") String e3, @QueryParam("e4") String e4) {
      Long toReturn;
      Object element1 = e1;
      Object element2 = e2;
      Object element3 = e3;
      Object element4 = e4;
      if (Strings.isNumeric(e1)) {
         element1 = Long.parseLong(e1);
      }
      if (Strings.isNumeric(e2)) {
         element2 = Long.parseLong(e2);
      }
      if (Strings.isNumeric(e3)) {
         element3 = Long.parseLong(e3);
      }
      if (Strings.isNumeric(e4)) {
         element4 = Long.parseLong(e4);
      }
      TupleTypeId typleTypeId = TupleTypeId.valueOf(tupleType);

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, getUser(), "Add Tuple 4");
      toReturn = tx.addTuple(typleTypeId, element1, element2, element3, element4);
      tx.commit();
      return toReturn;
   }

   private BranchId getAdminBranch() {
      return CoreBranches.COMMON;
   }

   private ArtifactReadable getUser() {
      return getQuery().fromBranch(getAdminBranch()).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private QueryFactory getQuery() {
      return orcsApi.getQueryFactory();
   }
}
