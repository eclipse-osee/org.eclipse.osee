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

package org.eclipse.osee.define.operations.publisher.templatemanager;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroup;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateScalarKey;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateVectorKey;
import org.eclipse.osee.define.util.AttributeUtils;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.InvalidPublishOptionsException;
import org.eclipse.osee.framework.core.publishing.PublishOptions;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The Template Manager internal class used to cache a Publishing Template.
 *
 * @author Loren K. Ashley
 */

class PublishingTemplate implements ToMessage {

   /**
    * Extracts the Publishing Template Artifact's {@link CoreAttributeTypes#TemplateMatchCriteria} attribute values and
    * builds an unmodifiable {@link List}.
    *
    * @param artifactReadable the artifact to read from.
    * @return on success a {@link Pair} with the {@link PublishingTemplateVectorKey} of the Publishing Template's match
    * criteria and no {@link Message}; otherwise, an empty {@link PublishingTemplateVectorKey} and a {@link Message}
    * describing the failure.
    */

   private static @NonNull Pair<@NonNull PublishingTemplateVectorKey, @Nullable Message> buildTemplateMatchCriteriaList(@NonNull ArtifactReadable artifactReadable) {
      try {

         var matchCriteriaObjectList = artifactReadable.getAttributeValues(CoreAttributeTypes.TemplateMatchCriteria);
         //@formatter:off
         var matchCriteriaList =
            matchCriteriaObjectList
               .stream()
               .map( ( matchCriteria ) -> new PublishingTemplateScalarKey( matchCriteria.toString(), PublishingTemplateKeyType.MATCH_CRITERIA ) )
               .collect( Collectors.toUnmodifiableList() );
         //@formatter:on
         var vectorKey = new PublishingTemplateVectorKey(matchCriteriaList);

         return Pair.createNullableImmutable(vectorKey, null);

      } catch (Exception e) {
         //@formatter:off
         var message =
            new Message()
                   .title( "Failed to build the Match Criteria list." )
                   .reasonFollows( e )
                   ;

         var vectorKey = new PublishingTemplateVectorKey( List.of() );

         return Pair.createNullableImmutable( vectorKey, message );
         //@formatter:on
      }
   }

   /**
    * Parses an {@link ArtifactReadable} into an {@link PublishingTemplate}.
    *
    * @param artifactReadable the OSEE Artifact containing the Publishing Template.
    * @return on success a {@link Pair} containing the {@link PublishingTemplate} and no {@link Message}; otherwise, a
    * {@link Pair} containing the possible incomplete {@link PublishingTemplate} and a {@link Message} containing the
    * errors that occurred creating the {@link PublishingTemplate}.
    * @throws NullPointerException when the <code>artifactReadable</code> is <code>null</code>.
    * @implNote When the {@link CoreAttributeTypes#PublishingTemplateContentByFormatMapEntry} attribute does not contain
    * an entry for the {@link FormatIndicator#WORD_ML}, the attribute {@link CoreAttributeTypes#WholeWordContent} will
    * be checked for Word ML content.
    */

