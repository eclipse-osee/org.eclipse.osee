/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public class WordEditTest extends AbstractEditTest {

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordEditTest.xml";

   public WordEditTest() {
      super(SAW_Bld_1, TEST_WORD_EDIT_FILE_NAME, CoreArtifactTypes.SoftwareRequirementMsWord, new MSWordTemplateClientRenderer());
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
