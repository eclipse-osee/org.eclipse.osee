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

package org.eclipse.osee.define.api.publishing.templatemanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.osee.define.api.publishing.AttributeOptions;
import org.eclipse.osee.define.api.publishing.MetadataOptions;
import org.eclipse.osee.define.api.publishing.OutliningOptions;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Contains options for {@link IRenderer} implementations provided by a publishing template.
 *
 * @author Loren K. Ashley
 */

public class RendererOptions implements ToMessage {

   @JsonProperty("AttributeOptions")
   private AttributeOptions[] attributeOptions;

   @JsonProperty("ElementType")
   private String elementType;

   @JsonProperty("MetadataOptions")
   private MetadataOptions[] metadataOptions;

   @JsonProperty("OutliningOptions")
   private OutliningOptions[] outliningOptions;

   public RendererOptions() {
      this.attributeOptions = null;
      this.elementType = null;
      this.metadataOptions = null;
      this.outliningOptions = null;
   }

   public RendererOptions(String elementType, OutliningOptions[] outliningOptions, AttributeOptions[] attributeOptions, MetadataOptions[] metadataOptions) {
      this.attributeOptions = Objects.requireNonNull(attributeOptions,
         "RendererOptions::new, parameter \"attributeOptions\" cannot be null.");
      this.elementType =
         Objects.requireNonNull(elementType, "RendererOptions::new, parameter \"elementType\" cannot be null.");
      this.metadataOptions =
         Objects.requireNonNull(metadataOptions, "RendererOptions::new, parameter \"metadataOptions\" cannot be null.");
      this.outliningOptions = Objects.requireNonNull(outliningOptions,
         "RendererOptions::new, parameter \"outliningOptions\" cannot be null.");
   }

   public static RendererOptions create(String jsonRendererOptionsString) {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         var rendererOptions = objectMapper.readValue(jsonRendererOptionsString, RendererOptions.class);
         rendererOptions.defaults();
         return rendererOptions;
      } catch (Exception e) {
         throw new InvalidRendererOptionsException(jsonRendererOptionsString, e);
      }
   }

   public void defaults() {
      if (Objects.isNull(this.attributeOptions)) {
         this.attributeOptions = new AttributeOptions[0];
      } else {
         Arrays.stream(this.attributeOptions).forEach(AttributeOptions::defaults);
      }
      if (Objects.isNull(this.elementType)) {
         this.elementType = "";
      }
      if (Objects.isNull(this.metadataOptions)) {
         this.metadataOptions = new MetadataOptions[0];
      } else {
         Arrays.stream(this.metadataOptions).forEach(MetadataOptions::defaults);
      }
      if (Objects.isNull(this.outliningOptions)) {
         this.outliningOptions = new OutliningOptions[0];
      } else {
         Arrays.stream(this.outliningOptions).forEach(OutliningOptions::defaults);
      }
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

   public OutliningOptions[] getOutliningOptions() {
      if (Objects.isNull(this.outliningOptions)) {
         throw new IllegalStateException(
            "RendererOptions::getOutliningOptions, the member \"outliningOptions\" has not been set.");
      }
      return this.outliningOptions;
   }

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.attributeOptions )
         && Objects.nonNull( this.elementType )
         && Objects.nonNull( this.metadataOptions )
         && Objects.nonNull( this.outliningOptions )
         && !Arrays.stream( this.attributeOptions ).anyMatch( Predicate.not( AttributeOptions::isValid ) )
         && !Arrays.stream( this.metadataOptions  ).anyMatch( Predicate.not( MetadataOptions::isValid  ) )
         && !Arrays.stream( this.outliningOptions ).anyMatch( Predicate.not( OutliningOptions::isValid ) );
      //@formatter:on
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
      this.elementType =
         Objects.requireNonNull(elementType, "RendererOptions::new, parameter \"elementType\" cannot be null.");
   }

   public void setMetadataOptions(MetadataOptions[] metadataOptions) {
      if (Objects.nonNull(this.metadataOptions)) {
         throw new IllegalStateException(
            "RendererOptions::setMetadataOptions, member \"metadataOptions\" has already been set.");
      }
      this.metadataOptions =
         Objects.requireNonNull(metadataOptions, "RendererOptions::new, parameter \"metadataOptions\" cannot be null.");
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
         .segmentIndexedArray( "Attribute Options", this.attributeOptions )
         .segmentIndexedArray( "Metadata Options",  this.metadataOptions  )
         .segmentIndexedArray( "Outlining Options", this.outliningOptions )
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
