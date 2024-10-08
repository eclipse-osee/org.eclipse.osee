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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Contains the Publishing Options specified in a Publishing Template.
 *
 * @author Loren K. Ashley
 */

public class PublishOptions implements ToMessage {

   /**
    * The allowable values for the member {@link #elementType}.
    */

   //@formatter:off
   private static Set<String> elementTypes =
      Set.of
         (
            "Artifact",
            "NestedTemplate"
         );
   //@formatter:off

   /**
    * Parses the JSON Publishing Options string read from the {@link CoreAttributeTypes#RendererOptions} attribute.
    * @param jsonPublishOptionsString the JSON string to be parsed.
    * @return a {@link PublishingOptions} structure populated from the <code>jsonPublsihOptionsString</code>.
    * @throws InvalidPublishOptionsException when parsing fails.
    */

   public static PublishOptions create(String jsonPublishOptionsString) {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         var publishOptions = objectMapper.readValue(jsonPublishOptionsString, PublishOptions.class);
         publishOptions.defaults();
         return publishOptions;
      } catch (Exception e) {
         throw new InvalidPublishOptionsException(jsonPublishOptionsString, e);
      }
   }

   /**
    * Validates the provided value for the member {@link #elementType}.
    *
    * @param elementType the value to check.
    * @throws IllegalArgumentException when the parameter <code>elementType</code> is not one of the following strings:
    * <ul>
    * <li>"Artifact"</li>
    * <li>"NestedTemplate"</li>
    * </ul>
    */

   private static void validateElementType(String elementType) {
      if (!PublishOptions.elementTypes.contains(elementType)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "RendererOptions::validateElementType, parameter \"elementType\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified elementType", elementType )
                             .segmentIndexed( "Allowed Values", PublishOptions.elementTypes.toArray() )
                             .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Saves the publishing options for attributes as an array of {@link AttributeOptions}.
    */

   @JsonProperty("AttributeOptions")
   private AttributeOptions[] attributeOptions;

   /**
    * Known values:
    * <ul>
    * <li>Artifact</li>
    * <li>NestedTemplate</li>
    * </ul>
    * The element type must be specified.
    */

   @JsonProperty("ElementType")
   private String elementType;

   /**
    * Saves the publishing options for pseudo-attributes as an array of {@link MetadataOptions}.
    * <p>
    * The metadata options array may be empty.
    */

   @JsonProperty("MetadataOptions")
   private MetadataOptions[] metadataOptions;

   /**
    * Saves the publishing options for nested publishes.
    * <p>
    * The nested templates array may be empty.
    */

   @JsonProperty("NestedTemplates")
   private NestedTemplates[] nestedTemplates;

   /**
    * Saves the publishing options for outlining as an array of {@link OutliningOptions}.
    * <p>
    * The outlining options array may be empty but should not contain more than one element.
    */

   @JsonProperty("OutliningOptions")
   private OutliningOptions[] outliningOptions;

   public PublishOptions() {
      this.attributeOptions = null;
      this.elementType = null;
      this.metadataOptions = null;
      this.nestedTemplates = null;
      this.outliningOptions = null;
   }

   public PublishOptions(String elementType, OutliningOptions[] outliningOptions, AttributeOptions[] attributeOptions, MetadataOptions[] metadataOptions, NestedTemplates[] nestedTemplates) {

      this.attributeOptions = Objects.requireNonNull(attributeOptions,
         "RendererOptions::new, parameter \"attributeOptions\" cannot be null.");

      Objects.requireNonNull(elementType, "RendererOptions::new, parameter \"elementType\" cannot be null.");
      PublishOptions.validateElementType(elementType);
      this.elementType = elementType;

      this.metadataOptions =
         Objects.requireNonNull(metadataOptions, "RendererOptions::new, parameter \"metadataOptions\" cannot be null.");
      this.nestedTemplates =
         Objects.requireNonNull(nestedTemplates, "RendererOptions::new, parameter \"nestedTemplates\" cannot be null.");
      this.outliningOptions = Objects.requireNonNull(outliningOptions,
         "RendererOptions::new, parameter \"outliningOptions\" cannot be null.");
   }

   @JsonIgnore
   public @NonNull PublishOptions defaults() {
      if (Objects.isNull(this.attributeOptions)) {
         this.attributeOptions = new AttributeOptions[0];
      } else {
         Arrays.stream(this.attributeOptions).forEach(AttributeOptions::defaults);
      }
      if (Objects.isNull(this.elementType)) {
         this.elementType = "Artifact";
      }
      if (Objects.isNull(this.metadataOptions)) {
         this.metadataOptions = new MetadataOptions[0];
      } else {
         Arrays.stream(this.metadataOptions).forEach(MetadataOptions::defaults);
      }
      if (Objects.isNull(this.nestedTemplates)) {
         this.nestedTemplates = new NestedTemplates[0];
      } else {
         Arrays.stream(this.nestedTemplates).forEach(NestedTemplates::defaults);
      }
      if (Objects.isNull(this.outliningOptions)) {
         this.outliningOptions = new OutliningOptions[0];
      } else {
         Arrays.stream(this.outliningOptions).forEach(OutliningOptions::defaults);
      }

      return this;
   }

   public AttributeOptions[] getAttributeOptions() {
      if (Objects.isNull(this.attributeOptions)) {
         throw new IllegalStateException(
            "RendererOptions::getAttributeOptions, the member \"attributeOptions\" has not been set.");
      }
      return this.attributeOptions;
   }

   public String getElementType() {
      if (Objects.isNull(this.elementType)) {
         throw new IllegalStateException(
            "RendererOptions::getElementType, the member \"elementType\" has not been set.");
      }
      return this.elementType;
   }

   public MetadataOptions[] getMetadataOptions() {
      if (Objects.isNull(this.metadataOptions)) {
         throw new IllegalStateException(
            "RendererOptions::getMetadataOptions, the member \"metadataOptions\" has not been set.");
      }
      return this.metadataOptions;
   }

   public NestedTemplates[] getNestedTemplates() {
      if (Objects.isNull(this.nestedTemplates)) {
         throw new IllegalStateException(
            "RendererOptions::getNestedTemplates, the member \"nestedTemplates\" has not been set.");
      }
      return this.nestedTemplates;
   }

   public OutliningOptions[] getOutliningOptions() {
      if (Objects.isNull(this.outliningOptions)) {
         throw new IllegalStateException(
            "RendererOptions::getOutliningOptions, the member \"outliningOptions\" has not been set.");
      }
      return this.outliningOptions;
   }

   @JsonIgnore
   public boolean isValid() {

      //@formatter:off
      if(    Objects.isNull( this.attributeOptions )
          || Objects.isNull( this.elementType      ) || !PublishOptions.elementTypes.contains( this.elementType )
          || Objects.isNull( this.metadataOptions  )
          || Objects.isNull( this.nestedTemplates  )
          || Objects.isNull( this.outliningOptions )
        ) {
         return false;
      }

      switch( this.elementType ) {
         case "Artifact":

            return
                  ( this.nestedTemplates.length == 0 )
               && !Arrays.stream( this.attributeOptions ).anyMatch( Predicate.not( AttributeOptions::isValid ) )
               && !Arrays.stream( this.metadataOptions  ).anyMatch( Predicate.not( MetadataOptions::isValid  ) )
               && !Arrays.stream( this.outliningOptions ).anyMatch( Predicate.not( OutliningOptions::isValid ) );

         case "NestedTemplate":

            return
                  ( this.attributeOptions.length == 0 )
               && ( this.metadataOptions.length  == 0 )
               && ( this.outliningOptions.length == 0 )
               && !Arrays.stream( this.nestedTemplates ).anyMatch( Predicate.not( NestedTemplates::isValid ) );
      }
      //@formatter:on

      return false;
   }

   public void setAttributeOptions(AttributeOptions[] attributeOptions) {
      if (Objects.nonNull(this.attributeOptions)) {
         throw new IllegalStateException(
            "RendererOptions::setAttributeOptions, member \"attributeOptions\" has already been set.");
      }
      this.attributeOptions = Objects.requireNonNull(attributeOptions,
         "RendererOptions::new, parameter \"attributeOptions\" cannot be null.");
   }

   public void setElementType(String elementType) {
      if (Objects.nonNull(this.elementType)) {
         throw new IllegalStateException(
            "RendererOptions::setElementType, member \"elementType\" has already been set.");
      }
      Objects.requireNonNull(elementType, "RendererOptions::new, parameter \"elementType\" cannot be null.");
      PublishOptions.validateElementType(elementType);
      this.elementType = elementType;
   }

   public void setMetadataOptions(MetadataOptions[] metadataOptions) {
      if (Objects.nonNull(this.metadataOptions)) {
         throw new IllegalStateException(
            "RendererOptions::setMetadataOptions, member \"metadataOptions\" has already been set.");
      }
      this.metadataOptions =
         Objects.requireNonNull(metadataOptions, "RendererOptions::new, parameter \"metadataOptions\" cannot be null.");
   }

   public void setNestedTemplates(NestedTemplates[] nestedTemplates) {
      if (Objects.nonNull(this.nestedTemplates)) {
         throw new IllegalStateException(
            "RendererOptions::setNestedTemplates, member \"nestedTemplates\" has already been set.");
      }
      this.nestedTemplates =
         Objects.requireNonNull(nestedTemplates, "RendererOptions::new, parameter \"nestedTemplates\" cannot be null.");
   }

   public void setOutliningOptions(OutliningOptions[] outliningOptions) {
      if (Objects.nonNull(this.outliningOptions)) {
         throw new IllegalStateException(
            "RendererOptions::setOutliningOptions, member \"outliningOptions\" has already been set.");
      }
      this.outliningOptions = Objects.requireNonNull(outliningOptions,
         "RendererOptions::new, parameter \"outliningOptions\" cannot be null.");
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
         .title( "Renderer Options" )
         .indentInc()
         .segment( "Element Type", this.elementType )
         ;

      switch( this.elementType ) {

         case "Artifact":

            outMessage
               .segmentIndexed( "Attribute Options", this.attributeOptions )
               .segmentIndexed( "Metadata Options",  this.metadataOptions  )
               .segmentIndexed( "Outlining Options", this.outliningOptions )
               ;

            break;

         case "NestedTemplate":

            outMessage
               .segmentIndexed( "NestedTemplates",   this.nestedTemplates  )
               ;

            break;
      }

      outMessage
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
}

/* EOF */
