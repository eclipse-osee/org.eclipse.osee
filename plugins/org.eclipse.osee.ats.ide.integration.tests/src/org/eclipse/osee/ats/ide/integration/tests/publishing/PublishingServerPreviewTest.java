/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import com.google.common.collect.Streams;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.core.xml.publishing.WordBody;
import org.eclipse.osee.framework.core.xml.publishing.WordDocument;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphList;
import org.eclipse.osee.framework.core.xml.publishing.WordSection;
import org.eclipse.osee.framework.core.xml.publishing.WordSectionList;
import org.eclipse.osee.framework.core.xml.publishing.WordSubSection;
import org.eclipse.osee.framework.core.xml.publishing.WordSubSectionList;
import org.eclipse.osee.framework.core.xml.publishing.WordTextList;
import org.eclipse.osee.framework.core.xml.publishing.WordXmlTag;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.w3c.dom.Document;

/**
 * Tests for publishing preview functions.
 *
 * @author Loren K. Ashley
 */

public class PublishingServerPreviewTest {

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
    * </dl>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
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

   /**
    * Common publishing renderer options for the test.
    */

   //@formatter:off
   private static final RendererMap commonPublishingRendererOptions =
      RendererMap.of
         (
            RendererOption.PUBLISH_IDENTIFIER, "Publishing Preview Test",
            RendererOption.EXCLUDE_FOLDERS,    false,
            RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
            RendererOption.MAX_OUTLINE_DEPTH,  9,
            RendererOption.PUBLISHING_FORMAT,  FormatIndicator.WORD_ML
         );
   //@formatter:on

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static int testBranchSpecificationRecordIdentifier = 1;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Publishing Server Preview Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for Publishing Server Preview Testing";

   /**
    * The {@link BranchSpecificationRecord} identifier for the common branch.
    */

   private static int commonBranchSpecificationRecordIdentifier = 2;

   /**
    * List of {@link BranchSpecificationRecord} implementations describing the branches for the test.
    * <p>
    * Branches are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * </ul>
    */

   //@formatter:off
   private static final List<BranchSpecificationRecord> branchSpecifications =
      List.of
         (
            new BasicBranchSpecificationRecord
                   (
                      PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier,
                      PublishingServerPreviewTest.testBranchName,
                      PublishingServerPreviewTest.testBranchCreationComment
                   ),
            new BasicBranchSpecificationRecord
                   (
                      PublishingServerPreviewTest.commonBranchSpecificationRecordIdentifier, /* BranchSpecificationRecord Identifier */
                      CoreBranches.COMMON                                                    /* BranchToken                          */
                   )
         );
   //@formatter:on

   /**
    * {@link MapList} of {@link ArtifactSpecificationRecord}s describing the test artifacts for each branch.
    * <p>
    * Artifacts are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * <li>Top level test artifact have a hierarchical parent identifier of 0.</li>
    * <li>Ensure children artifact's of a hierarchical parent artifact have unique names.</li>
    * </ul>
    */

   //@formatter:off
   private static MapList<Integer,ArtifactSpecificationRecord> artifactSpecifications =
      MapList.ofEntries
         (
            /*
             * Artifacts for the test branch.
             */

            Map.entry
               (
                  PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier,                              /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Preview Artifacts Folder",                                                       /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This folder contains artifacts for publishing preview tests." ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement A",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement A's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "<w:p><w:r><w:t>This is Requirement A's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement B",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement B's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "<w:p><w:r><w:t>This is Requirement B's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Preview Artifacts Folder With Data Rights",                                      /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This folder contains artifacts for publishing preview tests." ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                                /* Identifier                             (Integer)                               */
                                  4,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement A",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement A's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "<w:p><w:r><w:t>This is Requirement A's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                                                /* Identifier                             (Integer)                               */
                                  4,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement B",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement B's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "<w:p><w:r><w:t>This is Requirement B's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.DataRightsClassification,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Restricted Rights" ),                                    /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )
                     )
               ),

            /*
             * Artifacts for the common branch.
             */

