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
package org.eclipse.osee.define.operations.publishing.datarights;

import java.util.Objects;
import org.eclipse.osee.define.api.publishing.datarights.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Encapsulates the text in a format for a Required Indicator.
 *
 * @author Loren K. Ashley
 */

public class StatementBody {

   /**
    * Specifies the format of the text contained in the member {@link #formattedText}.
    */

   FormatIndicator formatIndicator;

   /**
    * Save the Required Indicator statement in the format specified by the member {@link #formatIndicator}.
    */

   String formattedText;

   /**
    * Creates a new empty {@link StatementBody} for JSON deserialization.
    */

   public StatementBody() {
      this.formatIndicator = null;
      this.formattedText = null;
   }

   /**
    * Creates a new {@link StatementBody} for JSON serialization.
    *
    * @param formatIndicator the format of the Required Indicator Statement text.
    * @param formattedText the text for the Required Indicator Statement.
    */

   public StatementBody(FormatIndicator formatIndicator, String formattedText) {
      this.formatIndicator = Objects.requireNonNull(formatIndicator,
         "StatementBody::new, the parameter \"formatIndicator\" cannot be null.");
      this.formattedText =
         Objects.requireNonNull(formattedText, "StatementBody::new, the parameter \"formattedText\" cannot be null.");
   }

   /**
    * Gets the {@link #formatIndicator}.
    *
    * @return the formatIndicator.
    * @throws IllegalStateException when the member {@link #formatIndicator} has already been set.
    */

   public FormatIndicator getFormatIndicator() {
      if (Objects.isNull(this.formatIndicator)) {
         throw new IllegalStateException(
            "StatementBody::getFormatIndicator, the member \"formatIndicator\" has not yet been set.");
      }

      return this.formatIndicator;
   }

   /**
    * Gets the {@link #formattedText}.
    *
    * @return the formattedText.
    * @throws IllegalStateException when the member {@link #formattedText} has already been set.
    */

   public String getFormattedText() {
      if (Objects.isNull(this.formattedText)) {
         throw new IllegalStateException(
            "StatementBody::getFormattedText, the member \"formattedText\" has not yet been set.");
      }

      return this.formattedText;
   }

   /**
    * Predicate to test the validity of the {@link StatementBody}. The following checks are done:
    * <ul>
    * <li>The member {@link #formatIndicator} must be non-<code>null</code> and valid.</li>
    * <li>The member {@link #formattedText} must be non-<code>null</code> and non-blank.</li>
    * </ul>
    *
    * @return <code>true</code>, when the validity check passes; otherwise, <code>false</code>.
    */

   boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.formatIndicator ) /* && this.formatIndicator.isValid() */
         && Strings.isValidAndNonBlank( this.formattedText );
      //@formatter:on
   }

   /**
    * Sets the member {@link #formatIndicator}.
    *
    * @param formatIndicator the formatIndicator to set.
    * @throws NullPointerException when the parameter <code>formatIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #formatIndicator} has already been set.
    */

   public void setFormatIndicator(FormatIndicator formatIndicator) {
      if (Objects.nonNull(this.formatIndicator)) {
         throw new IllegalStateException(
            "StatementBody::setFormatIndicator, member \"formatIndicator\" has already been set.");
      }

      this.formatIndicator = Objects.requireNonNull(formatIndicator,
         "StatementBody::setFormatIndicator, parameter \"formatIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #formattedText}.
    *
    * @param formattedText the formattedText to set.
    * @throws NullPointerException when the parameter <code>formattedText</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #formattedText} has already been set.
    */

   public void setFormattedText(String formattedText) {
      if (Objects.nonNull(this.formattedText)) {
         throw new IllegalStateException(
            "StatementBody::setFormattedText, member \"formattedText\" has already been set.");
      }

      this.formattedText = Objects.requireNonNull(formattedText,
         "StatementBody::setFormattedText, parameter \"formattedText\" cannot be null.");
   }

}

/* EOF */
