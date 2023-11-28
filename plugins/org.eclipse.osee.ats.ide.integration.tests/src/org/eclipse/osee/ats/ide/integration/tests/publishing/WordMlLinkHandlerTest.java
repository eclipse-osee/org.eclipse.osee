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

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import java.net.URL;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.core.xml.publishing.WordFieldCharacter;
import org.eclipse.osee.framework.core.xml.publishing.WordFieldCharacterList;
import org.eclipse.osee.framework.core.xml.publishing.WordHlink;
import org.eclipse.osee.framework.core.xml.publishing.WordHlinkList;
import org.eclipse.osee.framework.core.xml.publishing.WordInstructionText;
import org.eclipse.osee.framework.core.xml.publishing.WordInstructionTextList;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraph;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphList;
import org.eclipse.osee.framework.core.xml.publishing.WordRun;
import org.eclipse.osee.framework.core.xml.publishing.WordRunList;
import org.eclipse.osee.framework.core.xml.publishing.WordRunStyle;
import org.eclipse.osee.framework.core.xml.publishing.WordRunStyleList;
import org.eclipse.osee.framework.core.xml.publishing.WordSection;
import org.eclipse.osee.framework.core.xml.publishing.WordSectionList;
import org.eclipse.osee.framework.core.xml.publishing.WordXmlAttribute;
import org.eclipse.osee.framework.core.xml.publishing.WordXmlTag;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
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
import org.w3c.dom.Document;

