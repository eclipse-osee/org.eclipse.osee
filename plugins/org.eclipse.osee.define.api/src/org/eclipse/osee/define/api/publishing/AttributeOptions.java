/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.define.api.publishing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */
public class AttributeOptions implements ToMessage {

   @JsonProperty("AttrType")
   private String attributeType;

   @JsonProperty("Format")
   private String format;

   @JsonProperty("FormatPost")
   private String formatPost;

   @JsonProperty("FormatPre")
   private String formatPre;

   @JsonProperty("Label")
   private String label;

   public AttributeOptions() {

      this.attributeType = null;
      this.format = null;
      this.formatPre = null;
      this.formatPost = null;
      this.label = null;
   }

   public AttributeOptions(String attributeType, String label, String format, String formatPre, String formatPost) {
      this.attributeType =
         Objects.requireNonNull(attributeType, "AttributeOptions::new, parameter \"attributeType\" cannot be null.");
      this.format = Objects.requireNonNull(format, "AttributeOptions::new, parameter \"format\" cannot be null.");
      this.formatPre =
         Objects.requireNonNull(formatPre, "AttributeOptions::new, parameter \"formatPre\" cannot be null.");
      this.formatPost =
         Objects.requireNonNull(formatPost, "AttributeOptions::new, parameter \"formatPost\" cannot be null.");
      this.label = Objects.requireNonNull(label, "AttributeOptions::new, parameter \"label\" cannot be null.");
   }

   public void defaults() {
      if (Objects.isNull(this.attributeType)) {
         this.attributeType = "";
      }
      if (Objects.isNull(this.format)) {
         this.format = "";
      }
      if (Objects.isNull(this.formatPre)) {
         this.formatPre = "";
      }
      if (Objects.isNull(this.formatPost)) {
         this.formatPost = "";
      }
      if (Objects.isNull(this.label)) {
         this.label = "";
      }
   }

   public String getAttributeName() {
      if (Objects.isNull(this.attributeType)) {
         throw new IllegalStateException(
            "AttributeOptions::getAttributeName, member \"attributeType\" has not been set.");
      }
      return this.attributeType;
   }

   public String getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("AttributeOptions::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   public String getFormatPost() {
      if (Objects.isNull(this.formatPost)) {
         throw new IllegalStateException("AttributeOptions::getFormatPost, member \"formatPost\" has not been set.");
      }
      return this.formatPost;
   }

   public String getFormatPre() {
      if (Objects.isNull(this.formatPre)) {
         throw new IllegalStateException("AttributeOptions::getFormatPre, member \"formatPre\" has not been set.");
      }
      return this.formatPre;
   }

   public String getLabel() {
      if (Objects.isNull(this.label)) {
         throw new IllegalStateException("AttributeOptions::getLabel, member \"label\" has not been set.");
      }
      return this.label;
   }

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull(this.attributeType)
         && Objects.nonNull(this.format)
         && Objects.nonNull(this.formatPost)
         && Objects.nonNull(this.formatPre)
         && Objects.nonNull(this.label);
      //@formatter:on
   }

   public void setAttributeName(String attributeType) {
      if (Objects.nonNull(this.attributeType)) {
         throw new IllegalStateException(
            "AttributeOptions::getAttributeName, member \"attributeType\" has already been set.");
      }
      this.attributeType =
         Objects.requireNonNull(attributeType, "AttributeOptions::new, parameter \"attributeType\" cannot be null.");
   }

   public void setFormat(String format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("AttributeOptions::getFormat, member \"format\" has already been set.");
      }
      this.format = Objects.requireNonNull(format, "AttributeOptions::new, parameter \"format\" cannot be null.");
   }

   public void setFormatPost(String formatPost) {
      if (Objects.nonNull(this.formatPost)) {
         throw new IllegalStateException(
            "AttributeOptions::setFormatPost, member \"formatPost\" has already been set.");
      }
      this.formatPost =
         Objects.requireNonNull(formatPost, "AttributeOptions::new, parameter \"formatPost\" cannot be null.");
   }

   public void setFormatPre(String formatPre) {
      if (Objects.nonNull(this.formatPre)) {
         throw new IllegalStateException("AttributeOptions::setFormatPre, member \"formatPre\" has already been set.");
      }
      this.formatPre =
         Objects.requireNonNull(formatPre, "AttributeOptions::new, parameter \"formatPre\" cannot be null.");
   }

   public void setLabel(String label) {
      if (Objects.nonNull(this.label)) {
         throw new IllegalStateException("AttributeOptions::setLabel, member \"label\" has already been set.");
      }
      this.label = Objects.requireNonNull(label, "AttributeOptions::new, parameter \"label\" cannot be null.");
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
         .title( "AttributeOptions" )
         .indentInc()
         .segment( "Attribute Type", this.attributeType )
         .segment( "Format",         this.format        )
         .segment( "Format Post",    this.formatPost    )
         .segment( "Format Pre",     this.formatPre     )
         .segment( "Label",          this.label         )
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
      return this.toMessage(0, null).toString();
   }
}

/* EOF */