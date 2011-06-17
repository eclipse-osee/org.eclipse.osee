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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.rule.OseeHousekeepingRule;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Megumi Telles
 */
public class PreviewAndMultiPreviewTest {

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private static final IOseeBranch BRANCH = DemoSawBuilds.SAW_Bld_1;

   private static SevereLoggingMonitor monitorLog;

   private static List<Artifact> testArtifacts = new ArrayList<Artifact>();

   @BeforeClass
   public static void setUpOnce() throws OseeCoreException {
      Assert.assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      RenderingUtil.setPopupsAllowed(false);
   }

   @Before
   public void setUp() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @After
   public void tearDown() throws Exception {
      Assert.assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).isEmpty());
      Assert.assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).isEmpty());
      TestUtil.severeLoggingEnd(monitorLog);
      new PurgeArtifacts(testArtifacts).execute();
      testArtifacts.clear();
   }

   private static Artifact createArtifact(IArtifactType type, IOseeBranch branch, String name) throws OseeCoreException {
      Artifact artifact = ArtifactTypeManager.addArtifact(type, branch, name);
      Assert.assertNotNull(artifact);
      testArtifacts.add(artifact);
      return artifact;
   }

   /*
    * Preview Requirements Artifact (includes a child artifact == general document but should not invoke a warning since
    * only previewing (no recurse)).
    */
   @Test
   public void testPreview() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("1a"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer.open(Arrays.asList(parentArtifact), PresentationType.PREVIEW);
   }

   @Test
   public void testPreviewUsingRendererManager() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.GeneralDocument, BRANCH, addPrefix("1b"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(Arrays.asList(parentArtifact), PresentationType.PREVIEW);
   }

   /*
    * Preview Requirements Artifact with valid children.
    */
   @Test
   public void testPreviewWithChildren() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("1c"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.open(parentArtifact, PresentationType.PREVIEW, ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR);
   }

   @Test
   public void testPreviewWithChildrenUsingRendererManager() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("1d"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(Arrays.asList(parentArtifact), PresentationType.PREVIEW);
   }

   /*
    * No warning expected in this fault case because the Renderer Manager resolves which render to use to preview.
    */
   @Test
   public void testPreviewWithChildrenUsingRendererManagerFault() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact childArt = createArtifact(CoreArtifactTypes.GeneralDocument, BRANCH, addPrefix("1f"));
      parentArtifact.addChild(childArt);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.openInJob(Arrays.asList(parentArtifact), PresentationType.PREVIEW);
   }

   /*
    * Preview multiple Requirement Artifacts
    */
   @Test
   public void testMultiPreview() throws Exception {
      Artifact multiArt1 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("3z"));
      multiArt1.persist(getClass().getSimpleName());
      Artifact multiArt2 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("2y"));
      multiArt2.persist(getClass().getSimpleName());
      Artifact multiArt3 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("1x"));
      multiArt3.persist(getClass().getSimpleName());

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      List<Artifact> newMultiArts = Arrays.asList(multiArt1, multiArt2, multiArt3);
      renderer.open(newMultiArts, PresentationType.PREVIEW);
   }

   @Test
   public void testMultiPreviewUsingRendererManager() throws Exception {
      Artifact parentArtifact =
         createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, PreviewAndMultiPreviewTest.class.getSimpleName());

      Artifact multiArt1 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("3o"));
      parentArtifact.addChild(multiArt1);

      Artifact multiArt2 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("2n"));
      parentArtifact.addChild(multiArt2);

      Artifact multiArt3 = createArtifact(CoreArtifactTypes.SoftwareRequirement, BRANCH, addPrefix("1m"));
      parentArtifact.addChild(multiArt3);

      parentArtifact.persist(getClass().getSimpleName());

      RendererManager.open(parentArtifact, PresentationType.PREVIEW);
   }

   /**
    * Preview a whole word doc
    */
   @Test
   public void testWholeWordPreview() throws Exception {
      Artifact art = createArtifact(CoreArtifactTypes.TestProcedureWML, BRANCH, addPrefix("4g"));
      art.persist(new SkynetTransaction(BRANCH, String.format("%s, persist on %s, guid: %s",
         PreviewAndMultiPreviewTest.class.getSimpleName(), BRANCH.getName(), BRANCH.getGuid())));
      WholeWordRenderer renderer = new WholeWordRenderer();
      renderer.open(Arrays.asList(art), PresentationType.PREVIEW);
   }

   @Test
   public void testWholeWordPreviewUsingRendererManager() throws Exception {
      Artifact art = createArtifact(CoreArtifactTypes.TestProcedureWML, BRANCH, addPrefix("4h"));
      art.persist(new SkynetTransaction(BRANCH, String.format("%s, persist on %s, guid: %s",
         PreviewAndMultiPreviewTest.class.getSimpleName(), BRANCH.getName(), BRANCH.getGuid())));
      RendererManager.openInJob(Arrays.asList(art), PresentationType.PREVIEW);
   }

   public String addPrefix(String name) {
      return String.format("%s.%s", "Template", name);
   }
}
