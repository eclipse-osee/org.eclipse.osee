/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.endpoint;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.DoorsArtifactExtractorTest;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.define.api.ImportEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David W. Miller
 */
public class ImportEndpointTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static ImportEndpoint importEndpoint;
   private static File wordFile;
   private static final BranchId branch = DemoBranches.SAW_PL;
   private static final Artifact parent =
      ArtifactQuery.getArtifactFromId(CoreArtifactTokens.SoftwareRequirementsFolder, branch);
   @ClassRule
   public static TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void testSetup() throws IOException {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      importEndpoint = oseeclient.getImportEndpoint();
      wordFile = folder.newFile("testWord.xml");
      copyResource("testWord.xml", wordFile);
   }

   @Test
   public void testImport() {
      XResultData results = importEndpoint.importWord(branch, wordFile.getAbsolutePath(), parent, 0);
      Assert.assertNotNull(results);
      Assert.assertTrue(results.getInfoCount() > 6);
   }

   private static void copyResource(String resource, File output) throws IOException {
      OutputStream outputStream = null;
      try (InputStream inputStream =
         OsgiUtil.getResourceAsStream(DoorsArtifactExtractorTest.class, "support/word/" + resource)) {
         outputStream = new BufferedOutputStream(new FileOutputStream(output));
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }
}