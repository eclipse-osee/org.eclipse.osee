/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreInfo;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class DatastoreEndpointImpl implements DatastoreEndpoint {
   @Context
   private UriInfo uriInfo;
   private final ActivityLog activityLog;
   private final OrcsApi orcsApi;

   public DatastoreEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.activityLog = activityLog;
   }

   protected void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   @Override
   public DatastoreInfo getInfo() {
      Callable<OrcsMetaData> callable = orcsApi.getAdminOps().createFetchOrcsMetaData();
      OrcsMetaData metaData = executeCallable(callable);
      return asDatastoreInfo(metaData);
   }

   @Override
   public void initialize(String typeModel) {
      activityLog.setEnabled(false);
      orcsApi.getAdminOps().createDatastoreAndSystemBranches(typeModel);
      activityLog.setEnabled(true);
   }

   @Override
   public Response migrate() {
      activityLog.setEnabled(false);

      Callable<OrcsMetaData> callable = orcsApi.getAdminOps().migrateDatastore();
      OrcsMetaData metaData = executeCallable(callable);
      URI location = getDatastoreLocation(uriInfo);
      return Response.created(location).entity(asDatastoreInfo(metaData)).build();
   }

   private URI getDatastoreLocation(UriInfo uriInfo) {
      return uriInfo.getRequestUriBuilder().path("../").path("info").build();
   }

   private DatastoreInfo asDatastoreInfo(OrcsMetaData metaData) {
      DatastoreInfo info = new DatastoreInfo();
      info.setProperties(metaData.getProperties());
      return info;
   }

   @Override
   public void createDemoBranches() {
      orcsApi.getAdminOps().createDemoBranches();
   }

   @Override
   public TransactionId createUsers(List<UserToken> users, UserId account) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(COMMON, account, "DatastoreEndpointImpl.createUsers()");
      orcsApi.getAdminOps().createUsers(tx, users);
      return tx.commit();
   }

   @Override
   public TransactionId createUser(UserToken user, UserId account) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(COMMON, account, "DatastoreEndpointImpl.createUser()");
      orcsApi.getAdminOps().createUser(tx, user);
      return tx.commit();
   }
}