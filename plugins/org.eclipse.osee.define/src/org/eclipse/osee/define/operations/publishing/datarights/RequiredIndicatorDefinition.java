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
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class holds the definition for a Required Indicator. Required Indicators may be required by the designating
 * agency, contract, and/or corporate policy.
 *
 * @author Loren K. Ashley
 */

public class RequiredIndicatorDefinition {

   /**
    * A reference by name to the footer statement for the Required Indicator. This member will be <code>null</code> when
    * a footer statement is not required.
    */

   String footerStatementIndicator;

   /**
    * A reference by name to the header statement for the Required Indicator. This member will be <code>null</code> when
    * a footer statement is not required.
    */

   String headerStatementIndicator;

   /**
    * The name of the Required Indicator Definition. The member must not be <code>null</code>.
    */

   String requiredIndicatorDefinitionIndicator;

   /**
    * The frequency and location of the statements for the Required Indicator. This member must not be <code>null</code>
    * or empty.
    */

   RequiredIndicatorFrequencyIndicator[] requiredIndicatorFrequencyIndicatorSet;

   /**
    * A reference by name to the title page statement for the Required Indicator. This member will be <code>null</code>
    * when a title page statement is not required.
    */

   String titlePageStatementIndicator;

   /**
    * Creates a new empty {@link RequiredIndicatorDefinition} for JSON deserialization.
    */

   public RequiredIndicatorDefinition() {
      this.requiredIndicatorDefinitionIndicator = null;
      this.titlePageStatementIndicator = null;
      this.headerStatementIndicator = null;
      this.footerStatementIndicator = null;
      this.requiredIndicatorFrequencyIndicatorSet = null;
   }

   /**
    * Create a new {@link RequiredIndicatorDefinition} with data for JSON serialization.
    *
    * @param requiredIndicatorDefinitionIndicator the name of this Required Indicator Definition.
    * @param titlePageStatementIndicator a reference by name to the title page statement. This parameter may be
    * <code>null</code>.
    * @param headerStatementIndicator a reference by name to the header statement. This parameter may be
    * <code>null</code>.
    * @param footerStatementIndicator a reference by name to the footer statement. This parameter may be
    * <code>null</code>.
    * @param requiredIndicatorFrequencyIndicatorSet the frequency and location of the required indicator statements.
    * @throws NullPointerException when either parameter <code>requiredIndicatorDefinitionIndicator</code> or
    * <code>requiredIndicatorFrequencyIndicatorSet</code> are <code>null</code>.
    */

   public RequiredIndicatorDefinition(String requiredIndicatorDefinitionIndicator, String titlePageStatementIndicator, String headerStatementIndicator, String footerStatementIndicator, RequiredIndicatorFrequencyIndicator[] requiredIndicatorFrequencyIndicatorSet) {
      this.requiredIndicatorDefinitionIndicator = Objects.requireNonNull(requiredIndicatorDefinitionIndicator,
         "RequiredIndicatorDefinition::new, the parameter \"requiredIndicatorDefinitionIndicator\" cannot be null.");
      this.titlePageStatementIndicator = titlePageStatementIndicator;
      this.headerStatementIndicator = headerStatementIndicator;
      this.footerStatementIndicator = footerStatementIndicator;
      this.requiredIndicatorFrequencyIndicatorSet = Objects.requireNonNull(requiredIndicatorFrequencyIndicatorSet,
         "RequiredIndicatorDefinition::new, the parameter \"requiredIndicatorFrequencyIndicatorSet\" cannot be null.");
   }

   /**
    * Gets the {@link #footerStatementIndicator}.
    *
    * @return the footerStatementIndicator.
    * @throws IllegalStateException when the member {@link #footerStatementIndicator} has already been set.
    */

   public String getFooterStatementIndicator() {
      if (Objects.isNull(this.footerStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::getFooterStatementIndicator, the member \"this.footerStatementIndicator\" has not yet been set.");
      }

      return this.footerStatementIndicator;
   }

