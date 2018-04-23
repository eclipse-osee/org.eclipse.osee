/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.editor;

import java.io.File;
import java.util.Arrays;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.editor.WfeEditorAddSupportingFiles;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link WfeEditorAddSupportingFiles}
 *
 * @author Donald G. Dunne
 */
public class WfeEditorAddSupportingFilesTest {

   private String homeDir;
   private File file1, file2;

   @Before
   public void setup() {
      homeDir = System.getProperty("user.home");

      file1 = new File(homeDir + File.separator + getClass().getSimpleName() + "_1.txt");
      file2 = new File(homeDir + File.separator + getClass().getSimpleName() + "_2.txt");
   }

   @After
   public void cleanup() {
      AtsTestUtil.cleanup();

      file1 = new File(homeDir + File.separator + getClass().getSimpleName() + "_1.txt");
      if (file1.exists()) {
         file1.delete();
      }
      file2 = new File(homeDir + File.separator + getClass().getSimpleName() + "_2.txt");
      if (file2.exists()) {
         file2.delete();
      }
   }

   @Test
   public void testValidateAndRun() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      Lib.writeStringToFile("test_1", file1);
      Lib.writeStringToFile("test_2", file2);

      WfeEditorAddSupportingFiles job = new WfeEditorAddSupportingFiles(teamWf, Arrays.asList(file1, file2));
      job.validate();
      job.run(null);

      int found = 0;
      for (Artifact art : ((Artifact) teamWf.getStoreObject()).getRelatedArtifacts(
         CoreRelationTypes.SupportingInfo_SupportingInfo)) {
         if (art.getName().contains(getClass().getSimpleName() + "_1")) {
            found++;
         } else if (art.getName().contains(getClass().getSimpleName() + "_2")) {
            found++;
         }
      }

      Assert.assertEquals(2, found);
   }

}
