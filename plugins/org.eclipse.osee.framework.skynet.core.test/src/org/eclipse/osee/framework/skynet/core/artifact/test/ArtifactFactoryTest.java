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
package org.eclipse.osee.framework.skynet.core.artifact.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 */
public final class ArtifactFactoryTest {

   private static final String[] TEST_DEFAULT_GROUPS = {"Alkali Metals", "Metals"};
   private static final String[] NEW_USER_NAMES = {"Lithium", "Sodium", "Potassium"};

   @Test
   public void test_makeNewArtifact_UserArtifactAndDefaultGroups() throws Exception {

      //New User - Yay!
      Artifact lithium = ArtifactTypeManager.makeNewArtifact(CoreArtifactTypes.User, CoreBranches.COMMON);
      lithium.setName(NEW_USER_NAMES[0]);

      //We like friends
      Artifact sodium = ArtifactTypeManager.makeNewArtifact(CoreArtifactTypes.User, CoreBranches.COMMON);
      sodium.setName(NEW_USER_NAMES[1]);

      //Viral
      Artifact potassium = ArtifactTypeManager.makeNewArtifact(CoreArtifactTypes.User, CoreBranches.COMMON);
      potassium.setName(NEW_USER_NAMES[2]);

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

   @BeforeClass
   public static void setUpOnce() throws Exception {
      createSampleDefaultGroups(CoreBranches.COMMON, TEST_DEFAULT_GROUPS);
   }

   @AfterClass
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
