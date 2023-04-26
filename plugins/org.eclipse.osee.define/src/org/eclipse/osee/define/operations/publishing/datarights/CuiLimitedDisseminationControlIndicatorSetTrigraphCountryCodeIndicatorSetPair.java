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

public class CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair {

   /**
    * Predicate to determine if the {@link #cuiLimitedDisseminationControlIndicatorSet} contains any of the
    * {@link CuiLimitedDisseminationControlIndicator}s specified by the parameter
    * <code>cuiLimitedDisseminationControlIndicators</code>.
    *
    * @param cuiLimitedDiseminationControlIndicators the {@link CuiLimitedDisseminationControlIndicator}s to check for.
    * @return <code>true</code>, when the member {@link #cuiLimitedDisseminationControlIndicatorSet} contains any of the
    * {@link CuiLimitedDisseminationControlIndicator}s specified by the parameter
    * <code>cuiLimitedDisseminationControlIndicators; otherwise, <code>false</code>.
    */

   private boolean contains(CuiLimitedDisseminationControlIndicator... cuiLimitedDiseminationControlIndicators) {

      for (var cuiLimitedDisseminationControlIndicator : cuiLimitedDiseminationControlIndicators) {
         for (var documentCuiLimitedDisseminationControlIndicators : this.cuiLimitedDisseminationControlIndicatorSet) {
            if (documentCuiLimitedDisseminationControlIndicators == cuiLimitedDisseminationControlIndicator) {
               return true;
            }
         }
      }
      return true;
   }

   /**
    * Saves the CUI Limited Dissemination Controls for the document to publish.
    */

   CuiLimitedDisseminationControlIndicator[] cuiLimitedDisseminationControlIndicatorSet;

   /**
    * Saves the Trigraph Country Code Indicators for the document to publish. This member should be <code>null</code>
    * when the member array {@link CuiLimitedDisseminationControlIndicator} does not contain
    * {@link CuiLimitedDisseminationControlIndicator#REL_TO} or
    * {@link CuiLimitedDisseminationControlIndicator#DISPLAY_ONLY}. When the member array
    * {@link CuiLimitedDisseminationControlIndicator} contains either
    * {@link CuiLimitedDisseminationControlIndicator#REL_TO} or
    * {@link CuiLimitedDisseminationControlIndicator#DISPLAY_ONLY} this member must be non-<code>null</code> but may be
    * empty. An empty array indicates "USA" only.
    */

   TrigraphCountryCodeIndicator[] trigraphCountryCodeIndicator;

   /**
    * Creates a new empty {@link CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair} for JSON
    * deserialization.
    */

   public CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair() {
      this.cuiLimitedDisseminationControlIndicatorSet = null;
      this.trigraphCountryCodeIndicator = null;
   }

   /**
    * Creates an new {@link CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair} for JSON
    * serialization.
    *
    * @param cuiLimitedDisseminationControlIndicatorSet the CUI Limited Dissemination Controls for the document.
    * @param trigraphCountryCodeIndicator the Trigraph Country Codes a document may be released or displayed to.
    * @throws NullPointerException when the parameter <code>cuiLimitedDisseminationControlIndicatorSet</code> is
    * <code>null</code>.
    */

   public CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair(CuiLimitedDisseminationControlIndicator[] cuiLimitedDisseminationControlIndicatorSet, TrigraphCountryCodeIndicator[] trigraphCountryCodeIndicator) {
      //@formatter:off
      this.cuiLimitedDisseminationControlIndicatorSet =
         Objects.requireNonNull
            (
               cuiLimitedDisseminationControlIndicatorSet,
               "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::new, the parameter \"cuiLimitedDisseminationControlIndicatorSet\" cannot be null."
            );
      //@formatter:on
      this.trigraphCountryCodeIndicator = trigraphCountryCodeIndicator;
   }

   /**
    * Gets the {@link #cuiLimitedDisseminationControlIndicatorSet}.
    *
    * @return the cuiLimitedDisseminationControlIndicatorSet.
    * @throws IllegalStateException when the member {@link #cuiLimitedDisseminationControlIndicatorSet} has already been
    * set.
    */

   public CuiLimitedDisseminationControlIndicator[] getCuiLimitedDisseminationControlIndicatorSet() {
      if (Objects.isNull(this.cuiLimitedDisseminationControlIndicatorSet)) {
         throw new IllegalStateException(
            "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::getCuiLimitedDisseminationControlIndicatorSet, the member \"this.cuiLimitedDisseminationControlIndicatorSet\" has not yet been set.");
      }

      return this.cuiLimitedDisseminationControlIndicatorSet;
   }

