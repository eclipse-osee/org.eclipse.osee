/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTemplateSetterImpl;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class PublishingIncludeFoldersTest {

   /**
    * Set this flag to <code>true</code> to print the received Word ML documents to <code>stdout</code>.
    */

   private static boolean printDocuments = false;

   /**
    * Set this flag to <code>false</code> to prevent the test setup code from altering attribute values in the database.
    * The default (normal for testing) value is <code>true</code>.
    */

   private static boolean setValues = true;

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
    * <dt>NoPopUpsRule</dt>
    * <dd>Prevents word documents from being launched for the user during tests.</dd>
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

   /*
    * Test level rules are applied before each test.
    */

   @Rule
   public TestInfo testInfo = new TestInfo();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Parameters(name = "{0}")
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.<Object[]>of
            (
               (Object[]) new RenderLocation[] { RenderLocation.CLIENT },
               (Object[]) new RenderLocation[] { RenderLocation.SERVER }
            );
      //@formatter:on
   }

   /**
    * Defines the publishing templates for the tests. The templates will be created on the Common Branch under the "OSEE
    * Configuration/Document Templates" folder.
    */

   //@formatter:off
   private static Supplier<List<PublishingTemplate>> publishingTemplatesSupplier = new Supplier<> () {

      @Override
      public List<PublishingTemplate> get() {
         return

      List.of
         (
            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      PublishingIncludeFoldersTest.class.getSimpleName(),                                    /* Name                       */
                      new PublishingTemplate.StringSupplier                                                  /* Publish Options Supplier   */
                             (
                                new StringBuilder( 1024 )
                                       .append( "{"                                                                  ).append( "\n" )
                                       .append( "   \"ElementType\":                \"Artifact\","                   ).append( "\n" )
                                       .append( "   \"OutliningOptions\":"                                           ).append( "\n" )
                                       .append( "      [ {"                                                          ).append( "\n" )
                                       .append( "         \"HeadingAttributeType\": \"Name\","                       ).append( "\n" )
                                       .append( "         \"IncludeEmptyHeaders\":  false,"                          ).append( "\n" )
                                       .append( "         \"OutlineNumber\":        \"\","                           ).append( "\n" )
                                       .append( "         \"Outlining\":            true,"                           ).append( "\n" )
                                       .append( "         \"RecurseChildren\":      true"                            ).append( "\n" )
                                       .append( "      } ],"                                                         ).append( "\n" )
                                       .append( "   \"AttributeOptions\":"                                           ).append( "\n" )
                                       .append( "      [ {"                                                          ).append( "\n" )
                                       .append( "         \"AttrType\":             \"<format-content-attribute>\"," ).append( "\n" )
                                       .append( "         \"Label\":                \"\","                           ).append( "\n" )
                                       .append( "         \"FormatPre\":            \"\","                           ).append( "\n" )
                                       .append( "         \"FormatPost\":           \"\""                            ).append( "\n" )
                                       .append( "      } ],"                                                         ).append( "\n" )
                                       .append( "   \"MetadataOptions\":"                                            ).append( "\n" )
                                       .append( "      ["                                                            ).append( "\n" )
                                       .append( "        {"                                                          ).append( "\n" )
                                       .append( "          \"Type\":                \"Artifact Type\","              ).append( "\n" )
                                       .append( "          \"Format\":              \"\","                           ).append( "\n" )
                                       .append( "          \"Label\":               \"\""                            ).append( "\n" )
                                       .append( "        },"                                                         ).append( "\n" )
                                       .append( "        {"                                                          ).append( "\n" )
                                       .append( "          \"Type\":                \"Artifact Id\","                ).append( "\n" )
                                       .append( "          \"Format\":              \"\","                           ).append( "\n" )
                                       .append( "          \"Label\":               \"\""                            ).append( "\n" )
                                       .append( "        }"                                                          ).append( "\n" )
                                       .append( "      ]"                                                            ).append( "\n" )
                                       .append( "}"                                                                  ).append( "\n" )
                                       .toString()
                             ),
                      new PublishingTemplate.StringSupplier                                                  /* Template Content Supplier  */
                             (
                                new StringBuilder( 1024 )
                                       .append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" ).append( "\n" )
                                       .append( "<?mso-application progid=\"Word.Document\"?>"                  ).append( "\n" )
                                       .append( "<w:wordDocument w:embeddedObjPresent=\"no\" w:macrosPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\" xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:ns0=\"http://www.w3.org/2001/XMLSchema\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\">" ).append( "\n" )
                                       .append( "  <w:ignoreElements w:val=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\"/>" ).append( "\n" )
                                       .append( "  <w:body>"                                                    ).append( "\n" )
                                       .append( "     <wx:sect>"                                                ).append( "\n" )
                                       .append( "        INSERT_ARTIFACT_HERE"                                  ).append( "\n" )
                                       .append( "     </wx:sect>"                                               ).append( "\n" )
                                       .append( "  </w:body>"                                                   ).append( "\n" )
                                       .append( "</w:wordDocument>"                                             ).append( "\n" )
                                       .toString()
                             ),
                      List.of(),                                                                             /* Publishing Template Content Map Entries */
                      List.of()                                                                              /* Match Criteria      */
                   )

            );
      }
   };
   //@formatter:on

   private String documentString;

   private final RenderLocation renderLocation;

   public PublishingIncludeFoldersTest(RenderLocation renderLocation) {
      this.renderLocation = renderLocation;
   }

   @BeforeClass
   public static void setUp() {

      /*
       * Setup publishing templates
       */

      var relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(CoreBranches.COMMON);

      var publishingTemplateSetter = new PublishingTemplateSetterImpl(relationEndpoint);

      PublishingTemplate.load(PublishingIncludeFoldersTest.publishingTemplatesSupplier, publishingTemplateSetter::set,
         PublishingIncludeFoldersTest.class, false);

      /*
       * Clear the publishing template cache, so newly created or modified publishing template artifacts are reloaded.
       */

      PublishingTestUtil.clearTemplateManagerCache();

   }

   private void generateDocument() {

      final var excludeTokens = new ArrayList<ArtifactTypeToken>();

      excludeTokens.add(CoreArtifactTypes.ImplementationDetailsMsWord);

      //@formatter:off
      EnumRendererMap rendererOptionsMap =
         new EnumRendererMap
                (
                   RendererOption.RENDER_LOCATION,                                  renderLocation,
                   RendererOption.TEMPLATE_OPTION,                                  PublishingIncludeFoldersTest.class.getSimpleName(),
                   RendererOption.BRANCH,                                           DemoBranches.SAW_Bld_1,
                   RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES, List.of( CoreArtifactTypes.ImplementationDetailsMsWord )
                );
      //@formatter:on

      final var softReqFolder =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Software Requirements", SAW_Bld_1);

      final var artifacts = List.of(softReqFolder);

      final var contentPath = RendererManager.open(artifacts, PresentationType.PREVIEW, rendererOptionsMap);

      final var document = PublishingTestUtil.loadContent(contentPath, this.testInfo.getTestName());

      final var documentString = PublishingTestUtil.prettyPrint(document, this.testInfo.getTestName(),
         PublishingIncludeFoldersTest.printDocuments);

      this.documentString = documentString;
   }

   @Test
   public void getEmptyHeadersTest() {

      this.generateDocument();

      //@formatter:off
      final var eventDetailHeading =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "Events Detail Header",
               SAW_Bld_1
            );

      final var eventDetailHeadingArtifactIdentifier = eventDetailHeading.getIdString();

      Assert.assertFalse
         (
            "Event Detail Header was found.",
            this.documentString.contains(eventDetailHeadingArtifactIdentifier)
         );

      final var virtualFixDetailHeader =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "VirtualFixDetailHeader",
               SAW_Bld_1
            );

      final var virtualFixDetailHeaderArtifactIdentifier = virtualFixDetailHeader.getIdString();

      final var virtualFixDetailRequirements =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.SoftwareRequirementMsWord,
               "Virtual Fix Detail Requirements",
               SAW_Bld_1
            );

      final var virtualFixDetailRequirementsArtifactIdentifier = virtualFixDetailRequirements.getIdString();

      Assert.assertTrue
         (
            "VirtualFixDetailHeader was not found.",
            this.documentString.contains(virtualFixDetailHeaderArtifactIdentifier)
         );

      Assert.assertTrue
         (
            "Virtual Fix Detail Requirements was not found.",
            this.documentString.contains(virtualFixDetailRequirementsArtifactIdentifier)
         );

      final var eventImplementation =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.SoftwareRequirementMsWord,
               "Event Implementation",
               SAW_Bld_1
            );

      final var eventImplementationArtifactIdentifier = eventImplementation.getIdString();

      final var methodName =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.SoftwareRequirementMsWord,
               "Method name",
               SAW_Bld_1
            );

      final var methodNameArtifactIdentifier = methodName.getIdString();

      Assert.assertTrue
         (
            "Event Implementation was not found.",
            this.documentString.contains(eventImplementationArtifactIdentifier)
         );

      Assert.assertTrue
         (
            "Method name was not found.",
            this.documentString.contains(methodNameArtifactIdentifier)
         );

      final var robotInterfaceHeading =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "Robot Interface Heading",
               SAW_Bld_1
            );

      final var robotInterfaceHeadingArtifactIdentifier = robotInterfaceHeading.getIdString();

      Assert.assertFalse
         (
            "Robot Interface Heading was found.",
            this.documentString.contains(robotInterfaceHeadingArtifactIdentifier)
         );

      final var robotUserInterfacesHeading =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "Robot User Interfaces",
               SAW_Bld_1
            );

      final var robotUserInterfacesHeadingArtifactIdentifier = robotUserInterfacesHeading.getIdString();

      final var robotAdminUIHeading =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "Robot Admin User Interface",
               SAW_Bld_1
            );

      final var robotAdminUIHeadingArtifactIdentifier = robotAdminUIHeading.getIdString();

      final var robotUIHeading =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.HeadingMsWord,
               "Robot User Interface",
               SAW_Bld_1
            );

      final var robotUIHeadingArtifactIdentifier = robotUIHeading.getIdString();

      Assert.assertFalse
         (
            "Robot User Interface was found.",
            this.documentString.contains(robotUserInterfacesHeadingArtifactIdentifier)
         );

      Assert.assertFalse
         (
            "Robot Admin User Interface was found.",
            this.documentString.contains(robotAdminUIHeadingArtifactIdentifier)
         );

      Assert.assertFalse
         (
            "Robot User Interaface was found.",
            this.documentString.contains(robotUIHeadingArtifactIdentifier)
         );
      //@formatter:on

   }
}