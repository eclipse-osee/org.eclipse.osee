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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
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
   private final JdbcService jdbcService;
   private final String GAMMA_IN_TXS_EXISTS =
      "SELECT count(*) FROM DUAL WHERE EXISTS (SELECT 1 FROM osee_txs WHERE gamma_id = ?)";
   private final String GAMMA_IN_TXS_ARCHIVED_EXISTS =
      "SELECT count(*) FROM DUAL WHERE EXISTS (SELECT 1 FROM osee_txs_archived WHERE gamma_id = ?)";

   public DatastoreEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog, JdbcService jdbcService) {
      this.activityLog = activityLog;
      this.jdbcService = jdbcService;
      adminOps = orcsApi.getAdminOps();
      userService = orcsApi.userService();
   }

   protected void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   @Override
   public List<String> getUnusedGammas(String ids) {
      // ssh into base machine
      // cd /<osee_server_data_path>/attr
      // find . -name *.zip > filelist.txt
      File fileList = new File("PUT_OSEE_SERVER_DATA_PATH_HERE");
      try {
         String[] split = Lib.fileToString(fileList).split("\n");
         int x = 1;
         int size = split.length;
         for (String zipFile : split) {
            System.err.print(String.format("Processing %s/%s", x++, size));
            String gammaId = zipFile;
            gammaId = gammaId.replaceFirst("/[0-9A-Za-z\\\\+_=]+.zip$", "");
            gammaId = gammaId.replaceFirst("^\\./", "");
            gammaId = gammaId.replaceAll("/", "");
            List<String> unUsed = getUnusedGammaById(gammaId);
            if (!unUsed.isEmpty()) {
               System.err.println(String.format(" - Not Found %s from %s", gammaId, zipFile));
            } else {
               System.err.println(" ");
            }
         }
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
      return Collections.emptyList();
   }

   @Override
   public List<String> getUnusedGammaById(String ids) {
      List<String> unUsedIds = new ArrayList<>();
      for (String id : ids.split(",")) {
         JdbcStatement chStmt = jdbcService.getClient().getStatement();
         try {
            chStmt.runPreparedQuery(GAMMA_IN_TXS_EXISTS, id);
            chStmt.next();
            int int1 = chStmt.getInt(1);
            if (int1 == 0) {
               JdbcStatement chStmt2 = jdbcService.getClient().getStatement();
               chStmt2.runPreparedQuery(GAMMA_IN_TXS_ARCHIVED_EXISTS, id);
               chStmt2.next();
               int int2 = chStmt2.getInt(1);
               if (int2 == 0) {
                  unUsedIds.add(id);
               }
            }
         } finally {
            chStmt.close();
         }
      }
      return unUsedIds;
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

   private DatastoreInfo asDatastoreInfo(OrcsMetaData metaData) {
      DatastoreInfo info = new DatastoreInfo();
      info.setProperties(metaData.getProperties());
      return info;
   }

   @Override
   public TransactionId createUsers(Iterable<UserToken> users) {
      return userService.createUsers(users, "Create Users via Rest");
   }

   @Override
   public UserToken getUserInfo(HttpHeaders headers, String userId, String authHeader) {
      String authHeaderHard = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
      activityLog.createEntry(CoreActivityTypes.JAXRS_METHOD_CALL,
         "userId " + userId + " authHeader " + authHeader + " authHeaderHard " + authHeaderHard);

      UserToken user = userService.getUser();
      if (user.isInvalid()) {
         if (userId != null) {
            user = userService.getUserByUserId(userId);
         }
         if (user.isInvalid()) {
            user = testAuthHeader(authHeaderHard);
            if (user.isInvalid()) {
               user = testAuthHeader(authHeader);
            }
         }
      }

      return user;
   }

   private UserToken testAuthHeader(String authHeader) {
      if (Strings.isInValid(authHeader)) {
         return UserToken.SENTINEL;
      }

      String userId;
      if (authHeader.startsWith(OseeProperties.LOGIN_ID_AUTH_SCHEME)) {
         userId = authHeader.substring(OseeProperties.LOGIN_ID_AUTH_SCHEME.length());
      } else {
         userId = authHeader;
      }
      return userService.getUserByUserId(userId);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearUserCache() {
      userService.clearCaches();
   }
}