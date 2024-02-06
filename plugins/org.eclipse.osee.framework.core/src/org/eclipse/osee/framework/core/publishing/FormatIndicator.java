/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownPublishingAppender;
import org.eclipse.osee.framework.core.publishing.wordml.ValidateWordMl;
import org.eclipse.osee.framework.core.publishing.wordml.WordMlPublishingAppender;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates the enumeration of supported publishing format types.
 *
 * @author Md I. Khan
 * @author Loren K. Ashley
 */

@JsonSerialize(using = FormatIndicatorSerializer.class)
@JsonDeserialize(using = FormatIndicatorDeserializer.class)
public enum FormatIndicator implements ToMessage {

   //@formatter:off
   MARKDOWN
      (
         "markdown",
         ContentPosition.START,
         CoreAttributeTypes.MarkdownContent,
         6,
         ( appender, maximumOutlineDepth ) -> new MarkdownPublishingAppender( appender, maximumOutlineDepth ),
         null
      ),

   TEXT
      (
         "text",
         ContentPosition.END,
         CoreAttributeTypes.PlainTextContent,
         -1,
         ( appender, maximumOutlineDepth ) -> new WordMlPublishingAppender( appender, maximumOutlineDepth ),
         null
      ),

   WORD_ML
      (
         "word-ml",
         ContentPosition.END,
         CoreAttributeTypes.WordTemplateContent,
         9,
         ( appender, maximumOutlineDepth ) -> new WordMlPublishingAppender( appender, maximumOutlineDepth ),
         ValidateWordMl::validateWordMl
      ),

   XHTML
      (
         "xhtml",
         ContentPosition.END,
         CoreAttributeTypes.HtmlContent,
         -1,
         ( appender, maximumOutlineDepth ) -> new WordMlPublishingAppender( appender, maximumOutlineDepth ),
         null
      );
   //@formatter:on

   /**
    * Interface for a factory method to create a {@link PublishingAppender}.
    */

   private interface PublishingAppenderFactory {

      /**
       * Creates a new {@link PublishingAppender} implementation.
       *
       * @param appendable the {@link Appendable} the publishing output will be appended to.
       * @param maxOutlineLevel the maximum number document levels allowed.
       * @return the created {@link PublishingAppender}.
       * @implSpec Implements are expected to throw a {@link RuntimeException} when unable to produce a
       * {@link PublishingAppender}.
       */

      public @NonNull PublishingAppender create(@NonNull Appendable appendable, @NonNull Integer maxOutlineLevel);
   }

   /**
    * Map of the enumeration members by the format names used in publishing template and data rights configuration
    * artifacts.
    */

   private static final @NonNull Map<@NonNull String, @NonNull FormatIndicator> formatIndicators;

   /**
    * Map of the format names used in publishing template and data rights configuration artifacts by their associated
    * enumeration member.
    */

   private static final @NonNull EnumMap<@NonNull FormatIndicator, @NonNull String> formatNames;

   /**
    * The JSON object name used to specify a publishing format.
    */

   private static final String jsonObjectName = "formatIndicator";

   static {

      formatNames = new EnumMap<>(FormatIndicator.class);

      for (var formatIndicator : FormatIndicator.values()) {
         formatNames.put(formatIndicator, formatIndicator.getFormatName());
      }

      formatIndicators = new HashMap<>();

      formatNames.entrySet().forEach((entry) -> formatIndicators.put(entry.getValue(), entry.getKey()));
   }

   /**
    * Finds the {@link FormatIndicator} enumeration member specified by the JSON contained in the
    * <code>jsonParser</code>.
    *
    * @param jsonParser a {@link JsonParser} object containing the JSON to deserialize.
    * @return when the
    * @throws NullPointerException when <code>jsonParser</code> is <code>null</code>.
    * @throws IOException when reading from the <code>jsonParser</code> fails.
    * @throws OseeCoreException when unable to obtain a {@link JsonNode} read tree from the {@link JsonParser}.
    * @throws OseeCoreExcetpion when the format name in the JSON is not a known format name.
    */

