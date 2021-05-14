/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.net.URI;
import java.util.concurrent.Callable;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokens;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreInfo;

/**
 * @author Roberto E. Escobar
 */
public class DatastoreEndpointImpl implements DatastoreEndpoint {
   @Context
   private UriInfo uriInfo;
   private final ActivityLog activityLog;
   private final OrcsAdmin adminOps;
   private final UserService userService;

   @HeaderParam(OseeClient.OSEE_ACCOUNT_ID)
   private UserId accountId;

   public DatastoreEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog) {
      this.activityLog = activityLog;
      adminOps = orcsApi.getAdminOps();
      userService = orcsApi.userService();
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
   public TransactionId initialize(UserToken superUser) {
      TransactionId txId = adminOps.createDatastoreAndSystemBranches(superUser);
      adminOps.createDemoBranches();
      return txId;
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
      activityLog.setEnabled(true);
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
   public TransactionId createUsers(UserTokens users) {
      return userService.createUsers(users.getUsers(), "DatastoreEndpointImpl.createUsers()");
   }
}