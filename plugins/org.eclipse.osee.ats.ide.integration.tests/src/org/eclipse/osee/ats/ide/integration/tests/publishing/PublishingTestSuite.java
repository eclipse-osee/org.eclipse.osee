/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.ats.ide.integration.tests.AtsIdeIntegrationTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.DemoDbPopulateSuite;
import org.eclipse.osee.ats.ide.integration.tests.publishing.markdown.PublishingMarkdownTestSuite;
import org.eclipse.osee.ats.ide.integration.tests.util.DbInitTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Test suite for Publishing.
 *
 * @author Loren K. Ashley
 * @implSpec Name test classes in the suite with the pattern Publishing*Test.
 */

@RunWith(PublishingTestSuite.class)
public class PublishingTestSuite extends Suite {

   /**
    * Combined array with the {@link PublishingTestSuite#initializationTests} followed by the
    * {@link PublishingTestSuite#publishingTests}.
    */

   private static final Class<?>[] allTests;

   /**
    * Tests that must be run when the Publishing Test Suite is run as the primary suite.
    */

   //@formatter:off
   private static final Class<?>[] initializationTests =
      new Class<?>[]
      {
         DbInitTest.class,
         /*
          * ToDo: Once all publishing tests are independent of demo artifacts the
          * DemoDbPopulateSuite can be removed from the initialization tests.
          */
         DemoDbPopulateSuite.class
      };
   //@formatter:on

   /**
    * Array of the publishing tests
    */

   //@formatter:off
   private static final Class<?>[] publishingTests =
      new Class<?>[]
      {
         PublishingDataRightsTest.class,
         PublishingDiffTest.class,
         PublishingIncludeFoldersTest.class,
         PublishingInlineTest.class,
         PublishingMarkdownTestSuite.class,
         PublishingOutlineNumberingTest.class,
         PublishingPreviewAndMultiPreviewTest.class,
         PublishingRendererTest.class,
         PublishingServerPreviewTest.class,
         PublishingSharedArtifactsFolderTest.class,
         PublishingWithFoldersTest.class
      };

   /**
    * Tracks the total time spent running the test suite.
    */

   private static ElapsedTime time;


   static {
      allTests =
         new Class<?>[PublishingTestSuite.initializationTests.length + PublishingTestSuite.publishingTests.length];
      System.arraycopy(PublishingTestSuite.initializationTests, 0, allTests, 0,
         PublishingTestSuite.initializationTests.length);
      System.arraycopy(PublishingTestSuite.publishingTests, 0, allTests, PublishingTestSuite.initializationTests.length,
         PublishingTestSuite.publishingTests.length);
   }

   /**
    * Flag set based upon command line property parameter. The flag is <code>true</code> when the {@link PublishingTestSuite}
    * is being run as the top level test suite.
    */

   private static boolean top;

   /**
    * Method run at the completion of the {@link PublishingTestSuite}. Stops the timer. when the
    * {@link PublishingTestSuite} is being run independently, the "is in test" property is set back
    * to <code>false</code>.
    */

   @AfterClass
   public static void cleanup() {

      time.end(Units.MIN);

      if (PublishingTestSuite.top) {

         OseeProperties.setIsInTest(false);

      }
   }

   /**
    * When the "is in test" property is set, it indicates that the {@link PublishingTestSuite} is
    * being run as a sub-suite of the {@link AtsIdeIntegrationTestSuite} and the initialization
    * tests have already been completed. A side-effect of this method is the setting of the {@link PublishingTestSuite#top}
    * flag to <code>true</code> when the {@link PublishingTestSuite} is being run independently; otherwise,
    * the flag is set to <code>false</code>.
    * @return the array of the tests to be run.
    */

   private static Class<?>[] getTests() {

      final var testSuite = System.getProperty( "testSuite" );
      PublishingTestSuite.top = PublishingTestSuite.class.getSimpleName().equals( testSuite );

      return PublishingTestSuite.top ? PublishingTestSuite.allTests : PublishingTestSuite.publishingTests;

   }

   /**
    * Starts the test suite timer.
    */

   @BeforeClass
   public static void setup() {

      assertFalse("Not to be run on a production database.", ClientSessionManager.isProductionDataStore());

      if (PublishingTestSuite.top) {
         OseeProperties.setIsInTest(true);
      }

      time = new ElapsedTime("PublishingTestSuite", true);
   }

   /**
    * Creates the {@link PublishingTestSuite} and selects the tests for the suite according to
    * the database property "is in test". When the property is set, it indicates the {@link PublishingTestSuite}
    * is running as a sub-suite of the {@link AtsIdeIntegrationTestSuite} and the initialization tests
    * do not need to be run.
    * @param klass the root of the test suite.
    * @param builder the {@link RunnerBuilder} to use for the test suite.
    * @throws InitializationError when super constructor fails.
    * @throws AssertionError when a production database is detected.
    */

   public PublishingTestSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
      //@formatter:off
      super
         (
            builder,
            klass,
            PublishingTestSuite.getTests()
         );
      //@formatter:on
   }
}

/* EOF */
