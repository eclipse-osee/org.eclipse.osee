/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.test.framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.internal.AbstractTestRule;
import org.eclipse.osee.client.test.framework.internal.DatabaseInitializer;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.init.IDbInitChoiceEnum;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.runners.model.FrameworkMethod;

/**
 * The OseeClientIntegration Rule initializes an OSEE database instance using the configuration specified. This rule
 * should be added to all OSEE integration tests. The database will only be initialized for the first test in the test
 * suite. Finally, the rule also guards against writing to production databases.
 *
 * <pre>
 * public class TestA {
 *
 *    &#064;Rule
 *    public OseeClientIntegrationRule rule = new OseeClientIntegrationRule(DemoChoice.OSEE_CLIENT_DEMO);
 *
 *    &#064;Test
 *    public void testA() {
 *       Artifact artifact =
 *          ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1, &quot;Folder&quot;);
 *       artifact.persist(&quot;write to db&quot;);
 *    }
 * }
 * </pre>
 *
 * @author Roberto E. Escobar
 */
public final class OseeClientIntegrationRule extends AbstractTestRule {

   private static boolean onFirstSuccess = false;

   //   private boolean originalIsInTest;
   //   private boolean popUps;

   private final IDbInitChoiceEnum choice;
   private final String authenticationType = "demo";

   public OseeClientIntegrationRule(IDbInitChoiceEnum choice) {
      this.choice = choice;
   }

   @Override
   public void onFirstTest(FrameworkMethod method) throws Throwable {
      String message = String.format("[%s] Application Server must be running", authenticationType.toUpperCase());
      assertTrue(message, ClientSessionManager.getAuthenticationProtocols().contains(authenticationType));

      checkNotProductionDataStore();

      DatabaseInitializer initializer = new DatabaseInitializer(choice);
      initializer.execute();
      onFirstSuccess = true;
   }

   /**
    * Invoked when a test method is about to start
    */
   @Override
   public void onTestStarting(FrameworkMethod method) throws Throwable {
      if (onFirstSuccess) {
         checkNotProductionDataStore();

         //         originalIsInTest = OseeProperties.isInTest();
         OseeProperties.setIsInTest(true);
         //         popUps = RenderingUtil.arePopupsAllowed();
         RenderingUtil.setPopupsAllowed(false);

         checkAuthenticatedSession(authenticationType);
      } else {
         Assert.fail("Error during database initialization");
      }
   }

   /**
    * Invoked when a test method finishes (whether passing or failing)
    */
   @Override
   public void onTestFinished(FrameworkMethod method) {
      //      OseeProperties.setIsInTest(originalIsInTest);
      //      RenderingUtil.setPopupsAllowed(popUps);
   }

   private static void checkAuthenticatedSession(String authenticationType) {
      String message = String.format("Client must authenticate using [%s] protocol", authenticationType);
      assertTrue(message, ClientSessionManager.getSession().getAuthenticationProtocol().equals(authenticationType));
   }

   private static void checkNotProductionDataStore() {
      assertFalse("Not to be run on a production database.", isProductionDataStore());
   }

   private static boolean isProductionDataStore() {
      return ClientSessionManager.isProductionDataStore();
   }
}