            Map.entry
               (
                  PublishingServerPreviewTest.commonBranchSpecificationRecordIdentifier,                            /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "OSEE Configuration",                                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.Description,                                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of( "OSEE Configuration" ),                                      /* Test Attribute Values                  (List<Object>)                          */
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Document Templates",                                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.Description,                                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of( "Publishing Templates" ),                                    /* Test Attribute Values                  (List<Object>)                          */
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         *  Menu Command:              MS Word Preview (Server)
                         *  Guide Publishing Template: PreviewAll
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "SERVER_PREVIEW_TEST_A",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.RendererOptions,                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "{"                                                  ).append( "\n" )
                                                       .append( "   \"ElementType\" : \"Artifact\","                 ).append( "\n" )
                                                       .append( "   \"OutliningOptions\" :"                          ).append( "\n" )
                                                       .append( "      [ {"                                          ).append( "\n" )
                                                       .append( "         \"Outlining\" : true,"                     ).append( "\n" )
                                                       .append( "         \"RecurseChildren\" : false,"              ).append( "\n" )
                                                       .append( "         \"HeadingAttributeType\" : \"Name\","      ).append( "\n" )
                                                       .append( "         \"ArtifactName\" : \"Default\","           ).append( "\n" )
                                                       .append( "         \"OutlineNumber\" : \"\""                  ).append( "\n" )
                                                       .append( "      } ],"                                         ).append( "\n" )
                                                       .append( "   \"AttributeOptions\" :"                          ).append( "\n" )
                                                       .append( "      [ {"                                          ).append( "\n" )
                                                       .append( "         \"AttrType\" : \"*\","                     ).append( "\n" )
                                                       .append( "         \"Label\" : \"\","                         ).append( "\n" )
                                                       .append( "         \"FormatPre\" : \"\","                     ).append( "\n" )
                                                       .append( "         \"FormatPost\" : \"\""                     ).append( "\n" )
                                                       .append( "      } ]"                                          ).append( "\n" )
                                                       .append( "}"                                                  ).append( "\n" )
                                                       .toString()
                                                  ),
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter(BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.WholeWordContent,                                  /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" ).append( "\n" )
                                                       .append( "<?mso-application progid=\"Word.Document\"?>" ).append( "\n" )
                                                       .append( "<w:wordDocument w:embeddedObjPresent=\"no\" w:macrosPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\" xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:ns0=\"http://www.w3.org/2001/XMLSchema\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\">" ).append( "\n" )
                                                       .append( "  <w:ignoreElements w:val=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\"/>" ).append( "\n" )
                                                       .append( "  <w:body>" ).append( "\n" )
                                                       .append( "     <wx:sect>" ).append( "\n" )
                                                       .append( "        INSERT_ARTIFACT_HERE" ).append( "\n" )
                                                       .append( "     </wx:sect>" ).append( "\n" )
                                                       .append( "  </w:body>" ).append( "\n" )
                                                       .append( "</w:wordDocument>" ).append( "\n" )
                                                       .toString()
                                                 ),
                                              AttributeSetters .stringAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         *  Menu Command:              MS Word Preview No Attributes (Server)
                         *  Guide Publishing Template: PREVIEW_ALL_NO_ATTRIBUTES
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "SERVER_PREVIEW_TEST_B",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.RendererOptions,                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "{"                                                             ).append( "\n" )
                                                       .append( "   \"ElementType\":                \"Artifact\","              ).append( "\n" )
                                                       .append( "   \"OutliningOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"Outlining\":            true,"                      ).append( "\n" )
                                                       .append( "         \"RecurseChildren\":      false,"                     ).append( "\n" )
                                                       .append( "         \"HeadingAttributeType\": \"Name\","                  ).append( "\n" )
                                                       .append( "         \"ArtifactName\":         \"Default\","               ).append( "\n" )
                                                       .append( "         \"OutlineNumber\":        \"\""                       ).append( "\n" )
                                                       .append( "      } ],"                                                    ).append( "\n" )
                                                       .append( "   \"AttributeOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"AttrType\":             \"Word Template Content\"," ).append( "\n" )
                                                       .append( "         \"Label\":                \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPre\":            \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPost\":           \"\""                       ).append( "\n" )
                                                       .append( "      } ]"                                                     ).append( "\n" )
                                                       .append( "}"                                                             ).append( "\n" )
                                                       .toString()
                                                 ),
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter(BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.WholeWordContent,                                  /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
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
                                              AttributeSetters .stringAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         *  Menu Command:              MS Word Preview With Children (Server)
                         *  Guide Publishing Template: PREVIEW_ALL_RECURSE
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "SERVER_PREVIEW_TEST_C",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.RendererOptions,                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "{"                                                             ).append( "\n" )
                                                       .append( "   \"ElementType\":                \"Artifact\","              ).append( "\n" )
                                                       .append( "   \"OutliningOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"Outlining\":            true,"                      ).append( "\n" )
                                                       .append( "         \"RecurseChildren\":      true,"                      ).append( "\n" )
                                                       .append( "         \"HeadingAttributeType\": \"Name\","                  ).append( "\n" )
                                                       .append( "         \"ArtifactName\":         \"Default\","               ).append( "\n" )
                                                       .append( "         \"OutlineNumber\":        \"\""                       ).append( "\n" )
                                                       .append( "      } ],"                                                    ).append( "\n" )
                                                       .append( "   \"AttributeOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"AttrType\":             \"*\","                     ).append( "\n" )
                                                       .append( "         \"Label\":                \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPre\":            \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPost\":           \"\""                       ).append( "\n" )
                                                       .append( "      } ]"                                                     ).append( "\n" )
                                                       .append( "}"                                                             ).append( "\n" )
                                                       .toString()
                                                  ),
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter(BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.WholeWordContent,                                  /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
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
                                              AttributeSetters .stringAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         *  Menu Command:              MS Word Preview With Children No Attributes (Server)
                         *  Guide Publishing Template: PREVIEW_ALL_RECURSE_NO_ATTRIBUTES
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "SERVER_PREVIEW_TEST_D",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.RendererOptions,                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "{"                                                             ).append( "\n" )
                                                       .append( "   \"ElementType\":                \"Artifact\","              ).append( "\n" )
                                                       .append( "   \"OutliningOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"Outlining\":            true,"                      ).append( "\n" )
                                                       .append( "         \"RecurseChildren\":      true,"                      ).append( "\n" )
                                                       .append( "         \"HeadingAttributeType\": \"Name\","                  ).append( "\n" )
                                                       .append( "         \"ArtifactName\":         \"Default\","               ).append( "\n" )
                                                       .append( "         \"OutlineNumber\":        \"\""                       ).append( "\n" )
                                                       .append( "      } ],"                                                    ).append( "\n" )
                                                       .append( "   \"AttributeOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"AttrType\":             \"Word Template Content\"," ).append( "\n" )
                                                       .append( "         \"Label\":                \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPre\":            \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPost\":           \"\""                       ).append( "\n" )
                                                       .append( "      } ]"                                                     ).append( "\n" )
                                                       .append( "}"                                                             ).append( "\n" )
                                                       .toString()
                                                  ),
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter(BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.WholeWordContent,                                  /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
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
                                              AttributeSetters .stringAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         *  All Metadata Options
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  7,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "SERVER_PREVIEW_TEST_METADATAOPTIONS_ALL",                                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.RendererOptions,                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
                                                 (
                                                    new StringBuilder( 1024 )
                                                       .append( "{"                                                             ).append( "\n" )
                                                       .append( "   \"ElementType\":                \"Artifact\","              ).append( "\n" )
                                                       .append( "   \"OutliningOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"Outlining\":            true,"                      ).append( "\n" )
                                                       .append( "         \"RecurseChildren\":      false,"                     ).append( "\n" )
                                                       .append( "         \"HeadingAttributeType\": \"Name\","                  ).append( "\n" )
                                                       .append( "         \"ArtifactName\":         \"Default\","               ).append( "\n" )
                                                       .append( "         \"OutlineNumber\":        \"\""                       ).append( "\n" )
                                                       .append( "      } ],"                                                    ).append( "\n" )
                                                       .append( "   \"AttributeOptions\":"                                      ).append( "\n" )
                                                       .append( "      [ {"                                                     ).append( "\n" )
                                                       .append( "         \"AttrType\":             \"Word Template Content\"," ).append( "\n" )
                                                       .append( "         \"Label\":                \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPre\":            \"\","                      ).append( "\n" )
                                                       .append( "         \"FormatPost\":           \"\""                       ).append( "\n" )
                                                       .append( "      } ],"                                                    ).append( "\n" )
                                                       .append( "   \"MetadataOptions\":"                                       ).append( "\n" )
                                                       .append( "      ["                                                       ).append( "\n" )
                                                       .append( "        {"                                                     ).append( "\n" )
                                                       .append( "          \"Type\":                \"Artifact Type\","         ).append( "\n" )
                                                       .append( "          \"Format\":              \"\","                      ).append( "\n" )
                                                       .append( "          \"Label\":               \"\""                       ).append( "\n" )
                                                       .append( "        },"                                                    ).append( "\n" )
                                                       .append( "        {"                                                     ).append( "\n" )
                                                       .append( "          \"Type\":                \"Artifact Id\","           ).append( "\n" )
                                                       .append( "          \"Format\":              \"\","                      ).append( "\n" )
                                                       .append( "          \"Label\":               \"\""                       ).append( "\n" )
                                                       .append( "        },"                                                    ).append( "\n" )
                                                       .append( "        {"                                                     ).append( "\n" )
                                                       .append( "          \"Type\":                \"Applicability\","         ).append( "\n" )
                                                       .append( "          \"Format\":              \"\","                      ).append( "\n" )
                                                       .append( "          \"Label\":               \"\""                       ).append( "\n" )
                                                       .append( "        }"                                                     ).append( "\n" )
                                                       .append( "      ]"                                                       ).append( "\n" )
                                                       .append( "}"                                                             ).append( "\n" )
                                                       .toString()
                                                 ),
                                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter(BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                        new BasicAttributeSpecificationRecord
                                           (
                                              CoreAttributeTypes.WholeWordContent,                                  /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                              List.of                                                               /* Test Attribute Values                  (List<Object>)                          */
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
                                              AttributeSetters .stringAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )
                     )
               )
         );
   //@formatter:on

   /**
    * Saves a {@link Map} of the artifact identifiers for each artifact associated with each
    * {@link ArtifactSpecificationRecord}.
    */

   private static Map<Integer, Optional<ArtifactId>> builderRecordMap;

   /**
    * Saves a handle to the Publishing REST API end point.
    */

   private static PublishingEndpoint publishingEndpoint;

   /**
    * Save a reference to the {@link PublishingXmlUtils} helper object.
    */

   private static PublishingXmlUtils publishingXmlUtils;

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId rootBranchId;

   /**
    * Build a new {@link AssertionError} with error message for a {@link PublishingXmlUtils} method failure.
    *
    * @param publishingXmlUtils reference to the {@link PublishingXmlUtils} object that contains the failure.
    * @param errorStatement description of the error
    * @param documentString {@link String} representation of the XML document being processed with the error occurred.
    * @return the new {@link AssertionError} object.
    */

   private static AssertionError buildAssertionError(PublishingXmlUtils publishingXmlUtils, String errorStatement, String documentString) {

      var error = publishingXmlUtils.getLastError();

      //@formatter:off
      var message =
         new Message()
                .title( errorStatement )
                .indentInc()
                .segment( "Cause", publishingXmlUtils.getLastCause() )
                .reasonFollowsIfPresent( error )
                .followsIfNonNull( "XML Follows", documentString )
                .indentDec();

      return
         error.isPresent()
            ? new AssertionError( message.toString(), error.get() )
            : new AssertionError( message.toString() );
      //@formatter:on
   }

   /**
    * The published preview XML is parsed and then pretty printed to this string.
    */

   String documentString;

   /**
    * Saves an encapsulation of the "&lt;w:body&gt;" element from the Word ML preview.
    */

   WordBody wordBody;

   /**
    * Saves an encapsulation of the "&lt;w:wordDocument&gt;" element from the Word ML preview.
    */

   WordDocument wordDocument;

   /**
    * Saves an encapsulation of a list of the first level "&lt;wx:sect&gt;" elements from the Word ML preview.
    */

   WordSectionList wordSectionList;

   /**
    * Saves an encapsulation of a list of the first level "&lt;wx:sub-section&gt;" elements in each first level
    * "&lt;wx:sect&gt;" of the Word ML preview.
    */

   List<WordSubSectionList> wordSubSectionList;

   WordSubSectionList firstWordSubSectionSubSectionList;

   /**
    * Saves an encapsulation of a list of the first level "&lt;w:p&gt;" elements in each first level
    * "&lt;wx:sub-section&gt;" of each first level "&lt;wx:sect&gt;" of the Word ML preview.
    */

   List<List<WordParagraphList>> wordParagraphListForEachSubSection;

   /**
    * Requests the Word ML preview from the server, parses the received Word ML, and indexes some common elements of the
    * Word ML.
    *
    * @param msWordPreviewRequestData the Word preview request data to be sent to the server.
    */

   private void publishPreview(PublishingRequestData msWordPreviewRequestData) {

      /*
       * Set sentinel values for unparsed document parts
       */

      this.wordDocument = null;
      this.wordBody = null;
      this.wordSectionList = null;
      this.wordSubSectionList = null;
      this.wordParagraphListForEachSubSection = null;
      String documentString = null;

      //@formatter:off
      var attachment = PublishingServerPreviewTest.publishingEndpoint.msWordPreview(msWordPreviewRequestData);

      Document document;

      try( var inputStream = attachment.getDataHandler().getInputStream() ) {

        document = PublishingServerPreviewTest.publishingXmlUtils.parse( inputStream )
                .orElseThrow
                   (
                     () -> PublishingServerPreviewTest.buildAssertionError
                              (
                                 PublishingServerPreviewTest.publishingXmlUtils,
                                 "Failed to parse preview XML.",
                                 null
                              )
                   );
      } catch( Exception e )
      {
         throw
            PublishingServerPreviewTest.buildAssertionError
               (
                  PublishingServerPreviewTest.publishingXmlUtils,
                  e.getMessage(),
                  null
               );
      }

      this.documentString =
         PublishingServerPreviewTest.publishingXmlUtils.prettyPrint( document )
            .orElseThrow
               (
                  () -> PublishingServerPreviewTest.buildAssertionError
                           (
                              PublishingServerPreviewTest.publishingXmlUtils,
                              "Failed to pretty print preview XML.",
                              null
                           )
               );

      this.printDocument( this.documentString );

      this.wordDocument =
         PublishingServerPreviewTest.publishingXmlUtils.parseWordDocument( document )
            .orElseThrow
               (
                  () -> PublishingServerPreviewTest.buildAssertionError
                           (
                              PublishingServerPreviewTest.publishingXmlUtils,
                              "Failed to parse Word Document from XML Document.",
                              this.documentString
                           )
               );

      this.wordBody =
         PublishingServerPreviewTest.publishingXmlUtils.parseWordBody( wordDocument )
            .orElseThrow
               (
                 () -> PublishingServerPreviewTest.buildAssertionError
                          (
                             PublishingServerPreviewTest.publishingXmlUtils,
                            "Failed to parse Word Body from XML Document.",
                            this.documentString
                          )
               );

      this.wordSectionList =
         PublishingServerPreviewTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordBody,
                  WordXmlTag.SECTION,
                  WordSectionList::new,
                  WordSection::new
               )
            .orElseThrow
               (
                  () -> PublishingServerPreviewTest.buildAssertionError
                           (
                              PublishingServerPreviewTest.publishingXmlUtils,
                              "Failed to parse Word Section List from Word Body.",
                              this.documentString
                           )
               );

      if( this.wordSectionList.size() == 0 ) {
         return;
      }

      this.wordSubSectionList =
         this.wordSectionList.stream()
            .map
               (
                  ( wordSection ) -> PublishingServerPreviewTest.publishingXmlUtils
                                        .parseChildListFromParent
                                           (
                                              wordSection,
                                              WordXmlTag.SUBSECTION,
                                              WordSubSectionList::new,
                                              WordSubSection::new
                                           )
                                        .orElseThrow
                                           (
                                             () -> PublishingServerPreviewTest.buildAssertionError
                                                      (
                                                        PublishingServerPreviewTest.publishingXmlUtils,
                                                        "Failed to parse Word Sub-Section List from Word Section.",
                                                        this.documentString
                                                      )
                                           )
               )
            .collect( Collectors.toList() );

      /*
       * If any sections were found
       */

      this.firstWordSubSectionSubSectionList = null;

      if( !this.wordSubSectionList.isEmpty() ) {

         /*
          * Get the sub-section list for the first section
          */

         var firstWordSubSectionList = this.wordSubSectionList.get(0);

         if( firstWordSubSectionList.size() > 0 ) {

            var firstWordSubSection = firstWordSubSectionList.get(0).get();

            this.firstWordSubSectionSubSectionList =
               PublishingServerPreviewTest.publishingXmlUtils
                  .parseChildListFromParent
                     (
                        firstWordSubSection,
                        WordXmlTag.SUBSECTION,
                        WordSubSectionList::new,
                        WordSubSection::new
                     )
                  .orElseThrow
                     (
                        () -> PublishingServerPreviewTest.buildAssertionError
                                 (
                                    PublishingServerPreviewTest.publishingXmlUtils,
                                    "Failed to parse Word Sub-Section List from First Word Sub-Section.",
                                    this.documentString
                                 )
                     );
         }
      }

      this.wordParagraphListForEachSubSection =
         this.wordSubSectionList.stream()
            .map
               (
                  ( wordSubSectionList ) ->
                     Streams.concat( wordSubSectionList.stream(), this.firstWordSubSectionSubSectionList.stream() )
                        .map
                           (
                              ( wordSubSection ) -> PublishingServerPreviewTest.publishingXmlUtils.parseImmediateChildrenWordParagraphListFromWordSubSection( wordSubSection )
                                                       .orElseThrow
                                                          (
                                                             () -> PublishingServerPreviewTest.buildAssertionError
                                                                      (
                                                                         PublishingServerPreviewTest.publishingXmlUtils,
                                                                         "Failed to parse Word Sub-Section List from Word Section.",
                                                                         documentString
                                                                      )
                                                          )
                           )
                        .collect( Collectors.toList() )
               )
            .collect( Collectors.toList() );
   }

   /**
    * Gets the {@link WordTextList} for the specified paragraph.
    *
    * @param sectionIndex the index of the first level section.
    * @param subSectionIndex the index of the first level sub-section in the indexed section.
    * @param paragraphIndex the index of the first level paragraph in the index section and sub-section.
    * @return the {@link WordTextList}, possible empty, of the specified paragraph.
    */

   private WordTextList getWordTextList( int sectionIndex, int subSectionIndex, int paragraphIndex ) {
      //@formatter:off
      return
         PublishingServerPreviewTest.publishingXmlUtils.parseWordTextListFromWordParagraph
            (
               this.wordParagraphListForEachSubSection.get( sectionIndex ).get( subSectionIndex ).get( paragraphIndex ).get()
            )
            .orElseThrow
               (
                  () -> PublishingServerPreviewTest.buildAssertionError
                           (
                              PublishingServerPreviewTest.publishingXmlUtils,
                              "Failed to parse Word Text List from Word Paragraph.",
                              documentString
                           )
               );
      //@formatter:on
   }

   /**
    * Prints the currently running test method name followed by the received Word ML document for the test.
    *
    * @param documentString the Word ML document as a {@link String}.
    */

   private void printDocument(String documentString) {
      if (PublishingServerPreviewTest.printDocuments) {
         System.out.println("=============================================================");
         System.out.println("Test Name: " + this.testName.getMethodName());
         System.out.println("-------------------------------------------------------------");
         System.out.println(documentString);
         System.out.println("=============================================================");
      }
   }

   @BeforeClass
   public static void testSetup() {

      /*
       * Setup XML utils
       */

      PublishingServerPreviewTest.publishingXmlUtils = new PublishingXmlUtils();

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingServerPreviewTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            PublishingServerPreviewTest.branchSpecifications,
            PublishingServerPreviewTest.artifactSpecifications
         );

      /*
       * Save identifiers of test document root
       */

      //@formatter:off
      PublishingServerPreviewTest.rootBranchId =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier
               )
            .get();

      PublishingServerPreviewTest.rootArtifactId =
         testDocumentBuilder
            .getArtifactIdentifier
               (
                  PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier,
                  1
               )
            .get();

      PublishingServerPreviewTest.builderRecordMap =
         PublishingServerPreviewTest
            .artifactSpecifications
            .stream( PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier )
            .map( ArtifactSpecificationRecord::getIdentifier )
            .collect
               (
                  Collectors.toMap
                     (
                        Function.identity(),
                        ( builderRecordIdentifier ) -> testDocumentBuilder
                                                          .getArtifactIdentifier
                                                             (
                                                                PublishingServerPreviewTest.testBranchSpecificationRecordIdentifier,
                                                                builderRecordIdentifier
                                                             )
                     )
               );
      //@formatter:on
      /*
       * Get services
       */

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      PublishingServerPreviewTest.publishingEndpoint = oseeClient.getPublishingEndpoint();

      /*
       * Clear the publishing template cache, so newly created or modified publishing template artifacts are reloaded.
       */

      Objects.requireNonNull(oseeClient.getTemplateManagerEndpoint()).deleteCache();
   }

   @Test
   public void testMsWordPreviewServerFolder() {

      //@formatter:off
      var publishingRendererOptions =
         new EnumRendererMap( PublishingServerPreviewTest.commonPublishingRendererOptions );

      publishingRendererOptions.setRendererOption(RendererOption.BRANCH, PublishingServerPreviewTest.rootBranchId);

      var msWordPreviewRequestData =
         new PublishingRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_A",
                             FormatIndicator.WORD_ML
                          ),
                   publishingRendererOptions,
                   List.of( PublishingServerPreviewTest.rootArtifactId )
                );

      this.publishPreview( msWordPreviewRequestData );

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sect>\" expected in the \"<w:body>\".",
            1,
            this.wordSectionList.size()
         );

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sub-section>\" expected in \"<xw:sect>\"( 0 ).",
            1,
            this.wordSubSectionList.get( 0 ).size()
         );

      Assert.assertEquals
         (
            "Two un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 0 ).",
            2,
            this.wordParagraphListForEachSubSection.get( 0 ).get( 0 ).size()
         );

      var wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "First Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Preview Artifacts Folder",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 1 /* paragraph */ );

      Assert.assertEquals
         (
            "Second Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Attribute Label does not match.",
            "Description: This folder contains artifacts for publishing preview tests.",
            wordTextList.get( 0 ).get().getText()
         );
      //@formatter:on
   }

   @Test
   public void testMsWordPreviewServerFolderRecursiveWithRestrictedRightsRequirementB() {

      //@formatter:off
      var publishingRendererOptions =
         new EnumRendererMap( PublishingServerPreviewTest.commonPublishingRendererOptions );

      publishingRendererOptions.setRendererOption(RendererOption.BRANCH, PublishingServerPreviewTest.rootBranchId);

      var msWordPreviewRequestData =
         new PublishingRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_D",
                             FormatIndicator.WORD_ML
                          ),
                   publishingRendererOptions,
                   List.of( ArtifactId.valueOf( PublishingServerPreviewTest.builderRecordMap.get( 4 ).get() ) )
                );

      this.publishPreview( msWordPreviewRequestData );

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sect>\" expected in the \"<w:body>\".",
            1,
            this.wordSectionList.size()
         );

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sub-section>\" expected in \"<xw:sect>\"( 0 ).",
            1,
            this.wordSubSectionList.get( 0 ).size()
         );

      Assert.assertNotNull
         (
            "Two nested \"<wx:sub-section>\" are expected in the first \"<wx:sub-section>\" of the first \"<wx:sect>\".",
            this.firstWordSubSectionSubSectionList
         );

      Assert.assertEquals
         (
            "Two nested \"<wx:sub-section>\" are expected in the first \"<wx:sub-section>\" of the first \"<wx:sect>\".",
            2,
            this.firstWordSubSectionSubSectionList.size()
         );

      Assert.assertEquals
         (
            "One un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 0 ).",
            1,
            this.wordParagraphListForEachSubSection.get( 0 ).get( 0 ).size()
         );

      Assert.assertEquals
      (
         "Three un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 1 ).",
         3,
         this.wordParagraphListForEachSubSection.get( 0 ).get( 1 ).size()
      );

      Assert.assertEquals
      (
         "Three un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 2 ).",
         3,
         this.wordParagraphListForEachSubSection.get( 0 ).get( 2 ).size()
      );

      var wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "First Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Preview Artifacts Folder With Data Rights",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 1 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "Second Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Requirement A",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 1 /* sub-section */, 1 /* paragraph */ );

      Assert.assertEquals
         (
            "Third Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "This is Requirement A's WordTemplateContent.",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 1 /* sub-section */, 2 /* paragraph */ );

      Assert.assertEquals
         (
            "Fourth Paragraph is not expected to have texts.",
            0,
            wordTextList.size()
         );


      wordTextList = this.getWordTextList( 0 /* section */, 2 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "Fifth Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Requirement B",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 2 /* sub-section */, 1 /* paragraph */ );

      Assert.assertEquals
         (
            "Sixth Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "This is Requirement B's WordTemplateContent.",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 2 /* sub-section */, 2 /* paragraph */ );

      Assert.assertEquals
         (
            "Seventh Paragraph is expected to have seven texts.",
            7,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "5",
            wordTextList.get( 0 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "RESTRICTED RIGHTS – EXPORT CONTROLLED INFORMATION\n            ",
            wordTextList.get( 1 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "Contract No.: 0780663667",
            wordTextList.get( 2 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "Contractor Name: Acme Corporation",
            wordTextList.get( 3 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "Contractor Address: 1234 W. World Road; Jersey City, NJ 07002",
            wordTextList.get( 4 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract. Any reproduction of software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access to such software must promptly notify the above named Contractor.",
            wordTextList.get( 5 ).get().getText()
         );

      Assert.assertEquals
         (
            "Footer text does not match.",
            "Copyright (c) 2017 – Acme Corporation",
            wordTextList.get( 6 ).get().getText()
         );


      //@formatter:on
   }

   @Test
   public void testMsWordPreviewServerNoAttributesRequirementA() {

      //@formatter:off
      var publishingRendererOptions =
         new EnumRendererMap( PublishingServerPreviewTest.commonPublishingRendererOptions );

      publishingRendererOptions.setRendererOption(RendererOption.BRANCH, PublishingServerPreviewTest.rootBranchId);

      var msWordPreviewRequestData =
         new PublishingRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_B",
                             FormatIndicator.WORD_ML
                          ),
                   publishingRendererOptions,
                   List.of( ArtifactId.valueOf( PublishingServerPreviewTest.builderRecordMap.get( 2 ).get() ) )
                );

      this.publishPreview(msWordPreviewRequestData);

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sect>\" expected in the \"<w:body>\".",
            1,
            this.wordSectionList.size()
         );

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sub-section>\" expected in \"<xw:sect>\"( 0 ).",
            1,
            this.wordSubSectionList.get( 0 ).size()
         );


      //ToDo:
      //   Data rights footer section presentation is getting inserted into the middle of the final paragraph. Word does not display
      //   the footer. This also inserts a bunch of paragraphs into the sub-section that should not be there.
      //
      //Assert.assertEquals
      //   (
      //      "Three un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 0 ).",
      //      3,
      //      this.wordParagraphListForEachSubSection.get( 0 ).get( 0 ).size()
      //   );

      var wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "First Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Requirement A",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList =  this.getWordTextList( 0 /* section */, 0 /* sub-section */, 1 /* paragraph */ );

      Assert.assertEquals
         (
            "Second Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact content does not match.",
            "This is Requirement A's WordTemplateContent.",
            wordTextList.get( 0 ).get().getText()
         );

      //ToDo:
      //   Data rights footer section presentation is getting inserted into the middle of the final paragraph. Word does not display
      //   the footer.
      //
      //wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 2 /* paragraph */ );
      //
      //Assert.assertEquals
      //   (
      //      "Third Paragraph is expected to not have any text.",
      //      0,
      //      wordTextList.size()
      //   );

      //@formatter:on
   }

   @Test
   public void testMetadataOptionsAll() {

      //@formatter:off
      var publishingRendererOptions =
         new EnumRendererMap( PublishingServerPreviewTest.commonPublishingRendererOptions );

      publishingRendererOptions.setRendererOption(RendererOption.BRANCH, PublishingServerPreviewTest.rootBranchId);

      var msWordPreviewRequestData =
         new PublishingRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_METADATAOPTIONS_ALL",
                             FormatIndicator.WORD_ML
                          ),
                   publishingRendererOptions,
                   List.of( PublishingServerPreviewTest.rootArtifactId )
                );

      this.publishPreview(msWordPreviewRequestData);

      Assert.assertEquals
         (
            "Only one un-nested \"<wx:sect>\" expected in the \"<w:body>\".",
            1,
            wordSectionList.size()
         );

      Assert.assertEquals
      (
         "Only one un-nested \"<wx:sub-section>\" expected in \"<xw:sect>\"( 0 ).",
         1,
         this.wordSubSectionList.get( 0 ).size()
      );

      Assert.assertEquals
         (
            "Four un-nested \"<w:p>\" expected in \"<wx:sub-section>\"( 0, 0 ).",
            4,
            this.wordParagraphListForEachSubSection.get( 0 ).get( 0 ).size()
         );

      var wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 0 /* paragraph */ );

      Assert.assertEquals
         (
            "First Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact title does not match.",
            "Preview Artifacts Folder",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 1 /* paragraph */ );

      Assert.assertEquals
         (
            "Second Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact metadata artifact type does not match.",
            "Artifact Type: Folder",
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 2 /* paragraph */ );

      Assert.assertEquals
         (
            "Third Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact metadata artifact identifier does not match.",
            "Artifact Id: ".concat( PublishingServerPreviewTest.rootArtifactId.getIdString() ),
            wordTextList.get( 0 ).get().getText()
         );

      wordTextList = this.getWordTextList( 0 /* section */, 0 /* sub-section */, 3 /* paragraph */ );

      Assert.assertEquals
         (
            "Fourth Paragraph is expected to have one text.",
            1,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Artifact metadata applicability does not match.",
            "Applicability: Base",
            wordTextList.get( 0 ).get().getText()
         );
      //@formatter:on
   }

}

/* EOF */
