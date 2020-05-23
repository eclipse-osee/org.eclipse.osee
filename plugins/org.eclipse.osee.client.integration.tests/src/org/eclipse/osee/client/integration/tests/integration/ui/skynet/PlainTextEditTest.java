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

package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.render.PlainTextRenderer;

/**
 * @author Shawn F. Cook
 */
public class PlainTextEditTest extends AbstractEditTest {

   private static final String TEST_PLAIN_TEXT_EDIT_FILE_NAME = "support/PlainTextEditTest.txt";

   public PlainTextEditTest() {
      super(SAW_Bld_1, TEST_PLAIN_TEXT_EDIT_FILE_NAME, CoreArtifactTypes.SoftwareRequirementPlainText,
         new PlainTextRenderer());
   }

   @Override
   protected String updateDataForTest(String testData) {
      return testData.replaceAll("###REPLACE_THIS_TEXT###", "***text has been updated***");
   }

}
