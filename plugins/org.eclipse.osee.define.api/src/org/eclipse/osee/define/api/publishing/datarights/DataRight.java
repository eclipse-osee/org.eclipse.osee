/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Record to contain a data rights classification and its associated Word ML footer content.
 *
 * @author Angel Avila
 * @author Loren K. Ashley
 */

public class DataRight implements ToMessage {

   /**
    * The name for the data rights classification.
    */

   private String classification;

   /**
    * The Word ML footer content.
    */

   private String content;

   /**
    * Creates a new empty {@link DataRight} object for JSON deserialization.
    */

   public DataRight() {
      this.classification = null;
      this.content = null;
   }

   /**
    * Creates a new {@link DataRight} object with data for serialization (client) or for making a Data Rights Manager
    * service call (server).
    *
    * @param classification the data rights classification name.
    * @param content the Word ML footer content.
    * @throws NullPointerException when either of the parameters <code>classification</code> or <code>content</code> are
    * <code>null</code>.
    */

   public DataRight(String classification, String content) {
      this.classification =
         Objects.requireNonNull(classification, "DataRight::new, the parameter \"classification\" cannot be null.");
      this.content = Objects.requireNonNull(content, "DataRight::new, the parameter \"content\" cannot be null.");
   }

   /**
    * Gets the name of the data rights classification.
    *
    * @return data rights classification name.
    * @throws IllegalStateException when an attempt is made to get the {@link #classification} for a {@link DataRight}
    * where the {@link #classification} has not yet been set.
    */

   public String getClassification() {
      if (Objects.isNull(this.classification)) {
         throw new IllegalStateException(
            "DataRight::getClassification, the member \"classification\" has not been set.");
      }
      return this.classification;
   }

   /**
    * Gets the Word ML footer content for the data rights classification.
    *
    * @return the Word ML footer content.
    * @throws IllegalStateException when an attempt is made to get the {@link #content} for a {@link DataRight} where
    * the {@link #content} has not yet been set.
    */

   public String getContent() {
      if (Objects.isNull(this.content)) {
         throw new IllegalStateException("DataRight::getContent, the member \"content\" has not been set.");
      }
      return this.content;
   }

   /**
    * Predicate to test the validity of the {@link DataRight} object.
    *
    * @return <code>true</code>, when the members {@link #classification} and {@link #content} are
    * non-<code>null</code>; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.classification )
         && Objects.nonNull( this.content        );
      //@formatter:on
   }

   /**
    * Sets the name of the data rights classification.
    *
    * @param classification the name of the data rights classification.
    * @throws NullPointerException when the parameter <code>classification</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the member {@link #classification} of an
    * {@link DataRight} when that member already has a non-<code>null</code> value.
    */

   public void setClassification(String classification) {
      if (Objects.nonNull(this.classification)) {
         throw new IllegalStateException(
            "DataRight::setClassification, the member \"classification\" has alreday been set.");
      }
      this.classification = Objects.requireNonNull(classification,
         "DataRight::setClassification, the parameter \"classification\" cannot be null.");
   }

   /**
    * Sets Word ML footer for the data rights classification.
    *
    * @param content the Word ML footer.
    * @throws NullPointerException when the parameter <code>content</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the member {@link #content} of an {@link DataRight}
    * when that member already has a non-<code>null</code> value.
    */

   public void setContent(String content) {
      if (Objects.nonNull(this.content)) {
         throw new IllegalStateException("DataRight::setContent, the member \"content\" has alreday been set.");
      }
      this.content =
         Objects.requireNonNull(content, "DataRight::setContent, the parameter \"content\" cannot be null.");
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
         .title( "DataRight" )
         .indentInc()
         .segment( "Classification", this.classification )
         .segment( "Content",        this.content,       ( s ) -> s.length() > 20 ? s.substring( 0, 20 ).concat( " ..." ) : s )
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
