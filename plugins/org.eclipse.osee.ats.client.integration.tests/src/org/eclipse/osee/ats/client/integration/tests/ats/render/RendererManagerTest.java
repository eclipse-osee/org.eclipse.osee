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
package org.eclipse.osee.ats.client.integration.tests.ats.render;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.client.integration.tests.ats.render.RendererManagerTest.DefaultOption.Both;
import static org.eclipse.osee.ats.client.integration.tests.ats.render.RendererManagerTest.DefaultOption.Off;
import static org.eclipse.osee.ats.client.integration.tests.ats.render.RendererManagerTest.DefaultOption.On;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralDocument;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementPlainText;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestCase;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedureWML;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.MERGE;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.editor.renderer.AtsWERenderer;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.JavaRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PlainTextRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link RendererManager}
 *
 * @author Ryan D. Brooks
 */
@RunWith(Parameterized.class)
public class RendererManagerTest {
   enum DefaultOption {
      On,
      Off,
      Both
   };

   private final IArtifactType artifactType;
   private final PresentationType presentationType;
   private final Class<? extends IRenderer> clazz;
   private final DefaultOption defaultOption;

   public RendererManagerTest(IArtifactType artifactType, PresentationType presentationType, Class<? extends IRenderer> clazz, DefaultOption defaultOption) {
      this.artifactType = artifactType;
      this.presentationType = presentationType;
      this.clazz = clazz;
      this.defaultOption = defaultOption;
   }

   @Test
   public void testGetBestRenderer() {
      Artifact artifact = new Artifact(CoreBranches.COMMON, artifactType);

      if (defaultOption == Both) {
         testGetBestRendererWithOption(artifact, On);
         testGetBestRendererWithOption(artifact, Off);
      } else {
         testGetBestRendererWithOption(artifact, defaultOption);
      }
   }

   private void testGetBestRendererWithOption(Artifact artifact, DefaultOption option) {
      UserManager.setSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT, String.valueOf(option == On));

      if (clazz == null) {
         try {
            IRenderer renderer = computeRenderer(artifact);
            String message = String.format(
               "Expected an OseeStateException to be thrown since no render should be applicable in this case.\nRenderer: [%s]",
               renderer);
            Assert.fail(message);
         } catch (OseeStateException ex) {
            Assert.assertEquals(String.format("No renderer configured for %s of %s", presentationType, artifact),
               ex.getMessage());
         }
      } else {
         IRenderer renderer = computeRenderer(artifact);
         Assert.assertEquals(clazz, renderer.getClass());
      }
   }

   private IRenderer computeRenderer(Artifact artifact) {
      IRenderer renderer = RendererManager.getBestRenderer(presentationType, artifact);
      Assert.assertNotNull(renderer);
      return renderer;
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();

      addTest(data, Folder, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, Folder, SPECIALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, Folder, DIFF, WordTemplateRenderer.class, Both);
      addTest(data, Folder, PREVIEW, WordTemplateRenderer.class, Both);
      addTest(data, Folder, MERGE, null, Both);
      addTest(data, Folder, DEFAULT_OPEN, DefaultArtifactRenderer.class, Off);
      addTest(data, Folder, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, Folder, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, SoftwareRequirement, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, SoftwareRequirement, SPECIALIZED_EDIT, WordTemplateRenderer.class, Both);
      addTest(data, SoftwareRequirement, DIFF, WordTemplateRenderer.class, Both);
      addTest(data, SoftwareRequirement, PREVIEW, WordTemplateRenderer.class, Both);
      addTest(data, SoftwareRequirement, MERGE, WordTemplateRenderer.class, Both);
      addTest(data, SoftwareRequirement, DEFAULT_OPEN, WordTemplateRenderer.class, Off);
      addTest(data, SoftwareRequirement, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, SoftwareRequirement, PRODUCE_ATTRIBUTE, WordTemplateRenderer.class, Both);

      addTest(data, Action, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, Action, SPECIALIZED_EDIT, AtsWERenderer.class, Both);
      addTest(data, Action, DIFF, AtsWERenderer.class, Both);
      addTest(data, Action, PREVIEW, AtsWERenderer.class, Both);
      addTest(data, Action, MERGE, AtsWERenderer.class, Both);
      addTest(data, Action, DEFAULT_OPEN, AtsWERenderer.class, Off);
      addTest(data, Action, DEFAULT_OPEN, AtsWERenderer.class, On);
      addTest(data, Action, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, TestProcedureWML, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, TestProcedureWML, SPECIALIZED_EDIT, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWML, DIFF, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWML, PREVIEW, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWML, MERGE, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWML, DEFAULT_OPEN, WholeWordRenderer.class, Off);
      addTest(data, TestProcedureWML, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, TestProcedureWML, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, GeneralDocument, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, GeneralDocument, SPECIALIZED_EDIT, NativeRenderer.class, Both);
      addTest(data, GeneralDocument, DIFF, WordTemplateRenderer.class, Both);
      addTest(data, GeneralDocument, PREVIEW, NativeRenderer.class, Both);
      addTest(data, GeneralDocument, MERGE, null, Both);
      addTest(data, GeneralDocument, DEFAULT_OPEN, NativeRenderer.class, Off);
      addTest(data, GeneralDocument, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, GeneralDocument, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, TestCase, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, TestCase, SPECIALIZED_EDIT, JavaRenderer.class, Both);
      addTest(data, TestCase, DIFF, WordTemplateRenderer.class, Off);
      addTest(data, TestCase, PREVIEW, WordTemplateRenderer.class, Both);
      addTest(data, TestCase, MERGE, null, Both);
      addTest(data, TestCase, DEFAULT_OPEN, JavaRenderer.class, Off);
      addTest(data, TestCase, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, TestCase, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, SoftwareRequirementPlainText, SPECIALIZED_EDIT, PlainTextRenderer.class, Both);
      addTest(data, SoftwareRequirementPlainText, PREVIEW, PlainTextRenderer.class, Both);
      //      addTest(data, SoftwareRequirementPlainText, DEFAULT_OPEN, PlainTextRenderer.class, Both);
      addTest(data, SoftwareRequirementPlainText, PRODUCE_ATTRIBUTE, PlainTextRenderer.class, Both);

      return data;
   }

   private static void addTest(Collection<Object[]> data, Object... params) {
      data.add(params);
   }
}