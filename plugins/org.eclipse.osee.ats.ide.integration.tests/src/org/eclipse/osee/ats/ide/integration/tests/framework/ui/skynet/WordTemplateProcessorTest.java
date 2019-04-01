/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Branden W. Phillips
 */
public class WordTemplateProcessorTest {

   WordTemplateProcessor processor;
   List<ArtifactTypeToken> excludeTokens;
   Artifact softReqFolder;
   List<Artifact> requirements;
   Set<ArtifactId> results;

   @Before
   public void setUpProcessor() {
      processor = new WordTemplateProcessor(null);
      excludeTokens = new ArrayList<>();
      excludeTokens.add(CoreArtifactTypes.ImplementationDetails);
      processor.setExcludedArtifactTypeForTest(excludeTokens);

      softReqFolder =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Software Requirements", SAW_Bld_1);
      requirements = Arrays.asList(softReqFolder);
      processor.isEmptyHeaders(requirements);
      results = processor.getEmptyFolders();
   }

   @Test
   public void getEmptyHeadersTest_case1() {

      Artifact eventDetailHeading =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "Events Detail Header", SAW_Bld_1);

      Assert.assertTrue(results.contains(eventDetailHeading));

   }

   @Test
   public void getEmptyHeadersTest_case2() {

      Artifact virtualFixDetailHeader =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "VirtualFixDetailHeader", SAW_Bld_1);
      Artifact virtualFixDetailRequirements = ArtifactQuery.getArtifactFromTypeAndName(
         CoreArtifactTypes.SoftwareRequirement, "Virtual Fix Detail Requirements", SAW_Bld_1);

      Assert.assertFalse(results.contains(virtualFixDetailHeader));
      Assert.assertFalse(results.contains(virtualFixDetailRequirements));

   }

   @Test
   public void getEmptyHeadersTest_case3() {

      Artifact eventImplementation = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement,
         "Event Implementation", SAW_Bld_1);
      Artifact methodName =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Method name", SAW_Bld_1);

      Assert.assertFalse(results.contains(eventImplementation));
      Assert.assertFalse(results.contains(methodName));

   }

   @Test
   public void getEmptyHeadersTest_case4() {

      Artifact robotInterfaceHeading = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord,
         "Robot Interface Heading", SAW_Bld_1);

      Assert.assertTrue(results.contains(robotInterfaceHeading));

   }

   @Test
   public void getEmptyHeadersTest_case5() {

      Artifact robotUserInterfacesHeading =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "Robot User Interfaces", SAW_Bld_1);
      Artifact robotAdminUIHeading = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord,
         "Robot Admin User Interface", SAW_Bld_1);
      Artifact robotUIHeading =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "Robot User Interface", SAW_Bld_1);

      Assert.assertTrue(results.contains(robotUserInterfacesHeading));
      Assert.assertTrue(results.contains(robotAdminUIHeading));
      Assert.assertTrue(results.contains(robotUIHeading));

   }
}