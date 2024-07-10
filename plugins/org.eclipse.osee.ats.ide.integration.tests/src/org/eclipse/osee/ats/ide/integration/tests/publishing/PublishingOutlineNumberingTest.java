/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTemplateSetterImpl;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.IncludeHeadings;
import org.eclipse.osee.framework.core.publishing.IncludeMainContentForHeadings;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.core.xml.publishing.AbstractElement;
import org.eclipse.osee.framework.core.xml.publishing.AbstractElementList;
import org.eclipse.osee.framework.core.xml.publishing.AuxHintAttribute;
import org.eclipse.osee.framework.core.xml.publishing.AuxHintSubSectionList;
import org.eclipse.osee.framework.core.xml.publishing.AuxHintTextList;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.core.xml.publishing.WordBody;
import org.eclipse.osee.framework.core.xml.publishing.WordElementParserFactory;
import org.eclipse.osee.framework.core.xml.publishing.WordMlAttribute;
import org.eclipse.osee.framework.core.xml.publishing.WordMlTag;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraph;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphList;
import org.eclipse.osee.framework.core.xml.publishing.WordParagraphStyleList;
import org.eclipse.osee.framework.core.xml.publishing.WordSectionList;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.eclipse.osee.orcs.core.util.PublishingTemplateContentMapEntry;
import org.junit.AfterClass;
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
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class PublishingOutlineNumberingTest {

   /**
    * Set this flag to <code>true</code> to clean up of the test branches.
    */

   private static boolean cleanUp = true;

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
    * {@Link UserToken} cache has been flushed.</dd></dt>
    * <dt>NoPopUpsRule</dt>
    * <dd>Prevents word documents from being launched for the user during tests.</dd>
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

   /**
    * Method used to obtain the parameters for each test run.
    *
    * @return an array of the parameters for the test.
    */

   @Parameters(name = "{0}{1}")
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.<Object[]>of
            (
               /*
                * Client Tests
                */

               /*
                * Outline Numbering Tests with a Maximum Depth and Heading Artifact Type
                */

               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,Anything"                                                       },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth5,Anything,StartingOutlineNumber"                                 },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth4,Anything"                                                       },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,OnlyHeadersFolders"                                             },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth4,OnlyHeadersFolders"                                             },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,OnlyFolders"                                                    },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth4,OnlyFolders"                                                    },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,OnlyHeaders"                                                    },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth4,OnlyHeaders"                                                    },
               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,Anything,SystemHeadings"                                        },

               /*
                * Heading Attribute Type Tests
                */

               new Object[] { RenderLocation.CLIENT, ",Word,TempA,Depth3,OnlyFolders,Annotation"                                         },

               /*
                * Include Headings Tests
                */

               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Folders,NonHeadingDescendants"                                    },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Folders,MainContentDescendants"                                   },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Folders,MainContentDescendants,SoftwareControlCategoryRationale"  },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Folders,MainContentDescendants,Annotation"                        },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,HeadersInFoldersTree,NonHeadingDescendants"                       },

               new Object[] { RenderLocation.CLIENT, ",Headings,HeadersOnly,ExcludeFolders"                                              },

               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Headers,NonHeadingDescendants"                                    },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Headers,MainContentDescendants"                                   },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Headers,MainContentDescendants,SoftwareControlCategoryRationale"  },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,Headers,MainContentDescendants,Annotation"                        },
               new Object[] { RenderLocation.CLIENT, ",IncludeHeadings,FoldersInHeadersTree,NonHeadingDescendants"                       },

               new Object[] { RenderLocation.CLIENT, ",Headings,FoldersOnly,ExcludeHeaders"                                              },

               /*
                * Include Main Content For Headings
                */

               new Object[] { RenderLocation.CLIENT, ",IncludeMainContentForHeadingsAlways"                                              },
               new Object[] { RenderLocation.CLIENT, ",IncludeMainContentForHeadingsNever"                                               },

               /*
                * Server Tests
                */

               /*
                * Outline Numbering Tests with a Maximum Depth and Heading Artifact Type
                */

               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,Anything"                                                       },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth5,Anything,StartingOutlineNumber"                                 },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth4,Anything"                                                       },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,OnlyHeadersFolders"                                             },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth4,OnlyHeadersFolders"                                             },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,OnlyFolders"                                                    },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth4,OnlyFolders"                                                    },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,OnlyHeaders"                                                    },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth4,OnlyHeaders"                                                    },
               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,Anything,SystemHeadings"                                        },

               /*
                * Heading Attribute Type Tests
                */

               new Object[] { RenderLocation.SERVER, ",Word,TempA,Depth3,OnlyFolders,Annotation"                                         },

               /*
                * Include Headings Tests
                */

               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Folders,NonHeadingDescendants"                                    },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Folders,MainContentDescendants"                                   },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Folders,MainContentDescendants,SoftwareControlCategoryRationale"  },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Folders,MainContentDescendants,Annotation"                        },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,HeadersInFoldersTree,NonHeadingDescendants"                       },

               new Object[] { RenderLocation.SERVER, ",Headings,HeadersOnly,ExcludeFolders"                                              },

               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Headers,NonHeadingDescendants"                                    },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Headers,MainContentDescendants"                                   },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Headers,MainContentDescendants,SoftwareControlCategoryRationale"  },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,Headers,MainContentDescendants,Annotation"                        },
               new Object[] { RenderLocation.SERVER, ",IncludeHeadings,FoldersInHeadersTree,NonHeadingDescendants"                       },

               new Object[] { RenderLocation.SERVER, ",Headings,FoldersOnly,ExcludeHeaders"                                              },

               /*
                * Include Main Content For Headings
                */

               new Object[] { RenderLocation.SERVER, ",IncludeMainContentForHeadingsAlways"                                              },
               new Object[] { RenderLocation.SERVER, ",IncludeMainContentForHeadingsNever"                                               }

            );
   }

   private static Map<String,Object[]> testParameters =
      Map.ofEntries
         (
            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth3,Anything",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,     FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,       "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,     3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<any-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3, 2, 3 ),                                        //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 2, 2 ),                                     //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 ), //child paragraph counts
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of(),                                     //section 1.1.4
                                             List.of(),                                     //section 1.1.5
                                             List.of(),                                     //section 1.1.6
                                             List.of(),                                     //section 1.1.7
                                             List.of(),                                     //section 1.1.8
                                             List.of(),                                     //section 1.1.9
                                             List.of(),                                     //section 1.1.10
                                             List.of(),                                     //section 1.1.11
                                             List.of(),                                     //section 1.1.12
                                             List.of(),                                     //section 1.1.13
                                             List.of(),                                     //section 1.1.14
                                             List.of(),                                     //section 1.1.15
                                             List.of(),                                     //section 1.1.16
                                             List.of(),                                     //section 1.1.17
                                             List.of(),                                     //section 1.1.18
                                             List.of(),                                     //section 1.1.19
                                             List.of(),                                     //section 1.1.20
                                             List.of(),                                     //section 1.1.21
                                             List.of(),                                     //section 1.1.22
                                             List.of()                                      //section 1.1.23
                                          ),
                                       List.of()                                            //section 1.2
                                    ),
                                 List.of                                                    //section 2
                                    (
                                       List.of( 2 ),                                        //child paragraph counts
                                       List.of                                              //section 2.1
                                          (
                                             List.of( 2, 2, 2, 2 ),                         //child paragraph counts
                                             List.of(),                                     //section 2.1.1
                                             List.of(),                                     //section 2.1.2
                                             List.of(),                                     //section 2.1.3
                                             List.of()                                      //section 2.1.4
                                          )
                                    ),
                                 List.of()                                                  //section 3
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",      "Name: One (1)"                                                                      },
                        { "Heading2", "1.1",    "Name: One One (2)"                                                                  },
                        { "Heading3", "1.1.1",  "Name: One One One (3)"                                                              },
                        { "Heading3", "1.1.2",  "Name: One One One One (4)"                                                          },
                        { "Heading3", "1.1.3",  "Name: One One One One One (5)"                                                      },
                        { "Heading3", "1.1.4",  "Name: One One One One One One (6)"                                                  },
                        { "Heading3", "1.1.5",  "Name: One One One One One One One (7)"                                              },
                        { "Heading3", "1.1.6",  "Name: One One One One One One One One (8)"                                          },
                        { "Heading3", "1.1.7",  "Name: One One One One One One One One One (9)"                                      },
                        { "Heading3", "1.1.8",  "Name: One One One One One One One One One One (10)"                                 },
                        { "Heading3", "1.1.9",  "Name: One One One One One One One One One One One (11)"                             },
                        { "Heading3", "1.1.10", "Name: One One One One One One One One One One One One (12)"                         },
                        { "Heading3", "1.1.11", "Name: One One One One One One One One One One One One One (13)"                     },
                        { "Heading3", "1.1.12", "Name: One One One One One One One One One One One One One One (14)"                 },
                        { "Heading3", "1.1.13", "Name: One One One One One One One One One One One One One One One (15)"             },
                        { "Heading3", "1.1.14", "Name: One One One One One One One One One One One One One One One One (16)"         },
                        { "Heading3", "1.1.15", "Name: One One One One One One One One One One One One One One One One One (17)"     },
                        { "Heading3", "1.1.16", "Name: One One One One One One One One One One One One One One One One One One (18)" },
                        { "Heading3", "1.1.17", "Name: One One One One One One One One Two (9)"                                      },
                        { "Heading3", "1.1.18", "Name: One One One One One One One Two (8)"                                          },
                        { "Heading3", "1.1.19", "Name: One One One One One One Two (7)"                                              },
                        { "Heading3", "1.1.20", "Name: One One One One One Two (6)"                                                  },
                        { "Heading3", "1.1.21", "Name: One One One One Two (5)"                                                      },
                        { "Heading3", "1.1.22", "Name: One One One Two (4)"                                                          },
                        { "Heading3", "1.1.23", "Name: One One Two (3)"                                                              },
                        { "Heading2", "1.2",    "Name: One Two (2)"                                                                  },
                        { "Heading1", "2",      "Name: Two (1)"                                                                      },
                        { "Heading2", "2.1",    "Name: Two One (2)"                                                                  },
                        { "Heading3", "2.1.1",  "Name: Two One One (3)"                                                              },
                        { "Heading3", "2.1.2",  "Name: Two One One One (4)"                                                          },
                        { "Heading3", "2.1.3",  "Name: Two One One One One (5)"                                                      },
                        { "Heading3", "2.1.4",  "Name: Two One One One One One (6)"                                                  },
                        { "Heading1", "3",      "Name: Three (1)"                                                                    }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth5,Anything,StartingOutlineNumber",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                               FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                 "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,                               5,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<any-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_OUTLINE_NUMBER,        "5.8.4"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3, 2, 3 ),                                        //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 2, 2 ),                                     //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 ), //child paragraph counts
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of(),                                     //section 1.1.4
                                             List.of(),                                     //section 1.1.5
                                             List.of(),                                     //section 1.1.6
                                             List.of(),                                     //section 1.1.7
                                             List.of(),                                     //section 1.1.8
                                             List.of(),                                     //section 1.1.9
                                             List.of(),                                     //section 1.1.10
                                             List.of(),                                     //section 1.1.11
                                             List.of(),                                     //section 1.1.12
                                             List.of(),                                     //section 1.1.13
                                             List.of(),                                     //section 1.1.14
                                             List.of(),                                     //section 1.1.15
                                             List.of(),                                     //section 1.1.16
                                             List.of(),                                     //section 1.1.17
                                             List.of(),                                     //section 1.1.18
                                             List.of(),                                     //section 1.1.19
                                             List.of(),                                     //section 1.1.20
                                             List.of(),                                     //section 1.1.21
                                             List.of(),                                     //section 1.1.22
                                             List.of()                                      //section 1.1.23
                                          ),
                                       List.of()                                            //section 1.2
                                    ),
                                 List.of                                                    //section 2
                                    (
                                       List.of( 2 ),                                        //child paragraph counts
                                       List.of                                              //section 2.1
                                          (
                                             List.of( 2, 2, 2, 2 ),                         //child paragraph counts
                                             List.of(),                                     //section 2.1.1
                                             List.of(),                                     //section 2.1.2
                                             List.of(),                                     //section 2.1.3
                                             List.of()                                      //section 2.1.4
                                          )
                                    ),
                                 List.of()                                                  //section 3
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading3", "5.8.4",      "Name: One (1)"                                                                      },
                        { "Heading4", "5.8.4.1",    "Name: One One (2)"                                                                  },
                        { "Heading5", "5.8.4.1.1",  "Name: One One One (3)"                                                              },
                        { "Heading5", "5.8.4.1.2",  "Name: One One One One (4)"                                                          },
                        { "Heading5", "5.8.4.1.3",  "Name: One One One One One (5)"                                                      },
                        { "Heading5", "5.8.4.1.4",  "Name: One One One One One One (6)"                                                  },
                        { "Heading5", "5.8.4.1.5",  "Name: One One One One One One One (7)"                                              },
                        { "Heading5", "5.8.4.1.6",  "Name: One One One One One One One One (8)"                                          },
                        { "Heading5", "5.8.4.1.7",  "Name: One One One One One One One One One (9)"                                      },
                        { "Heading5", "5.8.4.1.8",  "Name: One One One One One One One One One One (10)"                                 },
                        { "Heading5", "5.8.4.1.9",  "Name: One One One One One One One One One One One (11)"                             },
                        { "Heading5", "5.8.4.1.10", "Name: One One One One One One One One One One One One (12)"                         },
                        { "Heading5", "5.8.4.1.11", "Name: One One One One One One One One One One One One One (13)"                     },
                        { "Heading5", "5.8.4.1.12", "Name: One One One One One One One One One One One One One One (14)"                 },
                        { "Heading5", "5.8.4.1.13", "Name: One One One One One One One One One One One One One One One (15)"             },
                        { "Heading5", "5.8.4.1.14", "Name: One One One One One One One One One One One One One One One One (16)"         },
                        { "Heading5", "5.8.4.1.15", "Name: One One One One One One One One One One One One One One One One One (17)"     },
                        { "Heading5", "5.8.4.1.16", "Name: One One One One One One One One One One One One One One One One One One (18)" },
                        { "Heading5", "5.8.4.1.17", "Name: One One One One One One One One Two (9)"                                      },
                        { "Heading5", "5.8.4.1.18", "Name: One One One One One One One Two (8)"                                          },
                        { "Heading5", "5.8.4.1.19", "Name: One One One One One One Two (7)"                                              },
                        { "Heading5", "5.8.4.1.20", "Name: One One One One One Two (6)"                                                  },
                        { "Heading5", "5.8.4.1.21", "Name: One One One One Two (5)"                                                      },
                        { "Heading5", "5.8.4.1.22", "Name: One One One Two (4)"                                                          },
                        { "Heading5", "5.8.4.1.23", "Name: One One Two (3)"                                                              },
                        { "Heading4", "5.8.4.2",    "Name: One Two (2)"                                                                  },
                        { "Heading3", "5.8.5",      "Name: Two (1)"                                                                      },
                        { "Heading4", "5.8.5.1",    "Name: Two One (2)"                                                                  },
                        { "Heading5", "5.8.5.1.1",  "Name: Two One One (3)"                                                              },
                        { "Heading5", "5.8.5.1.2",  "Name: Two One One One (4)"                                                          },
                        { "Heading5", "5.8.5.1.3",  "Name: Two One One One One (5)"                                                      },
                        { "Heading5", "5.8.5.1.4",  "Name: Two One One One One One (6)"                                                  },
                        { "Heading3", "5.8.6",      "Name: Three (1)"                                                                    }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth4,Anything",
                   new Object[]
                   {
                      /* artifact roots */
                      new int[] { 1, 2, 3 },
                      /* test parameters */
                      new EnumRendererMap
                             (
                                RendererOption.PUBLISHING_FORMAT,     FormatIndicator.WORD_ML,
                                RendererOption.TEMPLATE_OPTION,       "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                                RendererOption.MAX_OUTLINE_DEPTH,     4,
                                RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<any-heading-artifact-type>"
                             ),
                      /* expected paragraph counts */
                      List.of                                                                //body
                         (
                            List.of( 4, 0 ),                                                 //child paragraph counts
                            List.of(),                                                       //section a
                            List.of                                                          //section b
                               (
                                  List.of( 3, 2, 3 ),                                        //child paragraph counts
                                  List.of                                                    //section 1
                                     (
                                        List.of( 2, 2 ),                                     //child paragraph counts
                                        List.of                                              //section 1.1
                                           (
                                              List.of( 2, 2 ),                               //child paragraph counts
                                              List.of                                        //section 1.1.1
                                                 (
                                                    List.of( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 ), //child paragraph counts
                                                    List.of(),                               //section 1.1.1.1
                                                    List.of(),                               //section 1.1.1.2
                                                    List.of(),                               //section 1.1.1.3
                                                    List.of(),                               //section 1.1.1.4
                                                    List.of(),                               //section 1.1.1.5
                                                    List.of(),                               //section 1.1.1.6
                                                    List.of(),                               //section 1.1.1.7
                                                    List.of(),                               //section 1.1.1.8
                                                    List.of(),                               //section 1.1.1.9
                                                    List.of(),                               //section 1.1.1.10
                                                    List.of(),                               //section 1.1.1.11
                                                    List.of(),                               //section 1.1.1.12
                                                    List.of(),                               //section 1.1.1.13
                                                    List.of(),                               //section 1.1.1.14
                                                    List.of(),                               //section 1.1.1.15
                                                    List.of(),                               //section 1.1.1.16
                                                    List.of(),                               //section 1.1.1.17
                                                    List.of(),                               //section 1.1.1.18
                                                    List.of(),                               //section 1.1.1.19
                                                    List.of(),                               //section 1.1.1.20
                                                    List.of()                                //section 1.1.1.21
                                                 ),
                                              List.of()                                      //section 1.1.2
                                           ),
                                        List.of()                                            //section 1.2
                                     ),
                                  List.of                                                    //section 2
                                     (
                                        List.of( 2 ),                                        //child paragraph counts
                                        List.of                                              //section 2.1
                                           (
                                              List.of( 2 ),                                  //section 2.1.1
                                              List.of
                                                 (
                                                    List.of( 2, 2, 2 ),                      //child paragraph counts
                                                    List.of(),                               //section 2.1.1.1
                                                    List.of(),                               //section 2.1.1.2
                                                    List.of()                                //section 2.1.1.3
                                                 )
                                           )
                                     ),
                                  List.of()                                                  //section 3
                               )
                         ),
                      /* expected heading numbers */
                      new String[][]
                      {
                         { "Heading1", "1",        "Name: One (1)"                                                                       },
                         { "Heading2", "1.1",      "Name: One One (2)"                                                                   },
                         { "Heading3", "1.1.1",    "Name: One One One (3)"                                                               },
                         { "Heading4", "1.1.1.1",  "Name: One One One One (4)"                                                           },
                         { "Heading4", "1.1.1.2",  "Name: One One One One One (5)"                                                       },
                         { "Heading4", "1.1.1.3",  "Name: One One One One One One (6)"                                                   },
                         { "Heading4", "1.1.1.4",  "Name: One One One One One One One (7)"                                               },
                         { "Heading4", "1.1.1.5",  "Name: One One One One One One One One (8)"                                           },
                         { "Heading4", "1.1.1.6",  "Name: One One One One One One One One One (9)"                                       },
                         { "Heading4", "1.1.1.7",  "Name: One One One One One One One One One One (10)"                                  },
                         { "Heading4", "1.1.1.8",  "Name: One One One One One One One One One One One (11)"                              },
                         { "Heading4", "1.1.1.9",  "Name: One One One One One One One One One One One One (12)"                          },
                         { "Heading4", "1.1.1.10", "Name: One One One One One One One One One One One One One (13)"                      },
                         { "Heading4", "1.1.1.11", "Name: One One One One One One One One One One One One One One (14)"                  },
                         { "Heading4", "1.1.1.12", "Name: One One One One One One One One One One One One One One One (15)"              },
                         { "Heading4", "1.1.1.13", "Name: One One One One One One One One One One One One One One One One (16)"          },
                         { "Heading4", "1.1.1.14", "Name: One One One One One One One One One One One One One One One One One (17)"      },
                         { "Heading4", "1.1.1.15", "Name: One One One One One One One One One One One One One One One One One One (18)"  },
                         { "Heading4", "1.1.1.16", "Name: One One One One One One One One Two (9)"                                       },
                         { "Heading4", "1.1.1.17", "Name: One One One One One One One Two (8)"                                           },
                         { "Heading4", "1.1.1.18", "Name: One One One One One One Two (7)"                                               },
                         { "Heading4", "1.1.1.19", "Name: One One One One One Two (6)"                                                   },
                         { "Heading4", "1.1.1.20", "Name: One One One One Two (5)"                                                       },
                         { "Heading4", "1.1.1.21", "Name: One One One Two (4)"                                                           },
                         { "Heading3", "1.1.2",    "Name: One One Two (3)"                                                               },
                         { "Heading2", "1.2",      "Name: One Two (2)"                                                                   },
                         { "Heading1", "2",        "Name: Two (1)"                                                                       },
                         { "Heading2", "2.1",      "Name: Two One (2)"                                                                   },
                         { "Heading3", "2.1.1",    "Name: Two One One (3)"                                                               },
                         { "Heading4", "2.1.1.1",  "Name: Two One One One (4)"                                                           },
                         { "Heading4", "2.1.1.2",  "Name: Two One One One One (5)"                                                       },
                         { "Heading4", "2.1.1.3",  "Name: Two One One One One One (6)"                                                   },
                         { "Heading1", "3",        "Name: Three (1)"                                                                     }
                      }
                   }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth3,OnlyHeadersFolders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,     FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,       "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,     3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-and-headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 8 ),                                                 //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 4 ),                                              //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 5 ),                                        //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 2, 3, 2, 3, 2, 3, 2, 3, 2, 8 ),       //child paragraph counts
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of(),                                     //section 1.1.4
                                             List.of(),                                     //section 1.1.5
                                             List.of(),                                     //section 1.1.6
                                             List.of(),                                     //section 1.1.7
                                             List.of(),                                     //section 1.1.8
                                             List.of(),                                     //section 1.1.9
                                             List.of()                                      //section 1.1.10
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",      "Name: One (1)"                                                                  },
                        { "Heading2", "1.1",    "Name: One One (2)"                                                              },
                        { "Heading3", "1.1.1",  "Name: One One One One (4)"                                                      },
                        { "Heading3", "1.1.2",  "Name: One One One One One (5)"                                                  },
                        { "Heading3", "1.1.3",  "Name: One One One One One One One (7)"                                          },
                        { "Heading3", "1.1.4",  "Name: One One One One One One One One (8)"                                      },
                        { "Heading3", "1.1.5",  "Name: One One One One One One One One One One (10)"                             },
                        { "Heading3", "1.1.6",  "Name: One One One One One One One One One One One (11)"                         },
                        { "Heading3", "1.1.7",  "Name: One One One One One One One One One One One One One (13)"                 },
                        { "Heading3", "1.1.8",  "Name: One One One One One One One One One One One One One One (14)"             },
                        { "Heading3", "1.1.9",  "Name: One One One One One One One One One One One One One One One One (16)"     },
                        { "Heading3", "1.1.10", "Name: One One One One One One One One One One One One One One One One One (17)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth4,OnlyHeadersFolders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,   "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH, 4,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-and-headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 8 ),                                                 //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 4 ),                                              //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 5 ),                                        //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 3 ),                                  //child paragraph counts
                                             List.of                                        //section 1.1.1
                                                (
                                                   List.of( 3, 2, 3, 2, 3, 2, 3, 2, 7 ),    //child paragraph counts
                                                   List.of(),                               //section 1.1.1.1
                                                   List.of(),                               //section 1.1.1.2
                                                   List.of(),                               //section 1.1.1.3
                                                   List.of(),                               //section 1.1.1.4
                                                   List.of(),                               //section 1.1.1.5
                                                   List.of(),                               //section 1.1.1.6
                                                   List.of(),                               //section 1.1.1.7
                                                   List.of(),                               //section 1.1.1.8
                                                   List.of()                                //section 1.1.1.9
                                                )
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",       "Name: One (1)"                                                                  },
                        { "Heading2", "1.1",     "Name: One One (2)"                                                              },
                        { "Heading3", "1.1.1",   "Name: One One One One (4)"                                                      },
                        { "Heading4", "1.1.1.1", "Name: One One One One One (5)"                                                  },
                        { "Heading4", "1.1.1.2", "Name: One One One One One One One (7)"                                          },
                        { "Heading4", "1.1.1.3", "Name: One One One One One One One One (8)"                                      },
                        { "Heading4", "1.1.1.4", "Name: One One One One One One One One One One (10)"                             },
                        { "Heading4", "1.1.1.5", "Name: One One One One One One One One One One One (11)"                         },
                        { "Heading4", "1.1.1.6", "Name: One One One One One One One One One One One One One (13)"                 },
                        { "Heading4", "1.1.1.7", "Name: One One One One One One One One One One One One One One (14)"             },
                        { "Heading4", "1.1.1.8", "Name: One One One One One One One One One One One One One One One One (16)"     },
                        { "Heading4", "1.1.1.9", "Name: One One One One One One One One One One One One One One One One One (17)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth3,OnlyFolders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,     FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,       "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,     3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 8 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 8 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 7 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 4, 4, 4, 6 ),                         //child paragraph count
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of()                                      //section 1.1.4
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: One (1)"                                                              },
                        { "Heading2", "1.1",   "Name: One One One One (4)"                                                  },
                        { "Heading3", "1.1.1", "Name: One One One One One One One (7)"                                      },
                        { "Heading3", "1.1.2", "Name: One One One One One One One One One One (10)"                         },
                        { "Heading3", "1.1.3", "Name: One One One One One One One One One One One One One (13)"             },
                        { "Heading3", "1.1.4", "Name: One One One One One One One One One One One One One One One One (16)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth4,OnlyFolders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,   "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH, 4,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 8 ),                                                 //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 8 ),                                              //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 7 ),                                        //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 6 ),                                  //child paragraph counts
                                             List.of                                        //section 1.1.1
                                                (
                                                   List.of( 4, 4, 4 ),                      //child paragraph counts
                                                   List.of(),                               //section 1.1.1.1
                                                   List.of(),                               //section 1.1.1.2
                                                   List.of()                                //section 1.1.1.3
                                                )
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",       "Name: One (1)"                                                              },
                        { "Heading2", "1.1",     "Name: One One One One (4)"                                                  },
                        { "Heading3", "1.1.1",   "Name: One One One One One One One (7)"                                      },
                        { "Heading4", "1.1.1.1", "Name: One One One One One One One One One One (10)"                         },
                        { "Heading4", "1.1.1.2", "Name: One One One One One One One One One One One One One (13)"             },
                        { "Heading4", "1.1.1.3", "Name: One One One One One One One One One One One One One One One One (16)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test name */
                  ",Word,TempA,Depth3,OnlyHeaders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,     FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,       "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,     3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 11 ),                                                //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 7 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 7 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 4, 4, 4, 4 ),                         //child paragraph count
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of()                                      //section 1.1.4
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: One One (2)"                                                              },
                        { "Heading2", "1.1",   "Name: One One One One One (5)"                                                  },
                        { "Heading3", "1.1.1", "Name: One One One One One One One One (8)"                                      },
                        { "Heading3", "1.1.2", "Name: One One One One One One One One One One One (11)"                         },
                        { "Heading3", "1.1.3", "Name: One One One One One One One One One One One One One One (14)"             },
                        { "Heading3", "1.1.4", "Name: One One One One One One One One One One One One One One One One One (17)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth4,OnlyHeaders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test paramters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,   "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<headers-only-heading-artifact-type>",
                               RendererOption.MAX_OUTLINE_DEPTH, 4
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 11 ),                                                //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 7 ),                                              //child paragraph counts
                                 List.of                                                    //section 1
                                    (
                                       List.of( 7 ),                                        //child paragraph counts
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 5 ),                                  //child paragraph counts
                                             List.of                                        //section 1.1.1
                                                (
                                                   List.of( 4, 4, 3 ),                      //child paragraph counts
                                                   List.of(),                               //section 1.1.1.1
                                                   List.of(),                               //section 1.1.1.2
                                                   List.of()                                //section 1.1.1.3
                                                )
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",       "Name: One One (2)"                                                              },
                        { "Heading2", "1.1",     "Name: One One One One One (5)"                                                  },
                        { "Heading3", "1.1.1",   "Name: One One One One One One One One (8)"                                      },
                        { "Heading4", "1.1.1.1", "Name: One One One One One One One One One One One (11)"                         },
                        { "Heading4", "1.1.1.2", "Name: One One One One One One One One One One One One One One (14)"             },
                        { "Heading4", "1.1.1.3", "Name: One One One One One One One One One One One One One One One One One (17)" }
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth3,Anything,SystemHeadings",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                               FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                 "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,                               3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, CoreArtifactTypes.SystemRequirementMsWord.getName()
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 29 ),                                                //child paragraph counts
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 2 ),                                              //child paragraph counts
                                 List.of                                                    //section 2
                                    (
                                       List.of( 2 ),                                        //child paragraph counts
                                       List.of                                              //section 2.1
                                          (
                                             List.of( 2, 2, 2, 2 ),                         //child paragraph counts
                                             List.of(),                                     //section 2.1.1
                                             List.of(),                                     //section 2.1.2
                                             List.of(),                                     //section 2.1.3
                                             List.of()                                      //section 2.1.4
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",      "Name: Two (1)"                     },
                        { "Heading2", "1.1",    "Name: Two One (2)"                 },
                        { "Heading3", "1.1.1",  "Name: Two One One (3)"             },
                        { "Heading3", "1.1.2",  "Name: Two One One One (4)"         },
                        { "Heading3", "1.1.3",  "Name: Two One One One One (5)"     },
                        { "Heading3", "1.1.4",  "Name: Two One One One One One (6)" },
                     }
                  }
               ),

            Map.entry
               (
                  /* test key */
                  ",Word,TempA,Depth3,OnlyFolders,Annotation",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 1, 2, 3 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.MAX_OUTLINE_DEPTH,                                3,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ATTRIBUTE_TYPE, "Annotation"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 8 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 8 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 7 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          (
                                             List.of( 4, 4, 4, 6 ),                         //child paragraph count
                                             List.of(),                                     //section 1.1.1
                                             List.of(),                                     //section 1.1.2
                                             List.of(),                                     //section 1.1.3
                                             List.of()                                      //section 1.1.4
                                          )
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Annotation: One (1)"                                                              },
                        { "Heading2", "1.1",   "Annotation: One One One One (4)"                                                  },
                        { "Heading3", "1.1.1", "Annotation: One One One One One One One (7)"                                      },
                        { "Heading3", "1.1.2", "Annotation: One One One One One One One One One One (10)"                         },
                        { "Heading3", "1.1.3", "Annotation: One One One One One One One One One One One One One (13)"             },
                        { "Heading3", "1.1.4", "Annotation: One One One One One One One One One One One One One One One One (16)" }
                     }
                  }
               ),


            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with non-heading descendants are allowed.
             *
             * Four                      (1) - Folder         <- Included
             *    Four One               (2) - Folder         <- Excluded, no descendant that is not a heading
             *       Four One One        (3) - Folder         <- Excluded, no descendant that is not a heading
             *          Four One One One (4) - Folder         <- Excluded, no descendant that is not a heading
             *    Four Two               (2) - Folder         <- Included
             *       Four Two One        (3) - Requirement    <- Included, non-heading, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Folders,NonHeadingDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_NON_HEADING_DESCENDANTS,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Four (1)"         },
                        { "Heading2", "1.1",   "Name: Four Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with main content descendants are allowed.
             * The main content attribute is default according to the publishing format.
             *
             * Four                      (1) - Folder         <- Included
             *    Four One               (2) - Folder         <- Excluded, no descendant that has main content
             *       Four One One        (3) - Folder         <- Excluded, no descendant that has main content
             *          Four One One One (4) - Folder         <- Excluded, no descendant that has main content
             *    Four Two               (2) - Folder         <- Included
             *       Four Two One        (3) - Requirement    <- Included, has main content, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Folders,MainContentDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Four (1)"         },
                        { "Heading2", "1.1",   "Name: Four Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with main content descendants are allowed.
             * The main content attribute is SoftwareControlCategoryRational.
             *
             * Four                      (1) - Folder         <- Included
             *    Four One               (2) - Folder         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *       Four One One        (3) - Folder         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *          Four One One One (4) - Folder         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *    Four Two               (2) - Folder         <- Included
             *       Four Two One        (3) - Requirement    <- Included, has SoftwareControlCategoryRational content, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Folders,MainContentDescendants,SoftwareControlCategoryRationale",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<folders-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE, "Software Control Category Rationale"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Four (1)"         },
                        { "Heading2", "1.1",   "Name: Four Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with main content descendants are allowed.
             * The main content attribute is Annotation.
             *
             * Four                      (1) - Folder         <- Included, no descendant with Annotation content
             *    Four One               (2) - Folder         <- Included, no descendant with Annotation content
             *       Four One One        (3) - Folder         <- Included, has Annotation content, parents to be included
             *          Four One One One (4) - Folder         <- Excluded, no descendant with Annotation content
             *    Four Two               (2) - Folder         <- Excluded, no descendant with Annotation content
             *       Four Two One        (3) - Requirement    <- Excluded, no descendant with Annotation content
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Folders,MainContentDescendants,Annotation",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<folders-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE, "Annotation"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                             (
                                List.of( 4 ),                                               //child paragraph count
                                List.of                                                     //section 1
                                   (
                                      List.of( 2 ),                                         //child paragraph count
                                      List.of                                               //section 1.1
                                         (
                                            List.of( 3 ),                                   //child paragraph count
                                            List.of()                                       //section 1.1.1
                                         )
                                   )
                             )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Four (1)"         },
                        { "Heading2", "1.1",   "Name: Four One (2)"     },
                        { "Heading3", "1.1.1", "Name: Four One One (3)" }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only headers are allowed as headings.
             * Only headings with main content descendants are allowed.
             *
             * Four                      (1) - Folder         <- Excluded, not a header
             *    Four One               (2) - Folder         <- Excluded, not a header
             *       Four One One        (3) - Folder         <- Excluded, not a header
             *          Four One One One (4) - Folder         <- Excluded, not a header
             *    Four Two               (2) - Folder         <- Excluded, not a header
             *       Four Two One        (3) - Requirement    <- Content Included, has main content
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,HeadersInFoldersTree,NonHeadingDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 5 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of()                                                        //section b
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only headers are allowed as headings.
             * Only headings with main content descendants are allowed.
             *
             * Four                      (1) - Folder         <- Excluded, is a folder
             *    Four One               (2) - Folder         <- Excluded, is a folder
             *       Four One One        (3) - Folder         <- Excluded, is a folder
             *          Four One One One (4) - Folder         <- Excluded, is a folder
             *    Four Two               (2) - Folder         <- Excluded, is a folder
             *       Four Two One        (3) - Requirement    <- Content Included, has main content
             */

            Map.entry
               (
                  /* test key */
                  ",Headings,HeadersOnly,ExcludeFolders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 4 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<headers-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES, List.of( CoreArtifactTypes.Folder )
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 2 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of()                                                        //section b
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                     }
                  }
               ),


            /*
             * Empty Headings Test
             *
             * Only headers are allowed as headings.
             * Only headings with non-heading descendants are allowed.
             *
             * Five                      (1) - Header         <- Included
             *    Five One               (2) - Header         <- Excluded, no descendant that is not a heading
             *       Five One One        (3) - Header         <- Excluded, no descendant that is not a heading
             *          Five One One One (4) - Header         <- Excluded, no descendant that is not a heading
             *    Five Two               (2) - Header         <- Included
             *       Five Two One        (3) - Requirement    <- Included, non-heading, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Headers,NonHeadingDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_NON_HEADING_DESCENDANTS,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Five (1)"         },
                        { "Heading2", "1.1",   "Name: Five Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only headers are allowed as headings.
             * Only headings with main content descendants are allowed.
             * The main content attribute is default according to the publishing format.
             *
             * Five                      (1) - Header         <- Included
             *    Five One               (2) - Header         <- Excluded, no descendant that has main content
             *       Five One One        (3) - Header         <- Excluded, no descendant that has main content
             *          Five One One One (4) - Header         <- Excluded, no descendant that has main content
             *    Five Two               (2) - Header         <- Included
             *       Five Two One        (3) - Requirement    <- Included, has main content, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Headers,MainContentDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<headers-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Five (1)"         },
                        { "Heading2", "1.1",   "Name: Five Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only Headers are allowed as headings.
             * Only headings with main content descendants are allowed.
             * The main content attribute is SoftwareControlCategoryRational.
             *
             * Five                      (1) - Header         <- Included
             *    Five One               (2) - Header         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *       Five One One        (3) - Header         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *          Five One One One (4) - Header         <- Excluded, no descendant with SoftwareControlCategoryRational content
             *    Five Two               (2) - Header         <- Included
             *       Five Two One        (3) - Requirement    <- Included, has SoftwareControlCategoryRational content, parents to be included
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Headers,MainContentDescendants,SoftwareControlCategoryRationale",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<headers-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE, "Software Control Category Rationale"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                              (
                                 List.of( 3 ),                                              //child paragraph count
                                 List.of                                                    //section 1
                                    (
                                       List.of( 4 ),                                        //child paragraph count
                                       List.of                                              //section 1.1
                                          ()
                                    )
                              )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Five (1)"         },
                        { "Heading2", "1.1",   "Name: Five Two (2)"     }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only Headers are allowed as headings.
             * Only Headers with main content descendants are allowed.
             * The main content attribute is Annotation.
             *
             * Five                      (1) - Header        <- Included, no descendant with Annotation content
             *    Five One               (2) - Header        <- Included, no descendant with Annotation content
             *       Five One One        (3) - Header        <- Included, has Annotation content, parents to be included
             *          Five One One One (4) - Header        <- Excluded, no descendant with Annotation content
             *    Five Two               (2) - Header        <- Excluded, no descendant with Annotation content
             *       Five Two One        (3) - Requirement    <- Excluded, no descendant with Annotation content
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,Headers,MainContentDescendants,Annotation",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<headers-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_CONTENT_ATTRIBUTE_TYPE, "Annotation"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 0 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of                                                          //section b
                             (
                                List.of( 4 ),                                               //child paragraph count
                                List.of                                                     //section 1
                                   (
                                      List.of( 2 ),                                         //child paragraph count
                                      List.of                                               //section 1.1
                                         (
                                            List.of( 3 ),                                   //child paragraph count
                                            List.of()                                       //section 1.1.1
                                         )
                                   )
                             )
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                        { "Heading1", "1",     "Name: Five (1)"         },
                        { "Heading2", "1.1",   "Name: Five One (2)"     },
                        { "Heading3", "1.1.1", "Name: Five One One (3)" }
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with main content descendants are allowed.
             *
             * Five                      (1) - Header         <- Excluded, not a folder
             *    Five One               (2) - Header         <- Excluded, not a folder
             *       Five One One        (3) - Header         <- Excluded, not a folder
             *          Five One One One (4) - Header         <- Excluded, not a folder
             *    Five Two               (2) - Header         <- Excluded, not a folder
             *       Five Two One        (3) - Requirement    <- Content Included, has main content
             */

            Map.entry
               (
                  /* test key */
                  ",IncludeHeadings,FoldersInHeadersTree,NonHeadingDescendants",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ONLY_WITH_MAIN_CONTENT,
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE, "<folders-only-heading-artifact-type>"
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 5 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of()                                                        //section b
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                     }
                  }
               ),

            /*
             * Empty Headings Test
             *
             * Only folders are allowed as headings.
             * Only headings with main content descendants are allowed.
             *
             * Five                      (1) - Header         <- Excluded, is a Header
             *    Five One               (2) - Header         <- Excluded, is a Header
             *       Five One One        (3) - Header         <- Excluded, is a Header
             *          Five One One One (4) - Header         <- Excluded, is a Header
             *    Five Two               (2) - Header         <- Excluded, is a Header
             *       Five Two One        (3) - Requirement    <- Content Included, has main content
             */

            Map.entry
               (
                  /* test key */
                  ",Headings,FoldersOnly,ExcludeHeaders",
                  new Object[]
                  {
                     /* artifact roots */
                     new int[] { 5 },
                     /* test parameters */
                     new EnumRendererMap
                            (
                               RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                               RendererOption.TEMPLATE_OPTION,                                  "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,  "<folders-only-heading-artifact-type>",
                               RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES, List.of( CoreArtifactTypes.AbstractHeading )
                            ),
                     /* expected paragraph counts */
                     List.of                                                                //body
                        (
                           List.of( 4, 2 ),                                                 //child paragraph count
                           List.of(),                                                       //section a
                           List.of()                                                        //section b
                        ),
                     /* expected heading numbers */
                     new String[][]
                     {
                     }
                  }
               ),

               /*
                * Include Main Content For Headings Always Test
                *
                * Only folders are allowed as headings.
                * Only headings with main content descendants are allowed.
                *
                * Five                      (1) - Header         <- Included, is a Header
                *    Five One               (2) - Header         <- Included, is a Header
                *       Five One One        (3) - Header         <- Included, is a Header
                *          Five One One One (4) - Header         <- Included, is a Header
                *    Five Two               (2) - Header         <- Included, is a Header
                *       Five Two One        (3) - Requirement    <- Included, has main content
                */

               Map.entry
                  (
                     /* test key */
                     ",IncludeMainContentForHeadingsAlways",
                     new Object[]
                     {
                        /* artifact roots */
                        new int[] { 5 },
                        /* test parameters */
                        new EnumRendererMap
                               (
                                  RendererOption.PUBLISHING_FORMAT,                                              FormatIndicator.WORD_ML,
                                  RendererOption.TEMPLATE_OPTION,                                                "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                                  RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,                "<any-heading-artifact-type>",
                                  RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_MAIN_CONTENT_FOR_HEADINGS,    IncludeMainContentForHeadings.ALWAYS
                               ),
                        /* expected paragraph counts */
                        List.of                                                                //body
                           (
                              List.of( 4, 0 ),                                                 //child paragraph count
                              List.of(),                                                       //section a
                              List.of                                                         //section b
                              (
                                 List.of( 3 ),
                                 List.of(                                                      //section 5
                                    List.of( 2, 2 ),
                                    List.of(                                             //section 5.1
                                       List.of( 2 ),
                                       List.of                                          //section 5.1.1
                                       (
                                          List.of( 2 ),
                                          List.of()                                           //section 5.1.1.1
                                       )
                                    ),
                                    List.of(                                              //section 5.2
                                       List.of( 3 ),
                                       List.of(                                           //section 5.2.1

                                       )
                                    )
                                 )
                              )
                           ),
                        /* expected heading numbers */
                        new String[][]
                        {
                           { "Heading1", "1",       "Name: Five (1)"             },
                           { "Heading2", "1.1",     "Name: Five One (2)"         },
                           { "Heading3", "1.1.1",   "Name: Five One One (3)"     },
                           { "Heading4", "1.1.1.1", "Name: Five One One One (4)" },
                           { "Heading2", "1.2",     "Name: Five Two (2)"         },
                           { "Heading3", "1.2.1",   "Name: Five Two One (3)"     }
                        },
                        Pair.createNonNullImmutable("WordTemplateContent: This is the Five Two One. (3)(Requirement)", true)
                     }
                  ),

                  /*
                   * Include Main Content For Headings Never Test
                   *
                   * Only folders are allowed as headings.
                   * Only headings with main content descendants are allowed.
                   *
                   * Five                      (1) - Header         <- Included, is a Header
                   *    Five One               (2) - Header         <- Included, is a Header
                   *       Five One One        (3) - Header         <- Included, is a Header
                   *          Five One One One (4) - Header         <- Included, is a Header
                   *    Five Two               (2) - Header         <- Included, is a Header
                   *       Five Two One        (3) - Requirement    <- Included, exclude main content
                   */

                  Map.entry
                     (
                        /* test key */
                        ",IncludeMainContentForHeadingsNever",
                        new Object[]
                        {
                           /* artifact roots */
                           new int[] { 5 },
                           /* test parameters */
                           new EnumRendererMap
                                  (
                                     RendererOption.PUBLISHING_FORMAT,                                              FormatIndicator.WORD_ML,
                                     RendererOption.TEMPLATE_OPTION,                                                "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",
                                     RendererOption.OUTLINING_OPTION_OVERRIDE_HEADING_ARTIFACT_TYPE,                "<any-heading-artifact-type>",
                                     RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_MAIN_CONTENT_FOR_HEADINGS,    IncludeMainContentForHeadings.NEVER
                                  ),
                           /* expected paragraph counts */
                         List.of                                                                //body
                         (
                            List.of( 4, 0 ),                                                 //child paragraph count
                            List.of(),                                                       //section a
                            List.of                                                         //section b
                            (
                               List.of( 3 ),
                               List.of(                                                      //section 5
                                  List.of( 2, 2 ),
                                  List.of(                                             //section 5.1
                                     List.of( 2 ),
                                     List.of                                          //section 5.1.1
                                     (
                                        List.of( 2 ),
                                        List.of()                                           //section 5.1.1.1
                                     )
                                  ),
                                  List.of(                                              //section 5.2
                                     List.of( 1 ),
                                     List.of(                                           //section 5.2.1

                                     )
                                  )
                               )
                            )
                           ),
                           /* expected heading numbers */
                           new String[][]
                           {
                              { "Heading1", "1",       "Name: Five (1)"             },
                              { "Heading2", "1.1",     "Name: Five One (2)"         },
                              { "Heading3", "1.1.1",   "Name: Five One One (3)"     },
                              { "Heading4", "1.1.1.1", "Name: Five One One One (4)" },
                              { "Heading2", "1.2",     "Name: Five Two (2)"         },
                              { "Heading3", "1.2.1",   "Name: Five Two One (3)"     }
                           },
                           Pair.createNonNullImmutable("WordTemplateContent: This is the Five Two One. (3)(Requirement)", false)
                        }
                     )



         );

   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static final int testBranchSpecificationRecordIdentifier = 1;

   /**
    * Creation comment used for the OSEE test branch
    */

   private static final String testBranchCreationComment = "Branch for Publishing Outline Numbering Test";

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static final String testBranchName = "Publishing Outline Numbering Test Branch";

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
                      PublishingOutlineNumberingTest.testBranchSpecificationRecordIdentifier,
                      PublishingOutlineNumberingTest.testBranchName,
                      PublishingOutlineNumberingTest.testBranchCreationComment
                   )
         );
   //@formatter:on

   /**
    * {@link MapList} of {@ArtifactSpecificationRecord}s describing the test artifacts for each branch.
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
                  PublishingOutlineNumberingTest.testBranchSpecificationRecordIdentifier,                       /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (

                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                            /* Identifier                             (Integer)                               */
                                  0,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One (1)",                                                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Description: This is the One. (1)(Folder)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One (1)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                            /* Identifier                             (Integer)                               */
                                  1,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One (2)",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                        /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                         beginWordString
                                                       + "WordTemplateContent: This is the One One. (2)(Heading)"
                                                       + endWordString
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                            )
                                  ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                            /* Identifier                             (Integer)                               */
                                  2,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One (3)",                                                      /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One. (3)(Requirement)"
                                                        + endWordString
                                                    ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                            /* Identifier                             (Integer)                               */
                                  3,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One (4)",                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the One One One One. (4)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One One One One (4)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                            /* Identifier                             (Integer)                               */
                                  4,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One (5)",                                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One. (5)(Heading)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                                            /* Identifier                             (Integer)                               */
                                  5,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One (6)",                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One. (6)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  7,                                                                            /* Identifier                             (Integer)                               */
                                  6,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One (7)",                                      /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the One One One One One One One. (7)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One One One One One One One (7)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  8,                                                                            /* Identifier                             (Integer)                               */
                                  7,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One (8)",                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One. (8)(Heading)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  9,                                                                            /* Identifier                             (Integer)                               */
                                  8,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One (9)",                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One. (9)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  10,                                                                           /* Identifier                             (Integer)                               */
                                  9,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One (10)",                         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the One One One One One One One One One One. (10)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One One One One One One One One One One (10)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  11,                                                                           /* Identifier                             (Integer)                               */
                                  10,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One (11)",                     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One. (11)(Heading)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  12,                                                                           /* Identifier                             (Integer)                               */
                                  11,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One (12)",                 /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One One. (12)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  13,                                                                           /* Identifier                             (Integer)                               */
                                  12,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One (13)",             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the One One One One One One One One One One One One One. (13)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One One One One One One One One One One One One One (13)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  14,                                                                           /* Identifier                             (Integer)                               */
                                  13,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One One (14)",         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One One One One. (14)(Heading)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  15,                                                                           /* Identifier                             (Integer)                               */
                                  14,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One One One (15)",     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One One One One One. (15)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  16,                                                                           /* Identifier                             (Integer)                               */
                                  15,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One One One One (16)", /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the One One One One One One One One One One One One One One One One. (16)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Annotation,                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "Annotation: One One One One One One One One One One One One One One One One (16)"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  17,                                                                           /* Identifier                             (Integer)                               */
                                  16,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One One One One One (17)", /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One One One One One One One. (17)(Heading)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  18,                                                                           /* Identifier                             (Integer)                               */
                                  17,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One One One One One One One One One One One (18)", /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One One One One One One One One One One One. (18)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  19,                                                                           /* Identifier                             (Integer)                               */
                                  8,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One One Two (9)",                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One One Two. (9)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  20,                                                                           /* Identifier                             (Integer)                               */
                                  7,                                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One One Two (8)",                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One One Two. (8)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  21,                                                                          /* Identifier                             (Integer)                               */
                                  6,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One One Two (7)",                                     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One One Two. (7)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  22,                                                                          /* Identifier                             (Integer)                               */
                                  5,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One One Two (6)",                                         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One One Two. (6)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  23,                                                                          /* Identifier                             (Integer)                               */
                                  4,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One One Two (5)",                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One One Two. (5)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  24,                                                                          /* Identifier                             (Integer)                               */
                                  3,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One One Two (4)",                                                 /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One One Two. (4)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  25,                                                                          /* Identifier                             (Integer)                               */
                                  2,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One One Two (3)",                                                     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One One Two. (3)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  26,                                                                          /* Identifier                             (Integer)                               */
                                  1,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: One Two (2)",                                                         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the One Two. (2)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  27,                                                                          /* Identifier                             (Integer)                               */
                                  0,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two (1)",                                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Two. (1)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  28,                                                                          /* Identifier                             (Integer)                               */
                                  27,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two One (2)",                                                         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                       /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                       /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                         beginWordString
                                                       + "WordTemplateContent: This is the Two One. (2)(Requirement)"
                                                       + endWordString
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                        /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                            )
                                  ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  29,                                                                          /* Identifier                             (Integer)                               */
                                  28,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two One One (3)",                                                     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Two One One. (3)(Requirement)"
                                                        + endWordString
                                                    ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  30,                                                                          /* Identifier                             (Integer)                               */
                                  29,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two One One One (4)",                                                 /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Two One One One. (4)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  31,                                                                          /* Identifier                             (Integer)                               */
                                  30,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two One One One One (5)",                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Two One One One One. (5)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  32,                                                                          /* Identifier                             (Integer)                               */
                                  31,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Two One One One One One (6)",                                         /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SystemRequirementMsWord,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Two One One One One One. (6)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  33,                                                                          /* Identifier                             (Integer)                               */
                                  0,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Three (1)",                                                           /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Three. (1)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  34,                                                                          /* Identifier                             (Integer)                               */
                                  0,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four (1)",                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                    /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Four. (1)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  35,                                                                          /* Identifier                             (Integer)                               */
                                  34,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four One (2)",                                                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                    /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Four One. (2)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                               new BasicArtifactSpecificationRecord
                               (
                                  36,                                                                          /* Identifier                             (Integer)                               */
                                  35,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four One One (3)",                                                    /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                    /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Four One One. (3)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Annotation,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Annotation: This is the Four One One. (3)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  37,                                                                          /* Identifier                             (Integer)                               */
                                  36,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four One One One (4)",                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                    /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Four One One One. (4)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  38,                                                                          /* Identifier                             (Integer)                               */
                                  34,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four Two (2)",                                                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                    /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Four Two. (2)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  39,                                                                          /* Identifier                             (Integer)                               */
                                  38,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Four Two One (3)",                                                    /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Four Two One. (3)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.SoftwareControlCategoryRationale,         /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "SoftwareContrlCategoryRationale: This is the Four Two One. (3)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  40,                                                                          /* Identifier                             (Integer)                               */
                                  0,                                                                           /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five (1)",                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Five. (1)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  41,                                                                          /* Identifier                             (Integer)                               */
                                  40,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five One (2)",                                                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Five One. (2)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                               new BasicArtifactSpecificationRecord
                               (
                                  42,                                                                          /* Identifier                             (Integer)                               */
                                  41,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five One One (3)",                                                    /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Five One One. (3)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Annotation,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Annotation: This is the Five One One. (3)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  43,                                                                          /* Identifier                             (Integer)                               */
                                  42,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five One One One (4)",                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Five One One One. (4)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  44,                                                                          /* Identifier                             (Integer)                               */
                                  40,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five Two (2)",                                                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.Description,                              /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                        "Description: This is the Five Two. (2)(Folder)"
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  45,                                                                          /* Identifier                             (Integer)                               */
                                  44,                                                                          /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Name: Five Two One (3)",                                                    /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                      /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,                      /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "WordTemplateContent: This is the Five Two One. (3)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.SoftwareControlCategoryRationale,         /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                                      /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "SoftwareContrlCategoryRationale: This is the Five Two One. (3)(Requirement)"
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter                       /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                    /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )

                     )
               )

            );
   //@formatter:on

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
                      "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_A",                                           /* Name                       */
                      new PublishingTemplate.StringSupplier                                                  /* Publish Options Supplier   */
                             (
                                new StringBuilder( 1024 )
                                       .append( "{"                                                         ).append( "\n" )
                                       .append( "   \"ElementType\" : \"Artifact\","                        ).append( "\n" )
                                       .append( "   \"OutliningOptions\" :"                                 ).append( "\n" )
                                       .append( "      ["                                                   ).append( "\n" )
                                       .append( "        {"                                                 ).append( "\n" )
                                       .append( "         \"HeadingAttributeType\" : \"Name\","             ).append( "\n" )
                                       .append( "         \"OutlineNumber\"        : \"\","                 ).append( "\n" )
                                       .append( "         \"Outlining\"            : true,"                 ).append( "\n" )
                                       .append( "         \"RecurseChildren\"      : true"                  ).append( "\n" )
                                       .append( "        }"                                                 ).append( "\n" )
                                       .append( "      ],"                                                  ).append( "\n" )
                                       .append( "   \"AttributeOptions\" :"                                 ).append( "\n" )
                                       .append( "      ["                                                   ).append( "\n" )
                                       .append( "        {"                                                 ).append( "\n" )
                                       .append( "         \"AttrType\"   : \"<format-content-attribute>\"," ).append( "\n" )
                                       .append( "         \"FormatPost\" : \"\","                           ).append( "\n" )
                                       .append( "         \"FormatPre\"  : \"\","                           ).append( "\n" )
                                       .append( "         \"Label\"      : \"\""                            ).append( "\n" )
                                       .append( "        },"                                                ).append( "\n" )
                                       .append( "        {"                                                 ).append( "\n" )
                                       .append( "         \"AttrType\"   : \"Description\","                ).append( "\n" )
                                       .append( "         \"FormatPost\" : \"\","                           ).append( "\n" )
                                       .append( "         \"FormatPre\"  : \"\","                           ).append( "\n" )
                                       .append( "         \"Label\"      : \" \""                           ).append( "\n" )
                                       .append( "        }"                                                 ).append( "\n" )
                                       .append( "      ]"                                                   ).append( "\n" )
                                       .append( "}"                                                         ).append( "\n" )
                                       .toString()
                             ),
                      null,                                                                                  /* Template Content File Name */
                      List.of                                                                                /* Publishing Template Content Map Entries */
                         (
                            new PublishingTemplateContentMapEntry
                                   (
                                      FormatIndicator.WORD_ML,                                               /* Template Content Format    */
                                      "PublishingOutlineNumberingTestTemplate.xml"                           /* Template Content File Path */
                                   ),
                            new PublishingTemplateContentMapEntry
                                   (
                                      FormatIndicator.MARKDOWN,                                              /* Template Content Format    */
                                      "PublishingOutlineNumberingTestTemplate.md"                            /* Template Content File Path */
                                   )
                         ),
                      List.of()                                                                             /* Match Criteria      */
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      "PUBLISHING_OUTLINE_NUMBER_TEST_TEMPLATE_B",                                           /* Name                       */
                      new PublishingTemplate.StringSupplier                                                  /* Publish Options Supplier   */
                             (
                                new StringBuilder( 1024 )
                                       .append( "{"                                                         ).append( "\n" )
                                       .append( "   \"ElementType\" : \"Artifact\","                        ).append( "\n" )
                                       .append( "   \"OutliningOptions\" :"                                 ).append( "\n" )
                                       .append( "      ["                                                   ).append( "\n" )
                                       .append( "        {"                                                 ).append( "\n" )
                                       .append( "         \"HeadingAttributeType\"     : \"Name\","         ).append( "\n" )
                                       .append( "         \"OutlineNumber\"            : \"\","             ).append( "\n" )
                                       .append( "         \"Outlining\"                : true,"             ).append( "\n" )
                                       .append( "         \"RecurseChildren\"          : true"              ).append( "\n" )
                                       .append( "        }"                                                 ).append( "\n" )
                                       .append( "      ],"                                                  ).append( "\n" )
                                       .append( "   \"AttributeOptions\" :"                                 ).append( "\n" )
                                       .append( "      ["                                                   ).append( "\n" )
                                       .append( "        {"                                                 ).append( "\n" )
                                       .append( "         \"AttrType\"   : \"Description\","                ).append( "\n" )
                                       .append( "         \"FormatPost\" : \"\","                           ).append( "\n" )
                                       .append( "         \"FormatPre\"  : \"\","                           ).append( "\n" )
                                       .append( "         \"Label\"      : \"\""                            ).append( "\n" )
                                       .append( "        }"                                                 ).append( "\n" )
                                       .append( "      ]"                                                   ).append( "\n" )
                                       .append( "}"                                                         ).append( "\n" )
                                       .toString()
                             ),
                      null,                                                                                  /* Template Content File Name */
                      List.of                                                                                /* Publishing Template Content Map Entries */
                         (
                            new PublishingTemplateContentMapEntry
                                   (
                                      FormatIndicator.WORD_ML,                                               /* Template Content Format    */
                                      "PublishingOutlineNumberingTestTemplate.xml"                           /* Template Content File Path */
                                   ),
                            new PublishingTemplateContentMapEntry
                                   (
                                      FormatIndicator.MARKDOWN,                                              /* Template Content Format    */
                                      "PublishingOutlineNumberingTestTemplate.md"                            /* Template Content File Path */
                                   )
                         ),
                      List.of()                                                                              /* Match Criteria      */
                   )

            );
      }
   };
   //@formatter:on

   /**
    * Saves the root artifacts.
    */

   private static Artifact[] rootArtifacts;

   private final int[] rootArtifactNumbers;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId workingBranchId;

   /**
    * Map of renderer options for the test.
    */

   private final RendererMap rendererMap;

   private final String testName;

   private final String[][] expectedHeadings;

   private final PublishingXmlUtils publishingXmlUtils;

   private int paragraphIndex;
   private int verifiedHeadingCount;
   private int verifiedOutlineNumberCount;

   private final List<?> expectedCounts;

   private final Pair<String, Boolean> expectString;

   /**
    * Constructor saves the parameters for the test.
    *
    * @param renderLocation flag to indicate if the publish is to be performed on the client or server.
    * @param formatIndicator indicates which publishing format to test.
    * @param maxOutlineLevel the maximum outlining level for the test.
    */

   public PublishingOutlineNumberingTest(RenderLocation renderLocation, String testKey) {

      final var testParameters = PublishingOutlineNumberingTest.testParameters.get(testKey);

      //@formatter:off
      Asserts.assertTrue(
         () -> new Message()
                      .title( "Test parameters not found for test key." )
                      .indentInc()
                      .segment( "Render Location", renderLocation )
                      .segment( "Test Key",        testKey        )
                      .toString(),
         Objects.nonNull(testParameters));
      //@formatter:on

      var i = 0;

      this.rootArtifactNumbers = (int[]) testParameters[i++];

      this.rendererMap = (RendererMap) testParameters[i++];

      this.rendererMap.setRendererOption(RendererOption.RENDER_LOCATION, renderLocation);

      this.expectedCounts = (List<?>) testParameters[i++];
      this.expectedHeadings = (String[][]) testParameters[i++];

      this.testName = renderLocation.name() + testKey;
      this.publishingXmlUtils = new PublishingXmlUtils();

      if (testParameters.length > i) {
         this.expectString = (Pair<String, Boolean>) testParameters[i++];
      } else {
         this.expectString = null;
      }
   }

   @BeforeClass
   public static void beforeClass() {

      /*
       * Clean up test branches that may be left over
       */

      if (PublishingOutlineNumberingTest.cleanUp) {
         PublishingTestUtil.cleanUpBranches(PublishingOutlineNumberingTest.testBranchName);
      }

      /*
       * Create the test branches and artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingOutlineNumberingTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            PublishingOutlineNumberingTest.branchSpecifications,
            PublishingOutlineNumberingTest.artifactSpecifications
         );

      /*
       * Save identifiers of test branches and root artifacts
       */

      PublishingOutlineNumberingTest.workingBranchId =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  PublishingOutlineNumberingTest.testBranchSpecificationRecordIdentifier
               )
            .get();

      final int[] rootRecordIdentifiers = { 1, 27, 33, 34, 40 };

      PublishingOutlineNumberingTest.rootArtifacts = new Artifact[rootRecordIdentifiers.length];

      for(int i = 0; i < rootRecordIdentifiers.length; i++ ) {
         PublishingOutlineNumberingTest.rootArtifacts[i] =
            testDocumentBuilder
               .getArtifact
                  (
                     PublishingOutlineNumberingTest.testBranchSpecificationRecordIdentifier,
                     rootRecordIdentifiers[i]
                  )
               .get();
      }

      /*
       * Setup publishing templates
       */

      var relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint( CoreBranches.COMMON );

      var publishingTemplateSetter = new PublishingTemplateSetterImpl( relationEndpoint );

      var publishingTemplateList =
         PublishingTemplate
            .load
               (
                  PublishingOutlineNumberingTest.publishingTemplatesSupplier,
                  publishingTemplateSetter::set,
                  PublishingOutlineNumberingTest.class,
                  false
               );

      /*
       * Clear the publishing template cache, so newly created or modified publishing template artifacts are reloaded.
       */

      PublishingRequestHandler.deletePublishingTemplateCache();
      //@formatter:on
   }

   @AfterClass
   public static void afterClass() {

      if (PublishingOutlineNumberingTest.cleanUp) {
         PublishingTestUtil.cleanUpBranch(PublishingOutlineNumberingTest.workingBranchId);
      }
   }

   private String testName() {

      return this.testName;
   }

   @SuppressWarnings("incomplete-switch")
   @Test
   public void test() {

      this.rendererMap.setRendererOption(RendererOption.BRANCH, PublishingOutlineNumberingTest.workingBranchId);

      //@formatter:off
      final var artifacts = new ArrayList<Artifact>();

      for( int i = 0; i < this.rootArtifactNumbers.length; i++ ) {
         artifacts.add( PublishingOutlineNumberingTest.rootArtifacts[ this.rootArtifactNumbers[i] - 1 ] );
      }

      final var contentPath =
         RendererManager.open
            (
               artifacts,
               PresentationType.PREVIEW,
               this.rendererMap
            );

      final var formatIndicator =
         (FormatIndicator) this.rendererMap.getRendererOptionValue( RendererOption.PUBLISHING_FORMAT );

      switch( (formatIndicator)  ) {
         case WORD_ML:
            this.parseWordMl(contentPath);
            break;
         case MARKDOWN:
            this.parseMarkdown(contentPath);
            break;
      }
      //@formatter:on

   }

   @SuppressWarnings("resource")
   private void parseWordMl(String contentPath) {
      //@formatter:off

      final var testName = this.testName();

      final var document = PublishingTestUtil.loadContent(contentPath, testName);

      final var documentString =
         PublishingTestUtil.prettyPrint
            (
               document,
               testName,
               PublishingOutlineNumberingTest.printDocuments
            );

      final var wordDocument =
         PublishingXmlUtils
            .parseWordDocument( document )
            .orElseThrow
               (
                  ( throwable ) ->
                     PublishingTestUtil.buildAssertionError
                        (
                           publishingXmlUtils,
                           "Failed to parse Word Document from Document.",
                           documentString
                        )
               );

      final var wordBody =
         publishingXmlUtils
            .parseChild( wordDocument, WordMlTag.BODY, WordBody::new )
            .orElseThrow
               (
                  () -> PublishingTestUtil.buildAssertionError
                           (
                              publishingXmlUtils,
                              "Failed to parse Word Body from Word Document.",
                              documentString
                           )
               );

      final var wordSectionList =
         PublishingXmlUtils
            .parseChildList( wordBody, WordSectionList.wordBodyParentFactory )
            .orElseThrow
               (
                  ( throwable ) ->
                     new AssertionError
                            (
                               new Message()
                                      .title( "Failed to parse Word Section List from Word Body." )
                                      .indentInc()
                                      .segment( "Parent", wordBody )
                                      .indentDec()
                                      .reasonFollows( throwable )
                                      .follows( "Document", documentString )
                                      .toString(),
                               throwable
                            )
               );

      Assert.assertEquals( "Unexpected number of sections.", 2, wordSectionList.size() );

      var wordSection = wordSectionList.get(1).get();

      /*
       * Check expected headings
       */

      this.paragraphIndex = 0;
      this.verifiedHeadingCount = 0;
      this.verifiedOutlineNumberCount = 0;

      PublishingXmlUtils
         .parseDescendantsList( wordSection, WordParagraphList.wordSectionParentFactory )
         .orElseThrow
            (
               ( throwable ) ->
                  new AssertionError
                         (
                            "Failed to parse Word Paragraph List from Word Section.",
                            throwable
                         )
            )
         .forEach( this::checkParagraph );

      Assert.assertEquals
         (
            "Not all headings were verified.",
            this.expectedHeadings.length,
            this.verifiedHeadingCount
         );

      Assert.assertEquals
         (
            "Not all outline numbers were verified.",
            this.expectedHeadings.length,
            this.verifiedOutlineNumberCount
         );

      /*
       * Check sub-section and paragraph counts
       */


      final var wordDocument2 =
         PublishingXmlUtils
            .parseWordDocument( document )
            .orElseThrow
               (
                  ( throwable ) ->
                     new AssertionError
                            (
                               "Failed to parse Word Document from Document.",
                               throwable
                            )
               );

      final var wordBody2 =
         publishingXmlUtils
            .parseChild( wordDocument2, WordMlTag.BODY, WordBody::new )
            .orElseThrow
               (
                  () -> PublishingTestUtil.buildAssertionError
                           (
                              publishingXmlUtils,
                              "Failed to parse Word Body from Word Document.",
                              documentString
                           )
               );



      var depth = new int[0];

      this.checkLevel
         (
            publishingXmlUtils,
            depth,
            this.expectedCounts,
            wordBody2,
            this.getFactories( depth.length )
         );

      if (expectString != null) {
         Assert.assertTrue(documentString.contains(expectString.getFirst()) ^ !expectString.getSecond()) ;
      }
   }

   static Pair<?,?>[] factoriesByDepth =
   new Pair[]
   {
      Pair.createNonNull( WordSectionList.wordBodyParentFactory,             WordParagraphList.wordSectionParentFactory    ),
      Pair.createNonNull( AuxHintSubSectionList.wordSectionParentFactory,    WordParagraphList.wordSubSectionParentFactory ),
      Pair.createNonNull( AuxHintSubSectionList.wordSubSectionParentFactory, WordParagraphList.wordSubSectionParentFactory )
   };

   private <
             P  extends AbstractElement,
             C  extends AbstractElement,
             L  extends AbstractElementList<P,C>,
             LP extends AbstractElementList<C,WordParagraph>,
             F  extends WordElementParserFactory<P,L,C>,
             FP extends WordElementParserFactory<C,LP,WordParagraph>
           >
      Pair<F,FP>
      getFactories( int depth ) {
      depth = Math.min( depth, factoriesByDepth.length - 1 );

      @SuppressWarnings("unchecked")
      var pair = (Pair<F,FP>) factoriesByDepth[depth];

      return pair;
   }


   //@formatter:off
   @SuppressWarnings("resource")
   private <
             P  extends AbstractElement,
             C  extends AbstractElement,
             L  extends AbstractElementList<P,C>,
             LP extends AbstractElementList<C,WordParagraph>,
             F  extends WordElementParserFactory<P,L,C>,
             FP extends WordElementParserFactory<C,LP,WordParagraph>
           >
   void
      checkLevel
         (
            PublishingXmlUtils  publishingXmlUtils,
            int[]               depth,
            List<?>             expectedCounts,
            P                   parent,
            Pair<F,FP>          factories
         ) {

      var childListForParentFactory = factories.getFirst();
      var paragraphListForChildParentFactory = factories.getSecond();

      var expectedChildren = expectedCounts.size() - 1;
      @SuppressWarnings("unchecked")
      var paragraphsPerChild = (List<Integer>) expectedCounts.get(0);

      PublishingXmlUtils
         .parseChildList( parent, childListForParentFactory )
         .ifValueActionElseThrow
            (
               ( childList ) ->
               {
                  Asserts.assertTrue
                     (
                        () -> new Message()
                                     .title( "Unexpected number of children in document section." )
                                     .indentInc()
                                     .segmentIndexed( "Depth", depth )
                                     .segment( "Expected Children", expectedChildren )
                                     .segment( "Actual Children",   childList.size() )
                                     .toString(),
                        expectedChildren == childList.size()
                     );

                  for (int i = 0; i < expectedChildren; i++) {

                     final var finalI = i;
                     final C child = childList.get( i ).get();

                     final var wordParagraphList =
                        PublishingXmlUtils
                           .parseChildList( child, paragraphListForChildParentFactory )
                           .orElseThrow
                              (
                                 ( throwable ) ->
                                    new AssertionError
                                           (
                                              "Failed to parse Word Paragraph List from Word Sub-Section  for outline level ",
                                              throwable
                                           )
                              );

                     var expectedParagraphs = paragraphsPerChild.get(i);
                     var actualParagraphs = wordParagraphList.size();

                     Asserts.assertTrue
                        (
                           () -> new Message()
                                        .title( "Unexpected number of paragraphs for child." )
                                        .indentInc()
                                        .segmentIndexed( "Depth", depth )
                                        .segment( "Child",                    finalI             )
                                        .segment( "Expected Paragraph Count", expectedParagraphs )
                                        .segment( "Actual Paragraph Count",   actualParagraphs   )
                                        .toString(),
                           expectedParagraphs == actualParagraphs
                        );

                     var expectedCountsForChild = (List<?>) expectedCounts.get( i + 1 );

                     if( !expectedCountsForChild.isEmpty() ) {

                        var childDepth = Arrays.copyOf( depth, depth.length + 1 );
                        childDepth[ depth.length ] = i;

                        this.checkLevel
                           (
                              publishingXmlUtils,
                              childDepth,
                              (List<?>) expectedCounts.get( i + 1 ),
                              child,
                              this.getFactories( childDepth.length )
                           );
                     }
                  }
               },
               ( throwable ) ->
                  new AssertionError
                        (
                           "Failed to parse Word Sub-Section List from Word Section for outline level ",
                           throwable
                        )
            );
   }
   //@formatter:on

   private void checkParagraph(WordParagraph wordParagraph) {

      //@formatter:off
      PublishingXmlUtils
         .parseDescendantsList
            (
               wordParagraph,
               WordParagraphStyleList.wordParagraphParentFactory
            )
         .flatMapValue
            (
               ( wordParagraphStyleList ) ->
               {
                  return
                     ( wordParagraphStyleList.size() == 1 )
                        ?  Result.ofValue( wordParagraphStyleList )
                        :  Result.empty();
               }
            )
         .ifValueAction
            (
               ( wordParagraphStyleList ) ->
               {
                  final var wordParagraphStyle = wordParagraphStyleList.get(0).get();

                  final var paragraphStyle = wordParagraphStyle.getAttribute(WordMlAttribute.VALUE).get();

                  if( !paragraphStyle.startsWith( "Heading" ) ) {
                     return;
                  }

                  Asserts.assertTrue
                     (
                        () -> new Message()
                                     .title( "More headings than expected." )
                                     .indentInc()
                                     .segment( "Expected Heading Count", this.expectedHeadings.length )
                                     .toString(),
                        ( this.paragraphIndex < this.expectedHeadings.length )
                     );

                  final var expectedParagraphStyle      = this.expectedHeadings[this.paragraphIndex][0];
                  final var expectedOutlineNumberString = this.expectedHeadings[this.paragraphIndex][1];
                  final var expectedHeadingText         = this.expectedHeadings[this.paragraphIndex][2];

                  Asserts.assertTrue
                     (
                        () -> new Message()
                                 .title( "Unexpected heading style." )
                                 .indentInc()
                                 .segment( "Paragraph Index",        this.paragraphIndex    )
                                 .segment( "Expected Heading Style", expectedParagraphStyle )
                                 .segment( "Heading Style",          paragraphStyle         )
                                 .indentDec()
                                 .blank()
                                 .title(  "Only paragraphs with paragraph styles are counted by the paragraph index." )
                                 .append( "The test expects only heading artifacts to have paragraph styles." )
                                 .toString(),
                        expectedParagraphStyle.equals(paragraphStyle)
                     );

                  this.verifiedHeadingCount++;

                  PublishingXmlUtils
                     .parseDescendantsList( wordParagraph, AuxHintTextList.wordParagraphParentFactory )
                     .ifValueActionElseAction
                        (
                           ( auxHintTextList ) ->
                           {
                              Asserts.assertTrue
                                 (
                                    () -> new Message()
                                                 .title( "Unexpected number of Aux Hint Text elements." )
                                                 .indentInc()
                                                 .segment( "Paragraph Index",              this.paragraphIndex           )
                                                 .segment( "Expected Aux Hint Text Count", 1                             )
                                                 .segment( "Aux Hint Text Count",          wordParagraphStyleList.size() )
                                                 .indentDec()
                                                 .blank()
                                                 .title(  "Only paragraphs with paragraph styles are counted by the paragraph index." )
                                                 .toString(),
                                    wordParagraphStyleList.size() == 1
                                 );

                              final var auxHintText = auxHintTextList.get(0).get();

                              final var outlineNumberString = auxHintText.getAttribute(AuxHintAttribute.VALUE).get();

                              Asserts.assertTrue
                                 (
                                    () -> new Message()
                                                 .title( "Unexpected outline number." )
                                                 .indentInc()
                                                 .segment( "Paragraph Index",         this.paragraphIndex         )
                                                 .segment( "Expected Outline Number", expectedOutlineNumberString )
                                                 .segment( "Outline Number",          outlineNumberString         )
                                                 .indentDec()
                                                 .blank()
                                                 .title(  "Only paragraphs with paragraph styles are counted by the paragraph index." )
                                                 .toString(),
                                    expectedOutlineNumberString.equals(outlineNumberString)
                                 );

                              this.verifiedOutlineNumberCount++;
                           },

                           () ->
                           {
                              throw
                                 new AssertionError
                                        (
                                           new Message()
                                                  .title( "Aux Hint Text not found in heading paragraph." )
                                                  .indentInc()
                                                  .segment( "Paragraph Index", this.paragraphIndex )
                                                  .indentDec()
                                                  .blank()
                                                  .title(  "Only paragraphs with paragraph styles are counted by the paragraph index." )
                                                  .toString()
                                        );
                           }
                        );

                  final var headingText = wordParagraph.getElement().getTextContent();

                  Asserts.assertTrue
                     (
                        ()-> new Message()
                                    .title( "Unexpected Heading Text" )
                                    .indentInc()
                                    .segment( "Paragraph Index",         this.paragraphIndex )
                                    .segment( "Expected Heading Text",   expectedHeadingText )
                                    .segment( "Heading Text",            headingText         )
                                    .indentDec()
                                    .blank()
                                    .title(  "Only paragraphs with paragraph styles are counted by the paragraph index." )
                                    .toString(),
                        expectedHeadingText.equals( headingText )
                     );
                  this.paragraphIndex++;
               }
            );
      //@formatter:on
   }

   private void parseMarkdown(String contentPath) {
      //not imp
   }
}

/* EOF */
