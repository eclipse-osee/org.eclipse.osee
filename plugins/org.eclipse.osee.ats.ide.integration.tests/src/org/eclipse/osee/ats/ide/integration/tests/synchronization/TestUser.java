/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * A class for managing the test user.
 *
 * @author Loren K. Ashley
 */

public class TestUser {

   /**
    * A functional interface that takes no parameters and returns no value.
    */

   @FunctionalInterface
   private interface Action {
      void action();
   }

   /**
    * Saves the single instance of the {@link TestUser} class;
    */

   private static TestUser testUser = null;

   /**
    * Saves the {@link UserToken} for the test user.
    */

   private final UserToken testUserUserToken;

   /**
    * Saves the {@link IUserGroup} handle for the publishing group.
    */

   private final IUserGroup publishingIUserGroup;

   /**
    * Save a handle to the test user's {@link Artifact}.
    */

   private final Artifact testUserArtifact;

   /**
    * Saves a {@link List} of the login identifiers the test user had when the single {@link TestUser} instance was
    * created.
    */

   private final List<String> testUserLoginIds;

   /**
    * An implementation of the {@link Action} functional interface used to clear the OSEE server's {@link UserToken}
    * cache.
    */

   private final Action clearServerCache;

   /**
    * Creates a new {@link TestUser} instance.
    *
    * @param testUserUserToken the {@link UserToken} for the test user.
    * @param testUserArtifact the {@link Artifact} for the test user.
    * @param publishingIUserGroup the {@link IUserGroup} for the publishing group.
    * @param clearServerCache an implementation of the {@link Action} functional interface to clear the OSEE server's
    * {@link UserToken} cache.
    */

   private TestUser(UserToken testUserUserToken, Artifact testUserArtifact, IUserGroup publishingIUserGroup, Action clearServerCache) {
      this.testUserUserToken = testUserUserToken;
      this.testUserArtifact = testUserArtifact;
      this.publishingIUserGroup = publishingIUserGroup;
      this.clearServerCache = clearServerCache;

      this.testUserLoginIds = TestUtil.getAttributes(this.testUserArtifact, CoreAttributeTypes.LoginId).orElseGet(
         () -> List.of()).stream().map((attribute) -> ((StringAttribute) attribute).getValue()).collect(
            Collectors.toList());
   }

   /**
    * Creates a new instance of {@link TestUser} for the test user. If the database is still in initialization mode,
    * exits database initialization mode and changes the user to the test user.
    */

   static void create() {

      if (Objects.nonNull(TestUser.testUser)) {
         return;
      }

      /*
       * When the test suite is run directly it will be in Database Initialization mode.
       */

      if (OseeProperties.isInDbInit()) {

         /*
          * Get out of database initialization mode and re-authenticate as the test user
          */

         OseeProperties.setInDbInit(false);
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
      }

      var userService = OsgiUtil.getService(DemoChoice.class, UserService.class);
      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      var testUserUserToken = userService.getUser();
      var publishingIUserGroup = userService.getUserGroup(CoreUserGroups.Publishing);

      var testUserArtifact = ArtifactQuery.getArtifactFromId(testUserUserToken, CoreBranches.COMMON);

      Action clearServerCache = oseeClient.getDatastoreEndpoint()::clearUserCache;

      TestUser.testUser = new TestUser(testUserUserToken, testUserArtifact, publishingIUserGroup, clearServerCache);
   }

   /**
    * Sets the {@link CoreAttributeType#Active} attribute of the test user to <code>true</code>.
    */

   static void activateTestUser() {

      TestUser.create();

      var valueList = new ArrayList<Boolean>();
      valueList.add(true);

      //@formatter:off
      TestUtil.setAttributeValues
         (
            TestUser.testUser.testUserArtifact,
            CoreAttributeTypes.Active,
            (List<Object>) (Object) valueList,
            ( attribute, value ) -> ((BooleanAttribute) attribute).setValue( (Boolean) value )
         );
      //@formatter:on

      TestUser.testUser.testUserArtifact.persist("Activate test user.");
   }

   /**
    * Adds the test user to the publishing group.
    */

   static void addTestUserToPublishingGroup() {

      TestUser.create();

      /*
       * To access the Synchronization API end point the test user must be in the Publishing group
       */

      TestUser.testUser.publishingIUserGroup.addMember(TestUser.testUser.testUserUserToken, true);

      TestUser.testUser.clearServerCache.action();
   }

   /**
    * Clears the OSEE server's {@link UserToken} cache so that changes to user {@link Artifact}s can be reflected in
    * {@link UserToken}s.
    */

   static void clearServerCache() {

      TestUser.create();

      TestUser.testUser.clearServerCache.action();
   }

   /**
    * Sets the {@link CoreAttributeType#Active} attribute of the test user to <code>false</code>.
    */

   static void inactivateTestUser() {

      TestUser.create();

      var valueList = new ArrayList<Boolean>();
      valueList.add(false);

      //@formatter:off
      TestUtil.setAttributeValues
         (
            TestUser.testUser.testUserArtifact,
            CoreAttributeTypes.Active,
            (List<Object>) (Object) valueList,
            ( attribute, value ) -> ((BooleanAttribute) attribute).setValue( (Boolean) value )
         );
      //@formatter:on

      TestUser.testUser.testUserArtifact.persist("Deactivate test user.");
   }

   /**
    * Removes all values from the test user's {@link CoreAttributeType#LoginId} attribute.
    */

   static void removeLoginIds() {

      TestUser.create();

      var valueList = new ArrayList<String>();

      //@formatter:off
      TestUtil.setAttributeValues
         (
            TestUser.testUser.testUserArtifact,
            CoreAttributeTypes.LoginId,
            (List<Object>) (Object) valueList,
            ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
         );
      //@formatter:on

      TestUser.testUser.testUserArtifact.persist("Remove test user login ids.");
   }

   /**
    * Sets the test user's {@link CoreAttributeType#LoginId} attribute to the list of login identifiers that were
    * present in the attribute when the {@link TestUser} instance was created.
    */

   static void restoreLoginIds() {

      TestUser.create();

      var valueList = new ArrayList<String>(TestUser.testUser.testUserLoginIds);

      //@formatter:off
      TestUtil.setAttributeValues
         (
            TestUser.testUser.testUserArtifact,
            CoreAttributeTypes.LoginId,
            (List<Object>) (Object) valueList,
            ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
         );

      TestUser.testUser.testUserArtifact.persist("Restore test user login ids.");
   }


   /**
    * Removes the test user from the publishing group.
    */

   static void removeTestUserFromPublishingGroup() {

      TestUser.create();

      TestUser.testUser.publishingIUserGroup.removeMember(TestUser.testUser.testUserUserToken, true);
   }

}

/* EOF */
