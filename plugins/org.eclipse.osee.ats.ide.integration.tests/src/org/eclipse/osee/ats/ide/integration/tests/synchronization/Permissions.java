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

import java.util.regex.Pattern;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * ReqIF relationship tests for the Synchronization REST API End point defined in the package
 * {@link org.eclipse.osee.synchronization.rest}.
 *
 * @author Loren K. Ashley
 */

public class Permissions {

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NotProductionDataStoreRule() );
   //@formatter:on

   /**
    * Class used to define relationships between test artifacts.
    */

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   @SuppressWarnings("unused")
   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   @SuppressWarnings("unused")
   private static BranchId rootBranchId;

   /**
    * Saves a reference to the {@link SynchronizationArtifactParser} used to make test calls to the API.
    */

   private static SynchronizationArtifactParser synchronizationArtifactParser;

   /**
    * Sets up the {@link TestUser} and the {@link SynchronizationArtifactParser}. When the test suite is run directly it
    * will be in Database Initialization mode. The test user creation will take the database out of initialization mode
    * and re-authenticate as the test user, when necessary. To access the Synchronization API end point the test user
    * must be in the Publishing group. It is assumed the test user is already an active user with login identifiers.
    */

   @BeforeClass
   public static void testSetup() {

      TestUser.removeTestUserFromPublishingGroup();
      TestUser.activateTestUser();
      TestUser.restoreLoginIds();
      TestUser.clearServerCache();

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      var synchronizationEndpoint = oseeClient.getSynchronizationEndpoint();

      Permissions.synchronizationArtifactParser = new SynchronizationArtifactParser(synchronizationEndpoint);
   }

   /**
    * Restores the test user to it's assumed initial state by doing the following:
    * <ul>
    * <li>removing the test user from the Publishing group,</li>
    * <li>activating the test user, and</li>
    * <li>restoring the login identifiers the test user originally had.</li>
    */

   @After
   public void testCleanup() {

      TestUser.removeTestUserFromPublishingGroup();
      TestUser.activateTestUser();
      TestUser.restoreLoginIds();
      TestUser.clearServerCache();
   }

   /*
    * Unit Tests
    */

   @Test
   public void testActiveLoginUserInPublishingGroupOk() {

      TestUser.addTestUserToPublishingGroup();
      TestUser.activateTestUser();
      TestUser.restoreLoginIds();
      TestUser.clearServerCache();

      var branchId = DemoBranches.SAW_PL_Working_Branch;
      var artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      var synchronizationArtifactType = "reqif";

      try {
         var reqIF = Permissions.synchronizationArtifactParser.parseTestDocument(branchId, artifactId,
            synchronizationArtifactType);
         Assert.assertNotNull(reqIF);
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
      }
   }

   @Test
   public void testActiveLoginUserNotInPublishingGroupKo() {

      TestUser.removeTestUserFromPublishingGroup();
      TestUser.activateTestUser();
      TestUser.restoreLoginIds();
      TestUser.clearServerCache();

      var branchId = DemoBranches.SAW_PL_Working_Branch;
      var artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      var synchronizationArtifactType = "reqif";

      try {
         var reqIF = Permissions.synchronizationArtifactParser.parseTestDocument(branchId, artifactId,
            synchronizationArtifactType);
         Assert.assertNotNull(reqIF);
      } catch (Exception e) {
         var message = e.getMessage();
         var pattern = Pattern.compile("User is not in the publishing group\\.");
         var matcher = pattern.matcher(message);

         Assert.assertTrue(message, matcher.find());
      }

   }

   @Test
   public void testNonActiveLoginUserInPublishingGroupKo() {

      TestUser.addTestUserToPublishingGroup();
      TestUser.inactivateTestUser();
      TestUser.restoreLoginIds();
      TestUser.clearServerCache();

      var branchId = DemoBranches.SAW_PL_Working_Branch;
      var artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      var synchronizationArtifactType = "reqif";

      try {
         var reqIF = Permissions.synchronizationArtifactParser.parseTestDocument(branchId, artifactId,
            synchronizationArtifactType);
         Assert.assertNotNull(reqIF);
      } catch (Exception e) {
         var message = e.getMessage();
         var pattern = Pattern.compile("User is not an active user\\.");
         var matcher = pattern.matcher(message);

         Assert.assertTrue(message, matcher.find());
      }

   }

   @Test
   public void testActiveNonLoginUserInPublishingGroupKo() {

      TestUser.addTestUserToPublishingGroup();
      TestUser.activateTestUser();
      TestUser.removeLoginIds();
      TestUser.clearServerCache();

      var branchId = DemoBranches.SAW_PL_Working_Branch;
      var artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      var synchronizationArtifactType = "reqif";

      try {
         var reqIF = Permissions.synchronizationArtifactParser.parseTestDocument(branchId, artifactId,
            synchronizationArtifactType);
         Assert.assertNotNull(reqIF);
      } catch (Exception e) {
         var message = e.getMessage();
         var pattern = Pattern.compile("User is not a login user\\.");
         var matcher = pattern.matcher(message);

         Assert.assertTrue(message, matcher.find());
      }
   }

}

/* EOF */
