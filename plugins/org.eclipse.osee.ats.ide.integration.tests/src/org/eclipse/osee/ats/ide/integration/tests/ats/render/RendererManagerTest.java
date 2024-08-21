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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractHeading;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingHtml;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingMarkdown;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HtmlArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.InterfaceArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Markdown;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.MsWordTemplate;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.MsWordWholeDocument;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.NativeArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Parameter;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.PlainText;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestCase;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedureNative;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestProcedureWholeWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.TestUnit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Url;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.User;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.F5_DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.MERGE;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.RENDER_AS_HUMAN_READABLE_TEXT;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.WEB_PREVIEW;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.editor.renderer.AtsWfeRenderer;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.HTMLRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.JavaRenderer;
import org.eclipse.osee.framework.ui.skynet.render.MarkdownRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PlainTextRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link RendererManager}
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class RendererManagerTest {

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd>
    * <dt>No Pop Ups Rule</dt>
    * <dd>This rule prevents the code under test from displaying pop up dialog boxes that must acknowledged before the
    * test can resume.</dd>
    * </dl>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NoPopUpsRule() )
         ;
   //@formatter:on

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   /**
    * A rule to get the method name of the currently running test.
    */

   @Rule
   public TestName testName = new TestName();

   enum DefaultOption {
      On,
      Off,
      Both
   };

   private final String subTestName;
   private final ArtifactTypeToken artifactType;
   private final PresentationType presentationType;
   private final Class<? extends IRenderer> clazz;
   private final DefaultOption defaultOption;
   private final Integer artifactIndex;

   public RendererManagerTest(String subTestName, ArtifactTypeToken artifactType, PresentationType presentationType, Class<? extends IRenderer> clazz, DefaultOption defaultOption, Integer artifactIndex) {
      this.subTestName = subTestName;
      this.artifactType = artifactType;
      this.presentationType = presentationType;
      this.clazz = clazz;
      this.defaultOption = defaultOption;
      this.artifactIndex = artifactIndex;
   }

   @Before
   public void setup() {
      OseeProperties.setIsInTest(true);
   }

   //@formatter:off
   private static int CASE_OPEN_IN_MS_WORD       = 0x01;
   private static int CASE_OPEN_IN_MARKDOWN      = 0x02;
   private static int CASE_OPEN_IN_HTML          = 0x04;
   private static int CASE_OPEN_IN_PLAIN_TEXT    = 0x08;
   private static int CASE_OPEN_IN_NATIVE        = 0x10;

   private static int CASE_CONTENT_VALUE_SET     = 0x20;
   private static int CASE_DEFAULT_EXTENSION_SET = 0x40;
   private static int CASE_EXTENSION_SET_TEST    = 0x80;
   //@formatter:on

   /**
    * The main content attribute to set with a value for test cases with {@link #CASE_CONTENT_VALUE_SET}. The map
    * contains an entry for each {@link ArtifactTypeToken} that has a test. Only the entries where the artifact type has
    * a test case with {@link #CASE_CONTENT_VALUE_SET} have a value set.
    *
    * @implNote <code>null</code> map entry values are not allowed, so those lines below are committed out.
    */

   //@formatter:off
   private static Map<ArtifactTypeToken,AttributeTypeToken> contentAttribute =
      Map.ofEntries
         (
          //Map.entry( AtsArtifactTypes.Action,                        null                                   ),
          //Map.entry( CoreArtifactTypes.AbstractHeading,              null                                   ),
          //Map.entry( CoreArtifactTypes.Folder,                       null                                   ),
            Map.entry( CoreArtifactTypes.GeneralData,                  CoreAttributeTypes.GeneralStringData   ),
            Map.entry( CoreArtifactTypes.HeadingHtml,                  CoreAttributeTypes.HtmlContent         ),
            Map.entry( CoreArtifactTypes.HeadingMarkdown,              CoreAttributeTypes.MarkdownContent     ),
            Map.entry( CoreArtifactTypes.HeadingMsWord,                CoreAttributeTypes.WordTemplateContent ),
            Map.entry( CoreArtifactTypes.HtmlArtifact,                 CoreAttributeTypes.HtmlContent         ),
          //Map.entry( CoreArtifactTypes.InterfaceArtifact,            null                                   ),
            Map.entry( CoreArtifactTypes.NativeArtifact,               CoreAttributeTypes.NativeContent       ),
            Map.entry( CoreArtifactTypes.Markdown,                     CoreAttributeTypes.MarkdownContent     ),
            Map.entry( CoreArtifactTypes.MsWordWholeDocument,          CoreAttributeTypes.WholeWordContent    ),
            Map.entry( CoreArtifactTypes.MsWordTemplate,               CoreAttributeTypes.WordTemplateContent ),
          //Map.entry( CoreArtifactTypes.Parameter,                    null                                   ),
            Map.entry( CoreArtifactTypes.PlainText,                    CoreAttributeTypes.PlainTextContent    ),
          //Map.entry( CoreArtifactTypes.TestCase,                     null                                   ),
            Map.entry( CoreArtifactTypes.TestProcedureNative,          CoreAttributeTypes.NativeContent       ),
            Map.entry( CoreArtifactTypes.TestProcedureWholeWord,       CoreAttributeTypes.WholeWordContent    )
          //Map.entry( CoreArtifactTypes.TestUnit,                     null                                   ),
          //Map.entry( CoreArtifactTypes.Url,                          null                                   ),
          //Map.entry( CoreArtifactTypes.User,                         null                                   )
         );
   //@formatter:on

   //@formatter:off
   private static Map<AttributeTypeToken,Object> attributeContent =
      Map.of
         (
            CoreAttributeTypes.GeneralStringData,      "Hello",
            CoreAttributeTypes.HtmlContent,            "Hello",
            CoreAttributeTypes.NativeContent,          new ByteArrayInputStream( "Hello".getBytes() ),
            CoreAttributeTypes.MarkdownContent,        "Hello",
            CoreAttributeTypes.WholeWordContent,       "Hello",
            CoreAttributeTypes.WordTemplateContent,    "Hello",
            CoreAttributeTypes.PlainTextContent,       "Hello"
         );
   //@formatter:on

   /**
    * The file extension to set the artifact with for test cases with {@link #CASE_DEFAULT_EXTENSION_SET}. The map
    * contains an entry for each {@link ArtifactTypeToken} that has a test. Only the entries where the artifact type has
    * a test case with {@link #CASE_DEFAULT_EXTENSION_SET} have a value set.
    */

   //@formatter:off
   private static Map<ArtifactTypeToken,String> defaultExtension =
      Map.ofEntries
         (
            Map.entry( AtsArtifactTypes.Action,                        Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.AbstractHeading,              Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Folder,                       Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.GeneralData,                  Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingHtml,                  Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingMarkdown,              Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingMsWord,                Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HtmlArtifact,                 Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.InterfaceArtifact,            Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.NativeArtifact,               "xml"                ),
            Map.entry( CoreArtifactTypes.Markdown,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.MsWordWholeDocument,          Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.MsWordTemplate,               Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Parameter,                    Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.PlainText,                    Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestCase,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestProcedureNative,          "xml"                ),
            Map.entry( CoreArtifactTypes.TestProcedureWholeWord,       Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestUnit,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Url,                          Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.User,                         Strings.EMPTY_STRING )
         );
   //@formatter:on

   /**
    * The file extension to set the artifact with for test cases with {@link #CASE_EXTENSION_SET}. The map contains an
    * entry for each {@link ArtifactTypeToken} that has a test. Only the entries where the artifact type has a test case
    * with {@link #CASE_EXTENSION_SET} have a value set.
    */

   //@formatter:off
   private static Map<ArtifactTypeToken,String> testExtension =
      Map.ofEntries
         (
            Map.entry( AtsArtifactTypes.Action,                        Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.AbstractHeading,              Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Folder,                       Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.GeneralData,                  Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingHtml,                  Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingMarkdown,              Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HeadingMsWord,                Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.HtmlArtifact,                 Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.InterfaceArtifact,            Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.NativeArtifact,               "doc"                ),
            Map.entry( CoreArtifactTypes.Markdown,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.MsWordWholeDocument,          Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.MsWordTemplate,               Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Parameter,                    Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.PlainText,                    Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestCase,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestProcedureNative,          "doc"                ),
            Map.entry( CoreArtifactTypes.TestProcedureWholeWord,       Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.TestUnit,                     Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.Url,                          Strings.EMPTY_STRING ),
            Map.entry( CoreArtifactTypes.User,                         Strings.EMPTY_STRING )
         );
   //@formatter:on

   @Test
   public void testGetBestRenderer() {

      var artifactIndex = (Objects.isNull(this.artifactIndex) ? 0 : this.artifactIndex);

      Artifact artifact = null;
      EnumRendererMap rendererOptions = new EnumRendererMap();

      artifact = new Artifact(CoreBranches.COMMON, artifactType, this.subTestName);

      if ((artifactIndex & CASE_OPEN_IN_MS_WORD) > 0) {
         rendererOptions.setRendererOption(RendererOption.OPEN_OPTION, RendererOption.OPEN_IN_MS_WORD_VALUE.getKey());
      }

      if ((artifactIndex & CASE_OPEN_IN_MARKDOWN) > 0) {
         rendererOptions.setRendererOption(RendererOption.OPEN_OPTION,
            RendererOption.OPEN_IN_MARKDOWN_EDITOR_VALUE.getKey());
      }

      if ((artifactIndex & CASE_CONTENT_VALUE_SET) > 0) {
         var contentAttributeType = RendererManagerTest.contentAttribute.get(this.artifactType);
         if (contentAttributeType != null) {
            var contentValue = RendererManagerTest.attributeContent.get(contentAttributeType);
            artifact.setAttributeFromValues(contentAttributeType, List.of(contentValue));
         }
      }

      if ((artifactIndex & CASE_DEFAULT_EXTENSION_SET) > 0) {
         var defaultExtension = RendererManagerTest.defaultExtension.get(artifactType);
         if (defaultExtension != null) {
            artifact.setAttributeFromValues(CoreAttributeTypes.Extension, List.of(defaultExtension));
         }
      }

      if ((artifactIndex & CASE_EXTENSION_SET_TEST) > 0) {
         var testExtension = RendererManagerTest.testExtension.get(artifactType);
         if (Strings.isValidAndNonBlank(testExtension)) {
            artifact.setAttributeFromValues(CoreAttributeTypes.Extension, List.of(testExtension));
         }
      }

      if (defaultOption == Both) {
         testGetBestRendererWithOption(artifact, rendererOptions, On);
         testGetBestRendererWithOption(artifact, rendererOptions, Off);
      } else {
         testGetBestRendererWithOption(artifact, rendererOptions, defaultOption);
      }
   }

   private void testGetBestRendererWithOption(Artifact artifact, RendererMap rendererOptions, DefaultOption option) {
      RendererManager.setDefaultArtifactEditor(option == On);

      if (clazz == null) {
         try {
            IRenderer renderer = computeRenderer(artifact, rendererOptions);
            String message = String.format(
               "Expected an OseeStateException to be thrown since no render should be applicable in this case.\nRenderer: [%s]",
               renderer);
            Assert.fail(message);
         } catch (OseeStateException ex) {
            Assert.assertEquals(String.format("No renderer configured for %s of %s", presentationType, artifact),
               ex.getMessage());
         }
      } else {
         IRenderer renderer = computeRenderer(artifact, rendererOptions);
         Assert.assertEquals(clazz, renderer.getClass());
      }
   }

   private IRenderer computeRenderer(Artifact artifact, RendererMap rendererOptions) {
      IRenderer renderer = RendererManager.getBestRenderer(presentationType, artifact, rendererOptions);
      Assert.assertNotNull(renderer);
      return renderer;
   }

   @Parameters(name = "{index}: {0}")
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();

      //@formatter:off

      addTest( data, Action,                       DEFAULT_OPEN,                  AtsWfeRenderer.class,               Off , null );
      addTest( data, Action,                       DEFAULT_OPEN,                  AtsWfeRenderer.class,               On  , null );
      addTest( data, Action,                       DIFF,                          AtsWfeRenderer.class,               Both, 0    );
      addTest( data, Action,                       DIFF,                          AtsWfeRenderer.class,               Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, Action,                       DIFF,                          AtsWfeRenderer.class,               Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, Action,                       F5_DIFF,                       AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       GENERAL_REQUESTED,             AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Action,                       MERGE,                         AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       PREVIEW,                       AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Action,                       RENDER_AS_HUMAN_READABLE_TEXT, AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       SPECIALIZED_EDIT,              AtsWfeRenderer.class,               Both, null );
      addTest( data, Action,                       WEB_PREVIEW,                   AtsWfeRenderer.class,               Both, null );

      addTest( data, AbstractHeading,              DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, AbstractHeading,              DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, AbstractHeading,              DIFF,                          WordTemplateRenderer.class,         Both, null );
      addTest( data, AbstractHeading,              DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, AbstractHeading,              DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, AbstractHeading,              F5_DIFF,                       null,                               Both, null );
      addTest( data, AbstractHeading,              GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, AbstractHeading,              GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, AbstractHeading,              MERGE,                         null,                               Both, null );
      addTest( data, AbstractHeading,              PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, AbstractHeading,              PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, AbstractHeading,              RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, AbstractHeading,              SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, AbstractHeading,              WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, Folder,                       DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, Folder,                       DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, Folder,                       DIFF,                          WordTemplateRenderer.class,         Both, null );
      addTest( data, Folder,                       DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, Folder,                       DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, Folder,                       F5_DIFF,                       null,                               Both, null );
      addTest( data, Folder,                       GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Folder,                       GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Folder,                       MERGE,                         null,                               Both, null );
      addTest( data, Folder,                       PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, Folder,                       PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Folder,                       RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Folder,                       SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Folder,                       WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, GeneralData,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , 0 );
      addTest( data, GeneralData,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , CASE_CONTENT_VALUE_SET );
      addTest( data, GeneralData,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, GeneralData,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, GeneralData,                  DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, GeneralData,                  DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, GeneralData,                  DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, GeneralData,                  F5_DIFF,                       null,                               Both, null );
      addTest( data, GeneralData,                  GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, GeneralData,                  GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, GeneralData,                  MERGE,                         null,                               Both, null );
      addTest( data, GeneralData,                  PREVIEW,                       WordTemplateRenderer.class,         Both, 0 );
      addTest( data, GeneralData,                  PREVIEW,                       WordTemplateRenderer.class,         Both, CASE_CONTENT_VALUE_SET );
      addTest( data, GeneralData,                  PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, GeneralData,                  RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, GeneralData,                  SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, GeneralData,                  WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, HeadingHtml,                  DEFAULT_OPEN,                  HTMLRenderer.class,                 Off , 0 );
      addTest( data, HeadingHtml,                  DEFAULT_OPEN,                  HTMLRenderer.class,                 Off , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingHtml,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, HeadingHtml,                  DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingHtml,                  DIFF,                          HTMLRenderer.class,                 Both, 0    );
      addTest( data, HeadingHtml,                  DIFF,                          HTMLRenderer.class,                 Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, HeadingHtml,                  DIFF,                          HTMLRenderer.class,                 Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, HeadingHtml,                  F5_DIFF,                       HTMLRenderer.class,                 Both, null );
      addTest( data, HeadingHtml,                  GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingHtml,                  GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingHtml,                  MERGE,                         HTMLRenderer.class,                 Both, null );
      addTest( data, HeadingHtml,                  PREVIEW,                       HTMLRenderer.class,                 Both, 0 );
      addTest( data, HeadingHtml,                  PREVIEW,                       HTMLRenderer.class,                 Both, CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingHtml,                  PRODUCE_ATTRIBUTE,             HTMLRenderer.class,                 Both, null );
      addTest( data, HeadingHtml,                  RENDER_AS_HUMAN_READABLE_TEXT, HTMLRenderer.class,                 Both, null );
      addTest( data, HeadingHtml,                  SPECIALIZED_EDIT,              HTMLRenderer.class,                 Both, null );
      addTest( data, HeadingHtml,                  WEB_PREVIEW,                   HTMLRenderer.class,                 Both, null );

      addTest( data, HeadingMarkdown,              DEFAULT_OPEN,                  MarkdownRenderer.class,             Off , 0 );
      addTest( data, HeadingMarkdown,              DEFAULT_OPEN,                  MarkdownRenderer.class,             Off , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMarkdown,              DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, HeadingMarkdown,              DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMarkdown,              DIFF,                          MarkdownRenderer.class,             Both, 0    );
      addTest( data, HeadingMarkdown,              DIFF,                          MarkdownRenderer.class,             Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, HeadingMarkdown,              DIFF,                          MarkdownRenderer.class,             Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, HeadingMarkdown,              F5_DIFF,                       MarkdownRenderer.class,             Both, null );
      addTest( data, HeadingMarkdown,              GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingMarkdown,              GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingMarkdown,              MERGE,                         MarkdownRenderer.class,             Both, null );
      addTest( data, HeadingMarkdown,              PREVIEW,                       MarkdownRenderer.class,             Both, 0 );
      addTest( data, HeadingMarkdown,              PREVIEW,                       MarkdownRenderer.class,             Both, CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMarkdown,              PRODUCE_ATTRIBUTE,             MarkdownRenderer.class,             Both, null );
      addTest( data, HeadingMarkdown,              RENDER_AS_HUMAN_READABLE_TEXT, MarkdownRenderer.class,             Both, null );
      addTest( data, HeadingMarkdown,              SPECIALIZED_EDIT,              MarkdownRenderer.class,             Both, null );
      addTest( data, HeadingMarkdown,              WEB_PREVIEW,                   MarkdownRenderer.class,             Both, null );

      addTest( data, HeadingMsWord,                DEFAULT_OPEN,                  WordTemplateRenderer.class,         Off , 0 );
      addTest( data, HeadingMsWord,                DEFAULT_OPEN,                  WordTemplateRenderer.class,         Off , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMsWord,                DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, HeadingMsWord,                DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMsWord,                DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, HeadingMsWord,                DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, HeadingMsWord,                DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, HeadingMsWord,                F5_DIFF,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, HeadingMsWord,                GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingMsWord,                GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HeadingMsWord,                MERGE,                         WordTemplateRenderer.class,         Both, null );
      addTest( data, HeadingMsWord,                PREVIEW,                       WordTemplateRenderer.class,         Both, 0 );
      addTest( data, HeadingMsWord,                PREVIEW,                       WordTemplateRenderer.class,         Both, CASE_CONTENT_VALUE_SET );
      addTest( data, HeadingMsWord,                PRODUCE_ATTRIBUTE,             WordTemplateRenderer.class,         Both, null );
      addTest( data, HeadingMsWord,                RENDER_AS_HUMAN_READABLE_TEXT, WordTemplateRenderer.class,         Both, null );
      addTest( data, HeadingMsWord,                SPECIALIZED_EDIT,              WordTemplateRenderer.class,         Both, null );
      addTest( data, HeadingMsWord,                WEB_PREVIEW,                   WordTemplateRenderer.class,         Both, null );

      addTest( data, HtmlArtifact,                 DEFAULT_OPEN,                  HTMLRenderer.class,                 Off , 0 );
      addTest( data, HtmlArtifact,                 DEFAULT_OPEN,                  HTMLRenderer.class,                 Off , CASE_CONTENT_VALUE_SET );
      addTest( data, HtmlArtifact,                 DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, HtmlArtifact,                 DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, HtmlArtifact,                 DIFF,                          HTMLRenderer.class,                 Both, 0    );
      addTest( data, HtmlArtifact,                 DIFF,                          HTMLRenderer.class,                 Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, HtmlArtifact,                 DIFF,                          HTMLRenderer.class,                 Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, HtmlArtifact,                 F5_DIFF,                       HTMLRenderer.class,                 Both, null );
      addTest( data, HtmlArtifact,                 GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HtmlArtifact,                 GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, HtmlArtifact,                 MERGE,                         HTMLRenderer.class,                 Both, null );
      addTest( data, HtmlArtifact,                 PREVIEW,                       HTMLRenderer.class,                 Both, 0 );
      addTest( data, HtmlArtifact,                 PREVIEW,                       HTMLRenderer.class,                 Both, CASE_CONTENT_VALUE_SET );
      addTest( data, HtmlArtifact,                 PRODUCE_ATTRIBUTE,             HTMLRenderer.class,                 Both, null );
      addTest( data, HtmlArtifact,                 RENDER_AS_HUMAN_READABLE_TEXT, HTMLRenderer.class,                 Both, null );
      addTest( data, HtmlArtifact,                 SPECIALIZED_EDIT,              HTMLRenderer.class,                 Both, null );
      addTest( data, HtmlArtifact,                 WEB_PREVIEW,                   HTMLRenderer.class,                 Both, null );

      addTest( data, InterfaceArtifact,            DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, InterfaceArtifact,            DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, InterfaceArtifact,            DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, InterfaceArtifact,            DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, InterfaceArtifact,            DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, InterfaceArtifact,            F5_DIFF,                       null,                               Both, null );
      addTest( data, InterfaceArtifact,            GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, InterfaceArtifact,            GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, InterfaceArtifact,            MERGE,                         null,                               Both, null );
      addTest( data, InterfaceArtifact,            PREVIEW,                       WordTemplateRenderer.class,         Both, 0 );
      addTest( data, InterfaceArtifact,            PREVIEW,                       WordTemplateRenderer.class,         Both, CASE_CONTENT_VALUE_SET );
      addTest( data, InterfaceArtifact,            PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, InterfaceArtifact,            RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, InterfaceArtifact,            SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, InterfaceArtifact,            WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, NativeArtifact,               DEFAULT_OPEN,                  NativeRenderer.class,               Off , 0 );
      addTest( data, NativeArtifact,               DEFAULT_OPEN,                  NativeRenderer.class,               Off , CASE_CONTENT_VALUE_SET );
      addTest( data, NativeArtifact,               DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, NativeArtifact,               DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, NativeArtifact,               DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, NativeArtifact,               DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, NativeArtifact,               DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, NativeArtifact,               DIFF,                          WordTemplateRenderer.class,         Both, CASE_DEFAULT_EXTENSION_SET );
      addTest( data, NativeArtifact,               DIFF,                          NativeRenderer.class,               Both, CASE_EXTENSION_SET_TEST );
      addTest( data, NativeArtifact,               F5_DIFF,                       null,                               Both, null );
      addTest( data, NativeArtifact,               GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, NativeArtifact,               GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, NativeArtifact,               MERGE,                         null,                               Both, null );
      addTest( data, NativeArtifact,               PREVIEW,                       NativeRenderer.class,               Both, 0 );
      addTest( data, NativeArtifact,               PREVIEW,                       NativeRenderer.class,               Both, CASE_CONTENT_VALUE_SET );
      addTest( data, NativeArtifact,               PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, NativeArtifact,               RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, NativeArtifact,               SPECIALIZED_EDIT,              NativeRenderer.class,               Both, null );
      addTest( data, NativeArtifact,               WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, Markdown,                     DEFAULT_OPEN,                  MarkdownRenderer.class,             Off , 0 );
      addTest( data, Markdown,                     DEFAULT_OPEN,                  MarkdownRenderer.class,             Off , CASE_CONTENT_VALUE_SET );
      addTest( data, Markdown,                     DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, Markdown,                     DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, Markdown,                     DIFF,                          MarkdownRenderer.class,             Both, 0    );
      addTest( data, Markdown,                     DIFF,                          MarkdownRenderer.class,             Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, Markdown,                     DIFF,                          MarkdownRenderer.class,             Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, Markdown,                     F5_DIFF,                       MarkdownRenderer.class,             Both, null );
      addTest( data, Markdown,                     GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Markdown,                     GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Markdown,                     MERGE,                         MarkdownRenderer.class,             Both, null );
      addTest( data, Markdown,                     PREVIEW,                       MarkdownRenderer.class,             Both, 0 );
      addTest( data, Markdown,                     PREVIEW,                       MarkdownRenderer.class,             Both, CASE_CONTENT_VALUE_SET );
      addTest( data, Markdown,                     PRODUCE_ATTRIBUTE,             MarkdownRenderer.class,             Both, null );
      addTest( data, Markdown,                     RENDER_AS_HUMAN_READABLE_TEXT, MarkdownRenderer.class,             Both, null );
      addTest( data, Markdown,                     SPECIALIZED_EDIT,              MarkdownRenderer.class,             Both, null );
      addTest( data, Markdown,                     WEB_PREVIEW,                   MarkdownRenderer.class,             Both, null );

      addTest( data, MsWordWholeDocument,          DEFAULT_OPEN,                  WholeWordRenderer.class,            Off , 0 );
      addTest( data, MsWordWholeDocument,          DEFAULT_OPEN,                  WholeWordRenderer.class,            Off , CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordWholeDocument,          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, MsWordWholeDocument,          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordWholeDocument,          DIFF,                          WholeWordRenderer.class,            Both, null );
      addTest( data, MsWordWholeDocument,          DIFF,                          WholeWordRenderer.class,            Both, 0    );
      addTest( data, MsWordWholeDocument,          DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, MsWordWholeDocument,          F5_DIFF,                       WholeWordRenderer.class,            Both, null );
      addTest( data, MsWordWholeDocument,          GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, MsWordWholeDocument,          GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, MsWordWholeDocument,          MERGE,                         WholeWordRenderer.class,            Both, null );
      addTest( data, MsWordWholeDocument,          PREVIEW,                       WholeWordRenderer.class,            Both, 0 );
      addTest( data, MsWordWholeDocument,          PREVIEW,                       WholeWordRenderer.class,            Both, CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordWholeDocument,          PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, MsWordWholeDocument,          RENDER_AS_HUMAN_READABLE_TEXT, WholeWordRenderer.class,            Both, null );
      addTest( data, MsWordWholeDocument,          SPECIALIZED_EDIT,              WholeWordRenderer.class,            Both, null );
      addTest( data, MsWordWholeDocument,          WEB_PREVIEW,                   WholeWordRenderer.class,            Both, null );

      addTest( data, MsWordTemplate,               DEFAULT_OPEN,                  WordTemplateRenderer.class,         Off , 0 );
      addTest( data, MsWordTemplate,               DEFAULT_OPEN,                  WordTemplateRenderer.class,         Off , CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordTemplate,               DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, MsWordTemplate,               DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordTemplate,               DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, MsWordTemplate,               DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, MsWordTemplate,               DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, MsWordTemplate,               F5_DIFF,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, MsWordTemplate,               GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, MsWordTemplate,               GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, MsWordTemplate,               MERGE,                         WordTemplateRenderer.class,         Both, null );
      addTest( data, MsWordTemplate,               PREVIEW,                       WordTemplateRenderer.class,         Both, 0 );
      addTest( data, MsWordTemplate,               PREVIEW,                       WordTemplateRenderer.class,         Both, CASE_CONTENT_VALUE_SET );
      addTest( data, MsWordTemplate,               PRODUCE_ATTRIBUTE,             WordTemplateRenderer.class,         Both, null );
      addTest( data, MsWordTemplate,               RENDER_AS_HUMAN_READABLE_TEXT, WordTemplateRenderer.class,         Both, null );
      addTest( data, MsWordTemplate,               SPECIALIZED_EDIT,              WordTemplateRenderer.class,         Both, null );
      addTest( data, MsWordTemplate,               WEB_PREVIEW,                   WordTemplateRenderer.class,         Both, null );

      addTest( data, Parameter,                    DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, Parameter,                    DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, Parameter,                    DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, Parameter,                    DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, Parameter,                    DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, Parameter,                    F5_DIFF,                       null,                               Both, null );
      addTest( data, Parameter,                    GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Parameter,                    GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Parameter,                    MERGE,                         null,                               Both, null );
      addTest( data, Parameter,                    PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, Parameter,                    PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Parameter,                    RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Parameter,                    SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Parameter,                    WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, PlainText,                    DEFAULT_OPEN,                  PlainTextRenderer.class,            Off , 0 );
      addTest( data, PlainText,                    DEFAULT_OPEN,                  PlainTextRenderer.class,            Off , CASE_CONTENT_VALUE_SET );
      addTest( data, PlainText,                    DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, PlainText,                    DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, PlainText,                    DIFF,                          PlainTextRenderer.class,            Both, 0    );
      addTest( data, PlainText,                    DIFF,                          PlainTextRenderer.class,            Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, PlainText,                    DIFF,                          PlainTextRenderer.class,            Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, PlainText,                    F5_DIFF,                       PlainTextRenderer.class,            Both, null );
      addTest( data, PlainText,                    GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, PlainText,                    GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, PlainText,                    MERGE,                         PlainTextRenderer.class,            Both, null );
      addTest( data, PlainText,                    PREVIEW,                       PlainTextRenderer.class,            Both, 0 );
      addTest( data, PlainText,                    PREVIEW,                       PlainTextRenderer.class,            Both, CASE_CONTENT_VALUE_SET );
      addTest( data, PlainText,                    PRODUCE_ATTRIBUTE,             PlainTextRenderer.class,            Both, null );
      addTest( data, PlainText,                    RENDER_AS_HUMAN_READABLE_TEXT, PlainTextRenderer.class,            Both, null );
      addTest( data, PlainText,                    SPECIALIZED_EDIT,              PlainTextRenderer.class,            Both, null );
      addTest( data, PlainText,                    WEB_PREVIEW,                   PlainTextRenderer.class,            Both, null );

      addTest( data, TestCase,                     DEFAULT_OPEN,                  JavaRenderer.class,                 Off , null );
      addTest( data, TestCase,                     DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, TestCase,                     DIFF,                          WordTemplateRenderer.class,         Off , 0    );
      addTest( data, TestCase,                     DIFF,                          WordTemplateRenderer.class,         Off , CASE_OPEN_IN_MS_WORD    );
      addTest( data, TestCase,                     DIFF,                          WordTemplateRenderer.class,         Off , CASE_OPEN_IN_MARKDOWN    );
      addTest( data, TestCase,                     F5_DIFF,                       null,                               Off , null );
      addTest( data, TestCase,                     GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestCase,                     GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestCase,                     MERGE,                         null,                               Both, null );
      addTest( data, TestCase,                     PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, TestCase,                     PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestCase,                     RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestCase,                     SPECIALIZED_EDIT,              JavaRenderer.class,                 Both, null );
      addTest( data, TestCase,                     WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, TestProcedureNative,          DEFAULT_OPEN,                  NativeRenderer.class,               Off , 0 );
      addTest( data, TestProcedureNative,          DEFAULT_OPEN,                  NativeRenderer.class,               Off , CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureNative,          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, TestProcedureNative,          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureNative,          DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, TestProcedureNative,          DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, TestProcedureNative,          DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, TestProcedureNative,          DIFF,                          WordTemplateRenderer.class,         Both, CASE_DEFAULT_EXTENSION_SET );
      addTest( data, TestProcedureNative,          DIFF,                          NativeRenderer.class,               Both, CASE_EXTENSION_SET_TEST );
      addTest( data, TestProcedureNative,          F5_DIFF,                       null,                               Both, null );
      addTest( data, TestProcedureNative,          GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureNative,          GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureNative,          MERGE,                         null,                               Both, null );
      addTest( data, TestProcedureNative,          PREVIEW,                       NativeRenderer.class,               Both, 0 );
      addTest( data, TestProcedureNative,          PREVIEW,                       NativeRenderer.class,               Both, CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureNative,          PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureNative,          RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureNative,          SPECIALIZED_EDIT,              NativeRenderer.class,               Both, null );
      addTest( data, TestProcedureNative,          WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, TestProcedureWholeWord,       DEFAULT_OPEN,                  WholeWordRenderer.class,            Off , 0 );
      addTest( data, TestProcedureWholeWord,       DEFAULT_OPEN,                  WholeWordRenderer.class,            Off , CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureWholeWord,       DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , 0 );
      addTest( data, TestProcedureWholeWord,       DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureWholeWord,       DIFF,                          WholeWordRenderer.class,            Both, 0    );
      addTest( data, TestProcedureWholeWord,       DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, TestProcedureWholeWord,       DIFF,                          WholeWordRenderer.class,            Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, TestProcedureWholeWord,       F5_DIFF,                       WholeWordRenderer.class,            Both, null );
      addTest( data, TestProcedureWholeWord,       GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureWholeWord,       GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureWholeWord,       MERGE,                         WholeWordRenderer.class,            Both, null );
      addTest( data, TestProcedureWholeWord,       PREVIEW,                       WholeWordRenderer.class,            Both, 0 );
      addTest( data, TestProcedureWholeWord,       PREVIEW,                       WholeWordRenderer.class,            Both, CASE_CONTENT_VALUE_SET );
      addTest( data, TestProcedureWholeWord,       PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestProcedureWholeWord,       RENDER_AS_HUMAN_READABLE_TEXT, WholeWordRenderer.class,            Both, null );
      addTest( data, TestProcedureWholeWord,       SPECIALIZED_EDIT,              WholeWordRenderer.class,            Both, null );
      addTest( data, TestProcedureWholeWord,       WEB_PREVIEW,                   WholeWordRenderer.class,            Both, null );

      addTest( data, TestUnit,                     DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, TestUnit,                     DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, TestUnit,                     DIFF,                          WordTemplateRenderer.class,         Off , 0    );
      addTest( data, TestUnit,                     DIFF,                          WordTemplateRenderer.class,         Off , CASE_OPEN_IN_MS_WORD    );
      addTest( data, TestUnit,                     DIFF,                          WordTemplateRenderer.class,         Off , CASE_OPEN_IN_MARKDOWN    );
      addTest( data, TestUnit,                     F5_DIFF,                       null,                               Off , null );
      addTest( data, TestUnit,                     GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestUnit,                     GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestUnit,                     MERGE,                         null,                               Both, null );
      addTest( data, TestUnit,                     PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, TestUnit,                     PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, TestUnit,                     PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestUnit,                     RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestUnit,                     SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, TestUnit,                     WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, Url,                          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, Url,                          DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, Url,                          DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, Url,                          DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, Url,                          DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, Url,                          F5_DIFF,                       null,                               Both, null );
      addTest( data, Url,                          GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Url,                          GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Url,                          MERGE,                         null,                               Both, null );
      addTest( data, Url,                          PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, Url,                          PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Url,                          RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Url,                          SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, Url,                          WEB_PREVIEW,                   null,                               Both, null );

      addTest( data, User,                         DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      Off , null );
      addTest( data, User,                         DEFAULT_OPEN,                  DefaultArtifactRenderer.class,      On  , null );
      addTest( data, User,                         DIFF,                          WordTemplateRenderer.class,         Both, 0    );
      addTest( data, User,                         DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MS_WORD    );
      addTest( data, User,                         DIFF,                          WordTemplateRenderer.class,         Both, CASE_OPEN_IN_MARKDOWN    );
      addTest( data, User,                         F5_DIFF,                       null,                               Both, null );
      addTest( data, User,                         GENERAL_REQUESTED,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, User,                         GENERALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, User,                         MERGE,                         null,                               Both, null );
      addTest( data, User,                         PREVIEW,                       WordTemplateRenderer.class,         Both, null );
      addTest( data, User,                         PRODUCE_ATTRIBUTE,             DefaultArtifactRenderer.class,      Both, null );
      addTest( data, User,                         RENDER_AS_HUMAN_READABLE_TEXT, DefaultArtifactRenderer.class,      Both, null );
      addTest( data, User,                         SPECIALIZED_EDIT,              DefaultArtifactRenderer.class,      Both, null );
      addTest( data, User,                         WEB_PREVIEW,                   null,                               Both, null );

      //@formatter:on
      return data;
   }

   private static void addTest(Collection<Object[]> data, Object... params) {
      //@formatter:off
      var subTestName =
           ((ArtifactTypeToken) params[0]).getName() + ":"
         + ((PresentationType)  params[1]).name() + ":"
         + (Objects.nonNull( params[2] ) ? ((Class<?>) params[2]).getSimpleName() : "(null-class)" ) + ":"
         + ((DefaultOption) params[3]).name() + ":"
         + (Objects.nonNull( params[4]) ? ((Integer) params[4]).toString() : "-1" );
      //@formatter:on

      var testParams = new Object[params.length + 1];
      testParams[0] = subTestName;
      System.arraycopy(params, 0, testParams, 1, params.length);
      data.add(testParams);
   }
}
