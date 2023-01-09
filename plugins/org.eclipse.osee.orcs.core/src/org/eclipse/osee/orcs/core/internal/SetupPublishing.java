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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Creates the artifacts for Publishing testing in the test database.
 *
 * @author Loren K. Ashley
 */

public class SetupPublishing {

   /**
    * Defines the parameters for the creation of a Publishing Template.
    */

   private static class PublishingTemplate {

      /**
       * A list, possibly empty, of the values to use for the publishing template's match criteria attribute values.
       */

      private final List<PublishingTemplateMatchCriterion> matchCriteria;

      /**
       * The name for the publishing template artifact.
       */

      private final String name;

      /**
       * The publishing template artifact will be created as a child of this artifact.
       */

      private final ArtifactToken parentArtifactToken;

      /**
       * When specified, the file contents will be used as the value of the publishing template's renderer options
       * attribute.
       */

      private final String rendererOptionsFileName;

      /**
       * When specified, the file contents will be used as the value of the publishing template's Word ML.
       */

      private final String templateContentFileName;

      /**
       * Creates a new {@link PublishingTemplate} with the specified parameters.
       *
       * @param parentArtifactToken the hierarchical parent of the publishing template artifact.
       * @param name the name of the publishing template artifact.
       * @param rendererOptionsFilename the publishing template renderer options filename. The parameter maybe
       * <code>null</code>.
       * @param templateContentFileName the filename of a file containing the Word ML publishing template content. The
       * parameter maybe <code>null</code>.
       * @param matchCriteria a list of {@link PublishingTemplateMatchCriterion} for the publishing template. The
       * parameter maybe <code>null</code>.
       * @throws NullPointerException when either of the parameters <code>parentArtifactToken</code> or
       * <code>name</code> are <code>null</code>.
       */

      public PublishingTemplate(ArtifactToken parentArtifactToken, String name, String rendererOptionsFileName, String templateContentFileName, List<PublishingTemplateMatchCriterion> matchCriteria) {
         this.parentArtifactToken = Objects.requireNonNull(parentArtifactToken,
            "SetupPublishing.PublishingTemplate::new, paramter \"parentArtifactToken\" cannot be null.");
         this.name = Objects.requireNonNull(name,
            "SetupPublishing.PublishingTemplate::new, parameter \"name\" cannnot be null.");
         this.rendererOptionsFileName = rendererOptionsFileName;
         this.templateContentFileName = templateContentFileName;
         this.matchCriteria = matchCriteria;
      }

      /**
       * Creates a new publishing template artifact.
       *
       * @param tx the {@link TransactionBuilder} used to create an modify the publishing template artifact.
       * @throws NullPointerException when the parameter <code>tx</code> is <code>null</code>.
       */

      public void createPublishingTemplate(TransactionBuilder tx) {

         Objects.requireNonNull(tx, "SetupPublishing::createPublishingTemplate, parameter \"tx\" cannot be null.");

         var publishingTemplateArtifact =
            tx.createArtifact(this.parentArtifactToken, CoreArtifactTypes.RendererTemplateWholeWord, this.name);

         if (Objects.nonNull(this.rendererOptionsFileName)) {
            tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.RendererOptions,
               OseeInf.getResourceContents(this.rendererOptionsFileName, SetupPublishing.class));
         }

         if (Objects.nonNull(this.templateContentFileName)) {
            tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.WholeWordContent,
               OseeInf.getResourceContents(this.templateContentFileName, SetupPublishing.class));
         }