   /**
    * Gets the {@link #trigraphCountryCodeIndicator}.
    *
    * @return the trigraphCountryCodeIndicator.
    * @throws IllegalStateException when the member {@link #trigraphCountryCodeIndicator} has already been set.
    */

   public TrigraphCountryCodeIndicator[] getTrigraphCountryCodeIndicator() {
      if (Objects.isNull(this.trigraphCountryCodeIndicator)) {
         throw new IllegalStateException(
            "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::getTrigraphCountryCodeIndicator, the member \"this.trigraphCountryCodeIndicator\" has not yet been set.");
      }

      return this.trigraphCountryCodeIndicator;
   }

   /**
    * Predicate to test the validity of the
    * {@link CuiLimitedDisseminationControlIndicatorSetTrigraphContryCodeIndicatorSetPair}. The following checks are
    * done:
    * <ul>
    * <li>The member {@link #cuiLimitedDisseminationControlIndicatorSet} is non-<code>null</code> and non-empty.</li>
    * <li>When the member {@link #cuiLimitedDisseminationControlIndicatorSet} contains
    * {@link CuiLimitedDisseminationControlIndicator#DISPLAY_ONLY} or
    * {@link CuiLimitedDisseminationControlIndicator#REL_TO}, the member {@link #trigraphCountryCodeIndicator} is
    * non-<code>null</code>; otherwise is <code>null</code>.</li>
    * </ul>
    *
    * @return <code>true</code>, when the {@link PublishingTemplateDataRights} object is valid; otherwise,
    * <code>false</code>.
    */

   boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.cuiLimitedDisseminationControlIndicatorSet ) && ( cuiLimitedDisseminationControlIndicatorSet.length > 0 )
         && ( this.contains( CuiLimitedDisseminationControlIndicator.REL_TO, CuiLimitedDisseminationControlIndicator.DISPLAY_ONLY )
                 ? Objects.nonNull( this.trigraphCountryCodeIndicator )
                 : Objects.isNull( this.trigraphCountryCodeIndicator  ) )
         ;
      //@formatter:on
   }

   /**
    * Sets the member {@link #cuiLimitedDisseminationControlIndicatorSet}.
    *
    * @param cuiLimitedDisseminationControlIndicatorSet the cuiLimitedDisseminationControlIndicatorSet to set.
    * @throws NullPointerException when the parameter <code>cuiLimitedDisseminationControlIndicatorSet</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #cuiLimitedDisseminationControlIndicatorSet} has already been
    * set.
    */

   public void setCuiLimitedDisseminationControlIndicatorSet(CuiLimitedDisseminationControlIndicator[] cuiLimitedDisseminationControlIndicatorSet) {
      if (Objects.nonNull(this.cuiLimitedDisseminationControlIndicatorSet)) {
         throw new IllegalStateException(
            "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::setCuiLimitedDisseminationControlIndicatorSet, member \"this.cuiLimitedDisseminationControlIndicatorSet\" has already been set.");
      }

      this.cuiLimitedDisseminationControlIndicatorSet = Objects.requireNonNull(
         cuiLimitedDisseminationControlIndicatorSet,
         "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::setCuiLimitedDisseminationControlIndicatorSet, parameter \"cuiLimitedDisseminationControlIndicatorSet\" cannot be null.");
   }

   /**
    * Sets the member {@link #trigraphCountryCodeIndicator}.
    *
    * @param trigraphCountryCodeIndicator the trigraphCountryCodeIndicator to set.
    * @throws NullPointerException when the parameter <code>trigraphCountryCodeIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #trigraphCountryCodeIndicator} has already been set.
    */

   public void setTrigraphCountryCodeIndicator(TrigraphCountryCodeIndicator[] trigraphCountryCodeIndicator) {
      if (Objects.nonNull(this.trigraphCountryCodeIndicator)) {
         throw new IllegalStateException(
            "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::setTrigraphCountryCodeIndicator, member \"this.trigraphCountryCodeIndicator\" has already been set.");
      }

      this.trigraphCountryCodeIndicator = Objects.requireNonNull(trigraphCountryCodeIndicator,
         "CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair::setTrigraphCountryCodeIndicator, parameter \"trigraphCountryCodeIndicator\" cannot be null.");
   }

}

/* EOF */