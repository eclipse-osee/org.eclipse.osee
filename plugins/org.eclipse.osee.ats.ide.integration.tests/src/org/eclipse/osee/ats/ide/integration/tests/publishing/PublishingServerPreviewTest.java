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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactInfoRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecification;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.core.xml.publishing.WordBody;
import org.eclipse.osee.framework.core.xml.publishing.WordDocument;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphList;
import org.eclipse.osee.framework.core.xml.publishing.WordSectionList;
import org.eclipse.osee.framework.core.xml.publishing.WordSubSectionList;
import org.eclipse.osee.framework.core.xml.publishing.WordTextList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

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
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NotProductionDataStoreRule() );
   //@formatter:on

   /**
    * A rule to get the method name of the currently running test.
    */

   @Rule
   public TestName testName = new TestName();

   /**
    * List of {@link BuilderRecord}s describing the test artifacts.
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
   private static List<BuilderRecord> artifactInfoRecords =
      List.of
         (
            new BasicArtifactInfoRecord
               (
                  1,                                                                                /* Identifier                             (Integer)                               */
                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Preview Artifacts Folder",                                                       /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This folder contains artifacts for publishing preview tests." ), /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  2,                                                                                /* Identifier                             (Integer)                               */
                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Requirement A",                                                                  /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This is Requirement A's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               ),
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "<w:p><w:r><w:t>This is Requirement A's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  3,                                                                                /* Identifier                             (Integer)                               */
                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Requirement B",                                                                  /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This is Requirement B's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               ),
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "<w:p><w:r><w:t>This is Requirement B's WordTemplateContent.</w:t></w:r></w:p>" ), /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               )
         );

   /**
    * A list of the test artifacts to be created on the Common branch.
    */

   //@formatter:off
   private static List<BuilderRecord> commonBranchArtifactInfoRecords =
      List.of
         (
            new BasicArtifactInfoRecord
               (
                  1,                                                                                /* Identifier                             (Integer)                               */
                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "OSEE Configuration",                                                             /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                           (
                              CoreAttributeTypes.Description,                                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "OSEE Configuration" ),                                      /* Test Attribute Values                  (List<Object>)                          */
                              AttributeSetters.stringAttributeSetter                                /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                           )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  2,                                                                                /* Identifier                             (Integer)                               */
                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Document Templates",                                                             /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
               (
                  4,                                                                                /* Identifier                             (Integer)                               */
                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "SERVER_PREVIEW_TEST_A",                                                          /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
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
                        new BasicAttributeSpecification
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

               new BasicArtifactInfoRecord
                  (
                     3,                                                                                /* Identifier                             (Integer)                               */
                     2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                     "SERVER_PREVIEW_TEST_B",                                                          /* Artifact Name                          (String)                                */
                     CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                     List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                        (
                           new BasicAttributeSpecification
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
                           new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
               (
                  5,                                                                                /* Identifier                             (Integer)                               */
                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "SERVER_PREVIEW_TEST_C",                                                          /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
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
                        new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
               (
                  6,                                                                                /* Identifier                             (Integer)                               */
                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "SERVER_PREVIEW_TEST_D",                                                          /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
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
                        new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
               (
                  7,                                                                                /* Identifier                             (Integer)                               */
                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "SERVER_PREVIEW_TEST_METADATAOPTIONS_ALL",                                        /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.RendererTemplateWholeWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
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
                        new BasicAttributeSpecification
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


            );
   //@formatter:on

   /**
    * Saves a {@link Map} of the artifact identifiers for each artifact associated with each {@link BuilderRecord}.
    */

   private static Map<Integer, Optional<Long>> builderRecordMap;

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
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Publishing Server Preview Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for Publishing Server Preview Testing";

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

   private void publishPreview(MsWordPreviewRequestData msWordPreviewRequestData) {

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
      var inputStream = PublishingServerPreviewTest.publishingEndpoint.msWordPreview(msWordPreviewRequestData);

      var document =
         PublishingServerPreviewTest.publishingXmlUtils.parse( inputStream )
            .orElseThrow
               (
                  () -> PublishingServerPreviewTest.buildAssertionError
                           (
                              PublishingServerPreviewTest.publishingXmlUtils,
                              "Failed to parse preview XML.",
                              null
                           )
               );

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
         PublishingServerPreviewTest.publishingXmlUtils.parseWordSectionListFromWordBody( wordBody )
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
                  ( wordSection ) -> PublishingServerPreviewTest.publishingXmlUtils.parseWordSubSectionListFromWordSection( wordSection )
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

      this.wordParagraphListForEachSubSection =
         this.wordSubSectionList.stream()
            .map
               (
                  ( wordSubSectionList ) -> wordSubSectionList.stream()
                                               .map
                                                  (
                                                    ( wordSubSection ) -> PublishingServerPreviewTest.publishingXmlUtils.parseWordParagraphListFromWordSubSection( wordSubSection )
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
       * When the test suit is run directly it will be in Database Initialization mode.
       */

      if (OseeProperties.isInDbInit()) {
         /*
          * Get out of database initialization mode and re-authenticate as the test user
          */

         OseeProperties.setInDbInit(false);
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
      }

      /*
       * Setup XML utils
       */

      PublishingServerPreviewTest.publishingXmlUtils = new PublishingXmlUtils();

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingServerPreviewTest.setValues);

      testDocumentBuilder.buildDocument(PublishingServerPreviewTest.artifactInfoRecords,
         PublishingServerPreviewTest.testBranchName, PublishingServerPreviewTest.testBranchCreationComment);

      /*
       * Create Publishing Templates on the common branch
       */

      var commonBranchTestDocumentBuilder = new TestDocumentBuilder(PublishingServerPreviewTest.setValues);

      commonBranchTestDocumentBuilder.buildDocument(PublishingServerPreviewTest.commonBranchArtifactInfoRecords,
         CoreBranches.COMMON.getName(), "Common Should Exist");

      /*
       * Save identifiers of test document root
       */

      PublishingServerPreviewTest.rootBranchId = testDocumentBuilder.getRootBranchId();
      PublishingServerPreviewTest.rootArtifactId = testDocumentBuilder.getRootArtifactId();

      //@formatter:off
      PublishingServerPreviewTest.builderRecordMap =
         PublishingServerPreviewTest.artifactInfoRecords.stream()
            .map( BuilderRecord::getIdentifier )
            .collect( Collectors.toMap( Function.identity(), testDocumentBuilder::getArtifactIdByBuilderRecordId ) )
            ;
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
      var msWordPreviewRequestData =
         new MsWordPreviewRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_A"
                          ),
                   PublishingServerPreviewTest.rootBranchId,
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
            "Second Paragraph is expected to have two text.",
            2,
            wordTextList.size()
         );

      Assert.assertEquals
         (
            "Attribute Label does not match.",
            "Description: ",
            wordTextList.get( 0 ).get().getText()
         );

      Assert.assertEquals
         (
            "Attribute Value does not match.",
            "This folder contains artifacts for publishing preview tests.",
            wordTextList.get( 1 ).get().getText()
         );
      //@formatter:on
   }

   @Test
   public void testMsWordPreviewServerNoAttributesRequirementA() {

      //@formatter:off
      var msWordPreviewRequestData =
         new MsWordPreviewRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_B"
                          ),
                   PublishingServerPreviewTest.rootBranchId,
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
      var msWordPreviewRequestData =
         new MsWordPreviewRequestData
                (
                   new PublishingTemplateRequest
                          (
                             "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer",
                             CoreArtifactTypes.Folder.getName(),
                             PresentationType.PREVIEW_SERVER.name(),
                             "SERVER_PREVIEW_TEST_METADATAOPTIONS_ALL"
                          ),
                   PublishingServerPreviewTest.rootBranchId,
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