   static @NonNull Pair<@NonNull PublishingTemplate, @Nullable String> create(@NonNull final ArtifactReadable artifactReadable) {

      /*
       * Validate the template artifact
       */

      Conditions.requireNonNull(artifactReadable, "PublishingTemplate", "create", "artifactReadable");

      var message = new Message();

      final var identifier =
         new PublishingTemplateScalarKey("AT-" + artifactReadable.getIdString(), PublishingTemplateKeyType.IDENTIFIER);

      //@formatter:off
      final var matchCriteria =
         PublishingTemplate
            .buildTemplateMatchCriteriaList( artifactReadable )
            .getFirstNonNullIfPresentOthers( message::copy );
      //@formatter:on

      final var name = new PublishingTemplateScalarKey(artifactReadable.getName(), PublishingTemplateKeyType.NAME);

      final var safeName =
         new PublishingTemplateScalarKey(artifactReadable.getSafeName(), PublishingTemplateKeyType.SAFE_NAME);

      /*
       * Default publish options will be provided if an error occurs.
       */

      //@formatter:off
      var publishOptions =
         PublishingTemplate
            .loadPublishOptions( artifactReadable, identifier.getKey(), name.getKey() )
            .getFirstNonNullIfPresentOthers( message::copy );
      //@formatter:on

      /*
       * An empty template content map will be provided if an error occurs.
       */

      //@formatter:off
      var templateContentMap =
         PublishingTemplate
            .loadTemplateContentMap( artifactReadable )
            .getFirstNonNullIfPresentOthers( message::copy );
      //@formatter:on

      if (!templateContentMap.containsKey(FormatIndicator.WORD_ML)) {

         //@formatter:off
         PublishingTemplate
            .loadLegacyWordMlTemplateContent( artifactReadable )
            .ifPresent
               (
                  ( content ) -> templateContentMap.put( FormatIndicator.WORD_ML, content ),
                  message::copy
               );
         //@formatter:on
      }

      var templateContent = templateContentMap.get(FormatIndicator.WORD_ML);

      if (templateContent != null) {

         //@formatter:off
         PublishingTemplate
            .updateWithAlternateStyles( artifactReadable, templateContent )
            .ifPresent
               (
                  ( templateContentWithAlternateStyles ) -> templateContentMap.put
                                                                (
                                                                   FormatIndicator.WORD_ML,
                                                                   templateContentWithAlternateStyles.toString()
                                                                ),
                  message::copy
               );
         //@formatter:on
      }

      if (templateContentMap.isEmpty()) {
         //@formatter:off
         message
            .title( "PublishingTemplate::create, publishing template does not have content for any format." )
            .indentInc()
            .segment( "Artifact Identifier", artifactReadable.getIdString() )
            .segment( "Artifact Name",       artifactReadable.getName()     );
         //@formatter:on
      }

      var messageString = message.isEmpty() ? null : message.toString();

      //@formatter:off
      var publishingTemplate =
             new PublishingTemplate
                    (
                       artifactReadable,
                       identifier,
                       matchCriteria,
                       name,
                       publishOptions,
                       safeName,
                       templateContentMap,
                       messageString
                    );
      //@formatter:on

      return new Pair<>(publishingTemplate, messageString);
   }

   /**
    * Loads the Word Markup Language publishing template content from the {@link CoreAttributeTypes#WholeWordContent}
    * attribute.
    *
    * @param artifactReadable the artifact to read from.
    * @return on success a {@link Pair} containing the Word ML publishing template content and no {@link Message};
    * otherwise, a {@link Pair} with <code>null</code> content and a {@link Message} describing the error.
    */

   private static @NonNull Pair<@Nullable String, @Nullable Message> loadLegacyWordMlTemplateContent(ArtifactReadable artifactReadable) {

      try {

         var wholeWordContent = artifactReadable.getSoleAttributeAsString(CoreAttributeTypes.WholeWordContent);

         if (wholeWordContent == null) {
            return Pair.empty();
         }

         var message = FormatIndicator.WORD_ML.validatePublishingTemplateContent(wholeWordContent);

         //@formatter:off
         return
            Objects.isNull( message )
               ? Pair.createNullableImmutable( wholeWordContent, null    )
               : Pair.createNullableImmutable( null,             message );
         //@formatter:on

      } catch (MultipleAttributesExist e) {

         //@formatter:off
         var message = new Message()
                              .title( "PublishingTemplate::create, failed to obtain publish options from the publishing template artifact." )
                              .indentInc()
                              .segment( "Artifact Identifier", artifactReadable.getIdString() )
                              .segment( "Artifact Name",       artifactReadable.getName()     )
                              .indentDec()
                              .reasonFollows( e )
                              ;
         //@formatter:on

         return Pair.createNullableImmutable(null, message);

      } catch (AttributeDoesNotExist e) {

         return Pair.empty();

      }

   }

   /**
    * Loads and parses the publish options from the {@link CoreAttributeTypes#RendererOptions} attribute.
    *
    * @param artifactReadable the {@link ArtifactReadable} to get the publishing options from.
    * @return on success a {@link Pair} with the loaded {@link PublishingOptions} and no {@link Message}; otherwise, a
    * {@link Pair} with default {@link PublishingOptions} and a {@link Message} describing the loading errors.
    */

