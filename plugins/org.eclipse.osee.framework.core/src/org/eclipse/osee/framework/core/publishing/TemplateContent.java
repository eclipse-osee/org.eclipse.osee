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
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.w3c.dom.Document;

/**
 * Encapsulates the Word ML content of a Publishing Template.
 *
 * @author Loren K. Ashley
 */

public class TemplateContent implements ToMessage {

   /**
    * Saves the publishing template's XML (Word ML) as a string.
    */

   private CharSequence templateString;

   /**
    * Flag to indicate if an attempt has been made to parse the member {@link #templateString} into an XML DOM.
    */

   @JsonIgnore
   boolean templateStringParsed;

   /**
    * Caches an {@link Optional} containing the publishing template's XML as an XML DOM (Document). If the XML parse
    * failed, this member will be set with an empty {@link Optional}.
    */

   @JsonIgnore
   Optional<Document> templateXmlOptional;

   /**
    * When the XML parse fails, this member saves an {@link Optional} containing the XML parse {@link Exception}. When
    * parsing was successful, this member will be set with an empty {@link Optional}.
    */

   @JsonIgnore
   Optional<Exception> templateStringParseErrorOptional;

   /**
    * Creates a new empty {@link TemplateContent} object.
    */

   public TemplateContent() {
      this.templateString = null;
      this.templateStringParsed = false;
      this.templateXmlOptional = Optional.empty();
      this.templateStringParseErrorOptional = Optional.empty();
   }

   /**
    * Creates a new {@link TemplateContent} object with the provided Word ML XML string. The XML is not parsed.
    *
    * @param templateString the Word ML XML {@link String}.
    * @throws NullPointerException when the parameter <code>templateString</code> is <code>null</code>.
    */

   public TemplateContent(CharSequence templateString) {
      this.templateString = Objects.requireNonNull(templateString,
         "TemplateContent::new, parameter \"templateString\" cannot be null.").toString();
      this.templateStringParsed = false;
      this.templateXmlOptional = Optional.empty();
      this.templateStringParseErrorOptional = Optional.empty();
   }

   /**
    * Sets default values for unspecified members.
    * <dl>
    * <dt>{@link #templateString}:</dt>
    * <dd>The template content is set to the empty string.</dd>
    * </dl>
    */

   @JsonIgnore
   public void defaults() {
      if (Objects.isNull(this.templateString)) {
         this.templateString = "";
      }
   }

   /**
    * Gets the Publishing Template Word ML XML as a {@link String}.
    *
    * @return the Publishing Template Word ML XML as a {@link String}.
    * @throws IllegalStateException when the member {@link #templateString} has not been set.
    */

   public CharSequence getTemplateString() {
      if (Objects.isNull(this.templateString)) {
         throw new IllegalStateException(
            "TemplateContent::getTemplateString, the member \"templateString\" has not been set.");
      }
      return this.templateString;
   }

   /**
    * Gets the Word ML XML as a DOM {@link Document}. If the Word MX XML {@link String} has not yet been parsed, it will
    * be parsed. If this method returns an empty {@link Optional}, the method {@link #getTemplateXmlParseError} can be
    * used to obtain the XML parsing {@link Exception}.
    *
    * @return if XML parsing was successful, an {@link Optional} containing the Word ML XML DOM as a {@link Document};
    * otherwise, an empty {@link Optional}.
    * @throws IllegalStateException when the member {@link #templateString} has not been set.
    */

   @JsonIgnore
   public Optional<Document> getTemplateXml() {
      if (Objects.isNull(this.templateString)) {
         throw new IllegalStateException(
            "TemplateContent::getTemplateXml, the member \"templateString\" has not been set.");
      }
      if (!this.templateStringParsed) {
         this.parseTemplateString();
      }
      return this.templateXmlOptional;
   }

   /**
    * If the method {@link getTemplateXml} returned an empty {@link Optional}, this method maybe used to obtain the XML
    * parsing error. If the Word ML XML has not yet been parsed, an empty {@link Optional} will be returned. Therefore,
    * receiving an empty {@link Optional} from this method does not indicate that XML parsing was successful.
    *
    * @return if XML parsing failed with an exception, an {@link Optional} containing the XML parsing {@link Exception};
    * otherwise, an empty {@link Optional}.
    */

   @JsonIgnore
   public Optional<Exception> getTemplateXmlParseError() {
      return this.templateStringParseErrorOptional;
   }

   /**
    * Determines the validity of the object from a JSON serialization/deserialization perspective. The
    * {@link TemplateContent} object is valid when the Word ML XML {@link String} has been set. This predicate does not
    * indicate whether the XML has been parsed or if the XML parsing was successful.
    *
    * @return <code>true</code>, when the Word ML XML {@link String} has been set; otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {

      return Objects.nonNull(this.templateString);
   }

   /**
    * Parses the Word ML XML {@link String} and caches the results.
    */

   private void parseTemplateString() {
      if (this.templateString.length() > 0) {
         var publishingXmlUtils = new PublishingXmlUtils();
         this.templateXmlOptional = publishingXmlUtils.parse(this.templateString.toString());
         if (publishingXmlUtils.isKo()) {
            this.templateStringParseErrorOptional = publishingXmlUtils.getLastError();
         }
      }
      this.templateStringParsed = true;
   }

   /**
    * Sets the Publishing Template Word ML XML as a {@link String}. The XML is not parsed.
    *
    * @param templateString the Word ML XML {@link String}.
    * @throws IllegalStateException when the member {@link #templateString} has already been set.
    * @throws NullPointerException when the parameter <code>templateString</code> is <code>null</code>.
    */

   public void setTemplateString(CharSequence templateString) {
      if (Objects.nonNull(this.templateString)) {
         throw new IllegalStateException(
            "TemplateContent::setTemplateString, the member \"templateString\" has already been set.");
      }
      this.templateString = Objects.requireNonNull(templateString,
         "TemplateContent::setTemplateString, parameter \"templateString\" cannot be null.");
   }

   /**
    * This method is used by the {@link PublishingTemplate#update} method to replace the Word ML content.
    *
    * @param templateString the new template content string.
    */

   @JsonIgnore
   void replace(CharSequence templateString) {
      this.templateString = Objects.requireNonNull(templateString,
         "TemplateContent::replace, parameter \"templateString\" cannot be null.");
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
         .title( "Template Content" )
         .indentInc()
         .segment( "Template Parsed",   this.templateStringParsed            )
         .segment( "XML DOM Available", this.templateXmlOptional.isPresent() )
         .segment( "Template String",   this.templateString                  )
         ;
      //@formatter:on

      if (this.templateStringParseErrorOptional.isPresent()) {
         //@formatter:off
         outMessage
            .segment( "XML Parse Error Follows", this.templateStringParseErrorOptional.get().getMessage() )
            ;
         //@formatter:on
      }

      outMessage.indentDec();

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }
}

/* EOF */
