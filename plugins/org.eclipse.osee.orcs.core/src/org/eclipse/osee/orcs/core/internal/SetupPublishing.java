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

package org.eclipse.osee.orcs.core.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.table.RelationTableOptions;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.eclipse.osee.orcs.core.util.PublishingTemplateContentMapEntry;
import org.eclipse.osee.orcs.core.util.PublishingTemplateMatchCriterion;
import org.eclipse.osee.orcs.core.util.PublishingTemplateMatchCriterionListBuilder;
import org.eclipse.osee.orcs.core.util.PublishingTemplateSetter;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Creates the artifacts for Publishing testing in the test database.
 *
 * @author Loren K. Ashley
 */

public class SetupPublishing {

   /**
    * When <code>true</code>, the test preview publishing templates will be created so that they match by name. When
    * <code>false</code>, the test preview publishing templates will be created so that they have to be found with
    * template match criteria.
    */

   private static final boolean matchPreviewTemplatesByName = true;

   /**
    * <pre>
    * TODO: LKA-PUB-TOG Remove this member with TW22218.
    *
    * "osee.publish.new.values": Set "false" until publishing templates have been adjusted for the new server side
    * publishing preview commands.
    *
    * Get toggle values using:
    *    Boolean.valueOf(OseeInfo.getValue(jdbcClient,"toggle-name"));
    *
    * When the OseeInfo key value has not been set the toggle value will be false. When setting the key value to
    * true from false or unset (false), the first run of the test suite may have inconsistent values for the toggles.
    * Static initializers may get the toggle value before it is set or changed here.
    * </pre>
    */

   private static Boolean newValues = false;

   /**
    * <pre>
    * TODO: LKA-PUB-TOG Remove this member with TW22218.
    *
    * "osee.publish.no.tags": Appears to be a legacy toggle that was never cleaned up used to transition preview
    * publishing templates for the local program.
    * </pre>
    */

   private static Boolean noTags = false;

   /**
    * Suffix to be added to publishing template artifact names when {@link #matchPreviewTemplatesByName} is
    * <code>false</code>.
    */

   private static final String previewTemplatesNotMatchByNameSuffix = " (NOT MATCHED BY NAME)";