   private static @NonNull Pair<@NonNull PublishOptions, @Nullable Message> loadPublishOptions(ArtifactReadable artifactReadable, String templateIdentifier, String templateName) {

      try {

         var publishOptions =
            PublishOptions.create(artifactReadable.getSoleAttributeAsString(CoreAttributeTypes.RendererOptions));

         return Pair.createNullableImmutable(publishOptions, null);

      } catch (InvalidPublishOptionsException e) {

         //@formatter:off
         e.setPublishingTemplateInformation
            (
               artifactReadable.getIdString(),
               artifactReadable.getName()
            );
         //@formatter:on

         var message = new Message().block(e.getMessage());

         return Pair.createNullableImmutable(new PublishOptions().defaults(), message);

      } catch (MultipleAttributesExist e) {

         //@formatter:off
         var message = new Message()
                              .title( "PublishingTemplate::create, failed to obtain publish options from the publishing template artifact." )
                              .indentInc()
                              .segment( "Artifact Identifier", artifactReadable.getIdString() )
                              .segment( "Artifact Name",       artifactReadable.getName()     )
                              .indentDec()
                              .reasonFollows( e )
                              ;
         //@formatter:on

         return Pair.createNullableImmutable(new PublishOptions().defaults(), message);

      } catch (AttributeDoesNotExist e) {

         return Pair.createNullableImmutable(new PublishOptions().defaults(), null);
      }

   }

   /**
    * Loads the publishing template content map from the
    * {@link CoreAttributeTypes#PublishingTemplateContentByFormatMapEntry} attribute.
    *
    * @param artifactReadable the {@link ArtifactReadable} to get the publishing template content map entries from.
    * @return on success a {@link Pair} containing the publishing template content map and no {@link Message};
    * otherwise, a {@link Pair} with a <code>null</code> publishing template content map an a {@link Message} describing
    * the loading errors.
    */

   private static Pair<@NonNull Map<FormatIndicator, String>, @Nullable Message> loadTemplateContentMap(@NonNull ArtifactReadable artifactReadable) {

      var outMessage = new Message();

      //@formatter:off
      var templateContentMap =
         AttributeUtils
            .getMapEntryAttributeValues
               (
                  artifactReadable,
                  CoreAttributeTypes.PublishingTemplateContentByFormatMapEntry
               )
            .stream()
            .filter
               (
                  ( mapEntry ) ->
                  {
                     var key = mapEntry.getKey();
                     return Objects.nonNull( key ) && !key.isBlank();
                  }
               )
            .collect
               (
                  org.eclipse.osee.framework.jdk.core.util.Collectors
                     .toMap
                        (
                           /* Map Factory           */ ()-> new EnumMap<>(FormatIndicator.class),
                           /* Key Transformer       */ ( formatName ) -> Conditions.requireNonNull( FormatIndicator.ofFormatName( formatName ).get() ),
                           /* Value Transformer     */ Conditions.requireNonNull( Function.identity() ),
                           /* Post Transform Filter */ ( format, content ) ->
                                                       {
                                                          if( content == null ) {
                                                             return false;
                                                          }
                                                          var message = format.validatePublishingTemplateContent( content );
                                                          if( Objects.nonNull( message ) ) {
                                                             outMessage.copy( message );
                                                             return false;
                                                          }
                                                          return true;
                                                       }
                        )
               );
      //@formatter:on
      return Pair.createNullableImmutable(templateContentMap, outMessage.isModified() ? outMessage : null);
   }

   /**
    * Publishing Templates containing Word Markup Language content may have a supporting information relationship to
    * another artifact containing Word ML style definitions in the {@link CoreAttributeTypes.WholeWordContent}
    * attribute. When a supporting information relationship exists the Word ML style definitions will be read from the
    * linked artifact and substituted into the Word ML content of the Publishing Template.
    *
    * @param artifactReadable the Publishing Template artifact.
    * @param templateContentWordMl the Word ML content of the Publishing Template.
    * @return when:
    * <dl>
    * <dt>A supporting information relationship does not exist:</dt>
    * <dd>an empty {@link Pair}.</dd>
    * <dt>Alternate styles were successfully read from the supporting information artifact:</dt>
    * <dd>a {@link Pair} containing the <code>templateContentWordMl</code> with the style definitions replaced and no
    * {@link Message}.</dd>
    * <dt>A error occured:</dt>
    * <dd>a {@link Pair} with <code>null</code> publishing template content and a {@link Message} describing the
    * error.</dd>
    * </dl>
    */

