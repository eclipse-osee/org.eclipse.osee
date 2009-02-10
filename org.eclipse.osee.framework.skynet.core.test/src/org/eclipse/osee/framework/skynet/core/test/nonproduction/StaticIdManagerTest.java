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
package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class StaticIdManagerTest extends TestCase {

   private static String STATIC_ID_AAA = "aaa";
   private static String STATIC_ID_BBB = "bbb";
   private static String STATIC_ID_CCC = "ccc";
   private static String STATIC_ID_DDD = "ddd";
   private static String STATIC_ID_EEE = "eee";

   public void testCleanupForReRun() throws OseeCoreException, InterruptedException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      for (String staticIdValue : Arrays.asList(STATIC_ID_AAA, STATIC_ID_BBB, STATIC_ID_CCC, STATIC_ID_DDD,
            STATIC_ID_EEE)) {
         for (Artifact artifact : ArtifactQuery.getArtifactsFromAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE,
               staticIdValue, BranchManager.getCommonBranch())) {
            artifact.delete(transaction);
            System.out.println("Deleting " + artifact.getHumanReadableId());
         }
      }
      transaction.execute();

      for (String staticIdValue : Arrays.asList(STATIC_ID_AAA, STATIC_ID_BBB, STATIC_ID_CCC, STATIC_ID_DDD,
            STATIC_ID_EEE)) {
         Collection<Artifact> artifacts =
               StaticIdManager.getArtifacts(GeneralData.ARTIFACT_TYPE, staticIdValue, BranchManager.getCommonBranch());
         for (Artifact artifact : artifacts) {
            System.err.println("Search returned non-deleted " + artifact.getHumanReadableId());
         }
         assertTrue("Expected 0 artifacts; Returned " + artifacts.size(), artifacts.size() == 0);
      }
   }

   public void testStaticIdsGettingCached() throws OseeCoreException {
      String staticId = "org." + GUID.generateGuidStr();
      Collection<Artifact> artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
      assertTrue("Should be 0; Returned " + artifacts.size(), artifacts.size() == 0);
      Artifact art = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      art.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, staticId);
      art.persistAttributesAndRelations();

      artifacts = ArtifactCache.getArtifactsByStaticId(staticId);
      assertTrue("Should be 1; Returned " + artifacts.size(), artifacts.size() == 1);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifact(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, boolean)}
    * .
    */
   public void testGetSingletonArtifactStringStringBranchBoolean() throws OseeCoreException {
      Artifact artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_AAA,
                  BranchManager.getCommonBranch(), false);
      assertNull(artifact);

      artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_AAA,
                  BranchManager.getCommonBranch(), true);
      assertNotNull(artifact);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#setSingletonAttributeValue(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String)}
    * .
    * 
    * @throws InterruptedException
    */
   public void testSetSingletonAttributeValue() throws OseeCoreException, InterruptedException {
      // create artifact with two of same static id values
      Artifact artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      artifact.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, STATIC_ID_BBB);
      artifact.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, STATIC_ID_BBB);
      artifact.persistAttributes();

      // call to search for artifact with bbb
      Artifact artifactWithDoubleBbb =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_BBB,
                  BranchManager.getCommonBranch(), false);
      assertNotNull(artifactWithDoubleBbb);

      // should be two static id attributes
      assertTrue("Expected 2 attributes; Returned " + artifactWithDoubleBbb.getAttributes(
            StaticIdManager.STATIC_ID_ATTRIBUTE).size(), artifactWithDoubleBbb.getAttributes(
            StaticIdManager.STATIC_ID_ATTRIBUTE).size() == 2);

      // call to set singleton which should resolve duplicates
      StaticIdManager.setSingletonAttributeValue(artifactWithDoubleBbb, STATIC_ID_BBB);

      // should now be only one static id attributes
      assertTrue("Expected 1 attributes; Returned " + artifactWithDoubleBbb.getAttributes(
            StaticIdManager.STATIC_ID_ATTRIBUTE).size(), artifactWithDoubleBbb.getAttributes(
            StaticIdManager.STATIC_ID_ATTRIBUTE).size() == 1);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getArtifacts(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    * 
    * @throws InterruptedException
    */
   public void testGetArtifacts() throws OseeCoreException, InterruptedException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      // Create three artifacts with ccc staticId
      Artifact artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_CCC);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);

      artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_CCC);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);

      artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_CCC);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);
      transaction.execute();

      // search for static attributes
      Collection<Artifact> artifacts =
            StaticIdManager.getArtifacts(GeneralData.ARTIFACT_TYPE, STATIC_ID_CCC, BranchManager.getCommonBranch());
      assertTrue("Expected 3 artifacts; Returned " + artifacts.size(), artifacts.size() == 3);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifactOrException(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    */
   public void testGetSingletonArtifactOrException() throws OseeCoreException {
      try {
         StaticIdManager.getSingletonArtifactOrException(GeneralData.ARTIFACT_TYPE, STATIC_ID_DDD,
               BranchManager.getCommonBranch());
         fail("ArtifactDoesNotExist should have been thrown.");
      } catch (ArtifactDoesNotExist ex) {
         assertTrue(true);
      }

      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      Artifact artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_DDD);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);

      artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_DDD);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);
      transaction.execute();

      try {
         StaticIdManager.getSingletonArtifactOrException(GeneralData.ARTIFACT_TYPE, STATIC_ID_DDD,
               BranchManager.getCommonBranch());
         fail("MultipleArtifactsExist should have been thrown");
      } catch (MultipleArtifactsExist ex) {
         assertTrue(true);
      }
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager#getSingletonArtifact(java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)}
    * .
    * 
    * @throws InterruptedException
    */
   public void testGetSingletonArtifactStringStringBranch() throws OseeCoreException, InterruptedException {
      // create single artifact with eee staticId
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      Artifact artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                  BranchManager.getCommonBranch(), true);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);
      transaction.execute();

      // test that singleton comes back
      artifact =
            StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                  BranchManager.getCommonBranch());
      assertNotNull(artifact);

      // create another artifact with eee staticId
      transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      artifact = ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch());
      StaticIdManager.setSingletonAttributeValue(artifact, STATIC_ID_EEE);
      artifact.persistAttributes(transaction);
      assertNotNull(artifact);
      transaction.execute();

      // test that there are now two artifacts with eee
      Collection<Artifact> artifacts =
            StaticIdManager.getArtifacts(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE, BranchManager.getCommonBranch());
      assertTrue("Expected 2 artifacts; Returned " + artifacts.size(), artifacts.size() == 2);

      // test that call to get singleton does NOT exception
      try {
         artifact =
               StaticIdManager.getSingletonArtifact(GeneralData.ARTIFACT_TYPE, STATIC_ID_EEE,
                     BranchManager.getCommonBranch());
         assertNotNull(artifact);
      } catch (Exception ex) {
         fail("Exception should not have occurred " + ex.getLocalizedMessage());
      }
   }
}
