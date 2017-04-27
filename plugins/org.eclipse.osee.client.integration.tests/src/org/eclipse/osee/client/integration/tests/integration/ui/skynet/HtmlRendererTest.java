/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static String INPUT_HTML;
   private static String EXPECTED_HTML;
   private final static String LEGACY_ID = "ABC-123,ABC-234";
   private final static String PARAGRAPH_NUMBER = "1.2.3.4";

   private BranchId rootBranch;

   private List<Artifact> theArtifacts = null;

   private HTMLRenderer renderer = null;

   @BeforeClass
   public static void loadTemplateInfo() throws Exception {
      INPUT_HTML = getResourceData("htmlRenderer/htmlTestInput.htm");
      EXPECTED_HTML = getResourceData("htmlRenderer/htmlExpectOutput.htm");
   }

   @Before
   public void setUp() throws OseeCoreException {
      Artifact Folder;
      Artifact htmlArtifact;
      renderer = new HTMLRenderer();
      // create example artifact
      theArtifacts = new ArrayList<>();

      String branchName = method.getQualifiedTestName();
      rootBranch = BranchManager.createTopLevelBranch(branchName);
      AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), rootBranch,
         PermissionEnum.FULLACCESS);

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(rootBranch);

      Folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, rootBranch, "Folder");

      programRoot.addChild(Folder);
      Folder.persist("FOLDER SETUP");

      htmlArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HTMLArtifact, rootBranch, "Html Artifact");
      htmlArtifact.addAttributeFromString(CoreAttributeTypes.LegacyId, LEGACY_ID);
      htmlArtifact.addAttributeFromString(CoreAttributeTypes.ParagraphNumber, PARAGRAPH_NUMBER);
      htmlArtifact.addAttributeFromString(CoreAttributeTypes.HTMLContent, INPUT_HTML);
      htmlArtifact.persist("create sample artifact");
      theArtifacts.add(htmlArtifact);

   }

   @After
   public void tearDown() throws Exception {
      if (BranchManager.branchExists(rootBranch)) {
         BranchManager.purgeBranch(rootBranch);
      }
   }

   @Test
   public void testHtmlRender() throws Exception {
      InputStream stream = renderer.getRenderInputStream(PresentationType.PREVIEW, theArtifacts);
      String theInput = Lib.inputStreamToString(stream);
      Assert.assertEquals("Expect HTMl does not equal rendered HTML", theInput, EXPECTED_HTML);

   }

   private static String getResourceData(String relativePath) throws IOException {
      String value = Lib.fileToString(HtmlRendererTest.class, "support/" + relativePath);
      Assert.assertTrue(Strings.isValid(value));
      return value;
   }

}