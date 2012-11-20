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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import org.eclipse.osee.client.demo.DemoBranches;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.render.PlainTextRenderer;

/**
 * @author Shawn F. Cook
 */
public class PlainTextEditTest extends AbstractEditTest {

   private static final String TEST_PLAIN_TEXT_EDIT_FILE_NAME = "support/PlainTextEditTest.txt";
   private static final IOseeBranch branch = DemoBranches.SAW_Bld_1;

   public PlainTextEditTest() {
      super(branch, TEST_PLAIN_TEXT_EDIT_FILE_NAME, CoreArtifactTypes.SoftwareRequirementPlainText,
         new PlainTextRenderer());
   }

   @Override
   protected String updateDataForTest(String testData) {
      return testData.replaceAll("###REPLACE_THIS_TEXT###", "***text has been updated***");
   }

}