   public static @NonNull FormatIndicator deserialize(@NonNull JsonParser jsonParser) throws IOException {

      Objects.requireNonNull(jsonParser);

      JsonNode readTree = jsonParser.getCodec().readTree(jsonParser);

      if (Objects.isNull(readTree)) {
         throw new OseeCoreException("FormatIndicator::deserialize, failed to get JsonNode readTree.");
      }

      //@formatter:off
      var formatName =
         ( readTree instanceof TextNode )
            ? ((TextNode) readTree).asText()
            : readTree.get(FormatIndicator.jsonObjectName).asText();

      var formatIndicator =
         FormatIndicator
            .ofFormatName( formatName )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "FormatIndicator::deserialize, unknown format name." )
                                         .indentInc()
                                         .segment( "format name", formatName )
                                         .toString()
                               )
               );
      //@formatter:on
      return Conditions.requireNonNull(formatIndicator);
   }

   /**
    * Gets the {@link FormatIndicator> for format name used in the publishing and data rights configuration artifacts.
    *
    * @param formatName the name of the format.
    * @return when a {@link FormatIndicator} is defined for the <code>formatName</code> an {@link Optional} containing
    * the associated {@link FormatIndicator} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<FormatIndicator> ofFormatName(@Nullable String formatName) {
      var formatIndicator = Conditions.applyWhenNonNull(formatName, formatIndicators::get);
      return Optional.ofNullable(formatIndicator);
   }

   /**
    * Saves the publishing position of the main artifact content in relation to the attributes for the format.
    */

   private final @NonNull ContentPosition contentPosition;

   /**
    * Saves a {@link Function} that can be used to validate the publishing template content for the format. The
    * validator's {@link String} parameter contains the template content to be validate. When validation is successful a
    * <code>null</code> {@link Message} is returned; otherwise, a {@link Message} with the validation error message.
    */

   private final @Nullable Function<String, Message> contentValidator;

   /**
    * Saves the format name used as publishing template format keys and in the publishing template publish options.
    */

   private final @NonNull String formatName;

   /**
    * Saves the {@link AttributeTypeToken} for the attribute that contains the main publishing content.
    */

   private final @NonNull AttributeTypeToken mainContentAttributeTypeToken;

   /**
    * Saves the maximum number of outlining levels supported by the format.
    */

   private final int maximumOutlineDepth;

   /**
    * Saves a {@link PublishingAppender} factory for the format. The factory's first parameter is the {@link Appendable}
    * used to accumulate the output for the publish. The second parameter is the maximum outlining depth to be used by
    * the appender. This value should be less than or equal to the maximum outlining depth for the format.
    */

   private final @NonNull PublishingAppenderFactory publishingAppenderFactory;

   /**
    * Creates a {@link FormatIndicator} enumeration member with the configuration data for the publishing format.
    *
    * @param formatName the name used as publishing template format keys and in the publishing template publish options.
    * @param contentPosition indicates the position of the main artifact content in relation to the attributes.
    * @param mainContentAttributeTypeToken the attribute that contains the main publishing content.
    * @param maximumOutlineDepth the maximum number of outlining levels supported by the format.
    * @param publishingAppenderFactory a factory method to create the {@link PublishingAppender} implementation for the
    * format.
    * @param contentValidator a method to validate the publishing content for the format.
    */

   //@formatter:off
   private
      FormatIndicator
         (
            @NonNull  String                             formatName,
            @NonNull  ContentPosition                    contentPosition,
            @Nullable AttributeTypeToken                 mainContentAttributeTypeToken,
                      int                                maximumOutlineDepth,
            @NonNull  PublishingAppenderFactory          publishingAppenderFactory,
            @Nullable Function<String, Message>          contentValidator
         ) {
   //@formatter:on
      this.formatName = Conditions.requireNonNull(formatName);
      this.contentPosition = Conditions.requireNonNull(contentPosition);
      this.mainContentAttributeTypeToken = Conditions.requireNonNull(mainContentAttributeTypeToken);
      this.maximumOutlineDepth = maximumOutlineDepth;
      this.publishingAppenderFactory = Conditions.requireNonNull(publishingAppenderFactory);
      this.contentValidator = contentValidator;
   }

   /**
    * Creates a {@link PublishingAppender} for the format with the maximum outlining depth.
    *
    * @param appendable the {@link Appendable} the publishing output will be appended to.
    * @return a {@link PublishingAppender} implementation for the format.
    */

   public @NonNull PublishingAppender createPublishingAppender(@NonNull Appendable appendable) {
      //@formatter:off
      return
         this.publishingAppenderFactory
            .create
               (
                  Conditions.requireNonNull( appendable ),
                  this.maximumOutlineDepth
               );
      //@formatter:on
   }

   /**
    * Creates a {@link PublishingAppender} for the format with a limited outlining depth.
    *
    * @param appendable the {@link Appendable} the publishing output will be appended to.
    * @param maximumOutlineDepth the maximum outlining depth to be allowed by the created {@link PublishingAppender}.
    * @return a {@link PublishingAppender} implementation for the format.
    */

   public @NonNull PublishingAppender createPublishingAppender(@NonNull Appendable appendable, int maximumOutlineDepth) {
      //@formatter:off
      return
         this.publishingAppenderFactory.create
            (
               Conditions.requireNonNull(appendable),
               Math.max
                  ( 0,
                    Math.min
                       (
                          maximumOutlineDepth,
                          this.maximumOutlineDepth
                       )
                  )
            );
      //@formatter:on
   }

   /**
    * Gets the position of the main artifact publishing content relative to the attributes.
    *
    * @return the {@link ContentPosition}.
    */

   public @NonNull ContentPosition getContentPosition() {
      return this.contentPosition;
   }

   /**
    * Gets publishing configuration name for the format.
    *
    * @return the name of the format type.
    */

   public @NonNull String getFormatName() {
      return this.formatName;
   }

   /**
    * Gets the attribute type that contains the main publishing format.
    *
    * @return the {@link AttributeTypeToken} of the main content attribute.
    */

   public @NonNull AttributeTypeToken getMainContentAttributeTypeToken() {
      return this.mainContentAttributeTypeToken;
   }

   /**
    * Gets the maximum outlining depth allowed by the format.
    *
    * @return the maximum outlining depth.
    */

   public int getMaximumOutlineDepth() {
      return this.maximumOutlineDepth;
   }

   /**
    * Predicate to determine if the {@link FormatIndicator} is {@link FormatIndicator#MARKDOWN}.
    *
    * @return <code>true</code> when the {@link FormatIndicator} is {@link FormatIndicator#MARKDOWN}; otherwise,
    * <code>false</code>.
    */

   public boolean isMarkdown() {
      return this == FormatIndicator.MARKDOWN;
   }

   /**
    * Predicate to determine if the {@link FormatIndicator} is {@link FormatIndicator#WORD_ML}.
    *
    * @return <code>true</code> when the {@link FormatIndicator} is {@link FormatIndicator#WORD_ML}; otherwise,
    * <code>false</code>.
    */

   public boolean isWordMl() {
      return this == FormatIndicator.WORD_ML;
   }

   /**
    * Creates the JSON serialized form of the enumeration member.
    *
    * <pre>
    * { "formatIndicator" : "&lt;format-name&gt;" }
    * </pre>
    *
    * Where format-name is the format name used in the publishing and data rights configuration artifacts.
    *
    * @param jsonGenerator the {@link JsonGenerator} used for the serialization.
    * @throws NullPointerException when <code>jsonGenerator</code> is <code>null</code>.
    * @throws IOException when serialization fails.
    */

   public void serialize(@NonNull JsonGenerator jsonGenerator) throws IOException {
      Objects.requireNonNull(jsonGenerator);
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField(FormatIndicator.jsonObjectName, this.getFormatName());
      jsonGenerator.writeEndObject();
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
         .title( "Format Indicator" )
         .indentInc()
         .segment( "Format Name", this.getFormatName() )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }

   /**
    * Validates a publishing template's content for the format.
    *
    * @param content the publishing template content to validate.
    * @param message validation errors are append to this {@link Message}.
    * @return when:
    * <dl>
    * <dt>a validator is defined for the format:</dt>
    * <dd><code>true</code> when the content validates; otherwise, <code>false</code>.</dd>
    * <dt>a validator is not defined for the format:</dt>
    * <dd><code>true</code>.
    * </dl>
    * @throws NullPointerException when <code>content</code> is <code>null</code>.
    */

   public @Nullable Message validatePublishingTemplateContent(@NonNull String content) {
      //@formatter:off
      return
         ( this.contentValidator != null )
            ? this.contentValidator.apply( Conditions.requireNonNull( content ) )
            : null;
      //@formatter:on
   }

}

/* EOF */
