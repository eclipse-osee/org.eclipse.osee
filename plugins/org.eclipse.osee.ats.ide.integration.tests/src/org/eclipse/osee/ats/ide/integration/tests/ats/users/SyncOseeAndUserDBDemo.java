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
package org.eclipse.osee.ats.ide.integration.tests.ats.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.UserActivityData;
import org.eclipse.osee.ats.api.user.UserUsageType;
import org.eclipse.osee.ats.core.users.SyncOseeAndUserDB;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.Test;

public class SyncOseeAndUserDBDemo extends SyncOseeAndUserDB {

   private SyncOseeAndUserDBDemo demo;

   @Test
   public void test() {
      demo = new SyncOseeAndUserDBDemo(persist, debug, atsApi);
      XResultData rd = demo.run();
      Assert.assertTrue(rd.isEmpty());
   }

   public SyncOseeAndUserDBDemo(boolean persist, boolean debug, AtsApi atsApi) {
      super(persist, debug, atsApi);
   }

   @Override
   protected List<String> getIgnoreStaticIds() {
      return Arrays.asList("labUser", "stayActive");
   }

   @Override
   protected void updateUserGroups() {
      // do nothing
   }

   @Override
   protected AtsUser getWssoUserByUserId(String userId) {
      if (userId.equals(DemoUsers.Kay_Wheeler.getUserId())) {
         AtsUser userByToken = atsApi.getUserService().getUserByToken(DemoUsers.Kay_Wheeler);
         userByToken.setEmail("kay.wheeler@google.com");
         return userByToken;
      } else if (userId.equals(DemoUsers.Keith_Johnson.getUserId())) {
         AtsUser userByToken = atsApi.getUserService().getUserByToken(DemoUsers.Keith_Johnson);
         userByToken.setActive(false);
         return userByToken;
      } else {
         AtsUser userByToken = atsApi.getUserService().getUserByUserId(userId);
         if (userByToken != null) {
            return userByToken;
         } else {
            return null;
         }
      }
   }

   @Override
   protected Date getTxDate(ArtifactToken art) {
      return ((Artifact) art).getLastModified();
   }

   @Override
   protected List<AtsUser> getUsersByUserIds(String userIds) {
      List<AtsUser> users = new ArrayList<>();
      for (String userId : userIds.split(",")) {
         users.add(getWssoUserByUserId(userId));
      }
      return users;
   }

   @Override
   protected void loadUsers() {
      super.loadUsers();
      // Set activity so other attributes are checked
      UserActivityData user = userArtIdToUserAct.get(DemoUsers.Kay_Wheeler.getArtifactId());
      user.addUsageType(UserUsageType.IDE_CLIENT_USE, 25);
   }

   @Override
   protected List<ArtifactToken> queryUsersWithTxsDetails() {
      return Collections.castAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, atsApi.getAtsBranch()));
   }

   @Override
   protected Collection<IUserGroupArtifactToken> getAdditionalUserGroups() {
      return java.util.Collections.emptyList();
   }

}
