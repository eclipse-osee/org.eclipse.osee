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
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserTokens;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreInfo;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class DatastoreEndpointImpl implements DatastoreEndpoint {
   @Context
   private UriInfo uriInfo;
   private final ActivityLog activityLog;
   private final OrcsAdmin adminOps;
   private final TransactionFactory txFactory;

   public DatastoreEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog) {
      this.activityLog = activityLog;
      adminOps = orcsApi.getAdminOps();
      txFactory = orcsApi.getTransactionFactory();
   }

   protected void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   @Override
   public DatastoreInfo getInfo() {
      Callable<OrcsMetaData> callable = adminOps.createFetchOrcsMetaData();
      OrcsMetaData metaData = executeCallable(callable);
      return asDatastoreInfo(metaData);
   }

   @Override
   public void initialize(String typeModel) {
      activityLog.setEnabled(false);
      adminOps.createDatastoreAndSystemBranches(typeModel);
      activityLog.setEnabled(true);
   }

   @Override
   public void synonyms() {
      activityLog.setEnabled(false);
      adminOps.createSynonymsAndGrants();
      activityLog.setEnabled(true);
   }

   @Override
   public Response migrate() {
      activityLog.setEnabled(false);

      Callable<OrcsMetaData> callable = adminOps.migrateDatastore();
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
      adminOps.createDemoBranches();
   }

   @Override
   public TransactionId createUsers(UserTokens users) {
      TransactionBuilder tx =
         txFactory.createTransaction(COMMON, users.getAccount(), "DatastoreEndpointImpl.createUsers()");
      adminOps.createUsers(tx, users.getUsers());
      return tx.commit();
   }
}