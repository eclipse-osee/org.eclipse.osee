/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to provide a Publishing Template.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplate implements ToMessage {

   /**
    * Saves a special {@link PublishingTemplate} sentinel value to guard against <code>null</code> values.
    */

   public static final PublishingTemplate SENTINEL;

   static {
      var sentinelPublishingTemplate = new PublishingTemplate();
      sentinelPublishingTemplate.sentinel();

      SENTINEL = sentinelPublishingTemplate;
   }

   /**
    * Saves the Publishing Template's identifier.
    */

   private String identifier;

   /**
    * Saves the Publishing Template's name.
    */

   private String name;

   /**
    * Saves the Publishing Template's {@link RendererOptions}.
    */

   private RendererOptions rendererOptions;

   /**
    * Saves the Publishing Template's style Word ML.
    */

   private String style;

   /**
    * Saves the Publishing Template's Word ML XML template content.
    */

   private TemplateContent templateContent;

   /**
    * Creates a new empty {@link PublishingTemplate} for JSON deserialization.
    */

   public PublishingTemplate() {
      this.identifier = null;
      this.name = null;
      this.rendererOptions = null;
      this.style = null;
      this.templateContent = null;
   }

   /**
    * Creates a new {@link PublishingTemplate} with the provided parameters.
    *
    * @param identifier the Publishing Template's identifier.
    * @param name the Publishing Template's name.
    * @param rendererOptions the Publishing Template's {@link RendererOptions}.
    * @param style the Publishing Template's style Word ML.
    * @param templateContent the Publishing Template's {@link TemplateConent}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public PublishingTemplate(String identifier, String name, RendererOptions rendererOptions, String style, TemplateContent templateContent) {
      this.identifier =
         Objects.requireNonNull(identifier, "PublishingTemplate::new, parameter \"identifier\" cannot be null.");
      this.name = Objects.requireNonNull(name, "PublishingTemplate::new, parameter \"name\" cannot be null.");
      this.rendererOptions = Objects.requireNonNull(rendererOptions,
         "PublishingTemplate::new, parameter \"rendererOptions\" cannot be null.");
      this.style = Objects.requireNonNull(style, "PublishingTemplate::new, parameter \"style\" cannot be null.");
      this.templateContent = Objects.requireNonNull(templateContent,
         "PublishingTemplate::new, parameter \"templateContent\" cannot be null.");
   }

   /**
    * Sets default values for unspecified members.
    * <dl>
    * <dt>{@link #identifier}:</dt>
    * <dd>When this member is unset, all members are set to the sentinel values.</dd>
    * <dt>{@link #name}:</dt>
    * <dd>When this member is unset, all members are set to the sentinel values.</dd>
    * <dt>{@link #RendererOptions}:</dt>
    * <dd>When this member is unset, it is set to a {@link RendererOptions} object with default values.</dd>
    * <dt>{@link #style}:</dt>
    * <dd>When this member is unset, it is set to the empty string.</dd>
    * <dt>{@link #templateContent}:</dt>
    * <dd>When this member is unset, it is set to a {@link TemplateContent} object with default values.</dd>
    * </dl>
    */

   public void defaults() {
      if (Objects.isNull(this.identifier)) {
         this.sentinel();
         return;
      }
      if (Objects.isNull(this.name)) {
         this.sentinel();
         return;
      }
      if (Objects.isNull(this.rendererOptions)) {
         this.rendererOptions = new RendererOptions();
      }
      this.rendererOptions.defaults();
      if (Objects.isNull(this.style)) {
         this.style = "";
      }
      if (Objects.isNull(this.templateContent)) {
         this.templateContent = new TemplateContent();
      }
      this.templateContent.defaults();
   }

   /**
    * Gets the Publishing Template's identifier.
    *
    * @return the Publishing Template's identifier.
    * @throws IllegalStateException when the member {@link #identifier} has not been set.
    */

   public String getIdentifier() {
      if (Objects.isNull(this.identifier)) {
         throw new IllegalStateException(
            "PublishingTemplate::getIdentifier, the member \"identifier\" has not been set.");
      }
      return this.identifier;
   }

   /**
    * Gets the Publishing Template's name.
    *
    * @return the Publishing Template's name.
    * @throws IllegalStateException when the member {@link #name} has not been set.
    */

   public String getName() {
      if (Objects.isNull(this.name)) {
         throw new IllegalStateException("PublishingTemplate::getName, the member \"name\" has not been set.");
      }
      return this.name;
   }

   /**
    * Gets the Publishing Template's {@link RendererOptions}.
    *
    * @return the {@link RendererOptions}.
    * @throws IllegalStateException when the member {@link #rendererOptions} has not been set.
    */

   public RendererOptions getRendererOptions() {
      if (Objects.isNull(this.rendererOptions)) {
         throw new IllegalStateException(
            "PublishingTemplate::getRendererOptions, the member \"rendererOptions\" has not been set.");
      }
      return this.rendererOptions;
   }

   /**
    * Gets the Publishing Template's style Word Ml.
    *
    * @return the style Word Ml as a {@link String}.
    * @throws IllegalStateException when the member {@link #style} has not been set.
    */

   public String getStyle() {
      if (Objects.isNull(this.style)) {
         throw new IllegalStateException("PublishingTemplate::getStyle, the member \"style\" has not been set.");
      }
      return this.style;
   }

   /**
    * Gets the Publishing Template's Word ML content.
    *
    * @return the {@link TemplateConent}.
    * @throws IllegalStateException when the member {@link #templateContent} has not been set.
    */

   public TemplateContent getTemplateContent() {
      if (Objects.isNull(this.templateContent)) {
         throw new IllegalStateException(
            "PublishingTemplate::getTemplateContent, the member \"templateContent\" has not been set.");
      }
      return this.templateContent;
   }

   /**
    * Predicate to determine if the {@link PublishingTemplate} is a sentinel template.
    *
    * @return <code>true</code>, when the {@link PublishingTemplate} is a sentinel; otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isSentinel() {
      //@formatter:off
      return
            ( this == PublishingTemplate.SENTINEL )
         || "SENTINEL".equals( this.identifier )
         || "SENTINEL".equals( this.name );
      //@formatter:on
   }

   /**
    * Determines the validity of the object from a JSON serialization/deserialization perspective. The
    * {@link PublishingTemplate} object is valid when all members have been set and are also valid.
    *
    * @return <code>true</code>, when the {@link PublishingTemplate} is valid; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.identifier )
         && !this.identifier.isBlank()
         && Objects.nonNull( this.name )
         && !this.name.isBlank()
         && Objects.nonNull( this.rendererOptions )
         && this.rendererOptions.isValid()
         && Objects.nonNull( this.style )
         && Objects.nonNull( this.templateContent )
         && this.templateContent.isValid();
      //@formatter:on
   }

   /**
    * Sets the members of the {@link PublishingTemplate} to the sentinel values.
    */

   private void sentinel() {
      this.identifier = "SENTINEL";
      this.name = "SENTINEL";
      this.rendererOptions = new RendererOptions();
      this.rendererOptions.defaults();
      this.style = "";
      this.templateContent = new TemplateContent();
      this.templateContent.defaults();
   }

   /**
    * Sets the Publishing Template's identifier.
    *
    * @param identifier the identifier.
    * @throws NullPointerException when the parameter <code>identifier</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #identifier} has already been set.
    */

   public void setIdentifier(String identifier) {
      if (Objects.nonNull(this.identifier)) {
         throw new IllegalStateException(
            "PublishingTemplate::setIdentifier, member \"identifier\" has already been set.");
      }
      this.identifier =
         Objects.requireNonNull(identifier, "PublishingTemplate::new, parameter \"identifier\" cannot be null.");
   }

   /**
    * Sets the Publishing Template's name.
    *
    * @param name the name.
    * @throws NullPointerException when the parameter <code>name</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #name} has already been set.
    */

   public void setName(String name) {
      if (Objects.nonNull(this.name)) {
         throw new IllegalStateException("PublishingTemplate::setName, member \"name\" has already been set.");
      }
      this.name = Objects.requireNonNull(name, "PublishingTemplate::new, parameter \"name\" cannot be null.");
   }

   /**
    * Sets the Publishing Template's {@link RendererOptions}.
    *
    * @param rendererOptions the {@link RendererOptions}.
    * @throws NullPointerException when the parameter <code>rendererOptions</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #rendererOptions} has already been set.
    */

   public void setRendererOptions(RendererOptions rendererOptions) {
      if (Objects.nonNull(this.rendererOptions)) {
         throw new IllegalStateException(
            "PublishingTemplate::setRendererOptions, member \"rendererOptions\" has already been set.");
      }
      this.rendererOptions = Objects.requireNonNull(rendererOptions,
         "PublishingTemplate::new, parameter \"rendererOptions\" cannot be null.");
   }

   /**
    * Sets the Publishing Template's style Word ML.
    *
    * @param style the style Word ML.
    * @throws NullPointerException when the parameter <code>style</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #style} has already been set.
    */

   public void setStyle(String style) {
      if (Objects.nonNull(this.style)) {
         throw new IllegalStateException("PublishingTemplate::setStyle, member \"style\" has already been set.");
      }
      this.style = Objects.requireNonNull(style, "PublishingTemplate::new, parameter \"style\" cannot be null.");
   }

   /**
    * Sets the Publishing Template's Word ML template content.
    *
    * @param templateContent the {@link TemplateContent}.
    * @throws NullPointerException when the parameter <code>templateContent</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #templateContent} has already been set.
    */

   public void setTemplateContent(TemplateContent templateContent) {
      if (Objects.nonNull(this.templateContent)) {
         throw new IllegalStateException(
            "PublishingTemplate::setTemplateContent, member \"templateContent\" has already been set.");
      }
      this.templateContent = Objects.requireNonNull(templateContent,
         "PublishingTemplate::new, parameter \"templateContent\" cannot be null.");
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
         .title( "Publishing Template" )
         .indentInc()
         .segment( "Identifier", this.identifier )
         .segment( "Name",       this.name       )
         .segment( "Style",      this.style      )
         .toMessage( this.rendererOptions )
         .toMessage( this.templateContent )
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
