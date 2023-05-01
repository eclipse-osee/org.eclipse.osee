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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to provide a Publishing Template. The reference to the Word ML for the publishing template is
 * mutable so that this can continue to be used as a carrier for the Render Options and the template Word ML as changes
 * are made to the template Word ML to prepare for the publish.
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

   public PublishingTemplate(String identifier, String name, RendererOptions rendererOptions, TemplateContent templateContent) {
      this.identifier =
         Objects.requireNonNull(identifier, "PublishingTemplate::new, parameter \"identifier\" cannot be null.");
      this.name = Objects.requireNonNull(name, "PublishingTemplate::new, parameter \"name\" cannot be null.");
      this.rendererOptions = Objects.requireNonNull(rendererOptions,
         "PublishingTemplate::new, parameter \"rendererOptions\" cannot be null.");
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
    * <dt>{@link #templateContent}:</dt>
    * <dd>When this member is unset, it is set to a {@link TemplateContent} object with default values.</dd>
    * </dl>
    */

   @JsonIgnore
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

   @JsonIgnore
   public boolean isNotSentinel() {
      return !this.isSentinel();
   }

   /**
    * Determines the validity of the object from a JSON serialization/deserialization perspective. The
    * {@link PublishingTemplate} object is valid when all members have been set and are also valid.
    *
    * @return <code>true</code>, when the {@link PublishingTemplate} is valid; otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.identifier )
         && !this.identifier.isBlank()
         && Objects.nonNull( this.name )
         && !this.name.isBlank()
         && Objects.nonNull( this.rendererOptions )
         && this.rendererOptions.isValid()
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
      this.identifier = Objects.requireNonNull(identifier,
         "PublishingTemplate::setIdentifier, parameter \"identifier\" cannot be null.");
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
      this.name = Objects.requireNonNull(name, "PublishingTemplate::setName, parameter \"name\" cannot be null.");
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
         "PublishingTemplate::setRendererOptions, parameter \"rendererOptions\" cannot be null.");
   }

   /**
    * Sets the Publishing Template's Word ML template content. Once the publishing template Word ML has been set this
    * method will not allow it to be set again. Use the method {@link #update} to make changes to the publishing
    * template Word ML.
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
         "PublishingTemplate::setTemplateContent, parameter \"templateContent\" cannot be null.");
   }

   /**
    * Updates the publishing template Word ML content using the provided {@link Function}.
    *
    * @param updater the {@link Function} to be used to update the WordML content.
    */

   @JsonIgnore
   public void update(Function<CharSequence, CharSequence> updater) {
      this.templateContent.replace(updater.apply(this.templateContent.getTemplateString()));
   }

   /**
    * Applies the provide predicate to the template content string and returns the result.
    *
    * @param tester the {@link Predicate} used to test the template content.
    * @return the results of the predicate.
    */

   @JsonIgnore
   public boolean test(Predicate<CharSequence> tester) {
      return tester.test(this.templateContent.getTemplateString());
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
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

   @JsonIgnore
   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