/**
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class WordMlLinkHandlerTest {

   /**
    * Set this flag to <code>true</code> to print a test start message, a test end message, and the received Word ML
    * documents to <code>stdout</code>.
    */

   private static boolean printDocuments = true;

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

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static int testBranchSpecificationRecordIdentifier = 1;

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
                      WordMlLinkHandlerTest.testBranchSpecificationRecordIdentifier,         /* BranchSpecificationRecord Identifier */
                      "Preview And MultiPreview Test Branch",                                /* Branch Name                          */
                      "Branch for Preview And MultiPreview Testing"                          /* Branch Creation Comment              */
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
                  WordMlLinkHandlerTest.testBranchSpecificationRecordIdentifier,                                    /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Link Test Folder",                                                               /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
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

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  ArtifactToken.valueOf                                                             /* Artifact Token                         (ArtifactToken)                         */
                                     (
                                        171717001L,                                                                 /* Artifact Identifier                    (Long)                                  */
                                        "Requirement_A_GUID___",                                                    /* Artifact GUID                          (String)                                */
                                        "Requirement A",                                                            /* Artifact Name                          (String)                                */
                                        BranchId.SENTINEL,                                                          /* Sentinel                               (BranchId)                              */
                                        CoreArtifactTypes.SoftwareRequirementMsWord                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                     ),
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
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                                       "<?mso-application progid=\"Word.Document\"?>\n" +
                                                       "<w:wordDocument\n" +
                                                       "   xmlns:w = \"http://schemas.microsoft.com/office/word/2003/wordml\"\n" +
                                                       "   xmlns:wx = \"http://schemas.microsoft.com/office/word/2003/auxHint\">\n" +
                                                       "   <w:body>\n" +
                                                       "      <wx:sect>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:hlink w:dest=\"http://127.0.0.1:8010/Define?guid=Requirement_B_GUID___\">\n" +
                                                       "               <w:r>\n" +
                                                       "                  <w:rPr>\n" +
                                                       "                     <w:rStyle w:val=\"Hyperlink\"/>\n" +
                                                       "                  </w:rPr>\n" +
                                                       "                  <w:t>Something Here</w:t>\n" +
                                                       "               </w:r>\n" +
                                                       "            </w:hlink>\n" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"begin\"/></w:r>\n" +
                                                       "            <w:r><w:instrText> HYPERLINK \\l \"OSEE.Requirement_B_GUID___\" </w:instrText></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n" +
                                                       "            <w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Something Here</w:t></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"end\"/></w:r>" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"begin\"/></w:r>\n" +
                                                       "            <w:r><w:instrText> HYPERLINK \\l \"OSEE.171717002\" </w:instrText></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n" +
                                                       "            <w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Something Here</w:t></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"end\"/></w:r>" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"begin\"/></w:r>\n" +
                                                       "            <w:r><w:instrText> REF OSEE.Requirement_B_GUID___ \\h \\n </w:instrText></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n" +
                                                       "            <w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Sec#</w:t></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"end\"/></w:r>" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"begin\"/></w:r>\n" +
                                                       "            <w:r><w:instrText> REF OSEE.171717002 \\h \\n </w:instrText></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n" +
                                                       "            <w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Sec#</w:t></w:r>\n" +
                                                       "            <w:r><w:fldChar w:fldCharType=\"end\"/></w:r>" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            OSEE_LINK(Requirement_B_GUID___)\n" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            OSEE_LINK(171717002)\n" +
                                                       "         </w:p>\n" +
                                                       "   </wx:sect>\n" +
                                                       "   </w:body>\n" +
                                                       "</w:wordDocument>\n"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  ArtifactToken.valueOf                                                             /* Artifact Token                         (ArtifactToken)                         */
                                     (
                                        171717002L,                                                                 /* Artifact Identifier                    (Long)                                  */
                                        "Requirement_B_GUID___",                                                    /* Artifact GUID                          (String)                                */
                                        "Requirement B",                                                            /* Artifact Name                          (String)                                */
                                        BranchId.SENTINEL,                                                          /* Sentinel                               (BranchId)                              */
                                        CoreArtifactTypes.SoftwareRequirementMsWord                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                     ),
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
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<w:p><w:r><w:t>This is Requirement B.</w:t></w:r></w:p>"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "6.6.6" ),                                                /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  ArtifactToken.valueOf                                                             /* Artifact Token                         (ArtifactToken)                         */
                                     (
                                        171717003L,                                                                 /* Artifact Identifier                    (Long)                                  */
                                        "Requirement_C_GUID___",                                                    /* Artifact GUID                          (String)                                */
                                        "Requirement C",                                                            /* Artifact Name                          (String)                                */
                                        BranchId.SENTINEL,                                                          /* Sentinel                               (BranchId)                              */
                                        CoreArtifactTypes.SoftwareRequirementMsWord                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                     ),
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement C's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                                       "<?mso-application progid=\"Word.Document\"?>\n" +
                                                       "<w:wordDocument\n" +
                                                       "   xmlns:w = \"http://schemas.microsoft.com/office/word/2003/wordml\"\n" +
                                                       "   xmlns:wx = \"http://schemas.microsoft.com/office/word/2003/auxHint\">\n" +
                                                       "   <w:body>\n" +
                                                       "      <wx:sect>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:hlink w:dest=\"http://localhost:8089/osee/loopback?branchUuid=7475842233288133453&amp;cmd=open.artifact&amp;guid=Requirement_B_GUID___&amp;isDeleted=false&amp;uuid=171717002\">\n" +
                                                       "               <w:r>\n" +
                                                       "                  <w:rPr>\n" +
                                                       "                     <w:rStyle w:val=\"Hyperlink\"/>\n" +
                                                       "                  </w:rPr>\n" +
                                                       "                  <w:t>Requirement B</w:t>\n" +
                                                       "               </w:r>\n" +
                                                       "            </w:hlink>\n" +
                                                       "         </w:p>\n" +
                                                       "   </wx:sect>\n" +
                                                       "   </w:body>\n" +
                                                       "</w:wordDocument>\n"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        /*
                         * Referencer artifact for testing missing references
                         */

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  ArtifactToken.valueOf                                                             /* Artifact Token                         (ArtifactToken)                         */
                                     (
                                        171717004L,                                                                 /* Artifact Identifier                    (Long)                                  */
                                        "Requirement_D_GUID___",                                                    /* Artifact GUID                          (String)                                */
                                        "Requirement D",                                                            /* Artifact Name                          (String)                                */
                                        BranchId.SENTINEL,                                                          /* Sentinel                               (BranchId)                              */
                                        CoreArtifactTypes.SoftwareRequirementMsWord                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                     ),
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement D's Description." ),                 /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                                       "<?mso-application progid=\"Word.Document\"?>\n" +
                                                       "<w:wordDocument\n" +
                                                       "   xmlns:w = \"http://schemas.microsoft.com/office/word/2003/wordml\"\n" +
                                                       "   xmlns:wx = \"http://schemas.microsoft.com/office/word/2003/auxHint\">\n" +
                                                       "   <w:body>\n" +
                                                       "      <wx:sect>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:hlink w:dest=\"http://localhost:8089/osee/loopback?branchUuid=7475842233288133453&amp;cmd=open.artifact&amp;guid=UNKNOWN_100_GUID_____&amp;isDeleted=false&amp;uuid=171717100\">\n" +
                                                       "               <w:r>\n" +
                                                       "                  <w:rPr>\n" +
                                                       "                     <w:rStyle w:val=\"Hyperlink\"/>\n" +
                                                       "                  </w:rPr>\n" +
                                                       "                  <w:t>Requirement X</w:t>\n" +
                                                       "               </w:r>\n" +
                                                       "            </w:hlink>\n" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:hlink w:dest=\"http://localhost:8089/osee/loopback?branchUuid=7475842233288133453&amp;cmd=open.artifact&amp;guid=UNKNOWN_100_GUID_____&amp;isDeleted=false\">\n" +
                                                       "               <w:r>\n" +
                                                       "                  <w:rPr>\n" +
                                                       "                     <w:rStyle w:val=\"Hyperlink\"/>\n" +
                                                       "                  </w:rPr>\n" +
                                                       "                  <w:t>Requirement X</w:t>\n" +
                                                       "               </w:r>\n" +
                                                       "            </w:hlink>\n" +
                                                       "         </w:p>\n" +
                                                       "         <w:p>\n" +
                                                       "            <w:hlink w:dest=\"http://localhost:8089/osee/loopback?branchUuid=7475842233288133453&amp;cmd=open.artifact&amp;isDeleted=false&amp;uuid=171717100\">\n" +
                                                       "               <w:r>\n" +
                                                       "                  <w:rPr>\n" +
                                                       "                     <w:rStyle w:val=\"Hyperlink\"/>\n" +
                                                       "                  </w:rPr>\n" +
                                                       "                  <w:t>Requirement X</w:t>\n" +
                                                       "               </w:r>\n" +
                                                       "            </w:hlink>\n" +
                                                       "         </w:p>\n" +
                                                       "   </wx:sect>\n" +
                                                       "   </w:body>\n" +
                                                       "</w:wordDocument>\n"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )
                     )
               )
         );

   /**
    *
    */

   private static PublishingEndpoint publishingEndpoint;

   /**
    * Saves a {@link Map} of the artifacts associated with each {@link BuilderRecord}.
    */

   private static Map<Integer, Optional<Artifact>> builderRecordMap;

   /**
    * Save a reference to the {@link PublishingXmlUtils} helper object.
    */

   private static PublishingXmlUtils publishingXmlUtils;

   /*
    * The base URL of the server. Used to build expected links.
    */

   private static String serverUrl;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Link Handler Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for OSEE Link Testing";

   @BeforeClass
   public static void testSetup() {

      /*
       * Setup XML utils
       */

      WordMlLinkHandlerTest.publishingXmlUtils = new PublishingXmlUtils();

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(WordMlLinkHandlerTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            WordMlLinkHandlerTest.branchSpecifications,
            WordMlLinkHandlerTest.artifactSpecifications
         );

      WordMlLinkHandlerTest.builderRecordMap =
         WordMlLinkHandlerTest
            .artifactSpecifications
            .stream( WordMlLinkHandlerTest.testBranchSpecificationRecordIdentifier )
            .map( ArtifactSpecificationRecord::getIdentifier )
            .collect
               (
                  Collectors.toMap
                     (
                        Function.identity(),
                        ( builderRecordIdentifier ) -> testDocumentBuilder.getArtifact
                                                          (
                                                             WordMlLinkHandlerTest.testBranchSpecificationRecordIdentifier,
                                                             builderRecordIdentifier
                                                          )
                     )
               );
      //@formatter:on

      /*
       * Get Services
       */

      WordMlLinkHandlerTest.publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();

      /*
       * Generate Server base URL
       */

      serverUrl = new ArtifactUrlClient().getSelectedPermanentLinkUrl();

   }

   /**
    * Announces the start of the current test to Stdout.
    */

   @Before
   public void before() {
      if (WordMlLinkHandlerTest.printDocuments) {
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
      if (WordMlLinkHandlerTest.printDocuments) {
         System.out.println("-------------------------------------------------------------");
         System.out.println("Test End: " + this.testName.getMethodName());
         System.out.println("=============================================================");
      }
   }

   /**
    * Build a new {@link AssertionError} with error message for a {@link PublishingXmlUtils} method failure.
    *
    * @param publishingXmlUtils reference to the {@link PublishingXmlUtils} object that contains the failure.
    * @param errorStatement description of the error
    * @param documentString {@link String} representation of the XML document being processed with the error occurred.
    * @return the new {@link AssertionError} object.
    */

   private static AssertionError buildAssertionError(String errorStatement, String... documentStrings) {

      var error = WordMlLinkHandlerTest.publishingXmlUtils.getLastError();
      var cause = WordMlLinkHandlerTest.publishingXmlUtils.getLastCause();
      //@formatter:off
      var documentString =
         ( Objects.nonNull(documentStrings) && documentStrings.length > 0 )
            ? documentStrings[0]
            : null;

      var message =
         new Message()
                .title( errorStatement )
                .indentInc()
                .segment( "Cause", cause )
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
    * Parse an XML document into an XML DOM to verify it is valid XML.
    *
    * @param file the XML document to be parsed.
    * @throws AssertionError if the XML document fails to parse.
    */

   private Document parseDocument(String content) {
      //@formatter:off
      var document =
         WordMlLinkHandlerTest.publishingXmlUtils.parse( content )
            .orElseThrow
               (
                 () -> WordMlLinkHandlerTest.buildAssertionError
                          (
                             "Failed to parse XML.",
                             content
                          )
               );

      var documentString =
         WordMlLinkHandlerTest.publishingXmlUtils.prettyPrint( document )
            .orElseThrow
               (
                 () -> WordMlLinkHandlerTest.buildAssertionError
                          (
                             "Failed to pretty print preview XML."
                          )
               );
      //@formatter:on
      this.printDocument(documentString);

      return document;
   }

   /**
    * Prints the currently running test method name followed by the received Word ML document for the test.
    *
    * @param documentString the Word ML document as a {@link String}.
    */

   private void printDocument(String documentString) {
      if (WordMlLinkHandlerTest.printDocuments) {
         System.out.println("=============================================================");
         System.out.println("Test Name: " + this.testName.getMethodName());
         System.out.println("-------------------------------------------------------------");
         System.out.println(documentString);
         System.out.println("=============================================================");
      }
   }

   private static void checkParagraphInternalDocReference(WordParagraph wordParagraph, String expectedLinkText) {

      //@formatter:off
      WordRunList wordRunList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordParagraph,
                  WordXmlTag.RUN,
                  WordRunList::new,
                  WordRun::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError("Failed to parse runs from a paragraph.")
               );
      //@formatter:on

      Assert.assertEquals("Unexpected number of runs in paragraph.", 5, wordRunList.size());

      var wordRun = wordRunList.get(0).orElseThrow();

      //@formatter:off
      WordFieldCharacterList wordFieldCharacterList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordRun,
                  WordXmlTag.FIELD_CHARACTER,
                  WordFieldCharacterList::new,
                  WordFieldCharacter::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError("Failed to parse runs from a paragraph.")
               );
      //@formatter:on
      //@formatter:off
      Assert.assertEquals
         (
            "Unexpected number of field characters in paragraph.",
            1,
            wordFieldCharacterList.size()
         );
      //@formatter:on

      var wordFieldCharacter = wordFieldCharacterList.get(0).orElseThrow();

      final var expectedOpenFieldCharacterType = "begin";
      final var openFieldCharacterType =
         wordFieldCharacter.getAttribute(WordXmlAttribute.FIELD_CHARACTER_TYPE).orElseThrow();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected Field Character Type in paragraph." )
                         .indentInc()
                         .segment( "Expected Type", expectedOpenFieldCharacterType )
                         .segment( "Actual Type",   openFieldCharacterType         )
                         .toString(),
            expectedOpenFieldCharacterType,
            openFieldCharacterType
         );
      //@formatter:on

      wordRun = wordRunList.get(1).orElseThrow();

      //@formatter:off
      WordInstructionTextList wordInstructionTextList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordRun,
                  WordXmlTag.INSTRUCTION_TEXT,
                  WordInstructionTextList::new,
                  WordInstructionText::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError( "Failed to parse runs from a paragraph." )
               );
      //@formatter:on

      //@formatter:off
      Assert.assertEquals
         (
            "Unexpected number of instruction text elements in paragraph.",
            1,
            wordInstructionTextList.size()
         );
      //@formatter:on

      var wordInstructionText = wordInstructionTextList.get(0).orElseThrow();

      final var expectedInstructionText = "HYPERLINK \\l \"OSEE.171717002\"";
      final var instructionText = wordInstructionText.getText().trim();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected Instruction Text in first paragraph." )
                         .indentInc()
                         .segment( "Expected Instruction Text", expectedInstructionText )
                         .segment( "Actual Instruction Text",   instructionText         )
                         .toString(),
            expectedInstructionText,
            instructionText
         );
      //@formatter:on

      wordRun = wordRunList.get(2).orElseThrow();

      //@formatter:off
      wordFieldCharacterList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordRun,
                  WordXmlTag.FIELD_CHARACTER,
                  WordFieldCharacterList::new,
                  WordFieldCharacter::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError( "Failed to parse runs from a paragraph." )
               );
      //@formatter:on

      Assert.assertEquals("Unexpected number of field characters in first paragraph.", 1,
         wordFieldCharacterList.size());

      wordFieldCharacter = wordFieldCharacterList.get(0).orElseThrow();

      final var expectedMiddleFieldCharacterType = "separate";
      final var middleFieldCharacterType =
         wordFieldCharacter.getAttribute(WordXmlAttribute.FIELD_CHARACTER_TYPE).orElseThrow();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected Field Character Type in first paragraph." )
                         .indentInc()
                         .segment("Expected Type", expectedMiddleFieldCharacterType )
                         .segment("Actual Type",   middleFieldCharacterType         )
                         .toString(),
            expectedMiddleFieldCharacterType,
            middleFieldCharacterType
         );
      //@formatter:on

      wordRun = wordRunList.get(3).orElseThrow();

      //@formatter:off
      WordRunStyleList wordRunStyleList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordRun,
                  WordXmlTag.RUN_STYLE,
                  WordRunStyleList::new,
                  WordRunStyle::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError( "Failed to parse runs from a paragraph." )
               );
      //@formatter:on

      Assert.assertEquals("Unexpected number of field characters in first paragraph.", 1, wordRunStyleList.size());

      var wordRunStyle = wordRunStyleList.get(0).orElseThrow();

      var expectedRunStyle = "Hyperlink";

      var runStyle = wordRunStyle.getAttribute(WordXmlAttribute.VALUE).orElseThrow();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected Run Style." )
                         .indentInc()
                         .segment( "Expected Value", expectedRunStyle )
                         .segment( "Actual Value",   runStyle )
                         .toString(),
            expectedRunStyle,
            runStyle
         );
      //@formatter:on

      final var linkText = wordRun.getText().trim();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title("Unexpected link text.")
                         .indentInc()
                         .segment( "Expected Link Text", expectedLinkText )
                         .segment( "Actual Link Text",   linkText         )
                         .toString(),
            expectedLinkText,
            linkText
         );
      //@formatter:on

      wordRun = wordRunList.get(4).orElseThrow();

      //@formatter:off
      wordFieldCharacterList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordRun,
                  WordXmlTag.FIELD_CHARACTER,
                  WordFieldCharacterList::new,
                  WordFieldCharacter::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError( "Failed to parse runs from a paragraph." )
               );
      //@formatter:on

      Assert.assertEquals("Unexpected number of field characters in first paragraph.", 1,
         wordFieldCharacterList.size());

      wordFieldCharacter = wordFieldCharacterList.get(0).orElseThrow();

      final var expectedCloseFieldCharacterType = "end";

      final var closeFieldCharacterType =
         wordFieldCharacter.getAttribute(WordXmlAttribute.FIELD_CHARACTER_TYPE).orElseThrow();

      //@formatter:off
      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
            .title( "Unexpected Field Character Type in first paragraph.")
            .indentInc()
            .segment("Expected Type", expectedCloseFieldCharacterType )
            .segment("Actual Type",   closeFieldCharacterType         )
            .toString(),
            expectedCloseFieldCharacterType,
            closeFieldCharacterType
         );
      //@formatter:on
   }

   private static class UrlParseResult {

      private final String path;
      private final Map<String, String> queryParameters;

      UrlParseResult(String path, Map<String, String> queryParameters) {
         this.path = path;
         this.queryParameters = queryParameters;
      }

      String getPath() {
         return this.path;
      }

      Map<String, String> getQueryParameters() {
         return Collections.unmodifiableMap(this.queryParameters);
      }

      String getQueryParameter(String parameterName) {
         return this.queryParameters.get(parameterName);
      }

      boolean contains(String queryParameterName) {
         return this.queryParameters.containsKey(queryParameterName);
      }
   }

   private static UrlParseResult parse(String xmlUrlString) {

      try {

         var parameters = new HashMap<String, String>();

         var urlString = XmlEncoderDecoder.xmlToText(xmlUrlString).toString();

         var url = new URL(urlString);

         var path = url.getPath();

         var queryParameters = url.getQuery();

         for (var pair : queryParameters.split("&")) {

            var i = pair.indexOf('=');

            //@formatter:off
            var name =
               ( i == -1 )
                  ? pair
                  : pair.substring( 0, i );
            //@formatter:on

            //@formatter:off
            var value =
               ( i == -1 )
                  ? null
                  : pair.substring( i + 1);
            //@formatter:on

            parameters.put(name, value);
         }

         return new UrlParseResult(path, parameters);

      } catch (Exception e) {

         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( "Failed to parse URL." )
                             .indentInc()
                             .segment( "XML URL String", xmlUrlString )
                             .reasonFollows( e )
                             .toString()
                   );
         //@formatter:on
      }
   }

   //@formatter:off
   private static void
      checkParagraphOseeServerLink
         (
            WordParagraph      wordParagraph,
            String             expectedUrlPath,
            Map<String,String> expectedQueryParameters,
            Set<String>        unexpectedQueryParameters,
            String             expectedLinkText
         ) {

      WordHlinkList wordHlinkList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordParagraph,
                  WordXmlTag.HLINK,
                  WordHlinkList::new,
                  WordHlink::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to parse hlinks from a paragraph."
                           )
               );

      Assert.assertEquals
         (
            "Unexpected number of hlinks in paragraph",
            1,
            wordHlinkList.size()
         );

      var wordHlink =
         wordHlinkList
            .get( 0 )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to get first hlink from paragraph."
                           )
               );

      var hlinkDest =
         wordHlink
            .getAttribute( WordXmlAttribute.DESTINATION )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to dest attribute from hlink."
                           )
               );

      var urlParseResult = WordMlLinkHandlerTest.parse( hlinkDest );

      var urlPath = urlParseResult.getPath();

      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected hlink dest attribute value." )
                         .indentInc()
                         .segment( "Expected URL Path", expectedUrlPath )
                         .segment( "Actual URL Path",   urlPath         )
                         .toString(),
            expectedUrlPath,
            urlParseResult.getPath()
         );

      for( var expectedQueryParameterName : expectedQueryParameters.keySet() ) {

         WordMlLinkHandlerTest.assertEquals
            (
               () -> new Message()
                            .title( "Expected query parameter is not present." )
                            .indentInc()
                            .segment( "Expected Query Parameter", expectedQueryParameterName )
                            .toString(),
               urlParseResult.contains( expectedQueryParameterName ),
               true
            );

         var expectedQueryParameterValue = expectedQueryParameters.get( expectedQueryParameterName );
         var actualQueryParameterValue = urlParseResult.getQueryParameter(expectedQueryParameterName);

         WordMlLinkHandlerTest.assertEquals
            (
               () -> new Message()
                            .title( "Unexpected query parameter value." )
                            .indentInc()
                            .segment( "Query Parameter Name", expectedQueryParameterName  )
                            .segment( "Expected Value",       expectedQueryParameterValue )
                            .segment( "Actual Value",         actualQueryParameterValue   )
                            .toString(),
               expectedQueryParameterValue,
               actualQueryParameterValue
            );
      }

      var linkText = wordParagraph.getText().trim();

      WordMlLinkHandlerTest.assertEquals
         (
            () -> new Message()
                         .title( "Unexpected link text." )
                         .indentInc()
                         .segment( "Expected Link Text", expectedLinkText )
                         .segment( "Actual Link Text",   linkText         )
                         .toString(),
            expectedLinkText,
            linkText
         );
      //@formatter:on
   }

   private static <T> void assertEquals(Supplier<String> messageGenerator, T expectedValue, T actualValue) {

      if (!expectedValue.equals(actualValue)) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      messageGenerator.get()
                   );
         //@formatter:on
      }
   }

   private static void processParagraphList(Document document, int expectedSectionCount, int expectedParagraphCount, BiConsumer<Integer, WordParagraph> paragraphChecker) {

      //@formatter:off
      var wordDocument =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseWordDocument( document )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to parse WordDocument."
                           )
               );

      var wordBody =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseWordBody( wordDocument )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                  (
                     "Failed to parse WordBody."
                  )
               );

      WordSectionList wordSectionList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordBody,
                  WordXmlTag.SECTION,
                  WordSectionList::new,
                  WordSection::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to parse Word Section List."
                           )
               );

      Assert.assertEquals
         (
            "Unexpected number of sections in word body.",
            expectedSectionCount,
            wordSectionList.size()
         );

      var wordSection =
         wordSectionList
            .get( 0 )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to get first section in word body."
                           )
               );

      WordParagraphList wordParagraphList =
         WordMlLinkHandlerTest.publishingXmlUtils
            .parseChildListFromParent
               (
                  wordSection,
                  WordXmlTag.PARAGRAPH,
                  WordParagraphList::new,
                  WordParagraph::new
               )
            .orElseThrow
               (
                  () -> WordMlLinkHandlerTest.buildAssertionError
                           (
                              "Failed to parse paragraphs from the first Word Section."
                           )
               );

      Assert.assertEquals
         (
            "Unexpected number of paragraphs in first Word Section",
            expectedParagraphCount,
            wordParagraphList.size()
         );

      for( var i = 0; i < expectedParagraphCount; i++ ) {

         final var iFinal = i;

         var wordParagraph =
            wordParagraphList
               .get( i )
               .orElseThrow
                  (
                     () -> WordMlLinkHandlerTest.buildAssertionError
                              (
                                 "Failed to get paragraph [" + iFinal + "] from the section."
                              )
                  );

         paragraphChecker.accept( i, wordParagraph );

      }
   }

   @Test
   public void testA() {

      var artifact = WordMlLinkHandlerTest.builderRecordMap.get(2).get();
      var branchIdString = artifact.getBranchIdString();

      /*
       * Setup link checkers map.
       */

      var linkCheckers = new EnumMap<LinkType,Consumer<WordParagraph>>(LinkType.class);
      //@formatter:off
      linkCheckers.put
         (
            LinkType.OSEE_SERVER_LINK,
            ( wordParagraph ) ->
               WordMlLinkHandlerTest
                  .checkParagraphOseeServerLink
                     (
                        wordParagraph,
                        "/osee/loopback",
                        Map.of
                           (
                              "branchUuid", branchIdString ,
                              "cmd",        "open.artifact",
                              "isDeleted",  "false",
                              "uuid",       "171717002",
                              "guid",       "Requirement_B_GUID___"
                           ),
                        Set.of(),
                        "Requirement B"
                     )
         );

      linkCheckers.put
         (
            LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
            ( wordParagraph ) ->
               WordMlLinkHandlerTest
                  .checkParagraphInternalDocReference
                     (
                        wordParagraph,
                        "Requirement B"
                     )
         );

      linkCheckers.put
         (
            LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER,
            ( wordParagraph ) ->
               WordMlLinkHandlerTest
                  .checkParagraphInternalDocReference
                     (
                        wordParagraph,
                        "6.6.6"
                     )
         );

      linkCheckers.put
         (
            LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME,
            ( wordParagraph ) ->
               WordMlLinkHandlerTest
                  .checkParagraphInternalDocReference
                     (
                        wordParagraph,
                        "6.6.6 Requirement B"
                     )
         );
      //@formatter:on

      //@formatter:off
      linkCheckers.keySet().forEach
         (
            ( linkType ) ->
            {

               var linkHandlerResult =
                  WordMlLinkHandlerTest.publishingEndpoint
                     .link
                        (
                           BranchId.valueOf( artifact.getBranch().getId() ),
                           ArtifactId.SENTINEL,
                           ArtifactId.valueOf( artifact.getId() ),
                           TransactionId.SENTINEL,
                           linkType,
                           PresentationType.DEFAULT_OPEN
                        );

               var content = linkHandlerResult.getContent();

               var document = this.parseDocument( content );

               WordMlLinkHandlerTest.processParagraphList
                  (
                     document,
                     1, /* expected section count   */
                     7, /* expected paragraph count */
                     ( i, wordParagraph ) ->  linkCheckers
                                                 .get( linkType )
                                                 .accept( wordParagraph )
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testB() {
      //@formatter:off
      var artifact = WordMlLinkHandlerTest.builderRecordMap.get(4).get();

      var linkHandlerResult =
         WordMlLinkHandlerTest.publishingEndpoint
            .link
               (
                  BranchId.valueOf( artifact.getBranch().getId() ),
                  ArtifactId.SENTINEL,
                  ArtifactId.valueOf( artifact.getId() ),
                  TransactionId.SENTINEL,
                  LinkType.OSEE_LINK_MARKER,
                  PresentationType.DEFAULT_OPEN
               );

      var content = linkHandlerResult.getContent();

      var document = this.parseDocument( content );
      //@formatter:on
   }

   @Test
   public void testC() {
      //@formatter:off
      var artifact = WordMlLinkHandlerTest.builderRecordMap.get(5).get();
      var branchIdString = artifact.getBranchIdString();

      var linkCheckers =
         List.<Consumer<WordParagraph>>of
            (
               ( wordParagraph ) ->
                  WordMlLinkHandlerTest
                     .checkParagraphOseeServerLink
                        (
                           wordParagraph,
                           "/unknown",
                           Map.of
                              (
                                 "branchUuid", branchIdString ,
                                 "uuid",       "171717100"
                              ),
                           Set.of
                              (
                                 "guid"
                              ),
                           new StringBuilder()
                                  .append( "Invalid Link: artifact with uuid:[" )
                                  .append( "171717100" )
                                  .append( "] on branchUuid:[" )
                                  .append( branchIdString )
                                  .append( "] does not exist" )
                                  .toString()
                        ),

               ( wordParagraph ) ->
                  WordMlLinkHandlerTest
                     .checkParagraphOseeServerLink
                        (
                           wordParagraph,
                           "/unknown",
                           Map.of
                              (
                                 "branchUuid", branchIdString ,
                                 "guid",       "UNKNOWN_100_GUID_____"
                              ),
                           Set.of
                              (
                                 "uuid"
                              ),
                           new StringBuilder()
                                  .append( "Invalid Link: artifact with guid:[" )
                                  .append( "UNKNOWN_100_GUID_____" )
                                  .append( "] on branchUuid:[" )
                                  .append( branchIdString )
                                  .append( "] does not exist" )
                                  .toString()
                        ),

               ( wordParagraph ) ->
                  WordMlLinkHandlerTest
                     .checkParagraphOseeServerLink
                        (
                           wordParagraph,
                           "/unknown",
                           Map.of
                              (
                                 "branchUuid", branchIdString ,
                                 "uuid",       "171717100"
                              ),
                           Set.of(),
                           new StringBuilder()
                                  .append( "Invalid Link: artifact with uuid:[" )
                                  .append( "171717100" )
                                  .append( "] on branchUuid:[" )
                                  .append( branchIdString )
                                  .append( "] does not exist" )
                                  .toString()
                        )
            );

      var linkHandlerResult =
         WordMlLinkHandlerTest.publishingEndpoint
            .link
               (
                  BranchId.valueOf( artifact.getBranch().getId() ),
                  ArtifactId.SENTINEL,
                  ArtifactId.valueOf( artifact.getId() ),
                  TransactionId.SENTINEL,
                  LinkType.OSEE_LINK_MARKER,
                  PresentationType.DEFAULT_OPEN
               );

      var content = linkHandlerResult.getContent();

      var document = this.parseDocument( content );

      WordMlLinkHandlerTest.processParagraphList
         (
            document,
            1,
            3,
            ( i, wordParagraph ) -> linkCheckers.get(i).accept( wordParagraph )
         );
      //@formatter:on

   }

}