   private static Pair<CharSequence, Message> updateWithAlternateStyles(final @NonNull ArtifactReadable artifactReadable, @NonNull CharSequence templateContentWordMl) {

      /*
       * Check for alternate styles
       */

      List<ArtifactReadable> templateRelatedArtifacts;

      try {

         templateRelatedArtifacts =
            artifactReadable.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getList();

      } catch (Exception e) {

         //@formatter:off
         var message =
            new Message()
                   .title( "PublishingTemplate::updateWithAlternateStyles, failed to follow supporting info relationship." )
                   .indentInc()
                   .segment( "Artifact Identifier", artifactReadable.getIdString() )
                   .segment( "Artifact Name",       artifactReadable.getName()     )
                   .indentDec()
                   .reasonFollows( e );
         //@formatter:on

         return Pair.createNullableImmutable(null, message);

      }

      if (templateRelatedArtifacts.isEmpty()) {
         return Pair.empty();
      }

      if (templateRelatedArtifacts.size() > 1) {

         //@formatter:off
         var message =
            new Message()
                   .title( "PublishingTemplate::updateWithAlternateStyles, only one supporting info relationship is allowed." )
                   .indentInc()
                   .segment( "Artifact Identifier", artifactReadable.getIdString() )
                   .segment( "Artifact Name",       artifactReadable.getName()     );
         //@formatter:on

         return Pair.createNullableImmutable(null, message);

      }

      var stylesArtifactReadable = templateRelatedArtifacts.get(0);

      String styles;

      try {

         styles = stylesArtifactReadable.getSoleAttributeAsString(CoreAttributeTypes.WholeWordContent);

      } catch (MultipleAttributesExist e) {

         //@formatter:off
         var message = new Message()
                              .title( "PublishingTemplate::updateWithAlternateStyles, only one WholeWordContent attribute value is allowed in the alternate styles artifact." )
                              .indentInc()
                              .segment( "Template Artifact Identifier",         artifactReadable.getIdString()       )
                              .segment( "Template Artifact Name",               artifactReadable.getName()           )
                              .segment( "Alternate Styles Artifact Identifier", stylesArtifactReadable.getIdString() )
                              .segment( "Alternate Styles Artifact Name",       stylesArtifactReadable.getName()     )
                              .indentDec()
                              .reasonFollows( e )
                              ;
         //@formatter:on

         return Pair.createNullableImmutable(null, message);

      } catch (AttributeDoesNotExist e) {

         //@formatter:off
         var message = new Message()
                              .title( "PublishingTemplate::updateWithAlternateStyles, the alternate styles artifact does not contain any WholeWordContent." )
                              .indentInc()
                              .segment( "Template Artifact Identifier",         artifactReadable.getIdString()       )
                              .segment( "Template Artifact Name",               artifactReadable.getName()           )
                              .segment( "Alternate Styles Artifact Identifier", stylesArtifactReadable.getIdString() )
                              .segment( "Alternate Styles Artifact Name",       stylesArtifactReadable.getName()     )
                              .indentDec()
                              .reasonFollows( e )
                              ;
         //@formatter:on

         return Pair.createNullableImmutable(null, message);

      }

      /*
       * If alternate styles were found with a supporting info relationship, replace the template styles
       */

      if (Strings.isInvalidOrBlank(styles)) {
         //@formatter:off
         var message = new Message()
                              .title( "PublishingTemplate::updateWithAlternateStyles, the alternate styles artifact WholeWordContent is invalid." )
                              .indentInc()
                              .segment( "Template Artifact Identifier",         artifactReadable.getIdString()       )
                              .segment( "Template Artifact Name",               artifactReadable.getName()           )
                              .segment( "Alternate Styles Artifact Identifier", stylesArtifactReadable.getIdString() )
                              .segment( "Alternate Styles Artifact Name",       stylesArtifactReadable.getName()     )
                              ;
         //@formatter:on
         return Pair.createNullableImmutable(null, message);
      }

      var replacedStylesTemplateContentWordMl =
         Conditions.requireNonNull(WordCoreUtil.replaceStyles(templateContentWordMl, styles).toString());

      var validationMessage =
         FormatIndicator.WORD_ML.validatePublishingTemplateContent(replacedStylesTemplateContentWordMl);

      if (Objects.nonNull(validationMessage)) {

         //@formatter:off
         var message =
            new Message()
                   .title( "PublishingTemplate::updateWithAlternateStyles, template content with styles replaced is not valid Word Markup Language." )
                   .indentInc()
                   .segment( "Template Artifact Identifier",         artifactReadable.getIdString()       )
                   .segment( "Template Artifact Name",               artifactReadable.getName()           )
                   .segment( "Alternate Styles Artifact Identifier", stylesArtifactReadable.getIdString() )
                   .segment( "Alternate Styles Artifact Name",       stylesArtifactReadable.getName()     )
                   .indentDec()
                   .title( "Reason Follows:" )
                   .blank()
                   .copy( validationMessage );
         //@formatter:off

         return Pair.createNullableImmutable(null, message);

      }

      return Pair.createNullableImmutable( templateContentWordMl, null );

   }

