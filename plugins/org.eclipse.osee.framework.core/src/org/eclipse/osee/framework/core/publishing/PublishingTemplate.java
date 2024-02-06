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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to provide a Publishing Template. The reference to the template content is mutable so that
 * this can continue to be used as a carrier for the Publish Options and the Template Content as changes are made to the
 * template content to prepare for the publish.
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
    * Saves the Publishing Template's {@link PublishOptions}.
    */

   private PublishOptions publishingOptions;

   /**
    * Saves the Publishing Template's template content.
    */

   private String templateContent;

   /**
    * Creates a new empty {@link PublishingTemplate} for JSON deserialization.
    */

   public PublishingTemplate() {
      this.identifier = null;
      this.name = null;
      this.publishingOptions = null;
      this.templateContent = null;
   }

   /**
    * Creates a new {@link PublishingTemplate} with the provided parameters.
    *
    * @param identifier the Publishing Template's identifier.
    * @param name the Publishing Template's name.
    * @param publishingOptions the Publishing Template's {@link PublishOptions}.
    * @param templateContent the Publishing Template's content.
    * @throws NullPointerException when any of the parameters <code>identifier</code>, <code>name</code>, or
    * <code>publishingOptions</code> are <code>null</code>.
    */

   public PublishingTemplate(@NonNull String identifier, @NonNull String name, @NonNull PublishOptions publishingOptions, @Nullable String templateContent) {
      this.identifier = Conditions.requireNonNull(identifier, "PublishingTemplate", "new", "identifier");
      this.name = Conditions.requireNonNull(name, "PublishingTemplate", "new", "name");
      this.publishingOptions =
         Conditions.requireNonNull(publishingOptions, "PublishingTemplate", "new", "publishingOptions");
      this.templateContent = Objects.nonNull(templateContent) ? templateContent : Strings.EMPTY_STRING;
   }

   /**
    * Sets default values for unspecified members.
    * <dl>
    * <dt>{@link #identifier}:</dt>
    * <dd>When this member is unset, all members are set to the sentinel values.</dd>
    * <dt>{@link #name}:</dt>
    * <dd>When this member is unset, all members are set to the sentinel values.</dd>
    * <dt>{@link #PublishOptions}:</dt>
    * <dd>When this member is unset, it is set to a {@link PublishOptions} object with default values.</dd>
    * <dt>{@link #templateContent}:</dt>
    * <dd>When this member is unset, it is set to an empty string.</dd>
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

      if (Objects.isNull(this.publishingOptions)) {
         this.publishingOptions = new PublishOptions();
      }

      this.publishingOptions.defaults();

      if (Objects.isNull(this.templateContent)) {
         this.templateContent = Strings.EMPTY_STRING;
      }
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
    * Gets the Publishing Template's {@link PublishOptions}.
    *
    * @return the {@link PublishOptions}.
    * @throws IllegalStateException when the member {@link #publishingOptions} has not been set.
    */

   public PublishOptions getPublishOptions() {
      if (Objects.isNull(this.publishingOptions)) {
         throw new IllegalStateException(
            "PublishingTemplate::getPublishOptions, the member \"publishOptions\" has not been set.");
      }
      return this.publishingOptions;
   }

   /**
    * Gets the Publishing Template content.
    *
    * @return the content.
    * @throws IllegalStateException when the member {@link #templateContent} has not been set.
    */

   public String getTemplateContent() {
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
    * Predicate to determine if the {@link PublishingTemplate} is not a sentinel template.
    *
    * @return <code>true</code>, when the {@link PublishingTemplate} is not a sentinel; otherwise, <code>false</code>.
    */

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
         && Objects.nonNull( this.publishingOptions )
         && this.publishingOptions.isValid()
         && Objects.nonNull( this.templateContent );
      //@formatter:on
   }

   /**
    * Sets the members of the {@link PublishingTemplate} to the sentinel values.
    */

   @JsonIgnore
   private void sentinel() {
      this.identifier = "SENTINEL";
      this.name = "SENTINEL";
      this.publishingOptions = new PublishOptions();
      this.publishingOptions.defaults();
      this.templateContent = Strings.EMPTY_STRING;
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
    * Sets the Publishing Template's {@link PublishOptions}.
    *
    * @param publishOptions the {@link PublishOptions}.
    * @throws NullPointerException when the parameter <code>publishOptions</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #publishingOptions} has already been set.
    */

   public void setPublishOptions(PublishOptions publishOptions) {
      if (Objects.nonNull(this.publishingOptions)) {
         throw new IllegalStateException(
            "PublishingTemplate::setPublishOptions, member \"publishOptions\" has already been set.");
      }
      this.publishingOptions = Objects.requireNonNull(publishOptions,
         "PublishingTemplate::setPublishOptions, parameter \"publishOptions\" cannot be null.");
   }

   /**
    * Sets the Publishing Template content. Once the publishing template content has been set this method will not allow
    * it to be set again. Use the method {@link #update} to make changes to the publishing template content.
    *
    * @param templateContent the {@link TemplateContent}.
    * @throws NullPointerException when the parameter <code>templateContent</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #templateContent} has already been set.
    */

   public void setTemplateContent(String templateContent) {
      if (Objects.nonNull(this.templateContent)) {
         throw new IllegalStateException(
            "PublishingTemplate::setTemplateContent, member \"templateContent\" has already been set.");
      }
      this.templateContent = Objects.requireNonNull(templateContent,
         "PublishingTemplate::setTemplateContent, parameter \"templateContent\" cannot be null.");
   }

   /**
    * Updates the publishing template content using the provided {@link Function}.
    *
    * @param updater the {@link Function} to be used to update the content.
    */

   @JsonIgnore
   public void update(Function<CharSequence, CharSequence> updater) {
      this.templateContent = updater.apply(this.templateContent).toString();
   }

   /**
    * Applies the provide predicate to the template content string and returns the result.
    *
    * @param tester the {@link Predicate} used to test the template content.
    * @return the results of the predicate.
    */

   @JsonIgnore
   public boolean test(Predicate<CharSequence> tester) {
      return tester.test(this.templateContent);
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
         .toMessage( this.publishingOptions )
         .title( "Template Content" )
         .indentInc()
         .block( this.templateContent )
         .indentDec()
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
