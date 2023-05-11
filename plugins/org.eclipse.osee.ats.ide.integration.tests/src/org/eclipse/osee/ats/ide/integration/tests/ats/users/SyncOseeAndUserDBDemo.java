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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.users.SyncOseeAndUserDB;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
      return Arrays.asList("labUser");
   }

   @Override
   protected void testUserGroups(List<UserToken> regUsers) {
      // do nothing
   }

   @Override
   protected AtsUser getUserByUserId(String userId) {
      if (userId.equals(DemoUsers.Kay_Wheeler.getUserId())) {
         AtsUser userByToken = atsApi.getUserService().getUserByToken(DemoUsers.Kay_Wheeler);
         userByToken.setEmail("kay.wheeler@google.com");
         return userByToken;
      } else if (userId.equals(DemoUsers.Keith_Johnson.getUserId())) {
         AtsUser userByToken = atsApi.getUserService().getUserByToken(DemoUsers.Keith_Johnson);
         userByToken.setActive(false);
         return userByToken;
      }
      return null;
   }

   @Override
   protected Date getTxDate(ArtifactToken art) {
      return ((Artifact) art).getLastModified();
   }

}
