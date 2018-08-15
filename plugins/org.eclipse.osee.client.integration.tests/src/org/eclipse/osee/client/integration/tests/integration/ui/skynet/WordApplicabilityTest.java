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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.HashSet;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.utility.ApplicabilityUtility;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Morgan Cook
 */
public class WordApplicabilityTest {

   private static final String TEST_INVALID_TAGS = "support/WordInvalidApplicabilityTags.xml";
   private static final String TEST_VALID_TAGS = "support/WordValidApplicabilityTags.xml";
   private static final String TEST_ElSE_TAGS = "support/WordApplicabilityElseTags.xml";
   private static final String TEST_EMBEDDED_TAGS = "support/WordApplicabilityEmbeddedTags.xml";

   HashCollection<String, String> validFeatureValuesForBranch;
   HashSet<String> validConfigurations;

   @Before
   public void setup() {
      validFeatureValuesForBranch = ApplicabilityUtility.getValidFeatureValuesForBranch(DemoBranches.SAW_PL);
      validConfigurations = ApplicabilityUtility.getBranchViewNamesUpperCase(DemoBranches.SAW_PL);
   }

   @Test
   public void testInvalidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_INVALID_TAGS);

      assertTrue(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_PL, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidElseApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_ElSE_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_PL, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidEmbeddedApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_EMBEDDED_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_PL, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_VALID_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_PL, validFeatureValuesForBranch,
         validConfigurations));
   }
}