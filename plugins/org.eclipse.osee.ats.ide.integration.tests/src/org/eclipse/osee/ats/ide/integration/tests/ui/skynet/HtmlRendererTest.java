/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.render.HTMLRenderer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Marc Potter
 */

public class HtmlRendererTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static String INPUT_HTML;
   private static String EXPECTED_HTML;
   private final static String LEGACY_ID = "ABC-123,ABC-234";
   private final static String PARAGRAPH_NUMBER = "1.2.3.4";
   private BranchToken rootBranch;

   private List<Artifact> theArtifacts = null;

   private HTMLRenderer renderer = null;

   @BeforeClass
   public static void loadTemplateInfo() throws Exception {
      INPUT_HTML = getResourceData("htmlRenderer/htmlTestInput.htm");
      INPUT_HTML = replaceQuotes(INPUT_HTML);
      EXPECTED_HTML = getResourceData("htmlRenderer/htmlExpectOutput.htm");
   }

   @Before
   public void setUp() {
      Artifact Folder;
      Artifact htmlArtifact;
      renderer = new HTMLRenderer(new HashMap<RendererOption, Object>());
      // create example artifact
      theArtifacts = new ArrayList<>();

      String branchName = method.getQualifiedTestName();
      rootBranch = BranchManager.createTopLevelBranch(branchName);
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(DemoUsers.Joe_Smith),
         rootBranch, PermissionEnum.FULLACCESS);

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(rootBranch);

      Folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, rootBranch, "Folder");

      programRoot.addChild(Folder);
      Folder.persist("FOLDER SETUP");

      htmlArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HtmlArtifact, rootBranch, "Html Artifact");
      htmlArtifact.addAttributeFromString(CoreAttributeTypes.LegacyId, LEGACY_ID);
      htmlArtifact.addAttributeFromString(CoreAttributeTypes.ParagraphNumber, PARAGRAPH_NUMBER);

      htmlArtifact.addAttributeFromString(CoreAttributeTypes.HtmlContent, INPUT_HTML);

      htmlArtifact.persist("create sample artifact");
      theArtifacts.add(htmlArtifact);

   }

   @After
   public void tearDown() throws Exception {
      if (BranchManager.branchExists(rootBranch)) {
         BranchManager.purgeBranch(rootBranch);
      }
   }

   public static String replaceQuotes(String HTML) {
      HTML = HTML.replaceAll("test: \"", "test: &quot;");
      HTML = HTML.replaceAll("test \"", "test &quot;");
      HTML = HTML.replaceAll(":\\[\"", ":[&quot;");
      return HTML;
   }

   @Test
   public void testHtmlRender() throws Exception {
      InputStream stream = renderer.getRenderInputStream(PresentationType.PREVIEW, theArtifacts);
      String theInput = Lib.inputStreamToString(stream);
      theInput = replaceQuotes(theInput);
      //The following is used to forcefully extract &quot; and "
      int inputHalfIndex = theInput.indexOf("&lt;&gt");
      int expectedHalfIndex = EXPECTED_HTML.indexOf("&lt;&gt");
      int inputSecondHalfIndex = theInput.indexOf(";'/?");
      int expectedSecondHalfIndex = EXPECTED_HTML.indexOf(";'/?");

      String inputFirstHalf = theInput.substring(0, inputHalfIndex);
      String expectedFirstHalf = EXPECTED_HTML.substring(0, expectedHalfIndex);
      String inputSecondHalf = theInput.substring(inputSecondHalfIndex);
      String expectedSecondHalf = EXPECTED_HTML.substring(expectedSecondHalfIndex);
      String ForcedInputExtraction = inputFirstHalf + inputSecondHalf;
      String TheExpectedExtraction = expectedFirstHalf + expectedSecondHalf;
      Assert.assertEquals("Expected HTMl does not equal rendered HTML even after extraction", TheExpectedExtraction,
         ForcedInputExtraction);
   }

   private static String getResourceData(String relativePath) throws IOException {
      String value = Lib.fileToString(HtmlRendererTest.class, "support/" + relativePath);
      value = replaceQuotes(value);
      Assert.assertTrue(Strings.isValid(value));
      return value;
   }

}