   /**
    * Gets the {@link #headerStatementIndicator}.
    *
    * @return the headerStatementIndicator.
    * @throws IllegalStateException when the member {@link #headerStatementIndicator} has already been set.
    */

   public String getHeaderStatementIndicator() {
      if (Objects.isNull(this.headerStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::getHeaderStatementIndicator, the member \"this.headerStatementIndicator\" has not yet been set.");
      }

      return this.headerStatementIndicator;
   }

   /**
    * Gets the {@link #requiredIndicatorDefinitionIndicator}.
    *
    * @return the requiredIndicatorDefinitionIndicator.
    * @throws IllegalStateException when the member {@link #requiredIndicatorDefinitionIndicator} has already been set.
    */

   public String getRequiredIndicatorDefinitionIndicator() {
      if (Objects.isNull(this.requiredIndicatorDefinitionIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::getRequiredIndicatorDefinitionIndicator, the member \"this.requiredIndicatorDefinitionIndicator\" has not yet been set.");
      }

      return this.requiredIndicatorDefinitionIndicator;
   }

   /**
    * Gets the {@link #requiredIndicatorFrequencyIndicatorSet}.
    *
    * @return the requiredIndicatorFrequencyIndicatorSet.
    * @throws IllegalStateException when the member {@link #requiredIndicatorFrequencyIndicatorSet} has already been
    * set.
    */

   public RequiredIndicatorFrequencyIndicator[] getRequiredIndicatorFrequencyIndicatorSet() {
      if (Objects.isNull(this.requiredIndicatorFrequencyIndicatorSet)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::getRequiredIndicatorFrequencyIndicatorSet, the member \"this.requiredIndicatorFrequencyIndicatorSet\" has not yet been set.");
      }

