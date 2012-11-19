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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class ArtifactGetLastModifiedTest {

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      cleanup();
   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   @org.junit.Test
   public void testGetLastModified() throws Exception {
      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, BranchManager.getCommonBranch(),
            getClass().getSimpleName());

      Assert.assertNotNull(artifact.getLastModified());
      Assert.assertEquals(UserManager.getUser(SystemUser.OseeSystem), artifact.getLastModifiedBy());
      Date previousModifyDate = artifact.getLastModified();

      Thread.sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.persist(getClass().getSimpleName());

      assertBefore(previousModifyDate, artifact);
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
      previousModifyDate = artifact.getLastModified();

      // Test post-modified
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "this");
      Thread.sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.persist(getClass().getSimpleName());

      assertBefore(previousModifyDate, artifact);
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
      previousModifyDate = artifact.getLastModified();

      // Test post deleted
      Thread.sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.deleteAndPersist();

      assertBefore(previousModifyDate, artifact);
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
   }

   private void assertBefore(Date previousModifyDate, Artifact artifact) throws OseeCoreException {
      Assert.assertTrue(String.format("expected %tc to be before %tc", previousModifyDate, artifact.getLastModified()),
         previousModifyDate.before(artifact.getLastModified()));
   }

   private static void cleanup() throws Exception {
      Collection<Artifact> arts =
         ArtifactQuery.getArtifactListFromName(ArtifactGetLastModifiedTest.class.getSimpleName(),
            BranchManager.getCommonBranch(), EXCLUDE_DELETED);
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(arts));
   }
}
