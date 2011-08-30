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
package org.eclipse.osee.framework.skynet.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.Assert;

/**
 * @author Karol M. Wilk
 * @author Donald G. Dunne
 */
public final class UserManagerTest {

   private static final String[] TEST_DEFAULT_GROUPS = {"Alkali Metals", "Metals"};
   private static final String[] NEW_USER_NAMES = {"Lithium", "Sodium", "Potassium"};

   private User createUser(SkynetTransaction transaction, int index) throws OseeCoreException {
      IUserToken token =
         TokenFactory.createUserToken(GUID.create(), NEW_USER_NAMES[index], "this" + index + "@that.com",
            "9999999" + index, true, index % 2 == 0, true);
      User user = UserManager.createUser(token, transaction);
      user.persist(transaction);
      return user;
   }

   @org.junit.Test
   public void testCreateUser() throws Exception {

      SkynetTransaction transaction =
         new SkynetTransaction(BranchManager.getCommonBranch(), getClass().getSimpleName());

      User lithium = createUser(transaction, 0);
      User sodium = createUser(transaction, 1);
      User potassium = createUser(transaction, 2);

      transaction.execute();

      Set<Artifact> groups = new HashSet<Artifact>();

      groups.addAll(lithium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));
      groups.addAll(sodium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));
      groups.addAll(potassium.getRelatedArtifacts(CoreRelationTypes.Users_Artifact));

      Assert.assertTrue(groups.size() > TEST_DEFAULT_GROUPS.length);

      Set<String> verifiedNames = new HashSet<String>();
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

   @org.junit.BeforeClass
   public static void setUpOnce() throws Exception {
      createSampleDefaultGroups(CoreBranches.COMMON, TEST_DEFAULT_GROUPS);
   }

   @org.junit.AfterClass
   public static void tearDownOnce() throws Exception {
      deleteSampleDefaultGroups(CoreBranches.COMMON, TEST_DEFAULT_GROUPS);
   }

   private static void createSampleDefaultGroups(IOseeBranch branch, String... names) throws OseeCoreException {
      for (String name : names) {
         //Create artifact
         Artifact groupArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.UserGroup, branch, name);
         groupArt.persist("Create user group");

         //Default Group Attribute
         groupArt.addAttribute(CoreAttributeTypes.DefaultGroup, true);

         //Create relation between containing folder and new UserGroup
         Artifact groupRoot = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "User Groups", branch);
         groupRoot.addRelation(CoreRelationTypes.Default_Hierarchical__Child, groupArt);
         groupRoot.persist("Create user group");
      }
   }

   private static void deleteSampleDefaultGroups(IOseeBranch branch, String... artifactNames) throws OseeCoreException {
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
