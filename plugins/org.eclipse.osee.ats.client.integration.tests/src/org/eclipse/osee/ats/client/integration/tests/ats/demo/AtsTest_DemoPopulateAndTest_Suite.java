/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.demo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This suite runs each demo population and immediately tests what was created for validity.
 *
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   Pdd10SetupAndImportReqsTest.class,
   //
   Pdd20CreateCommittedActionTest.class,
   Pdd21CreateUnCommittedActionTest.class,
   Pdd22CreateUnCommittedConflictedActionTest.class,
   Pdd23CreateNoBranchActionTest.class,
   //
   Pdd51CreateWorkaroundForGraphViewActionsTest.class,
   Pdd52CreateWorkingWithDiagramTreeActionsTest.class,
   //
   Pdd80CreateButtonSDoesntWorkActionTest.class,
   Pdd81CreateButtonWDoesntWorkActionTest.class,
   Pdd82CreateCantLoadDiagramTreeActionTest.class,
   Pdd83CreateCantSeeTheGraphViewActionTest.class,
   Pdd84CreateProblemInDiagramTreeActionTest.class,
   Pdd85CreateProblemWithTheGraphViewActionTest.class,
   Pdd86CreateProblemWithTheUserWindowActionTest.class,
   //
   Pdd90CreateDemoTasksTest.class,
   Pdd91CreateDemoGroupsTest.class,
   Pdd92CreateDemoReviewsTest.class,
   Pdd93CreateDemoAgileTest.class,
   Pdd94CreateDemoFavoritesTest.class,
   Pdd95CreateDemoWorkPackagesTest.class,
   Pdd97CreateSawWorkTypesTest.class,
   //
})

public class AtsTest_DemoPopulateAndTest_Suite {

   @BeforeClass
   public static void setup() throws Exception {
      if (!DemoUtil.isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
      DemoUtil.setPopulateDbSuccessful(false);

      ClientSessionManager.releaseSession();
      // Re-authenticate so we can continue
      IdeClientSession session = ClientSessionManager.getSession();
      UserManager.releaseUser();
      AtsClientService.get().clearCaches();

      Assert.assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(),
         session.getUserId());
      Assert.assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(),
         UserManager.getUser().getUserId());

      validateArtifactCache();

      OseeLog.log(AtsTest_DemoPopulateAndTest_Suite.class, Level.SEVERE, "\nBegin Populate Demo DB...");
   }

   private static void validateArtifactCache() {
      final Collection<Artifact> list = ArtifactCache.getDirtyArtifacts();
      if (!list.isEmpty()) {
         for (Artifact artifact : list) {
            OseeLog.log(AtsTest_DemoPopulateAndTest_Suite.class, Level.SEVERE, String.format(
               "Artifact [%s] is dirty [%s]", artifact.toStringWithId(), Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            list.size());
      }

   }

   @AfterClass
   public static void testPopulateDemoDb() {
      try {
         // This test should only be run on test db
         assertFalse(AtsUtil.isProductionDb());
         // Confirm test setup with demo data
         Result result = DemoUtil.isDbPopulatedWithDemoData();
         assertTrue(result.getText(), result.isTrue());
         // Confirm user is Joe Smith
         assertTrue("User \"3333\" does not exist in DB.  Run Demo DBInit prior to this test.",
            UserManager.getUserByUserId("3333") != null);
         // Confirm user is Joe Smith
         assertTrue(
            "Authenticated user should be \"3333\" and is not.  Check that Demo Application Server is being run.",
            AtsClientService.get().getUserService().getCurrentUser().getUserId().equals("3333"));
         System.out.println("End Populate Demo DB...\n");
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }
}
