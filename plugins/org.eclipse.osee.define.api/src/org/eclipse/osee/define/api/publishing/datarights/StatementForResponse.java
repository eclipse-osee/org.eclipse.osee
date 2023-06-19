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
package org.eclipse.osee.define.api.publishing.datarights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates properties of the statement for a title page, header, or footer in various formats
 *
 * @author Md I. Khan
 */

public class StatementForResponse implements ToMessage {

   /**
    * The format-indicator for the format of the statement in the \"formatted-text\" property.
    */

   private FormatIndicator format;

   /**
    * The statement in the format indicated by the property \"format-indicator\".
    */

   private String formattedText;

   /**
    * Creates a new empty {@link StatementForResponse} object for JSON deserialization
    */

   public StatementForResponse() {
      this.format = null;
      this.formattedText = null;
   }

   /**
    * Creates a new {@link StatementForResponse} object with data for JSON serialization
    *
    * @param format format-indicator for the format of the statement as an object of {@link FormatIndicator}
    * @param formattedText text formatted by property \"format-indicator\" as a {@link String}
    * @throws NullPointerException when any of the parameters are null
    */

   public StatementForResponse(FormatIndicator format, String formattedText) {
      this.format = Objects.requireNonNull(format, "StatementForResponse::new, parameter \"format\" cannot be null.");
      this.formattedText = Objects.requireNonNull(formattedText,
         "StatementForResponse::new, parameter \"formattedText\" cannot be null.");
   }

   /**
    * Gets the format indicator
    *
    * @return format of type {@link FormatIndicator}
    * @throws IllegalStateException when {@link #format} has not been set
    */

   public FormatIndicator getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("StatementForResponse::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   /**
    * Gets the formatted-text
    *
    * @return formattedText of type {@link String}
    * @throws IllegalStateException when {@link #formattedText} has not been set
    */

   public String getFormattedText() {
      if (Objects.isNull(this.formattedText)) {
         throw new IllegalStateException(
            "StatementForResponse::getFormattedText, member \"formattedText\" has not been set.");
      }
      return this.formattedText;
   }

   /**
    * Predicates to test the validity of {@link StatementForResponse} object
    *
    * @return <code>true</code> when all members are non-<code>null</code>; otherwise <code>false</code>
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
         Objects.nonNull(this.format) && this.format.isValid() &&
         Objects.nonNull(this.formattedText) && Strings.isValidAndNonBlank(this.formattedText);
      //@formatter:on
   }

   /**
    * Sets the format indicator
    *
    * @param format format-indicator for the format of the statement as an object of {@link FormatIndicator}
    * @throws IllegalStateException when the member {@link #format} has already been set
    * @throws NullPointerException when the parameter <code>format</code> is <code>null</code>
    */

   public void setFormat(FormatIndicator format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("StatementForResponse::setFormat, member \"format\" has already been set.");
      }
      this.format =
         Objects.requireNonNull(format, "StatementForResponse::setFormat, parameter \"format\" cannot be null.");
   }

   /**
    * Sets the formatted text
    *
    * @param formattedText formattedText text formatted by property \"format-indicator\" as a {@link String}
    * @throws IllegalSateException when the member {@link #formattedText} has already been set
    * @throws NullPointerException when the parameter <code>formattedText</code> is <code>null</code>
    */

   public void setFormattedText(String formattedText) {
      if (Objects.nonNull(this.formattedText)) {
         throw new IllegalStateException(
            "StatementForResponse::setFormattedText, member \"formattedText\" has already been set.");
      }
      this.formattedText = Objects.requireNonNull(formattedText,
         "StatementForResponse::setFormattedText, parameter \"formattedText\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Statement For Response" )
         .indentInc()
         .segment( "Format Indicator",   this.format   )
         .segment( "Formatted Text", this.formattedText )
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