         if (Objects.nonNull(this.matchCriteria)) {
            this.matchCriteria.forEach(
               (matchCriterion) -> matchCriterion.setTemplateMatchCriteria(tx, publishingTemplateArtifact));
         }
      }
   }

   /**
    * Defines a Publishing Template Match Criterion for the creation of Publishing Template.
    */

   private static class PublishingTemplateMatchCriterion {

      /**
       * Saves the "option" portion of the template match criteria string. This member maybe <code>null</code>.
       */

      private final String option;

      /**
       * Saves the "presentation type" portion of the template match criteria string.
       */

      private final String presentationType;

      /**
       * Saves the Renderer identification string.
       */

      private final String rendererIdentifier;

      /**
       * Creates a new {@link PublishingTemplateMatchCriterion} with the specified parameters.
       *
       * @param rendererIdentifier the identifier as returned by {@link IRenderer#getIdentifier} of the renderer.
       * @param presentationType the type of presentation the match criterion is for.
       * @throws NullPointerException when either of the parameters <code>rendererIdentifier</code> or
       * <code>presentationType</code> are <code>null</code>.
       * @throws IllegalArgumentException when either of the parameters <code>rendererIdentifier</code> or
       * <code>presentationType</code> are blank.
       */

      public PublishingTemplateMatchCriterion(String rendererIdentifier, String presentationType) {
         this(rendererIdentifier, presentationType, null);
      }

      /**
       * Creates a new {@link PublishingTemplateMatchCriterion} with the specified parameters.
       *
       * @param rendererIdentifier the identifier as returned by {@link IRenderer#getIdentifier} of the renderer.
       * @param presentationType the type of presentation the match criterion is for.
       * @param option the optional trailing portion of the match criterion.
       * @throws NullPointerException when either of the parameters <code>rendererIdentifier</code> or
       * <code>presentationType</code> are <code>null</code>.
       * @throws IllegalArgumentException when:
       * <ul>
       * <li>the parameter <code>rendererIdentifier</code> is blank, or</li>
       * <li>the parameter <code>presentationType</code> is blank, or</li>
       * <li>the parameter <code>option</code> is specified and blank.</li>
       * </ul>
       */

      public PublishingTemplateMatchCriterion(String rendererIdentifier, String presentationType, String option) {
         Objects.requireNonNull(rendererIdentifier,
            "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"rendererIdentifier\" cannot be null.");
         if (rendererIdentifier.isBlank()) {
            throw new IllegalArgumentException(
               "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"rendererIdentifier\" cannot be blank.");
         }
         Objects.requireNonNull(presentationType,
            "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"presentationType\" cannot be null.");
         if (presentationType.isBlank()) {
            throw new IllegalArgumentException(
               "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"presentationType\" cannot be blank.");
         }
         if (Objects.nonNull(option) && option.isBlank()) {
            throw new IllegalArgumentException(
               "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"option\" cannot be blank.");
         }
         this.rendererIdentifier = rendererIdentifier;
         this.presentationType = presentationType;
         this.option = option;
      }

      /**
       * Adds a {@link CoreAttributeTypes#TemplateMatchCritera} value to the publishing template artifact.
       *
       * @param tx the {@link TransactionBuilder} used to modify the artifact.
       * @param templateArtifact the identifier of the publishing template artifact.
       */

      public void setTemplateMatchCriteria(TransactionBuilder tx, ArtifactId templateArtifact) {
         //@formatter:off
         var matchCriteria =
            new StringBuilder
                   (
                        this.rendererIdentifier.length()
                      + this.presentationType.length()
                      + ( Objects.nonNull( this.option ) ? this.option.length() : 0 )
                      + 8
                   )
               .append( this.rendererIdentifier )
               .append( " " )
               .append( this.presentationType );

         if( Objects.nonNull( this.option ) ) {
            matchCriteria
               .append( " " )
               .append( this.option );
         }
         //@formatter:on

         tx.createAttribute(templateArtifact, CoreAttributeTypes.TemplateMatchCriteria, matchCriteria.toString());
      }

   }

   /**
    * A dynamic builder for a list of {@link PublishingTemplateMatchCriterion}. The list build has the following options
    * for appending match criteria:
    * <dl>
    * <dt>Always:</dt>
    * <dd>The provided match criteria is always appended to the list.</dd>
    * <dt>If Not Match By Names:</dt>
    * <dd>The provided match criteria is only appended when the flag {@link SetupPublishing#matchPreviewTemplatesByName}
    * is <code>false</code>.</dd>
    * </dl>
    */

   private static class PublishingTemplateMatchCriterionListBuilder {

      /**
       * Saves the list being built.
       */

      private final LinkedList<PublishingTemplateMatchCriterion> list;

      /**
       * Creates a new empty list of {@link PublishingTemplateMatchCriterion}.
       */

      PublishingTemplateMatchCriterionListBuilder() {
         this.list = new LinkedList<PublishingTemplateMatchCriterion>();
      }

      /**
       * Always appends the specified {@link PublishingTemplateMatchCriterion} to the list.
       *
       * @param publishingTemplateMatchCriterion the match criterion to add to the list.
       * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
       * @throws NullPointerException when the parameter <code>publishingTemplateMatchCriterion</code> is
       * <code>null</code>.
       */

      PublishingTemplateMatchCriterionListBuilder appendAlways(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
         this.list.add(Objects.requireNonNull(publishingTemplateMatchCriterion));
         return this;
      }

      /**
       * Only appends the specified {@link PublishingTemplateMatchCriterion} to the list when the flag
       * {@link SetupPublishing#matchPreviewTemplatesByName} is <code>true</code>.
       *
       * @param publishingTemplateMatchCriterion the match criterion to add to the list.
       * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
       */

      PublishingTemplateMatchCriterionListBuilder appendIfMatchByNames(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
         if (SetupPublishing.matchPreviewTemplatesByName) {
            this.list.add(publishingTemplateMatchCriterion);
         }
         return this;
      }

      /**
       * Only appends the specified {@link PublishingTemplateMatchCriterion} to the list when the flag
       * {@link SetupPublishing#matchPreviewTemplatesByName} is <code>false</code>.
       *
       * @param publishingTemplateMatchCriterion the match criterion to add to the list.
       * @return the {@link PublishingTemplateMatchCriterionListBuilder}.
       */

      PublishingTemplateMatchCriterionListBuilder appendIfNotMatchByNames(PublishingTemplateMatchCriterion publishingTemplateMatchCriterion) {
         if (!SetupPublishing.matchPreviewTemplatesByName) {
            this.list.add(publishingTemplateMatchCriterion);
         }
         return this;
      }

      /**
       * Gets the accumulated list of {@link PublishingTemplateMatchCriterion} as an unmodifiable list.
       *
       * @return the list of the match criteria.
       */

      List<PublishingTemplateMatchCriterion> toList() {
         return Collections.unmodifiableList(this.list);
      }

   }

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
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      "WordEditTemplate",                                                                    /* Name                       */
                      "templates/WordEditTemplate.json",                                                     /* Renderer Options File Name */
                      "templates/Word Edit Template.xml",                                                    /* Template Content File Name */
                      List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "SPECIALIZED_EDIT"                                                     /* Presentation Type   */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_TIS,                                               /* Renderer Identifier */
                                      "SPECIALIZED_EDIT"                                                     /* Presentation Type   */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      "WordMergeTemplate",                                                                   /* Name                       */
                      "templates/WordMergeTemplate.json",                                                    /* Renderer Options File Name */
                      "templates/PREVIEW_ALL.xml",                                                           /* Template Content File Name */
                      List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_WORD,                                              /* Renderer Identifier */
                                      "MERGE_EDIT"                                                           /* Presentation Type   */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_WORD,                                              /* Renderer Identifier */
                                      "MERGE"                                                                /* Presentation Type   */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "MERGE"                                                                /* Presentation Type   */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "MERGE_EDIT"                                                           /* Presentation Type   */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "DIFF THREE_WAY_MERGE"                                                 /* Presentation Type   */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      SetupPublishing.previewTemplateName                                                    /* Name                       */
                         (
                            RendererOption.PREVIEW_ALL_VALUE.getKey()
                         ),
                      null,                                                                                  /* Renderer Options File Name */
                      "templates/PREVIEW_ALL.xml",                                                           /* Template Content File Name */
                      new SetupPublishing.PublishingTemplateMatchCriterionListBuilder()                      /* Match Criteria             */
                         .appendAlways
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                       /* Renderer Identifier */
                                         "DIFF"                                                              /* Presentation Type   */
                                      )
                            )
                         .appendIfMatchByNames
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                       /* Renderer Identifier */
                                         "PREVIEW",                                                          /* Presentation Type   */
                                         RendererOption.PREVIEW_ALL_VALUE.getKey()                           /* Option              */
                                      )
                            )
                         .appendIfNotMatchByNames
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                       /* Renderer Identifier */
                                         "PREVIEW"                                                           /* Presentation Type   */
                                      )
                            )
                         .appendIfMatchByNames
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT,                       /* Renderer Identifier */
                                         "PREVIEW_SERVER",                                                   /* Presentation Type   */
                                         RendererOption.PREVIEW_ALL_VALUE.getKey()                           /* Option              */
                                      )
                            )
                         .appendIfNotMatchByNames
                            (
                               new PublishingTemplateMatchCriterion
                                      (
                                         RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT,                       /* Renderer Identifier */
                                         "PREVIEW_SERVER"                                                    /* Presentation Type   */
                                      )
                            )
                         .toList()
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      SetupPublishing.previewTemplateName                                                    /* Name                       */
                         (
                            RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()
                         ),
                      "templates/PREVIEW_ALL_NO_ATTRIBUTES.json",                                            /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL_NO_ATTRIBUTES.xml",                                             /* Template Content File Name */
                      List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "PREVIEW PREVIEW_ALL_NO_ATTRIBUTES"                                    /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "DIFF_NO_ATTRIBUTES"                                                   /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      SetupPublishing.previewTemplateName                                                    /* Name                       */
                         (
                            RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                         ),
                      "templates/PREVIEW_ALL_RECURSE.json",                                                  /* Renderer Options File Name */
                      "templates/PREVIEW_ALL_RECURSE.xml",                                                   /* Template Content File Name */
                      List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "PREVIEW PREVIEW_WITH_RECURSE"                                         /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                      SetupPublishing.previewTemplateName                                                    /* Name                       */
                         (
                            RendererOption.PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                         ),
                      "templates/PREVIEW_ALL_RECURSE_NO_ATTRIBUTES.json",                                    /* Renderer Options File Name */
                      "templates/PREVIEW_ALL_RECURSE_NO_ATTRIBUTES.xml",                                     /* Template Content File Name */
                      List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "PREVIEW PREVIEW_WITH_RECURSE_NO_ATTRIBUTES"                           /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT,                          /* Renderer Identifier */
                                      "PREVIEW_SERVER PREVIEW_WITH_RECURSE_NO_ATTRIBUTES"                    /* Match String        */
                                   )
                         )
                   )
         );
      }
   };
   //@formatter:on

   /**
    * The name of the client side renderer for publishing previews.
    */

   private static String RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT =
      "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer";

   /**
    * The name of the server side renderer for publishing previews.
    */

   private static String RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT =
      "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer";

   /**
    * Fake renderer used for testing.
    */

   private static String RENDERER_IDENTIFIER_TIS = "org.eclipse.osee.framework.ui.skynet.render.TisRenderer";

   /**
    * Fake renderer used of testing.
    */

   private static String RENDERER_IDENTIFIER_WORD = "org.eclipse.osee.framework.ui.skynet.word";

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
   }

   /**
    * Creates the publishing template artifacts defined by {@link SetupPublishing#publishingTemplatesSupplier} for the
    * test database.
    *
    * @param tx the {@link TransactionBuilder} to used for database operations.
    */

   private static void createWordTemplates(TransactionBuilder tx) {
      SetupPublishing.publishingTemplatesSupplier.get().forEach(
         (publishingTemplate) -> publishingTemplate.createPublishingTemplate(tx));
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