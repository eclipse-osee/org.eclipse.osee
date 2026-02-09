/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.framework.core.attribute.sanitizer.MarkdownSanitizer;
import org.junit.Test;

/**
 * @author Jaden W. Puckett
 */
public class MarkdownSanitizerTest {

   /*
    * Feature
    */
   @Test
   public void featureTagMissingCommentBeforeButNotAfter() {
      String md = "Feature[FEATURE_A=Included]`` some text";
      String expectedMd = "``Feature[FEATURE_A=Included]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void featureTagMissingCommentAfterButNotBefore() {
      String md = "``Feature[FEATURE_A=Included] some text";
      String expectedMd = "``Feature[FEATURE_A=Included]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void featureTagMissingCommentBothBeforeAndAfter() {
      String md = "Feature[FEATURE_A=Included] some text";
      String expectedMd = "``Feature[FEATURE_A=Included]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void featureTagNotMissingComment() {
      String md = "``Feature[FEATURE_A=Included]`` some text";
      String expectedMd = "``Feature[FEATURE_A=Included]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void featureTagMissingAllCasesCombined() {
      String md = "Feature[FEATURE_A=Included] some text ``Feature Else some other text End Feature``";
      String expectedMd = "``Feature[FEATURE_A=Included]`` some text ``Feature Else`` some other text ``End Feature``";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   /*
    * Configuration
    */
   @Test
   public void configurationTagMissingCommentBeforeButNotAfter() {
      String md = "Configuration[CONFIGURATION_A]`` some text";
      String expectedMd = "``Configuration[CONFIGURATION_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationTagMissingCommentAfterButNotBefore() {
      String md = "``Configuration[CONFIGURATION_A] some text";
      String expectedMd = "``Configuration[CONFIGURATION_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationTagMissingCommentBothBeforeAndAfter() {
      String md = "Configuration[CONFIGURATION_A] some text";
      String expectedMd = "``Configuration[CONFIGURATION_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationTagNotMissingComment() {
      String md = "``Configuration[CONFIGURATION_A=Included]`` some text";
      String expectedMd = "``Configuration[CONFIGURATION_A=Included]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationTagMissingAllCasesCombined() {
      String md = "Configuration[CONFIGURATION_A] some text ``Configuration Else some other text End Configuration``";
      String expectedMd =
         "``Configuration[CONFIGURATION_A]`` some text ``Configuration Else`` some other text ``End Configuration``";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   /*
    * ConfigurationGroup
    */
   @Test
   public void configurationGroupTagMissingCommentBeforeButNotAfter() {
      String md = "ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String expectedMd = "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationGroupTagMissingCommentAfterButNotBefore() {
      String md = "``ConfigurationGroup[CONFIGURATIONGROUP_A] some text";
      String expectedMd = "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationGroupTagMissingCommentBothBeforeAndAfter() {
      String md = "ConfigurationGroup[CONFIGURATIONGROUP_A] some text";
      String expectedMd = "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationGroupTagNotMissingComment() {
      String md = "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String expectedMd = "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }

   @Test
   public void configurationGroupTagMissingAllCasesCombined() {
      String md =
         "ConfigurationGroup[CONFIGURATIONGROUP_A] some text ``ConfigurationGroup Else some other text End ConfigurationGroup``";
      String expectedMd =
         "``ConfigurationGroup[CONFIGURATIONGROUP_A]`` some text ``ConfigurationGroup Else`` some other text ``End ConfigurationGroup``";
      String cleanedMd = MarkdownSanitizer.enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(md);
      assertEquals(expectedMd, cleanedMd);
   }
}
