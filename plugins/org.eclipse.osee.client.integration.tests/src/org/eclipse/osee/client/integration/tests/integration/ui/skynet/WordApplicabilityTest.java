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
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
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

   private OseeClient oseeClient;
   HashCollection<String, String> validFeatureValuesForBranch;
   HashSet<String> validConfigurations;

   @Before
   public void setup() {
      oseeClient = ServiceUtil.getOseeClient();
      validFeatureValuesForBranch = getValidFeatureValuesForBranch(DemoBranches.SAW_Bld_1);
      validConfigurations = getValidConfigurations(DemoBranches.SAW_Bld_1);
   }

   @Test
   public void testInvalidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_INVALID_TAGS);

      assertTrue(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidElseApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_ElSE_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidEmbeddedApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_EMBEDDED_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch,
         validConfigurations));
   }

   @Test
   public void testValidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_VALID_TAGS);

      assertFalse(WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch,
         validConfigurations));
   }

   private HashCollection<String, String> getValidFeatureValuesForBranch(BranchId branch) {
      List<FeatureDefinitionData> featureDefinitionData =
         oseeClient.getApplicabilityEndpoint(branch).getFeatureDefinitionData();

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinitionData feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName(), feat.getValues());
      }

      return validFeatureValues;
   }

   private HashSet<String> getValidConfigurations(BranchId branch) {
      return WordUtil.getValidConfigurations(branch);
   }
}
