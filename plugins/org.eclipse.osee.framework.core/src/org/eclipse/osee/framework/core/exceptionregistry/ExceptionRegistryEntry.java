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

package org.eclipse.osee.framework.core.exceptionregistry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to request suppression or restoration of automatic logging for an exception by the REST
 * server. Exceptions can be specified as follows:
 * <dl>
 * <dt>Primary Exception Only</dt>
 * <dd>When an exception is specified only by it's fully qualified class name logging will be suppressed for all
 * exceptions of the specified class regardless of the exceptions cause.</dd>
 * <dt>Primary Exception and Secondary (Cause) Exception</dt>
 * <dd>When an exception is specified with it's the fully qualified class name and the fully qualified class name of the
 * exception's cause, only exceptions of the specified type with a cause of the specified cause type will be
 * suppressed.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public class ExceptionRegistryEntry implements ToMessage {

   /**
    * Saves the fully qualified class name of the primary exception.
    */

   String primary;

   /**
    * Saves the fully qualified class name of the secondary (cause) exception or "(none)".
    */

   String secondary;

   /**
    * Caches the hash code.
    */

   @JsonIgnore
   int hashCode;

   /**
    * Creates a new empty {@link ExceptionRegistryEntry} for JSON deserialization.
    */

   public ExceptionRegistryEntry() {
      this.primary = null;
      this.secondary = null;
      this.hashCode = 0;
   }

   /**
    * Creates a new {@link ExceptionRegistryEntry} with data for JSON serialization.
    *
    * @param primary the fully qualified class name of the primary exception.
    * @param secondary the fully qualified class name of the secondary (cause) exception or <code>null</code>.
    * @throws NullPointerException when the parameter <code>primary</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>primary</code> is blank or if the parameter
    * <code>secondary</code> is non-<code>null</code> and blank.
    */

   public ExceptionRegistryEntry(String primary, String secondary) {
      this.primary =
         Objects.requireNonNull(primary, "ExceptionRegistryEntry::new, parameter \"primary\" cannot be null.");

      if (this.primary.isBlank()) {
         throw new IllegalArgumentException("ExceptionRegistryEntry::new, parameter \"primary\" cannot be blank.");
      }

      this.secondary = Objects.nonNull(secondary) ? secondary : "(none)";

      if (this.secondary.isBlank()) {
         throw new IllegalArgumentException("ExceptionRegistryEntry::new, parameter \"secondary\" cannot be blank.");
      }

      this.setHash();
   }

   /**
    * Compares two {@link ExceptionRegistryEntry} objects for equality. To be equal both objects must be
    * non-<code>null</code>, instances of the class {@link ExceptionRegistryEntry}, and have equal values for the
    * members {@link #primary} and {@link #secondary}. {@inheritDoc}
    */

   @Override
   public boolean equals(Object other) {

      //@formatter:off
      return
            ( other != null )
         && ( other instanceof ExceptionRegistryEntry )
         && ( this.hashCode == ((ExceptionRegistryEntry) other).hashCode )
         && ( this.primary.equals( ((ExceptionRegistryEntry) other).primary ) )
         && ( this.secondary.equals( ((ExceptionRegistryEntry) other).secondary) );
   }

   /**
    * {@inhertDoc}
    *
    * @implNote The hash code is computed and cached once both members {@link #primary} and {@link #secondary} have been set.
    */

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   /**
    * Gets the fully qualified class name of the primary exception.
    * @return fully qualified class name of the primary exception.
    * @throws IllegalStateException when the member {@link #primary} has not been set.
    */

   public String getPrimary() {
      if (Objects.isNull(this.primary)) {
         throw new IllegalStateException("ExceptionRegistryEntry::getPrimary, member \"primary\" has not been set.");
      }
      return this.primary;
   }

   /**
    * Gets the fully qualified class name of the secondary exception or the string "(none)".
    * @return fully qualified class name of the primary exception or the string "(none)".
    * @throws IllegalStateException when the member {@link #secondary} has not been set.
    */

   public String getSecondary() {
      if (Objects.isNull(this.secondary)) {
         throw new IllegalStateException("ExceptionRegistryEntry::getSecondary, member \"secondary\" has not been set.");
      }
      return this.secondary;
   }

   /**
    * Predicate to test the validity of the {@link ExceptionRegistryEntry} object.
    * @return <code>true</code>, when the members {@link #primary} and {@link #secondary} are non-<code>null</code>; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.primary )
         && Objects.nonNull( this.secondary );
      //@formatter:on
   }

   /**
    * Calculates the combined hashCode for the {@link ExceptionRegistryEntry} from the members {@link #primary} and
    * {@link #secondary}.
    */

   private void setHash() {
      this.hashCode = this.primary.hashCode() ^ this.secondary.hashCode() * 31;
   }

   /**
    * Sets the fully qualified class name for the primary exception being represented.
    *
    * @param primary fully qualified class name for the primary exception being represented.
    * @throws IllegalStateException when the member {@link #primary} is already set.
    * @throws NullPointerException when the parameter <code>primary</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>primary</code> is blank.
    */

   public void setPrimary(String primary) {
      if (Objects.nonNull(this.primary)) {
         throw new IllegalStateException(
            "ExceptionRegistryEntry::setPrimary, member \"primary\" has already been set.");
      }

      Objects.requireNonNull(primary, "ExceptionRegistryEntry::setPrimary, parameter \"primary\" cannot be null.");

      if (primary.isBlank()) {
         throw new IllegalArgumentException(
            "ExceptionRegistryEntry::setPrimary, parameter \"primary\" cannot be blank.");
      }

      this.primary = primary;

      if (this.isValid()) {
         this.setHash();
      }
   }

   /**
    * Sets the fully qualified class name for the secondary (cause) exception being represented. To specify any or no
    * cause as the secondary exception use the string "(none)".
    *
    * @param primary fully qualified class name for the secondary exception being represented.
    * @throws IllegalStateException when the member {@link #secondary} is already set.
    * @throws NullPointerException when the parameter <code>secondary</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>secondary<code> is blank.
    */

   public void setSecondary(String secondary) {
      if (Objects.nonNull(this.secondary)) {
         throw new IllegalStateException(
            "ExceptionRegistryEntry::setSecondary, member \"secondary\" has already been set.");
      }

      Objects.requireNonNull(primary, "ExceptionRegistryEntry::setSecondary, parameter \"primary\" cannot be null.");

      if (secondary.isBlank()) {
         throw new IllegalArgumentException(
            "ExceptionRegistryEntry::setSecondary, parameter \"secondary\" cannot be blank.");
      }

      this.secondary = secondary;

      if (this.isValid()) {
         this.setHash();
      }
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
         .title( "Exception Registry Entry" )
         .indentInc()
         .segment( "Primary Exception Name", this.primary )
         .segment( "Secondary Exception Name", this.secondary )
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
