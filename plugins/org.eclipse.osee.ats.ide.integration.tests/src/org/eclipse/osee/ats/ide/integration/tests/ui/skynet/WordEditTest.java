/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public class WordEditTest extends AbstractEditTest {

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordEditTest.xml";
   private static final String TEST_WORD_EDIT_NEW_TEXT = "This is the new text for the edited artifact.";

   public WordEditTest() {
      super(SAW_Bld_1, TEST_WORD_EDIT_FILE_NAME, CoreArtifactTypes.SoftwareRequirementMsWord,
         new WordTemplateRenderer());
   }

   @Override
   protected String updateDataForTest(String testData) {
      String guid = getArtifact().getGuid();
      String expected = testData.replaceAll("###ART_GUID###", guid);
      expected = expected.replaceAll("###TAG_GUID_START###", guid + "_START.jpg");
      expected = expected.replaceAll("###TAG_GUID_END###", guid + "_END.jpg");
      expected = expected.replace("###NEW_TEXT###", TEST_WORD_EDIT_NEW_TEXT);
      return expected;
   }

   //@formatter:off
   @SuppressWarnings("resource")
   @Override
   protected void verifyModifiedArtifactContent(String editorSaveContent, String modifiedArtifactContent) {

      final var document =
         PublishingXmlUtils
            .parse( modifiedArtifactContent )
            .orElseThrow
               (
                  ( throwable) ->
                     new AssertionError
                            (
                               new Message()
                                      .title( "Failed to parse edited Word artifact." )
                                      .reasonFollows( throwable )
                                      .toString(),
                               throwable
                            )
               );

      final var wordDocument =
         PublishingXmlUtils
            .parseWordDocument( document )
            .orElseThrow
               (
                  ( throwable) ->
                     new AssertionError
                            (
                               new Message()
                                      .title( "Failed to parse edited Word artifact." )
                                      .reasonFollows( throwable )
                                      .toString(),
                               throwable
                            )
               );

      final var wordParagraphList =
         PublishingXmlUtils
         .parseDescendantsList( wordDocument, WordParagraphList.wordDocumentParentFactory )
         .orElseThrow
            (
               ( throwable) ->
                  new AssertionError
                         (
                            new Message()
                                   .title( "Failed to parse paragraphs from word document." )
                                   .reasonFollows( throwable )
                                   .toString(),
                            throwable
                         )
            );

      wordParagraphList
         .stream()
         .filter( ( paragraph ) -> paragraph.getText().contains(TEST_WORD_EDIT_NEW_TEXT) )
         .findFirst()
         .orElseThrow
            (
               ( ) ->
                  new AssertionError
                         (
                            new Message()
                                   .title( "Failed to find a paragraph with the expected edit text." )
                                   .toString()
                         )
            );

   }
}
