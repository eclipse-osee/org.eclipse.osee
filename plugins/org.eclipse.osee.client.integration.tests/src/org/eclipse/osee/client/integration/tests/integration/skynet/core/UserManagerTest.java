/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 * @author Donald G. Dunne
 */
public final class UserManagerTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String[] TEST_DEFAULT_GROUPS = {"Alkali Metals", "Metals"};
   private static final String[] NEW_USER_NAMES = {"Lithium", "Sodium", "Potassium"};

   @Before
   public void setUpOnce() throws Exception {
      createSampleDefaultGroups(CoreBranches.COMMON, TEST_DEFAULT_GROUPS);
   }

   @After
   public void tearDownOnce() throws Exception {
      deleteSampleDefaultGroups(CoreBranches.COMMON, TEST_DEFAULT_GROUPS);
   }

   @Test
   public void testCreateUser() throws Exception {

      SkynetTransaction transaction =
         TransactionManager.createTransaction(CoreBranches.COMMON, getClass().getSimpleName());

      User lithium = createUser(transaction, 0);
      User sodium = createUser(transaction, 1);
      User potassium = createUser(transaction, 2);

      transaction.execute();

      Set<Artifact> groups = new HashSet<>();

      groups.addAll(lithium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));
      groups.addAll(sodium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));
      groups.addAll(potassium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));

      Assert.assertTrue(groups.size() > TEST_DEFAULT_GROUPS.length);

      Set<String> verifiedNames = new HashSet<>();
      for (Artifact group : groups) {
         for (String groupName : TEST_DEFAULT_GROUPS) {
            if (group.getName().equals(groupName)) {
               verifiedNames.add(groupName);
            }
         }
      }

      Assert.assertFalse("Members were not subscribed to any default groups.", verifiedNames.isEmpty());
      Assert.assertTrue("Members not subscribed to right groups.", verifiedNames.size() == TEST_DEFAULT_GROUPS.length);
   }

   private User createUser(SkynetTransaction transaction, int index) {
      UserToken token = UserToken.create(Lib.generateArtifactIdAsInt(), GUID.create(), NEW_USER_NAMES[index],
         "this" + index + "@that.com", "9999999" + index, true, index % 2 == 0, true);
      User user = UserManager.createUser(token, transaction);
      user.persist(transaction);
      return user;
   }

   private void createSampleDefaultGroups(BranchId branch, String... names) {
      for (String name : names) {
         //Create artifact
         Artifact groupArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.UserGroup, branch, name);
         groupArt.persist("Create user group");

         //Default Group Attribute
         groupArt.addAttribute(CoreAttributeTypes.DefaultGroup, true);

         //Create relation between containing folder and new UserGroup
         Artifact groupRoot = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "User Groups", branch);
         groupRoot.addRelation(CoreRelationTypes.Default_Hierarchical__Child, groupArt);
         groupRoot.persist(UserManagerTest.class.getSimpleName() + ": Create user group");
      }
   }

   private void deleteSampleDefaultGroups(BranchId branch, String... artifactNames) {
      Collection<Artifact> list = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UserGroup, branch);
      for (Artifact artifact : list) {
         for (String artifactName : artifactNames) {
            if (artifact.getName().equals(artifactName)) {
               artifact.deleteAndPersist();
            }
         }
      }
   }
}