   /**
    * Definitions for Publishing Templates to be created on the Common branch during initialization.
    *
    * @implNote The publishing template definition list is wrapped in a supplier method so that it's values can be
    * computed based upon publishing toggle values which may not have been determined at the time the list would have
    * been statically evaluated.
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
                         CoreArtifactTokens.DocumentTemplates,
                         "WordEditTemplate",                                                                               /* Name                       */
                         new PublishingTemplate.FileSupplierOseeInf( "templates/EDIT_TEMPLATE.json" ),                     /* Publish Options Supplier   */
                         null,                                                                                             /* Template Content File Name */
                         null,
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,                                                          /* Template Content Format    */
                                         "templates/EDIT_TEMPLATE.xml"                                                     /* Template Content File Path */
                                      ),
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,                                                         /* Template Content Format    */
                                         "templates/EDIT_TEMPLATE.md"                                                      /* Template Content File Path */
                                      )
                            ),
                         new PublishingTemplateMatchCriterionListBuilder                                                   /* Match Criteria      */
                               (
                                  SetupPublishing.matchPreviewTemplatesByName
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.SPECIALIZED_EDIT.name()                                       /* Presentation Type   */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_MARKDOWN,                                                  /* Renderer Identifier */
                                            PresentationType.SPECIALIZED_EDIT.name()                                       /* Presentation Type   */
                                         )
                               )
                            .toList(), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                             /* Parent Artifact Identifier */
                         "WordMergeTemplate",                                                                              /* Name                       */
                         new PublishingTemplate.FileSupplierOseeInf( "templates/WordMergeTemplate.json" ),                 /* Publish Options Supplier   */
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PREVIEW_ALL.xml" ),                        /* Template Content Supplier  */
                         null,
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                            ),
                         List.of                                                                                           /* Match Criteria             */
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_WORD_TEMPLATE,                                                /* Renderer Identifier */
                                         PresentationType.MERGE.name()                                                     /* Presentation Type   */
                                      ),

                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_WORD_TEMPLATE,                                                /* Renderer Identifier */
                                         PresentationType.DIFF.name(),                                                     /* Presentation Type   */
                                         RendererOption.THREE_WAY_MERGE.getKey()                                           /* Option              */
                                      )
                            ), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                             /* Parent Artifact Identifier */
                         SetupPublishing.previewTemplateName                                                               /* Name                       */
                            (
                               RendererOption.PREVIEW_ALL_VALUE.getKey()
                            ),
                         null,                                                                                             /* Publish Options File Name */
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PREVIEW_ALL.xml" ),                        /* Template Content Supplier */
                         null,
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,                                                          /* Template Content Format    */
                                         "templates/PREVIEW_ALL.xml"                                                       /* Template Content File Path */
                                      ),
                                new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,                                                         /* Template Content Format    */
                                         "templates/PREVIEW_ALL.md"                                                        /* Template Content File Path */
                                      )
                            ),
                         new PublishingTemplateMatchCriterionListBuilder                                                   /* Match Criteria      */
                                (
                                   SetupPublishing.matchPreviewTemplatesByName
                                )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.DIFF.name()                                                   /* Presentation Type   */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_VALUE.getKey()                                      /* Option              */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_MARKDOWN,                                                  /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_VALUE.getKey()                                      /* Option              */
                                         )
                               )
                            .toList(), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                             /* Parent Artifact Identifier */
                         SetupPublishing.previewTemplateName                                                               /* Name                       */
                            (
                               RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()
                            ),
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PREVIEW_ALL_NO_ATTRIBUTES.json" ),         /* Publish Options Supplier */
                         null,                                                                                             /* Template Content File Name */
                         null,
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,                                                          /* Template Content Format    */
                                         "templates/PREVIEW_ALL.xml"                                                       /* Template Content File Path */
                                      ),
                                new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,                                                         /* Template Content Format    */
                                         "templates/PREVIEW_ALL.md"                                                        /* Template Content File Path */
                                      )
                            ),
                         new PublishingTemplateMatchCriterionListBuilder                                                   /* Match Criteria      */
                                (
                                   SetupPublishing.matchPreviewTemplatesByName
                                )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            "DIFF_NO_ATTRIBUTES"                                                           /* Match String        */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()                        /* Option              */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_MARKDOWN,                                                  /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()                        /* Option              */
                                         )
                               )
                            .toList(), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                             /* Parent Artifact Identifier */
                         SetupPublishing.previewTemplateName                                                               /* Name                       */
                            (
                               RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                            ),
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PREVIEW_ALL_RECURSE.json" ),               /* Publish Options Supplier   */
                         null,                                                                                             /* Template Content File Name */
                         OseeInf.getResourceContents("templates/PREVIEW_ALL_RECURSE_md_content.md", getClass()),                                                                                             /* Markdown Content */
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,                                                          /* Template Content Format    */
                                         "templates/PREVIEW_ALL.xml"                                                       /* Template Content File Path */
                                      ),
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,                                                         /* Template Content Format    */
                                         "templates/PREVIEW_ALL.md"                                                        /* Template Content File Path */
                                      )
                            ),
                         new PublishingTemplateMatchCriterionListBuilder                                                   /* Match Criteria      */
                                (
                                   SetupPublishing.matchPreviewTemplatesByName
                                )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* PresentationType    */
                                            RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()                              /* Option              */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_MARKDOWN,                                                  /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* PresentationType    */
                                            RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()                              /* Option              */
                                         )
                               )
                            .toList(), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                             /* Parent Artifact Identifier */
                         SetupPublishing.previewTemplateName                                                               /* Name                       */
                            (
                               RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                            ),
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PREVIEW_ALL_RECURSE_NO_ATTRIBUTES.json" ), /* Publish Options Supplier */
                         null,                                                                                             /* Template Content File Name */
                         null,
                         List.of                                                                                           /* Publishing Template Content Map Entries */
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,                                                          /* Template Content Format    */
                                         "templates/PREVIEW_ALL.xml"                                                       /* Template Content File Path */
                                      ),
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,                                                         /* Template Content Format    */
                                         "templates/PREVIEW_ALL.md"                                                        /* Template Content File Path */
                                      )
                            ),
                         new PublishingTemplateMatchCriterionListBuilder                                                   /* Match Criteria             */
                                (
                                   SetupPublishing.matchPreviewTemplatesByName
                                )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_WORD_TEMPLATE,                                             /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()                /* Option              */
                                         )
                               )
                            .appendAlways
                               (
                                  new PublishingTemplateMatchCriterion
                                         (
                                            RENDERER_IDENTIFIER_MARKDOWN,                                                  /* Renderer Identifier */
                                            PresentationType.PREVIEW.name(),                                               /* Presentation Type   */
                                            RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()                /* Option              */
                                         )
                               )
                            .toList(), null
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,
                         "PreviewAll_HeadingsNoMainContent_RelationTable",
                         new PublishingTemplate.FileSupplierOseeInf( "templates/PreviewAllRecurse_HeadersOnlyHeading_HeadingsNoMainContent.json" ),
                         null,
                         null,
                         List.of
                            (
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.WORD_ML,
                                         "templates/PREVIEW_ALL.xml"
                                      ),
                               new PublishingTemplateContentMapEntry
                                      (
                                         FormatIndicator.MARKDOWN,
                                         "templates/EDIT_TEMPLATE.md"
                                      )
                            ),
                         null, new RelationTableOptions
                            (
                               Collections.emptyList(), // No artifact type filter
                               Collections.emptyList(), // Default columns
                               Arrays.asList // Requirements trace relation tables (both higher and lower level)
                                  (
                                     CoreRelationTypes.RequirementTrace.getName() + "|" + CoreRelationTypes.RequirementTrace.getSideName(RelationSide.SIDE_A),
                                     CoreRelationTypes.RequirementTrace.getName() + "|" + CoreRelationTypes.RequirementTrace.getSideName(RelationSide.SIDE_B)
                                  )
                            )
                      )

            );
         }
      };
   //@formatter:on

   /**
    * The name of the client side Word Markup Language renderer for publishing previews.
    */

   private static String RENDERER_IDENTIFIER_WORD_TEMPLATE =
      "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer";

   /**
    * The name of the Markdown renderer for publishing previews.
    */

   private static String RENDERER_IDENTIFIER_MARKDOWN = "org.eclipse.osee.framework.ui.skynet.render.MarkdownRenderer";

   /**
    * Creates the data rights artifact with the available data rights footers.
    *
    * @param tx the {@link TransactionBuilder} to used for database operations.
    */

   private static void createDataRights(TransactionBuilder tx) {
      ArtifactId dataRightsArt =
         tx.createArtifact(CoreArtifactTokens.DocumentTemplates, CoreArtifactTokens.DataRightsFooters);
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Unspecified.xml", SetupPublishing.class));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Default.xml", SetupPublishing.class));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("GovernmentPurposeRights.xml", SetupPublishing.class));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("RestrictedRights.xml", SetupPublishing.class));

      // HTML Datarights
      ArtifactId HtmlDataRightsArt =
         tx.createArtifact(CoreArtifactTokens.DocumentTemplates, CoreArtifactTokens.HtmlDataRightsFooters);
      tx.createAttribute(HtmlDataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("htmlDataRights/Unspecified.txt", SetupPublishing.class));
      tx.createAttribute(HtmlDataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("htmlDataRights/Default.txt", SetupPublishing.class));
      tx.createAttribute(HtmlDataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("htmlDataRights/GovernmentPurposeRights.txt", SetupPublishing.class));
      tx.createAttribute(HtmlDataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("htmlDataRights/RestrictedRights.txt", SetupPublishing.class));
      tx.createAttribute(HtmlDataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("htmlDataRights/Proprietary.txt", SetupPublishing.class));
   }

   /**
    * Creates the publishing template artifacts defined by {@link SetupPublishing#publishingTemplatesSupplier} for the
    * test database.
    *
    * @param tx the {@link TransactionBuilder} to used for database operations.
    */

   private static void createWordTemplates(TransactionBuilder tx) {
      //@formatter:off
      PublishingTemplate.load
         (
            SetupPublishing.publishingTemplatesSupplier,
            new PublishingTemplateSetter() {

               @Override
               public String set
               (
                  ArtifactToken parent,
                  String name,
                  String content,
                  String markdownContent,
                  String rendererOptions,
                  List<Map.Entry<String,String>> publishingTemplateContentMapEntries,
                  List<String> matchCriteria,
                  RelationTableOptions relationTableOptions
               ) {

                  Objects.requireNonNull(tx, "SetupPublishing::createPublishingTemplate, parameter \"tx\" cannot be null.");

                  var publishingTemplateArtifact =
                     tx.createArtifact(parent, CoreArtifactTypes.RendererTemplateWholeWord, name);

                  if (Objects.nonNull(rendererOptions)) {
                     tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.RendererOptions,
                        rendererOptions);
                  }

                  if (Objects.nonNull(content)) {
                     tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.WholeWordContent,
                        content);
                  }

                  if (Objects.nonNull(markdownContent)) {
                     tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.MarkdownContent,
                        markdownContent);
                  }

                  if (Objects.nonNull(publishingTemplateContentMapEntries)) {
                     publishingTemplateContentMapEntries.forEach
                        (
                           ( mapEntry ) -> tx.createAttribute
                                              (
                                                 publishingTemplateArtifact,
                                                 CoreAttributeTypes.PublishingTemplateContentByFormatMapEntry,
                                                 mapEntry
                                              )
                        );
                  }

                  if (Objects.nonNull(matchCriteria)) {
                     matchCriteria.forEach
                        (
                           ( matchCriterion ) -> tx.createAttribute
                                                    (
                                                       publishingTemplateArtifact,
                                                       CoreAttributeTypes.TemplateMatchCriteria,
                                                       matchCriterion
                                                    )
                        );
                  }

                  if (Objects.nonNull(relationTableOptions)) {
                     List<String> artTypes = relationTableOptions.getRelationTableArtifactTypeNamesAndOrIds();
                     List<String> cols = relationTableOptions.getRelationTableColumns();
                     List<String> relTypeSides = relationTableOptions.getRelationTableRelationTypeSides();

                     if (Objects.nonNull(artTypes)) {
                        for (String artType : artTypes) {
                           tx.createAttribute(publishingTemplateArtifact, CoreAttributeTypes.PublishingRelationTableArtifactTypeNameOrId, artType);
                        }
                     }
                     if (Objects.nonNull(cols)) {
                        for (String col : cols) {
                           tx.createAttribute(publishingTemplateArtifact, CoreAttributeTypes.PublishingRelationTableColumn, col);
                        }
                     }
                     if (Objects.nonNull(relTypeSides)) {
                        for (String relTypeSide : relTypeSides) {
                           tx.createAttribute(publishingTemplateArtifact, CoreAttributeTypes.PublishingRelationTableRelationTypeSide, relTypeSide);
                        }
                     }
                  }

                  return "AT-".concat( publishingTemplateArtifact.getIdString() );
               }

            },
            SetupPublishing.class,
            true
         );
      //@formatter:on
   }

   /**
    * Creates the publishing template artifact name. When the flag {@link SetupPublishing#previewTemplatesByName} is
    * <code>true</code>, the template name is just the <code>templateBaseName</code>; otherwise, the template name is
    * the <code>templateBaseName</code> with the string {@link SetupPublishing#previewTemplatesNotMatchByNameSuffix}
    * appended.
    *
    * @param templateBaseName
    * @return the name for the preview publishing template.
    */

   private static String previewTemplateName(String templateBaseName) {
     //@formatter:off
     return
        SetupPublishing.matchPreviewTemplatesByName
           ? templateBaseName
           : new StringBuilder( templateBaseName.length() + SetupPublishing.previewTemplatesNotMatchByNameSuffix.length() )
                    .append( templateBaseName )
                    .append( SetupPublishing.previewTemplatesNotMatchByNameSuffix )
                    .toString();
     //@formatter:on
   }

   /**
    * Creates the &quot;default&quot; artifacts for publishing testing in the test database.
    *
    * @param tx the {@link TransactionBuilder} used to create artifacts.
    * @throws NullPointerException when the parameter <code>tx</code> is <code>null</code>.
    */

   public static void setup(TransactionBuilder tx) {
      Objects.requireNonNull(tx, "SetupPublishing::setup, the parameter \"tx\" cannot be null.");
      SetupPublishing.createWordTemplates(tx);
      SetupPublishing.createDataRights(tx);
   }

   /**
    * Setup the configuration for publishing.
    *
    * @param orcsApi a handle to the {@link OrcsApi}.
    * @implNote When there is a need for publishing toggles, this method will obtains values for the publishing toggles.
    */

   public static void setupConfiguration(OrcsApi orcsApi) {

      /*
       * TODO: LKA-PUB-TOG Remove this block with TW22218.
       */

      {
         try {
            var jdbcClient = orcsApi.getJdbcService().getClient();
            OseeInfo.setValue(jdbcClient, "osee.publish.no.tags", SetupPublishing.noTags.toString());
            OseeInfo.setValue(jdbcClient, "osee.publish.new.values", SetupPublishing.newValues.toString());
         } catch (Exception e) {
            /*
             * Just eat the exception when unable to set toggle values. May cause downstream AtsIde Integration Tests to
             * fail.
             */
         }
      }

      /*
       * END: LKA-PUB-TOG
       */
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private SetupPublishing() {
   }

}

/* EOF */
