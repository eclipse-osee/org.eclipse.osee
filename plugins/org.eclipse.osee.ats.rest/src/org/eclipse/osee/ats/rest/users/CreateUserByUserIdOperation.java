/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.rest.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public abstract class CreateUserByUserIdOperation {

   protected final AtsApi atsApi;
   protected final OrcsApi orcsApi;
   protected String userIds;

   public CreateUserByUserIdOperation(String userIds, AtsApi atsApi, OrcsApi orcsApi) {
      this.userIds = userIds;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public abstract String getUserQuery(String userId);

   public abstract String getCommitComment();

   public XResultData run() {
      XResultData rd = new XResultData();

      int count = 0;
      userIds = userIds.replaceAll(" ", "");
      Set<String> bemsIds = new HashSet<String>(Arrays.asList(userIds.split(",")));
      List<UserToken> userTokens = new ArrayList<>();
      List<ArtifactId> inactiveUsers = new ArrayList<>();
      atsApi.getConfigService().getConfigurationsWithPend();

      for (String userId : bemsIds) {
         if (Strings.isNumeric(userId)) {
            Long id = atsApi.getConfigService().getConfigurations().getUserIdToUserArtId().get(userId);
            if (id != null) {
               AtsUser userById = atsApi.getConfigService().getConfigurations().getIdToUser().get(id);
               if (userById != null && userById.isValid()) {
                  rd.logf("User with UserId [%s] already exists %s\n", userId, userById.toStringWithId());
                  boolean active = userById.isActive();
                  if (!active) {
                     rd.logf("User %s not active and will be set as active\n", userById.toStringWithId());
                     inactiveUsers.add(userById);
                  }
                  continue;
               }
            }
            JdbcStatement chStmt = atsApi.getJdbcService().getClient().getStatement();
            try {
               String query = getUserQuery(userId);
               chStmt.runPreparedQuery(query);
               while (chStmt.next()) {
                  String json = chStmt.getString(1);
                  ObjectMapper mapper = new ObjectMapper();

                  String loginId = "";
                  String email = "";
                  String userName = "";
                  try {
                     // convert JSON string to Map
                     @SuppressWarnings("unchecked")
                     Map<String, String> map = mapper.readValue(json, Map.class);
                     loginId = map.get("login_id");
                     email = map.get("mail");
                     userName = map.get("user_name");

                  } catch (IOException e) {
                     rd.errorf("Problem occurred with mapping the user %s", userId);
                  }
                  if (Strings.isInValid(loginId)) {
                     rd.errorf("Login Id can't be empty for %s, skipping\n", userId);
                     continue;
                  }

                  List<IUserGroupArtifactToken> roles = new ArrayList<>();
                  roles.add(CoreUserGroups.Everyone);

                  UserToken userTok =
                     UserToken.create(Id.SENTINEL, userName, email, userId, true, Arrays.asList(loginId), roles);

                  count++;
                  userTokens.add(userTok);
                  rd.getIds().add(userTok.getIdString());
               }

            } finally {
               chStmt.close();
            }

         } else {
            rd.errorf("Error in UserId [%s]\n", userId);
            continue;
         }
      }
      if (!userTokens.isEmpty()) {
         orcsApi.userService().createUsers(userTokens, getCommitComment());
         rd.logf("Created %s Users\n", count);
      } else {
         rd.errorf("No users created.  Check that UserId(s) are valid: [%s]", userIds);
      }

      if (!inactiveUsers.isEmpty()) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Re-activate users from UserIds(s)");
         for (ArtifactId id : inactiveUsers) {
            tx.setSoleAttributeValue(id, CoreAttributeTypes.Active, true);
         }
         tx.commit();
      }
      return rd;
   }
}