   /**
    * Saves the {@link ArtifactReadable} containing the Publishing Template.
    */

   private @NonNull final ArtifactReadable artifactReadable;

   /**
    * A {@link String} representation of the OSEE Artifact Identifier.
    */

   private @NonNull final PublishingTemplateScalarKey identifier;

   /**
    * Saves a key extractor for each {@link PublishingTemplateKeyType}. For {@link PublishingTemplateScalarKey} keys the
    * iterator will provide only one key, the {@link PublishingTemplateScalarKey}. For
    * {@link PublishingTemplateVectorKey} keys, the iterator will provide all of the
    * {@link PublishingTemplateScalarKey}s in the key vector.
    */

   private final @NonNull EnumMap<PublishingTemplateKeyType, Iterable<PublishingTemplateScalarKey>> keyExtractors;

   /**
    * Saves an unmodifiable list of the Publishing Template Artifact's TemplateMatchCritera attribute values.
    */

   private @NonNull final PublishingTemplateVectorKey matchCriteria;

   /**
    * The OSEE Artifact name.
    */

   private @NonNull final PublishingTemplateScalarKey name;

   /**
    * Saves the parsed JSON Publish Options from the OSEE Artifact.
    */

   private @NonNull final PublishOptions publishOptions;

   /**
    * Saves the safe name of the Publishing Template's Artifact
    */

   private @NonNull final PublishingTemplateScalarKey safeName;

   /**
    * Saves a {@link String} describing any errors that occurred while loading the publishing template. A
    * <code>null</code> value indicates that no errors occurred.
    */

   private final @Nullable String status;

   /**
    * Saves the template content for each defined format.
    */

   private final @NonNull Map<FormatIndicator, String> templateContentMap;

   /**
    * Creates a new {@link PublishingTemplate} and generates the key extractors for the publishing template.
    *
    * @param artifactReadable the {@link ArtifactReadable} the {@link PublishingTemplate} was loaded from.
    * @param identifier the template manager generated identifier for the {@link PublishingTemplate}.
    * @param matchCriteria a list of the publishing template match criteria strings.
    * @param name the {@link PublishingTemplate} name.
    * @param publishOptions the renderer options JSON extracted from the publishing template artifact.
    * @param safeName the safe name of the artifact the publishing template was read from.
    * @param templateContentMap a {@link Map} of the publishing template content by the publishing format.
    * @param status a {@link String} describing any error that occurred while loading the publishing template.
    */