      return this.requiredIndicatorFrequencyIndicatorSet;
   }

   /**
    * Gets the {@link #titlePageStatementIndicator}.
    *
    * @return the titlePageStatementIndicator.
    * @throws IllegalStateException when the member {@link #titlePageStatementIndicator} has already been set.
    */

   public String getTitlePageStatementIndicator() {
      if (Objects.isNull(this.titlePageStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::getTitlePageStatementIndicator, the member \"this.titlePageStatementIndicator\" has not yet been set.");
      }

      return this.titlePageStatementIndicator;
   }

   /**
    * Predicate to test the validity of the {@link RequiredIndicatorDefiniton} object. The following test are made:
    * <ul>
    * <li>The member {@link #requiredIndicatorDefinitionIndicator} cannot be <code>null</code>.</li>
    * <li>The member {@link #requiredIndicatorFrequencyIndicatorSet} cannot be <code>null</code> or empty.</li>
    * <li>One of the following members must be non-<code>null</code>:</li>
    * <ul>
    * <li>{@link #footerStatementIndicator}</li>
    * <li>{@link #headerStatementIndicator}</li>
    * <li>{@link #titlePageStatementIndicator}</li>
    * </ul>
    * </ul>
    *
    * @return <code>true</code>, when the validation checks pass; otherwise, <code>false</code>.
    */

   boolean isValid() {
      //@formatter:off
      return
            Strings.isValidAndNonBlank( this.requiredIndicatorDefinitionIndicator )
         && Objects.nonNull( this.requiredIndicatorFrequencyIndicatorSet ) && ( this.requiredIndicatorFrequencyIndicatorSet.length > 0 )
         && (
                  Objects.nonNull( this.footerStatementIndicator    )
               || Objects.nonNull( this.headerStatementIndicator    )
               || Objects.nonNull( this.titlePageStatementIndicator )
            )
         ;
      //@formatter:on
   }

   /**
    * Sets the member {@link #footerStatementIndicator}.
    *
    * @param footerStatementIndicator the footerStatementIndicator to set.
    * @throws NullPointerException when the parameter <code>footerStatementIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #footerStatementIndicator} has already been set.
    */

   public void setFooterStatementIndicator(String footerStatementIndicator) {
      if (Objects.nonNull(this.footerStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::setFooterStatementIndicator, member \"this.footerStatementIndicator\" has already been set.");
      }

      this.footerStatementIndicator = Objects.requireNonNull(footerStatementIndicator,
         "RequiredIndicatorDefinition::setFooterStatementIndicator, parameter \"footerStatementIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #headerStatementIndicator}.
    *
    * @param headerStatementIndicator the headerStatementIndicator to set.
    * @throws NullPointerException when the parameter <code>headerStatementIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #headerStatementIndicator} has already been set.
    */

   public void setHeaderStatementIndicator(String headerStatementIndicator) {
      if (Objects.nonNull(this.headerStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::setHeaderStatementIndicator, member \"this.headerStatementIndicator\" has already been set.");
      }

      this.headerStatementIndicator = Objects.requireNonNull(headerStatementIndicator,
         "RequiredIndicatorDefinition::setHeaderStatementIndicator, parameter \"headerStatementIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #requiredIndicatorDefinitionIndicator}.
    *
    * @param requiredIndicatorDefinitionIndicator the requiredIndicatorDefinitionIndicator to set.
    * @throws NullPointerException when the parameter <code>requiredIndicatorDefinitionIndicator</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #requiredIndicatorDefinitionIndicator} has already been set.
    */

   public void setRequiredIndicatorDefinitionIndicator(String requiredIndicatorDefinitionIndicator) {
      if (Objects.nonNull(this.requiredIndicatorDefinitionIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::setRequiredIndicatorDefinitionIndicator, member \"this.requiredIndicatorDefinitionIndicator\" has already been set.");
      }

      this.requiredIndicatorDefinitionIndicator = Objects.requireNonNull(requiredIndicatorDefinitionIndicator,
         "RequiredIndicatorDefinition::setRequiredIndicatorDefinitionIndicator, parameter \"requiredIndicatorDefinitionIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #requiredIndicatorFrequencyIndicatorSet}.
    *
    * @param requiredIndicatorFrequencyIndicatorSet the requiredIndicatorFrequencyIndicatorSet to set.
    * @throws NullPointerException when the parameter <code>requiredIndicatorFrequencyIndicatorSet</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #requiredIndicatorFrequencyIndicatorSet} has already been
    * set.
    */

   public void setRequiredIndicatorFrequencyIndicatorSet(RequiredIndicatorFrequencyIndicator[] requiredIndicatorFrequencyIndicatorSet) {
      if (Objects.nonNull(this.requiredIndicatorFrequencyIndicatorSet)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::setRequiredIndicatorFrequencyIndicatorSet, member \"this.requiredIndicatorFrequencyIndicatorSet\" has already been set.");
      }

      this.requiredIndicatorFrequencyIndicatorSet = Objects.requireNonNull(requiredIndicatorFrequencyIndicatorSet,
         "RequiredIndicatorDefinition::setRequiredIndicatorFrequencyIndicatorSet, parameter \"requiredIndicatorFrequencyIndicatorSet\" cannot be null.");
   }

   /**
    * Sets the member {@link #titlePageStatementIndicator}.
    *
    * @param titlePageStatementIndicator the titlePageStatementIndicator to set.
    * @throws NullPointerException when the parameter <code>titlePageStatementIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #titlePageStatementIndicator} has already been set.
    */

   public void setTitlePageStatementIndicator(String titlePageStatementIndicator) {
      if (Objects.nonNull(this.titlePageStatementIndicator)) {
         throw new IllegalStateException(
            "RequiredIndicatorDefinition::setTitlePageStatementIndicator, member \"this.titlePageStatementIndicator\" has already been set.");
      }

      this.titlePageStatementIndicator = Objects.requireNonNull(titlePageStatementIndicator,
         "RequiredIndicatorDefinition::setTitlePageStatementIndicator, parameter \"titlePageStatementIndicator\" cannot be null.");
   }

}

/* EOF */
