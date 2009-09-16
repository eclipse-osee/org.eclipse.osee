/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class StaticIdManagerTest {

   private static final String STATIC_ID_AAA = "aaa";
   private static final String STATIC_ID_BBB = "bbb";
   private static final String STATIC_ID_CCC = "ccc";
   private static final String STATIC_ID_DDD = "ddd";
   private static final String STATIC_ID_EEE = "eee";

   private static final List<String> ALL_STATIC_IDS =
         Arrays.asList(STATIC_ID_AAA, STATIC_ID_BBB, STATIC_ID_CCC, STATIC_ID_DDD, STATIC_ID_EEE);

   @BeforeClass
   @AfterClass
   public static void testCleanupForReRun() throws OseeCoreException, InterruptedException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      for (String staticIdValue : ALL_STATIC_IDS) {
         for (Artifact artifact : ArtifactQuery.getArtifactListFromAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE,
               staticIdValue, BranchManager.getCommonBranch())) {
            artifact.deleteAndPersist(transaction);
            System.out.println("Deleting " + artifact.getGuid());
         }
      }
      transaction.execute();

      for (String staticIdValue : ALL_STATIC_IDS) {
         Collection<Artifact> artifacts =
               StaticIdManager.getArtifactsFromArtifactQuery(GeneralData.ARTIFACT_TYPE, staticIdValue,
                     BranchManager.getCommonBranch());
         for (Artifact artifact : artifacts) {
            System.err.println("Search returned non-deleted " + artifact.getGuid());
         }
         assertTrue("Expected 0 artifacts; Returned " + artifacts.size(), artifacts.size() == 0);
      }
   }

   @Before
   public void setup() throws Exception {
      assertTrue("Should be run on demo datbase.", TestUtil.isDemoDb());
   }

   @org.junit.Test
   public void testStaticIdsGettingCached() throws OseeCoreException {
      String staticId = "org." + GUID.create();
      Collection<Artifact> artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
      assertTrue("Should be 0; Returned " + artifacts.size(), artifacts.size() == 0);
      Artifact art = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      art.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, staticId);
      art.persist();

      artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
      assertTrue("Should be 1; Returned " + artifacts.size(), artifacts.size() == 1);

      art.deleteAndPersist();
      artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
      assertTrue("Should be 0; Returned " + artifacts.size(), artifacts.isEmpty());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifact(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, boolean)}
    * .
    */
   @org.junit.Test
   public void testGetSingletonArtifact() throws OseeCoreException {
      Artifact artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_AAA,
                  BranchManager.getCommonBranch(), true);
      assertNull(artifact);

      artifact =
            StaticIdManager.getOrCreateSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_AAA,
                  BranchManager.getCommonBranch());
      assertNotNull(artifact);

      deleteArtifacts(Arrays.asList(artifact), STATIC_ID_AAA);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#setSingletonAttributeValue(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String)}
    * 
    * @throws InterruptedException
    */
   @org.junit.Test
   public void testSetSingletonAttributeValue() throws OseeCoreException, InterruptedException {
      // create artifact with two of same static id values
      Artifact artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      artifact.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, STATIC_ID_BBB);
      artifact.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, STATIC_ID_BBB);
      artifact.persist();

      // call to search for artifact with STATIC_ID_BBB
      Artifact artifactWithDoubleBbb =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_BBB,
                  BranchManager.getCommonBranch(), false);
      assertNotNull(artifactWithDoubleBbb);

      // should be two static id attributes
      int count = artifactWithDoubleBbb.getAttributes(StaticIdManager.STATIC_ID_ATTRIBUTE).size();
      assertTrue("Expected 2 attributes; Returned " + count, count == 2);

      count = artifactWithDoubleBbb.getAttributeCount(StaticIdManager.STATIC_ID_ATTRIBUTE);
      assertTrue("Expected 2 attributes; Returned " + count, count == 2);

      // call to set singleton which should resolve duplicates
      StaticIdManager.setSingletonAttributeValue(artifactWithDoubleBbb, STATIC_ID_BBB);

      // should now be only one static id attributes
      count = artifactWithDoubleBbb.getAttributeCount(StaticIdManager.STATIC_ID_ATTRIBUTE);
      assertTrue("Expected 1 attributes; Returned " + count, count == 1);

      deleteArtifacts(Arrays.asList(artifact), STATIC_ID_BBB);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getArtifactsFromArtifactQuery(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    * 
    * @throws InterruptedException
    */
   @org.junit.Test
   public void testGetArtifacts() throws OseeCoreException, InterruptedException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      // Create three artifacts with ccc staticId

      for (int index = 0; index < 3; index++) {
         Artifact artifact =
               ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
         StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_CCC);
         artifact.persist(transaction);
         assertNotNull(artifact);
      }
      transaction.execute();

      // search for static attributes
      Collection<Artifact> artifacts =
            StaticIdManager.getArtifactsFromArtifactQuery(GeneralData.ARTIFACT_TYPE, STATIC_ID_CCC,
                  BranchManager.getCommonBranch());
      assertTrue("Expected 3 artifacts; Returned " + artifacts.size(), artifacts.size() == 3);

      deleteArtifacts(artifacts, STATIC_ID_CCC);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifactOrException(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    */
   @org.junit.Test
   public void testGetSingletonArtifactOrException() throws OseeCoreException {
      try {
         StaticIdManager.getSingletonArtifactOrException(GeneralData.ARTIFACT_TYPE, STATIC_ID_DDD,
               BranchManager.getCommonBranch());
         fail("ArtifactDoesNotExist should have been thrown.");
      } catch (Exception ex) {
         assertTrue("Was not ArtifactDoesNotExist was: " + ex.getClass().getSimpleName(),
               ex instanceof ArtifactDoesNotExist);
      }

      Collection<Artifact> artifacts = new ArrayList<Artifact>();
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      for (int index = 0; index < 2; index++) {
         Artifact artifact =
               ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
         StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_DDD);
         artifact.persist(transaction);
         assertNotNull(artifact);
         artifacts.add(artifact);
      }
      transaction.execute();

      try {
         StaticIdManager.getSingletonArtifactOrException(GeneralData.ARTIFACT_TYPE, STATIC_ID_DDD,
               BranchManager.getCommonBranch());
         fail("MultipleArtifactsExist should have been thrown");
      } catch (Exception ex) {
         assertTrue("Was not MultipleArtifactsExist was: " + ex.getClass().getSimpleName(),
               ex instanceof MultipleArtifactsExist);
      }

      deleteArtifacts(artifacts, STATIC_ID_DDD);
   }

   private void deleteArtifacts(Collection<Artifact> toDelete, String staticId) throws OseeCoreException {
      if (!toDelete.isEmpty()) {
         if (toDelete.size() == 1) {
            toDelete.iterator().next().deleteAndPersist();
         } else {
            SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
            for (Artifact artifact : toDelete) {
               artifact.delete();
            }
            for (Artifact artifact : toDelete) {
               artifact.persist(transaction);
            }
            transaction.execute();
         }

         Collection<Artifact> artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
         assertTrue("Should be 0; Returned " + artifacts.size(), artifacts.isEmpty());
      }
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifact(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    * 
    * @throws InterruptedException
    */
   @org.junit.Test
   public void testGetSingletonArtifactStringStringBranch() throws OseeCoreException, InterruptedException {
      List<Artifact> itemsCreated = new ArrayList<Artifact>();

      // create single artifact with eee staticId
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      Artifact artifact =
            StaticIdManager.getOrCreateSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                  BranchManager.getCommonBranch());
      artifact.persist(transaction);
      assertNotNull(artifact);
      transaction.execute();

      itemsCreated.add(artifact);

      // test that singleton comes back
      artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                  BranchManager.getCommonBranch(), false);
      assertNotNull(artifact);

      // create another artifact with eee staticId
      transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_EEE);
      artifact.persist(transaction);
      assertNotNull(artifact);
      transaction.execute();
      itemsCreated.add(artifact);

      // test that there are now two artifacts with eee
      Collection<Artifact> artifacts =
            StaticIdManager.getArtifactsFromArtifactQuery(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                  BranchManager.getCommonBranch());
      assertTrue("Expected 2 artifacts; Returned " + artifacts.size(), artifacts.size() == 2);

      // test that call to get singleton does NOT exception
      try {
         artifact =
               StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                     BranchManager.getCommonBranch(), false);
         assertNotNull(artifact);
      } catch (Exception ex) {
         fail("Exception should not have occurred " + ex.getLocalizedMessage());
      }
      deleteArtifacts(itemsCreated, STATIC_ID_EEE);
   }
}
