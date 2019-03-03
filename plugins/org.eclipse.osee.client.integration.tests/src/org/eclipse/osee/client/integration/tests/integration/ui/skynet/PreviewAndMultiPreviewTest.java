/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class PreviewAndMultiPreviewTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private final List<Artifact> testArtifacts = new ArrayList<>();

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(testArtifacts));
      testArtifacts.clear();
   }

   /*
    * Preview Requirements Artifact (includes a child artifact == general document but should not invoke a warning since
    * only previewing (no recurse)).
    */
   @Test
   public void testPreview() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("1a"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      WordTemplateRenderer renderer = new WordTemplateRenderer(new HashMap<RendererOption, Object>());
      renderer.open(Arrays.asList(parentArtifact), PresentationType.PREVIEW);
   }

   @Test
   public void testPreviewUsingRendererManager() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.GeneralDocument, SAW_Bld_1, addPrefix("1b"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(parentArtifact, PresentationType.PREVIEW);
   }

   /*
    * Preview Requirements Artifact with valid children.
    */
   @Test
   public void testPreviewWithChildren() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("1c"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.TEMPLATE_OPTION, RendererOption.PREVIEW_WITH_RECURSE_VALUE.getKey());

      RendererManager.open(parentArtifact, PresentationType.PREVIEW, rendererOptions);
   }

   @Test
   public void testPreviewWithChildrenUsingRendererManager() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("1d"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(parentArtifact, PresentationType.PREVIEW);
   }

   /*
    * No warning expected in this fault case because the Renderer Manager resolves which render to use to preview.
    */
   @Test
   public void testPreviewWithChildrenUsingRendererManagerFault() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.GeneralDocument, SAW_Bld_1, addPrefix("1f"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(parentArtifact, PresentationType.PREVIEW);
   }

   /*
    * Preview multiple Requirement Artifacts
    */
   @Test
   public void testMultiPreview() throws Exception {
      Artifact multiArt1 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("3z"));
      multiArt1.persist(getClass().getSimpleName());
      Artifact multiArt2 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("2y"));
      multiArt2.persist(getClass().getSimpleName());
      Artifact multiArt3 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("1x"));
      multiArt3.persist(getClass().getSimpleName());

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      List<Artifact> newMultiArts = Arrays.asList(multiArt1, multiArt2, multiArt3);
      renderer.open(newMultiArts, PresentationType.PREVIEW);
   }

   @Test
   public void testMultiPreviewUsingRendererManager() throws Exception {
      Artifact parentArtifact = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1,
         PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact multiArt1 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("3o"));
      parentArtifact.addChild(multiArt1);

      Artifact multiArt2 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("2n"));
      parentArtifact.addChild(multiArt2);

      Artifact multiArt3 = createArtifact(CoreArtifactTypes.SoftwareRequirement, SAW_Bld_1, addPrefix("1m"));
      parentArtifact.addChild(multiArt3);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.open(parentArtifact, PresentationType.PREVIEW);
   }

   /**
    * Preview a whole word doc
    */
   @Test
   public void testWholeWordPreview() throws Exception {
      Artifact art = createArtifact(CoreArtifactTypes.TestProcedureWML, SAW_Bld_1, addPrefix("4g"));
      art.persist(String.format("%s, persist on %s", PreviewAndMultiPreviewTest.class.getSimpleName(), SAW_Bld_1));
      WholeWordRenderer renderer = new WholeWordRenderer();
      renderer.open(Arrays.asList(art), PresentationType.PREVIEW);
   }

   @Test
   public void testWholeWordPreviewUsingRendererManager() throws Exception {
      Artifact art = createArtifact(CoreArtifactTypes.TestProcedureWML, SAW_Bld_1, addPrefix("4h"));
      art.persist(String.format("%s, persist on %s", PreviewAndMultiPreviewTest.class.getSimpleName(), SAW_Bld_1));
      RendererManager.openInJob(art, PresentationType.PREVIEW);
   }

   private Artifact createArtifact(ArtifactTypeToken type, BranchId branch, String name) {
      Artifact artifact = ArtifactTypeManager.addArtifact(type, branch, name);
      Assert.assertNotNull(artifact);
      testArtifacts.add(artifact);
      return artifact;
   }

   public String addPrefix(String name) {
      return String.format("Template.%s", name);
   }
}
