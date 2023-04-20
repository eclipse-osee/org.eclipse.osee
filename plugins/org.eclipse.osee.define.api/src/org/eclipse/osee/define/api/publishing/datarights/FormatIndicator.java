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

import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates the enumeration of supported format types
 *
 * @author Md I. Khan
 */

public class FormatIndicator implements ToMessage {

   /**
    * Set of supported format indicator types
    */

   private static Set<String> types = Set.of("markdown", "text", "xhtml", "word-ml");

   /**
    * String to represent one of the format types
    */

   public String type;

   /**
    * Creates a new empty {@link FormatIndicator} object for JSON deserialization
    */

   public FormatIndicator() {
      this.type = null;
   }

   /**
    * Creates a new {@link FormatIndicator} object with data for JSON serialization
    *
    * @param type format type as a {@link String}
    * @throws NullPointerException when <code>type</code> is <code>null</code>
    * @throws IllegalArgumentException when <code>type</code> is not one of the following strings:
    * <ul>
    * <li>"markdown"</li>
    * <li>"text"</li>
    * <li>"xhtml"</li>
    * <li>"word-ml"</li>
    * </ul>
    */

   public FormatIndicator(String type) {
      this.type = Objects.requireNonNull(type, "FormatIndicator::new, parameter \"type\" cannot be null.");

      if (!FormatIndicator.types.contains(this.type)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "FormatIndicator::new, parameter \"type\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified type", this.type )
                             .segmentIndexedArray( "Allowed Values", FormatIndicator.types.toArray() )
                             .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Gets the type of format indicator
    *
    * @return type
    * @throws IllegalStateException when the type has not been set
    */

   public String getType() {
      if (Objects.isNull(this.type)) {
         throw new IllegalStateException("FormatIndicator::getType, member \"type\" has not been set.");
      }
      return this.type;
   }

   /**
    * Predicate to test the validity of {@link FormatIndicator} object
    *
    * @return <code>true</code>, when member {@link #type} is non-<code>null</code> and, the member {@link #type} is a
    * valid format indicator type; otherwise <code>false</code>
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.type ) &&
            FormatIndicator.types.contains( this.type );
      //@formatter:on
   }

   /**
    * Sets the {@link #type} of {@link FormatIndicator}
    *
    * @param type format type as a {@link String}
    * @throws NullPointerException when parameter <code>type</code> is <code>null</code>
    * @throws IllegalStateException when {@link #type} has already been set
    * @throws IllegalArgumentException when {@link #type} is not one of the following strings:
    * <ul>
    * <li>"markdown"</li>
    * <li>"text"</li>
    * <li>"xhtml"</li>
    * <li>"word-ml"</li>
    * </ul>
    */

   public void setType(String type) {
      if (Objects.nonNull(this.type)) {
         throw new IllegalStateException("FormatIndicator::setType, member \"type\" has already been set.");
      }

      Objects.requireNonNull(type, "FormatIndicator::setType, parameter \"type\" cannot be null.");

      if (!FormatIndicator.types.contains(type)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "FormatIndicator::new, parameter \"type\" has an invalid value." )
                             .indentInc()
                             .segment( "Specified type", this.type )
                             .segmentIndexedArray( "Allowed Values", FormatIndicator.types.toArray() )
                             .toString()
                   );
         //@formatter:on
      }

      this.type = type;
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
