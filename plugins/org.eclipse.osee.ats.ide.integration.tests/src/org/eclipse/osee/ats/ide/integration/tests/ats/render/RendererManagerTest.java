/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.render;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.ide.integration.tests.ats.render.RendererManagerTest.DefaultOption.Both;
import static org.eclipse.osee.ats.ide.integration.tests.ats.render.RendererManagerTest.DefaultOption.Off;
import static org.eclipse.osee.ats.ide.integration.tests.ats.render.RendererManagerTest.DefaultOption.On;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralDocument;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementPlainText;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestCase;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedureWholeWord;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.MERGE;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.ide.editor.renderer.AtsWfeRenderer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.JavaRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PlainTextRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.junit.Assert;
import org.junit.Before;
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

   private final ArtifactTypeToken artifactType;
   private final PresentationType presentationType;
   private final Class<? extends IRenderer> clazz;
   private final DefaultOption defaultOption;

   public RendererManagerTest(ArtifactTypeToken artifactType, PresentationType presentationType, Class<? extends IRenderer> clazz, DefaultOption defaultOption) {
      this.artifactType = artifactType;
      this.presentationType = presentationType;
      this.clazz = clazz;
      this.defaultOption = defaultOption;
   }

   @Before
   public void setup() {
      OseeProperties.setIsInTest(true);
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
      RendererManager.setDefaultArtifactEditor(option == On);

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
      addTest(data, Folder, DIFF, MSWordTemplateClientRenderer.class, Both);
      addTest(data, Folder, PREVIEW, MSWordTemplateClientRenderer.class, Both);
      addTest(data, Folder, MERGE, null, Both);
      addTest(data, Folder, DEFAULT_OPEN, DefaultArtifactRenderer.class, Off);
      addTest(data, Folder, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, Folder, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, SoftwareRequirementMsWord, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, SoftwareRequirementMsWord, SPECIALIZED_EDIT, MSWordTemplateClientRenderer.class, Both);
      addTest(data, SoftwareRequirementMsWord, DIFF, MSWordTemplateClientRenderer.class, Both);
      addTest(data, SoftwareRequirementMsWord, PREVIEW, MSWordTemplateClientRenderer.class, Both);
      addTest(data, SoftwareRequirementMsWord, MERGE, MSWordTemplateClientRenderer.class, Both);
      addTest(data, SoftwareRequirementMsWord, DEFAULT_OPEN, MSWordTemplateClientRenderer.class, Off);
      addTest(data, SoftwareRequirementMsWord, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, SoftwareRequirementMsWord, PRODUCE_ATTRIBUTE, MSWordTemplateClientRenderer.class, Both);

      addTest(data, Action, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, Action, SPECIALIZED_EDIT, AtsWfeRenderer.class, Both);
      addTest(data, Action, DIFF, AtsWfeRenderer.class, Both);
      addTest(data, Action, PREVIEW, AtsWfeRenderer.class, Both);
      addTest(data, Action, MERGE, AtsWfeRenderer.class, Both);
      addTest(data, Action, DEFAULT_OPEN, AtsWfeRenderer.class, Off);
      addTest(data, Action, DEFAULT_OPEN, AtsWfeRenderer.class, On);
      addTest(data, Action, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, TestProcedureWholeWord, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, TestProcedureWholeWord, SPECIALIZED_EDIT, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWholeWord, DIFF, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWholeWord, PREVIEW, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWholeWord, MERGE, WholeWordRenderer.class, Both);
      addTest(data, TestProcedureWholeWord, DEFAULT_OPEN, WholeWordRenderer.class, Off);
      addTest(data, TestProcedureWholeWord, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, TestProcedureWholeWord, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, GeneralDocument, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, GeneralDocument, SPECIALIZED_EDIT, NativeRenderer.class, Both);
      addTest(data, GeneralDocument, DIFF, MSWordTemplateClientRenderer.class, Both);
      addTest(data, GeneralDocument, PREVIEW, NativeRenderer.class, Both);
      addTest(data, GeneralDocument, MERGE, null, Both);
      addTest(data, GeneralDocument, DEFAULT_OPEN, NativeRenderer.class, Off);
      addTest(data, GeneralDocument, DEFAULT_OPEN, DefaultArtifactRenderer.class, On);
      addTest(data, GeneralDocument, PRODUCE_ATTRIBUTE, DefaultArtifactRenderer.class, Both);

      addTest(data, TestCase, GENERALIZED_EDIT, DefaultArtifactRenderer.class, Both);
      addTest(data, TestCase, SPECIALIZED_EDIT, JavaRenderer.class, Both);
      addTest(data, TestCase, DIFF, MSWordTemplateClientRenderer.class, Off);
      addTest(data, TestCase, PREVIEW, MSWordTemplateClientRenderer.class, Both);
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
