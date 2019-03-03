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

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link WordOutlineExtractorDelegate} <br/>
 * Tests parsing mechanism to determine if all artifacts have been picked up. <br/>
 *
 * @author Karol M. Wilk
 */
@RunWith(Parameterized.class)
public final class WordOutlineAndStyleTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   /**
    * <pre>
    * 1.0   A
    * 2.0   B
    * 3.0   C
    *    3.0   Ignored
    * 4.0   D
    * </pre>
    *
    * All artifacts have same style.
    */
   private static final String CASE_1 = "WordOutlineAndStyle_case1.xml";

   /**
    * <pre>
    * 1.0   A
    *    1.0   A_1
    *    2.0   A_2
    * 2.0   B
    * </pre>
    *
    * A and B have a Heading8 style, while A_1 and A_2 are styled as list items.
    */
   private static final String CASE_2 = "WordOutlineAndStyle_case2.xml";

   /**
    * <pre>
    * 1.0   A
    * 2.0   B
    * 3.0   C
    * </pre>
    *
    * C's style is Normal should be treated as part of the body. This tests testing against last paragraph </br>
    * number that follows the sequence but not the style of paragraph numbers. A and B are of the same style.
    */
   private static final String CASE_3 = "WordOutlineAndStyle_case3.xml";

   private final String testComment;
   private final String wordMLFileName;
   private final List<String> expected;

   private Artifact folder;

   public WordOutlineAndStyleTest(String testComment, String wordMLFileName, List<String> expected) {
      this.testComment = testComment;
      this.wordMLFileName = wordMLFileName;
      this.expected = expected;
   }

   @Before
   public void setUp() throws Exception {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(SAW_Bld_1);
      String name = WordOutlineAndStyleTest.class.getSimpleName() + "_Folder";
      folder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, SAW_Bld_1, name);
      root.addChild(folder);
      root.persist(name);
   }

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(folder.getChildren(), true));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Collections.<Artifact> singleton(folder)));
   }

   @Test
   public void testStyleResolution() throws Exception {
      buildUpTest(wordMLFileName);
      Assert.assertEquals(testComment, expected, Artifacts.getNames(folder.getDescendants()));
   }

   @Parameters
   public static List<Object[]> getData() {
      List<Object[]> data = new LinkedList<>();

      data.add(new Object[] {"Case 1", CASE_1, Arrays.asList("A", "B", "C", "D")});
      data.add(new Object[] {"Case 2", CASE_2, Arrays.asList("A", "B")});
      data.add(new Object[] {"Case 3", CASE_3, Arrays.asList("A", "B")});

      return data;
   }

   private void buildUpTest(String testCaseFileName) throws Exception {
      File inputTestCase = OseeData.getFile("word.outline.and.style.test." + testCaseFileName);
      URL url = getClass().getResource("support/" + testCaseFileName);

      url = FileLocator.resolve(url);

      Assert.assertNotNull(url);

      Lib.copyFile(new File(url.toURI()), inputTestCase);

      Assert.assertTrue(inputTestCase.exists());

      try {
         IArtifactImportResolver resolver = ArtifactResolverFactory.createResolver(
            ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID, CoreArtifactTypes.HeadingMSWord, null, true, true);

         RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
         collector.reset();

         IArtifactExtractor extractor = new WordOutlineExtractor();
         extractor.setDelegate(new WordOutlineExtractorDelegate());

         List<ArtifactTypeToken> list = new ArrayList<>();
         list.add(CoreArtifactTypes.HeadingMSWord);

         IOperation operation = ArtifactImportOperationFactory.createOperation(inputTestCase, folder,
            NullOperationLogger.getSingleton(), extractor, resolver, collector, list, true, false, true);
         Operations.executeWorkAndCheckStatus(operation);

         boolean newArtifactsFound = !folder.getDescendants().isEmpty();
         Assert.assertTrue(newArtifactsFound);
      } finally {
         inputTestCase.delete();
      }
   }
}