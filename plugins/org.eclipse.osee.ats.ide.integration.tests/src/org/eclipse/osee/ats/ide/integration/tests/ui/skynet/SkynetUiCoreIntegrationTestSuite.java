/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

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
   ReplaceWithBaselineTest.class,
   StringGuidsToArtifactListOperationTest.class,
   TemplateArtifactValidatorTest.class,
   WordArtifactElementExtractorTest.class,
   WordEditTest.class,
   WordOutlineAndStyleTest.class,
   WordTrackedChangesTest.class,
   FrameworkImageTest.class,
   HtmlRendererTest.class,
   ArtifactRendererTest.class,
   WordApplicabilityTest.class})
/**
 * @author Ryan D. Brooks
 */
public class SkynetUiCoreIntegrationTestSuite {
   // Test Suite
}
