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

import static java.lang.Thread.sleep;
import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertEquals;
import java.util.Date;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactGetLastModifiedTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private String testName;
   private Artifact artifact;

   @Before
   public void setup() throws Exception {
      artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, CoreBranches.COMMON,
         method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (artifact != null) {
         artifact.purgeFromBranch();
      }
   }

   @Test
   public void testGetLastModified() throws Exception {
      Date previousModifyDate = artifact.getLastModified();

      Assert.assertNotNull(previousModifyDate);
      Assert.assertEquals(UserManager.getUser(SystemUser.OseeSystem), artifact.getLastModifiedBy());

      sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.persist(testName);

      assertBefore(previousModifyDate, artifact);
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());

      previousModifyDate = artifact.getLastModified();

      // Test post-modified
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "this");
      sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.persist(testName);

      assertBefore(previousModifyDate, artifact);
      Assert.assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());

      previousModifyDate = artifact.getLastModified();

      // Test post deleted
      sleep(1100); // just enough time to guarantee the date will be at least a second later
      artifact.deleteAndPersist();

      assertBefore(previousModifyDate, artifact);
      assertEquals(UserManager.getUser(), artifact.getLastModifiedBy());
   }

   private void assertBefore(Date previousModifyDate, Artifact artifact)  {
      String message = String.format("expected %tc to be before %tc", previousModifyDate, artifact.getLastModified());
      Assert.assertTrue(message, previousModifyDate.before(artifact.getLastModified()));
   }

}