   //@formatter:off
   private PublishingTemplate
      (
         @NonNull  ArtifactReadable             artifactReadable,
         @NonNull  PublishingTemplateScalarKey  identifier,
         @NonNull  PublishingTemplateVectorKey  matchCriteria,
         @NonNull  PublishingTemplateScalarKey  name,
         @NonNull  PublishOptions               publishOptions,
         @NonNull  PublishingTemplateScalarKey  safeName,
         @NonNull  Map<FormatIndicator, String> templateContentMap,
         @Nullable String                       status
      ) {

      this.artifactReadable   = artifactReadable;
      this.identifier         = identifier;
      this.matchCriteria      = matchCriteria;
      this.name               = name;
      this.publishOptions     = publishOptions;
      this.safeName           = safeName;
      this.templateContentMap = templateContentMap;
      this.status             = status;

      this.keyExtractors = new EnumMap<>( PublishingTemplateKeyType.class );
      this.keyExtractors.put( PublishingTemplateKeyType.IDENTIFIER,     this.getIdentifierKeyExtractor()    );
      this.keyExtractors.put( PublishingTemplateKeyType.MATCH_CRITERIA, this.getMatchCriteriaKeyExtractor() );
      this.keyExtractors.put( PublishingTemplateKeyType.NAME,           this.getNameKeyExtractor()          );
      this.keyExtractors.put( PublishingTemplateKeyType.SAFE_NAME,      this.getSafeNameKeyExtractor()      );

   }
   //@formatter:on

   /**
    * Creates a {@link PublishingTemplate} object populated with the standard publishing template data.
    *
    * @return a populated {@link PublishingTemplate} object.
    */

   public org.eclipse.osee.framework.core.publishing.PublishingTemplate getBean(@NonNull FormatIndicator formatIndicator) {
      //@formatter:off
      return
         new org.eclipse.osee.framework.core.publishing.PublishingTemplate
                (
                   Conditions.requireNonNull( this.identifier.getKey() ),
                   Conditions.requireNonNull( this.name.getKey() ),
                   this.publishOptions,
                   this.templateContentMap.get(formatIndicator)
                );
      //@formatter:on
   }

   /**
    * Gets the publishing template's unique identifier.
    *
    * @return publishing template identifier.
    */

   public PublishingTemplateScalarKey getIdentifier() {
      return this.identifier;
   }

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's identifier.
    *
    * @return an {@link Iterator} that returns the Publishing Template's identifier.
    */

   Iterable<PublishingTemplateScalarKey> getIdentifierKeyExtractor() {
      return this.makeScalarKeyIterable(this::getIdentifier);
   }

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's name.
    */

   public Iterable<PublishingTemplateScalarKey> getKeyIterable(PublishingTemplateKeyType keyType) {
      return this.keyExtractors.get(keyType);
   }

   /**
    * Gets an unmodifiable {@link List} of the Publishing Template's match criteria {@link String}s.
    * <p>
    * The match criteria strings for Artifact Publishing Templates are extracted from the OSEE Artifact's
    * TemplateMatchCriteria attribute values.
    *
    * @return a {@link List} of the Publishing Template's match criteria {@link String}s.
    */

   public PublishingTemplateVectorKey getMatchCriteria() {
      return this.matchCriteria;
   }

   /**
    * Gets a vector key supplier as an {@link Iterator} for the Publishing Template's match criteria.
    *
    * @return an {@link Iterator} that returns the Publishing Template's match criteria.
    */

   Iterable<PublishingTemplateScalarKey> getMatchCriteriaKeyExtractor() {
      return this.makeVectorKeyIterable(this::getMatchCriteria);
   }

   /**
    * Gets the publishing template's name. Name's are not guaranteed to be unique.
    *
    * @return the name of the publishing template.
    */

   public PublishingTemplateScalarKey getName() {
      return this.name;
   }

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's name.
    */

   public Iterable<PublishingTemplateScalarKey> getNameKeyExtractor() {
      return this.makeScalarKeyIterable(this::getName);
   }

   /**
    * Gets a new {@link PublishingTemplateKeyGroup} with all the cache keys for the publishing template.
    *
    * @return a {@link PublishingTemplateKeyGroup} will all of the publishing template cache keys.
    */

