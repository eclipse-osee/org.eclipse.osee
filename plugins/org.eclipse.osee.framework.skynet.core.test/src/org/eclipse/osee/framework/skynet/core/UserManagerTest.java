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
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
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

   @org.junit.Test
   public void testCreateUser() throws Exception {

      SkynetTransaction transaction =
         new SkynetTransaction(BranchManager.getCommonBranch(), getClass().getSimpleName());

      User lithium =
         UserManager.createUser(new OseeUser(NEW_USER_NAMES[0], HumanReadableId.generate(), "this1@that.com", true),
            transaction);
      lithium.persist(transaction);

      User sodium =
         UserManager.createUser(new OseeUser(NEW_USER_NAMES[1], HumanReadableId.generate(), "this2@that.com", true),
            transaction);
      sodium.persist(transaction);

      User potassium =
         UserManager.createUser(new OseeUser(NEW_USER_NAMES[2], HumanReadableId.generate(), "this3@that.com", true),
            transaction);
      potassium.persist(transaction);

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
         groupArt.persist();

         //Default Group Attribute
         groupArt.addAttribute(CoreAttributeTypes.DefaultGroup, true);

         //Create relation between containing folder and new UserGroup
         Artifact groupRoot = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "User Groups", branch);
         groupRoot.addRelation(CoreRelationTypes.Default_Hierarchical__Child, groupArt);
         groupRoot.persist();
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
