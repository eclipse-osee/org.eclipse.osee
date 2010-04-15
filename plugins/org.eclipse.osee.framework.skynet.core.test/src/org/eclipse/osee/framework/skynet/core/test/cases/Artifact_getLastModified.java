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

import java.util.Collection;
import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class Artifact_getLastModified {

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
      boolean debug = false;

      Branch branch = BranchManager.getCommonBranch();
      Assert.assertNotNull(branch);
      Artifact artifact = ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName());

      // Test pre-persist
      if (!debug) {
         System.out.println(String.format("Modified [%s] Author [%s]", artifact.getLastModified(),
               artifact.getLastModifiedBy()));
      }
      Assert.assertNotNull(artifact.getLastModified());
      Assert.assertEquals(UserManager.getUser(SystemUser.OseeSystem), artifact.getLastModifiedBy());
      Date createdDate = artifact.getLastModified();

      artifact.persist();

      // Test post-persist
      if (!debug) {
         System.out.println(String.format("Modified [%s] Author [%s]", artifact.getLastModified(),
               artifact.getLastModifiedBy()));
      }
      Date lastModifyDate = artifact.getLastModified();
      Assert.assertTrue(createdDate.before(lastModifyDate));
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());

      // Test post-modified
      StaticIdManager.setSingletonAttributeValue(artifact, "this");
      artifact.persist();

      if (!debug) {
         System.out.println(String.format("Modified [%s] Author [%s]", artifact.getLastModified(),
               artifact.getLastModifiedBy()));
      }

      Assert.assertTrue(lastModifyDate.before(artifact.getLastModified()));
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
      lastModifyDate = artifact.getLastModified();

      // Test post deleted
      artifact.deleteAndPersist();

      Assert.assertTrue(lastModifyDate.before(artifact.getLastModified()));
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
   }

   private static void cleanup() throws Exception {
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactListFromName(Artifact_getLastModified.class.getSimpleName(),
                  BranchManager.getCommonBranch(), false);
      new PurgeArtifacts(arts).execute();
   }
}
