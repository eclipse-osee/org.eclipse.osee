/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public class WordEditTest extends AbstractEditTest {

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordEditTest.xml";

   public WordEditTest() {
      super(SAW_Bld_1, TEST_WORD_EDIT_FILE_NAME, CoreArtifactTypes.SoftwareRequirementMsWord, new WordTemplateRenderer());
   }

   @Override
   protected String updateDataForTest(String testData) {
      String guid = getArtifact().getGuid();
      String expected = testData.replaceAll("###ART_GUID###", guid);
      expected = expected.replaceAll("###TAG_GUID_START###", guid + "_START.jpg");
      expected = expected.replaceAll("###TAG_GUID_END###", guid + "_END.jpg");
      return expected;
   }

}
