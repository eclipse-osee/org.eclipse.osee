/*********************************************************************
 * Copyright (c) 2017 Boeing
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
 * @author Dominic A. Guss
 * @author Loren K. Ashley
 */

public class MetadataOptions implements ToMessage {

   @JsonProperty("Format")
   private String format;

   @JsonProperty("Label")
   private String label;

   @JsonProperty("Type")
   private String type;

   public MetadataOptions() {
      this.format = null;
      this.label = null;
      this.type = null;
   }

   public MetadataOptions(String type, String format, String label) {
      this.format = Objects.requireNonNull(format, "MetadataOptions::new, parameter \"format\" cannot be null.");
      this.label = Objects.requireNonNull(label, "MetadataOptions::new, parameter \"label\" cannot be null.");
      this.type = Objects.requireNonNull(type, "MetadataOptions::new, parameter \"type\" cannot be null.");
   }

   public void defaults() {
      if (Objects.isNull(this.format)) {
         this.format = "";
      }
      if (Objects.isNull(this.label)) {
         this.label = "";
      }
      if (Objects.isNull(this.type)) {
         this.type = "";
      }
   }

   public String getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("MetadataOptions::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   public String getLabel() {
      if (Objects.isNull(this.label)) {
         throw new IllegalStateException("MetadataOptions::getLabel, member \"label\" has not been set.");
      }
      return this.label;
   }

   public String getType() {
      if (Objects.isNull(this.type)) {
         throw new IllegalStateException("MetadataOptions::getType, member \"type\" has not been set.");
      }
      return this.type;
   }

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull(this.format)
         && Objects.nonNull(this.label)
         && Objects.nonNull(this.type);
      //@formatter:on
   }

   public void setFormat(String format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("MetadataOptions::setFormat, member \"format\" has already been set.");
      }
      this.format = Objects.requireNonNull(format, "MetadataOptions::new, parameter \"format\" cannot be null.");
   }

   public void setLabel(String label) {
      if (Objects.nonNull(this.label)) {
         throw new IllegalStateException("MetadataOptions::setLabel, member \"label\" has already been set.");
      }
      this.label = Objects.requireNonNull(label, "MetadataOptions::new, parameter \"label\" cannot be null.");
   }

   public void setType(String type) {
      if (Objects.nonNull(this.type)) {
         throw new IllegalStateException("MetadataOptions::setType, member \"type\" has already been set.");
      }
      this.type = Objects.requireNonNull(type, "MetadataOptions::new, parameter \"type\" cannot be null.");
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
         .title( "MetadataOptions" )
         .indentInc()
         .segment( "Format", this.format )
         .segment( "Label",  this.label  )
         .segment( "Type",   this.type   )
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