   PublishingTemplateKeyGroup getPublishingTemplateKeyGroup() {
      return new PublishingTemplateKeyGroup(this.getIdentifier(), this.getMatchCriteria(), this.getName(),
         this.getSafeName());
   }

   /**
    * Gets the {@link PublishOptions} specified in the publishing template.
    *
    * @return the publish options.
    */

   public PublishOptions getPublishOptions() {
      return this.publishOptions;
   }

   /**
    * Gets the publishing template's name. Name's are not guaranteed to be unique.
    *
    * @return the name of the publishing template.
    */

   public PublishingTemplateScalarKey getSafeName() {
      return this.safeName;
   }

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's safe name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's safe name.
    */

   Iterable<PublishingTemplateScalarKey> getSafeNameKeyExtractor() {
      return this.makeScalarKeyIterable(this::getSafeName);
   }

   /**
    * Gets a {@link String} report on the status of the publishing template.
    *
    * @return a {@link String} describing the publishing template.
    */

   public String getStatus() {

      //@formatter:off
      var message =
         new Message()
                .title( "Publishing Template Status" )
                .indentInc()
                .segment( "Identifier",                 this.identifier                           )
                .segment( "Name",                       this.name                                 )
                .segment( "Safe Name",                  this.safeName                             )
                .segment( "Artifact Name",              this.artifactReadable.getName()           )
                .segment( "Artifact Identifier",        this.artifactReadable.getIdString()       )
                .segment( "Artifact Branch Identifier", this.artifactReadable.getBranchIdString() )
                .segment( "Formats",                    this.templateContentMap.keySet(),           FormatIndicator::getFormatName )
                .segment( "Match Criteria",             this.matchCriteria                        )
                .segment( "Publish Options",            this.publishOptions                       )
                ;

      if( Strings.isValidAndNonBlank( this.status ) ) {
         message
            .indentDec()
            .title( "Publishing Template Errors" )
            .indentInc()
            .block( this.status )
            ;
      }
      //@formatter:off

      return message.toString();
   }

   /**
    * Makes a new {@link Iterator} for scalar keys.
    *
    * @param scalarKeySupplier the scalar key supplier.
    * @return an {@link Iterator} that returns only one value, the value from the <code>keySupplier</code>.
    */

   Iterable<PublishingTemplateScalarKey> makeScalarKeyIterable(Supplier<PublishingTemplateScalarKey> scalarKeySupplier) {
      //@formatter:off
      return
         new Iterable<PublishingTemplateScalarKey> () {

         @Override
         public Iterator<PublishingTemplateScalarKey> iterator() {

            return
               new Iterator<PublishingTemplateScalarKey>() {

                  boolean first = true;
                  Supplier<PublishingTemplateScalarKey> iteratorKeySupplier = scalarKeySupplier;

                  @Override
                  public boolean hasNext() {
                     return this.first;
                  }

                  @Override
                  public PublishingTemplateScalarKey next() {
                     this.first = false;
                     return this.iteratorKeySupplier.get();
                  }
               };
         }
      };
      //@formatter:on
   }

   /**
    * Makes a new {@link Iterator} for vector keys.
    *
    * @param keyListSupplier the vector key supplier.
    * @return an {@link Iterator} that returns keys on the key vector provided by the <code>keyListSupplier</code>.
    */

   Iterable<PublishingTemplateScalarKey> makeVectorKeyIterable(Supplier<PublishingTemplateVectorKey> vectorKeySupplier) {
      //@formatter:off
      return
         new Iterable<PublishingTemplateScalarKey> () {

         @Override
         public Iterator<PublishingTemplateScalarKey> iterator() {
            return vectorKeySupplier.get().getKey().iterator();
         }
      };
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PublishingTemplate" )
         .indentInc()
         .segment( "Class",                   this.getClass().getSimpleName() )
         .segment( "Artifact Readable",       this.artifactReadable           )
         .segment( "Identifier",              this.identifier                 )
         .segment( "Name",                    this.name                       )
         .segment( "Template Match Criteria", this.matchCriteria              )
         .toMessage( this.publishOptions )
         .segmentMap( "Template Content Map", this.templateContentMap )
         ;
      //@formatter:off

     return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */