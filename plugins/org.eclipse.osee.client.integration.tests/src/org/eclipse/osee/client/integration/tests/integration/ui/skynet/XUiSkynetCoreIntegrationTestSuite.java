/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http:www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactImportWizardTest.class,
   ArtifactPasteOperationTest.class,
   AttributeTypeEditPresenterTest.class,
   BlamXWidgetTest.class,
   EmailGroupsBlamTest.class,
   InterArtifactDropTest.class,
   PlainTextEditTest.class,
   PreviewAndMultiPreviewTest.class,
   RelationIntegrityCheckTest.class,
   ReplaceWithBaselineTest.class,
   StringGuidsToArtifactListOperationTest.class,
   ViewWordChangeAndDiffTest.class,
   WordArtifactElementExtractorTest.class,
   WordEditTest.class,
   WordOutlineAndStyleTest.class,
   WordTemplateProcessorTest.class,
   WordTrackedChangesTest.class})
public class XUiSkynetCoreIntegrationTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + XUiSkynetCoreIntegrationTestSuite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + XUiSkynetCoreIntegrationTestSuite.class.getSimpleName());
   }
}
