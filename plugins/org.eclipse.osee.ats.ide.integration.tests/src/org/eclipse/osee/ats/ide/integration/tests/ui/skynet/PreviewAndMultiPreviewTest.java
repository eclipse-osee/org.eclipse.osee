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

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactInfoRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecification;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * Client side MS Word preview tests.
 *
 * @author Megumi Telles
 * @author Loren K. Ashley
 */

public class PreviewAndMultiPreviewTest {

   /**
    * Set this flag to <code>true</code> to print a test start message, a test end message, and the received Word ML
    * documents to <code>stdout</code>.
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
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                       "This folder contains artifacts for publishing preview and multi-preview tests."
                                    ),
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
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                       "<w:p><w:r><w:t>This is Requirement A's WordTemplateContent.</w:t></w:r></w:p>"
                                    ),
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  3,                                                                                /* Identifier                             (Integer)                               */
                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Requirement A-1",                                                                /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This is Requirement A-1's Description." ),               /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               ),
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                       "<w:p><w:r><w:t>This is Requirement A-1's WordTemplateContent.</w:t></w:r></w:p>"
                                    ),
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  4,                                                                                /* Identifier                             (Integer)                               */
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
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                       "<w:p><w:r><w:t>This is Requirement B's WordTemplateContent.</w:t></w:r></w:p>"
                                    ),
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  5,                                                                                /* Identifier                             (Integer)                               */
                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Requirement C",                                                                  /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This is Requirement C's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               ),
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                       "<w:p><w:r><w:t>This is Requirement C's WordTemplateContent.</w:t></w:r></w:p>"
                                    ),
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               )
                     ),
                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
               ),

            new BasicArtifactInfoRecord
               (
                  6,                                                                                /* Identifier                             (Integer)                               */
                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                  "Test Procedure A",                                                               /* Artifact Name                          (String)                                */
                  CoreArtifactTypes.TestProcedureWholeWord,                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                     (
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of( "This is Test Procedure A's Description." ),              /* Test Attribute Values                  (List<Object>)                          */
                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                               ),
                        new BasicAttributeSpecification
                               (
                                 CoreAttributeTypes.WholeWordContent,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                    (
                                         "<?xml version= '1.0' encoding= 'UTF-8' standalone= 'yes'?>"
                                       + "<?mso-application progid= 'Word.Document'?>"
                                       + "<w:wordDocument xmlns:w= 'http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v= 'urn:schemas-microsoft-com:vml' xmlns:w10= 'urn:schemas-microsoft-com:office:word' xmlns:sl= 'http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml= 'http://schemas.microsoft.com/aml/2001/core' xmlns:wx= 'http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o= 'urn:schemas-microsoft-com:office:office' xmlns:dt= 'uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp= 'http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0= 'http://www.w3.org/2001/XMLSchema' xmlns:ns1= 'http://eclipse.org/artifact.xsd' xmlns:st1= 'urn:schemas-microsoft-com:office:smarttags' w:macrosPresent= 'no' w:embeddedObjPresent= 'no' w:ocxPresent= 'no' xml:space= 'preserve'>"
                                       +    "<w:body>"
                                       +       "<w:p><w:r><w:t>This is Test Procedures A's WholeWordContent.</w:t></w:r></w:p>"
                                       +    "</w:body>"
                                       + "</w:wordDocument>"
                                    ),
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
                     "PreviewAndMultiPreviewTest-PREVIEW",                                             /* Artifact Name                          (String)                                */
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
             *  Menu Command:              MS Word Preview (Server)
             *  Guide Publishing Template: PreviewAll
             */

            new BasicArtifactInfoRecord
                  (
                     5,                                                                                /* Identifier                             (Integer)                               */
                     2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                     "PreviewAndMultiPreviewTest-PREVIEW_RECURSE",                                     /* Artifact Name                          (String)                                */
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
                                          .append( "         \"RecurseChildren\" : true,"               ).append( "\n" )
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
                  )

         );
   //@formatter:on

   /**
    * Saves a {@link Map} of the artifacts associated with each {@link BuilderRecord}.
    */

   private static Map<Integer, Optional<Artifact>> builderRecordMap;

   /**
    * Save a reference to the {@link PublishingXmlUtils} helper object.
    */

   private static PublishingXmlUtils publishingXmlUtils;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Preview And MultiPreview Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for Preview And MultiPreview Testing";

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
    * Parse a Word ML document into an XML DOM to verify it is valid XML. The XML DOM is then pretty printed for
    * possible display.
    *
    * @param file the Word ML document to be parsed.
    * @throws AssertionError if the Word ML document fails to parse.
    */

   private void parseFile(File file) {
      //@formatter:off
      var document =
         PreviewAndMultiPreviewTest.publishingXmlUtils.parse( file )
            .orElseThrow
               (
                 () -> PreviewAndMultiPreviewTest.buildAssertionError
                          (
                             PreviewAndMultiPreviewTest.publishingXmlUtils,
                             "Failed to parse preview XML.",
                             null
                          )
               );

      var documentString =
         PreviewAndMultiPreviewTest.publishingXmlUtils.prettyPrint( document )
            .orElseThrow
               (
                 () -> PreviewAndMultiPreviewTest.buildAssertionError
                          (
                             PreviewAndMultiPreviewTest.publishingXmlUtils,
                             "Failed to pretty print preview XML.",
                             null
                          )
               );
      //@formatter:on

      this.printDocument(documentString);
   }

   /**
    * Performs a client side publishing preview with the provided renderer.
    *
    * @param renderer the client side {@link IRenderer} to generate the preview with.
    * @param rootArtifacts a list of the root artifacts for the publish.
    * @param templateOption the name of the preview template to be used.
    * @throws AssertionError when the publishing template is not found or the preview fails.
    */

   private void publishPreview(FileSystemRenderer renderer, List<Artifact> rootArtifacts, String templateOption) {

      try {

         if (Objects.nonNull(templateOption) && !templateOption.isBlank()) {
            renderer.updateOption(RendererOption.TEMPLATE_OPTION, templateOption);
         }

         var iFile = renderer.renderToFile(rootArtifacts, PresentationType.PREVIEW);

         var filePath = iFile.getLocation();

         Assert.assertNotNull("File not generated by FileSystemRenderer.", filePath);

         var file = filePath.toFile();

         this.parseFile(file);

      } catch (Exception e) {

         Asserts.assertTrue(() -> e.getMessage(), false);

      }

   }

   /**
    * Performs a client side publishing preview using the {@link RendererManager}.
    *
    * @param rootArtifacts a list of the root artifacts for the publish.
    * @param templateOption the name of the preview template to be used.
    * @throws AssertionError when the publishing template is not found or the preview fails.
    */

   private void rendererManagerPublishPreview(List<Artifact> rootArtifacts, String templateOption) {

      try {
         var filePath = RendererManager.open(rootArtifacts, PresentationType.PREVIEW,
            Map.of(RendererOption.TEMPLATE_OPTION, templateOption));

         Assert.assertNotNull("File not generated by RendererManager.", filePath);

         var file = Paths.get(filePath).toFile();

         this.parseFile(file);

      } catch (Exception e) {

         Asserts.assertTrue(() -> e.getMessage(), false);

      }

   }

   @BeforeClass
   public static void testSetup() {

      /*
       * Setup XML utils
       */

      PreviewAndMultiPreviewTest.publishingXmlUtils = new PublishingXmlUtils();

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PreviewAndMultiPreviewTest.setValues);

      testDocumentBuilder.buildDocument(PreviewAndMultiPreviewTest.artifactInfoRecords,
         PreviewAndMultiPreviewTest.testBranchName, PreviewAndMultiPreviewTest.testBranchCreationComment);

      /*
       * Create Publishing Templates on the common branch
       */

      var commonBranchTestDocumentBuilder = new TestDocumentBuilder(PreviewAndMultiPreviewTest.setValues);

      commonBranchTestDocumentBuilder.buildDocument(PreviewAndMultiPreviewTest.commonBranchArtifactInfoRecords,
         CoreBranches.COMMON.getName(), "Common Should Exist");

      //@formatter:off
      PreviewAndMultiPreviewTest.builderRecordMap =
         PreviewAndMultiPreviewTest.artifactInfoRecords.stream()
            .map( BuilderRecord::getIdentifier )
            .collect( Collectors.toMap( Function.identity(), testDocumentBuilder::getArtifactByBuilderRecordId ) )
            ;
      //@formatter:on
      /*
       * Get services
       */

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      /*
       * Clear the publishing template cache, so newly created or modified publishing template artifacts are reloaded.
       */

      Objects.requireNonNull(oseeClient.getTemplateManagerEndpoint()).deleteCache();
   }

   /**
    * Announces the start of the current test to Stdout.
    */

   @Before
   public void before() {
      if (PreviewAndMultiPreviewTest.printDocuments) {
         System.out.println("=============================================================");
         System.out.println("Test Start: " + this.testName.getMethodName());
         System.out.println("-------------------------------------------------------------");
      }
   }

   /**
    * Clean up the test artifacts and announce the completion of the test to Stdout.
    *
    * @throws Exception
    */

   @After
   public void tearDown() throws Exception {
      if (PreviewAndMultiPreviewTest.printDocuments) {
         System.out.println("-------------------------------------------------------------");
         System.out.println("Test End: " + this.testName.getMethodName());
         System.out.println("=============================================================");
      }
   }

   /**
    * Prints the currently running test method name followed by the received Word ML document for the test.
    *
    * @param documentString the Word ML document as a {@link String}.
    */

   private void printDocument(String documentString) {
      if (PreviewAndMultiPreviewTest.printDocuments) {
         System.out.println("=============================================================");
         System.out.println("Test Name: " + this.testName.getMethodName());
         System.out.println("-------------------------------------------------------------");
         System.out.println(documentString);
         System.out.println("=============================================================");
      }
   }

   @Test
   public void testPreview() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get()
            );
      //@formatter:on

      this.publishPreview(new MSWordTemplateClientRenderer(), artifacts, "PreviewAndMultiPreviewTest-PREVIEW");
   }

   @Test
   public void testPreviewUsingRendererManager() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get()
            );
      //@formatter:on

      this.rendererManagerPublishPreview(artifacts, "PreviewAndMultiPreviewTest-PREVIEW");
   }

   @Test
   public void testPreviewWithChildren() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get()
            );
      //@formatter:on

      this.publishPreview(new MSWordTemplateClientRenderer(), artifacts, "PreviewAndMultiPreviewTest-PREVIEW_RECURSE");
   }

   @Test
   public void testPreviewWithChildrenUsingRendererManager() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get()
            );
      //@formatter:on

      this.rendererManagerPublishPreview(artifacts, "PreviewAndMultiPreviewTest-PREVIEW_RECURSE");
   }

   @Test
   public void testPreviewWithChildrenUsingRendererManagerTemplateNotFound() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get()
            );
      //@formatter:on

      try {
         @SuppressWarnings("unused")
         var filePath = RendererManager.open(artifacts, PresentationType.PREVIEW,
            Map.of(RendererOption.TEMPLATE_OPTION, "PreviewAndMultiPreviewTest-NOT_FOUND"));

         Assert.assertTrue("Exception is expected.", false);
      } catch (Exception exception) {

         Asserts.assertException("Exception type or message is not as expected.", OseeArgumentException.class,
            "Unable to find a valid template match.", exception);
         //@formatter:on
      }
   }

   @Test
   public void testMultiPreview() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get(),
               PreviewAndMultiPreviewTest.builderRecordMap.get(4).get(),
               PreviewAndMultiPreviewTest.builderRecordMap.get(5).get()
            );
      //@formatter:on

      this.publishPreview(new MSWordTemplateClientRenderer(), artifacts, "PreviewAndMultiPreviewTest-PREVIEW_RECURSE");
   }

   @Test
   public void testMultiPreviewUsingRendererManager() {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(2).get(),
               PreviewAndMultiPreviewTest.builderRecordMap.get(4).get(),
               PreviewAndMultiPreviewTest.builderRecordMap.get(5).get()
            );
      //@formatter:on

      this.rendererManagerPublishPreview(artifacts, "PreviewAndMultiPreviewTest-PREVIEW");
   }

   @Test
   public void testWholeWordPreview() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(6).get()
            );
      //@formatter:on

      this.publishPreview(new WholeWordRenderer(), artifacts, "PreviewAndMultiPreviewTest-PREVIEW");
   }

   @Test
   public void testWholeWordPreviewUsingRendererManager() throws Exception {
      //@formatter:off
      var artifacts =
         List.of
            (
               PreviewAndMultiPreviewTest.builderRecordMap.get(6).get()
            );
      //@formatter:on

      this.rendererManagerPublishPreview(artifacts, "PreviewAndMultiPreviewTest-PREVIEW");
   }

}

/* EOF */
