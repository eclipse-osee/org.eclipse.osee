/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.test.render.word;

import static org.eclipse.osee.support.test.util.DemoSawBuilds.SAW_Bld_1;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Karol M. Wilk
 * @link: WordTemplateProcessor
 */
public class WordTemplateProcessorTest {
   private static Artifact myRootArtifact = null;
   private static String ERROR_MESSAGE =
      "Found improper %s's WordML in result file. Testing for %s PublishInLine set to true.";
   private static SevereLoggingMonitor monitorLog = null;
   private static String C_WITH_PUBLISH_INLINE = null;
   private static String C_WITHOUT_PUBLISH_INLINE = null;
   private static String A_WITH_PUBLISH_INLINE = null;
   private static String A_WITHOUT_PUBLISH_INLINE = null;

   private static String F_STRING_TO_WRAP_IN_WORDML = "F's body content";
   private static String F_WITH_PUBLISH_INLINE = null;
   private static String F_WITHOUT_PUBLISH_INLINE = null;

   @Test
   public void publishInLineOnChild() throws Exception {
      Artifact artifact_C = myRootArtifact.getDescendant("A").getDescendant("C");
      artifact_C.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_C.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, C_WITH_PUBLISH_INLINE);
      artifact_C.persist();

      String fileName = String.format("WordTemplateProcessorTest_%s_%s.xml", artifact_C, Lib.getDateTimeString());
      String fullPath = RenderingUtil.getRenderFolder(SAW_Bld_1, PresentationType.PREVIEW).getLocation().toOSString();

      RenderingUtil.setPopupsAllowed(false);
      RendererManager.open(myRootArtifact, PresentationType.PREVIEW, new VariableMap(ITemplateRenderer.TEMPLATE_OPTION,
         ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE, IRenderer.FILE_NAME_OPTION, fileName));

      String fileContents = Lib.fileToString(new File(fullPath, fileName));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_C, "with"), fileContents.contains(C_WITH_PUBLISH_INLINE));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_C, "without"),
         !fileContents.contains(C_WITHOUT_PUBLISH_INLINE));
   }

   @Test
   public void publishInLineOnParent() throws Exception {
      Artifact artifact_A = myRootArtifact.getDescendant("A");
      artifact_A.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_A.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, A_WITH_PUBLISH_INLINE);
      artifact_A.persist();

      String fileName = String.format("WordTemplateProcessorTest_%s_%s.xml", artifact_A, Lib.getDateTimeString());
      String fullPath = RenderingUtil.getRenderFolder(SAW_Bld_1, PresentationType.PREVIEW).getLocation().toOSString();

      RenderingUtil.setPopupsAllowed(false);
      RendererManager.open(myRootArtifact, PresentationType.PREVIEW, new VariableMap(ITemplateRenderer.TEMPLATE_OPTION,
         ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE, IRenderer.FILE_NAME_OPTION, fileName));

      String fileContents = Lib.fileToString(new File(fullPath, fileName));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_A, "with"), fileContents.contains(A_WITH_PUBLISH_INLINE));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_A, "without"),
         !fileContents.contains(A_WITHOUT_PUBLISH_INLINE));
   }

   /**
    * The GeneralStringData argument in this case is just plain string as <br/>
    * WordTemplateProcessor will wrap it in a <w:p>..whatever..</w:p> xml.
    */
   @Test
   public void publishInLineNonWordArtifact() throws Exception {
      Artifact artifact_F = myRootArtifact.getDescendant("F");
      artifact_F.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_F.setSoleAttributeValue(CoreAttributeTypes.GeneralStringData, F_STRING_TO_WRAP_IN_WORDML);
      artifact_F.persist();

      String fileName = String.format("WordTemplateProcessorTest_%s_%s.xml", artifact_F, Lib.getDateTimeString());
      String fullPath = RenderingUtil.getRenderFolder(SAW_Bld_1, PresentationType.PREVIEW).getLocation().toOSString();

      RenderingUtil.setPopupsAllowed(false);
      RendererManager.open(myRootArtifact, PresentationType.PREVIEW, new VariableMap(ITemplateRenderer.TEMPLATE_OPTION,
         ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE, IRenderer.FILE_NAME_OPTION, fileName));

      String fileContents = Lib.fileToString(new File(fullPath, fileName));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_F, "with"), fileContents.contains(F_WITH_PUBLISH_INLINE));
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact_F, "without"),
         !fileContents.contains(F_WITHOUT_PUBLISH_INLINE));
   }

   @BeforeClass
   public static void setUpOnce() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();

      //load contents of C's with publish in line
      C_WITH_PUBLISH_INLINE = getResourceData("c_with_publish_inline.xml");
      Assert.assertFalse(C_WITH_PUBLISH_INLINE.isEmpty());

      //load contents of C's without publish in line
      C_WITHOUT_PUBLISH_INLINE = getResourceData("c_without_publish_inline.xml");
      Assert.assertFalse(C_WITHOUT_PUBLISH_INLINE.isEmpty());

      A_WITH_PUBLISH_INLINE = getResourceData("a_with_publish_inline.xml");
      Assert.assertFalse(A_WITH_PUBLISH_INLINE.isEmpty());

      A_WITHOUT_PUBLISH_INLINE = getResourceData("a_without_publish_inline.xml");
      Assert.assertFalse(A_WITHOUT_PUBLISH_INLINE.isEmpty());

      F_WITH_PUBLISH_INLINE = getResourceData("f_with_publish_inline.xml");
      Assert.assertFalse(F_WITH_PUBLISH_INLINE.isEmpty());

      F_WITHOUT_PUBLISH_INLINE = getResourceData("f_without_publish_inline.xml");
      Assert.assertFalse(F_WITHOUT_PUBLISH_INLINE.isEmpty());

      //@formatter:off
      /*
       setup artifact tree of this form:
          A
          |---- C
          |  |
          |  -- D
          B
          |
          |
          E
          |
          |
          F
       */
      //@formatter:on

      myRootArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, SAW_Bld_1, "WordTemplateProcessorTest_Root");
      Artifact artifactA = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "A");
      myRootArtifact.addChild(artifactA);

      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, "C"));
      artifactA.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, SAW_Bld_1, "D"));

      myRootArtifact.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, SAW_Bld_1, "B"));

      myRootArtifact.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, SAW_Bld_1, "E"));

      myRootArtifact.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, SAW_Bld_1, "F"));

      myRootArtifact.persist();
   }

   @AfterClass
   public static void tearDownOnce() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
      new PurgeArtifacts(myRootArtifact.getChildren()).execute();
      new PurgeArtifacts(Collections.singletonList(myRootArtifact)).execute();
   }

   private static String getResourceData(String name) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = WordTemplateProcessorTest.class.getResourceAsStream(name);
         String data = Lib.inputStreamToString(inputStream);
         Assert.assertTrue(Strings.isValid(data));
         return data;
      } finally {
         Lib.close(inputStream);
      }
   }
}
