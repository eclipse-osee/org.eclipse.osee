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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link RoughArtifactMetaData}
 * 
 * @author Karol M. Wilk
 */
public final class RoughArtifactMetaDataTest {

   private static final String headingStyle = "Heading2";

   @Test
   public void testRegParagraph() {
      Assert.assertTrue(RoughArtifactMetaData.matches(headingStyle, "Heading2"));
      Assert.assertTrue(RoughArtifactMetaData.matches(headingStyle, "Heading3"));
      Assert.assertTrue(RoughArtifactMetaData.matches(headingStyle, "Heading4"));
   }

   @Test
   public void testIrregularCase() {
      Assert.assertFalse(RoughArtifactMetaData.matches(headingStyle, "bltlvl1"));
      Assert.assertFalse(RoughArtifactMetaData.matches(headingStyle, "Normal"));
      Assert.assertTrue(RoughArtifactMetaData.matches(headingStyle, "Heading3"));
   }

   @Test
   public void testConfidenceCase() {
      String headingStyle = "0123456789";
      boolean paragraphStyleAre_80perc_TheSame = RoughArtifactMetaData.matches(headingStyle, "01234567xx"); //xx for same length check
      Assert.assertTrue(paragraphStyleAre_80perc_TheSame);
      paragraphStyleAre_80perc_TheSame = RoughArtifactMetaData.matches(headingStyle, "0123456xxx");
      Assert.assertFalse(paragraphStyleAre_80perc_TheSame);
   }
}
