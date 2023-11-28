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

package org.eclipse.osee.define.operations.publisher.datarights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.framework.core.publishing.CuiLimitedDisseminationControlIndicator;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class contains the data rights configuration for a publish.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateDataRights {

   /**
    * The name of the data rights configuration.
    */

   String dataRightsConfigurationIndicator;

   /**
    * The CUI Category and CUI Type pairs that Artifacts in the publish are allowed to be. This member may be
    * <code>null</code> when there are no CUI Category restrictions. However, this member should never be an empty
    * array.
    */

   CuiCategoryIndicatorCuiTypeIndicatorPair[] cuiCategoryIndicatorCuiTypeIndicatorPairSet;

   /**
    * The CUI Limited Dissemination Controls for the publish. When the dissemination control is
    * {@link CuiLimitedDisseminationControlIndicator#REL_TO} or
    * {@link CuiLimitedDisseminationControlIndicator#DISPLAY_ONLY} the member
    * {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair} must also contain a
    * {@link TrigraphCountryCodeIndicatorSet}. This member may be <code>null</code> when there aren't any CUI Limited
    * Dissemination Controls for the publish.
    */

   CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair;

   /**
    * An array of the names of the Required Indicator Definitions for the publish. The member may be <code>null</code>
    * when there arn't any Required Indicators. However, it should never be an empty array.
    */

   String[] requiredIndicatorDefinitionIndicatorSet;

   /**
    * Creates a new empty {@link PublishingeTemplateDataRights} object for JSON deserialization.
    */

   public PublishingTemplateDataRights() {
      this.dataRightsConfigurationIndicator = null;
      this.cuiCategoryIndicatorCuiTypeIndicatorPairSet = null;
      this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair = null;
      this.requiredIndicatorDefinitionIndicatorSet = null;
   }

   /**
    * Creates a new {@link PublishingTemplateDataRights} by options for serialization.
    *
    * @param dataRightsConfigurationIndicator the name of the data rights configuration.
    * @param cuiCategoryIndicatorCuiTypeIndicatorPairSet array of CUI Category and CUI Type indicators.
    * @param cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair the CUI Limited Dissemination
    * Control indicators and Trigraph Country Code indicators if necessary.
    * @param requiredIndicatorDefinitionIndicatorSet the names of the Required Indicator Definitions to use for the
    * publish.
    * @throws NullPointerException when the parameter <code>dataRightsConfigurationIndicator</code> is
    * <code>null</code>.
    */

   public PublishingTemplateDataRights(String dataRightsConfigurationIndicator, CuiCategoryIndicatorCuiTypeIndicatorPair[] cuiCategoryIndicatorCuiTypeIndicatorPairSet, CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair, String[] requiredIndicatorDefinitionIndicatorSet) {
      this.dataRightsConfigurationIndicator = Objects.requireNonNull(dataRightsConfigurationIndicator,
         "PublishingTemplateDataRights::new, the parameter \"dataRightsConfigurationIndicator\" cannot be null.");
      this.cuiCategoryIndicatorCuiTypeIndicatorPairSet = cuiCategoryIndicatorCuiTypeIndicatorPairSet;
      this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair =
         cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair;
      this.requiredIndicatorDefinitionIndicatorSet = requiredIndicatorDefinitionIndicatorSet;
   }

   /**
    * Gets the {@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet}.
    *
    * @return the cuiCategoryIndicatorCuiTypeIndicatorPairSet.
    * @throws IllegalStateException when the member {@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet} has already
    * been set.
    */

   public CuiCategoryIndicatorCuiTypeIndicatorPair[] getCuiCategoryIndicatorCuiTypeIndicatorPairSet() {
      if (Objects.isNull(this.cuiCategoryIndicatorCuiTypeIndicatorPairSet)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::getCuiCategoryIndicatorCuiTypeIndicatorPairSet, the member \"this.cuiCategoryIndicatorCuiTypeIndicatorPairSet\" has not yet been set.");
      }

      return this.cuiCategoryIndicatorCuiTypeIndicatorPairSet;
   }

   /**
    * Gets the {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair}.
    *
    * @return the cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair.
    * @throws IllegalStateException when the member
    * {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair} has already been set.
    */

   public CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair getCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair() {
      if (Objects.isNull(this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::getCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair, the member \"this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair\" has not yet been set.");
      }

      return this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair;
   }

   /**
    * Gets the {@link #dataRightsConfigurationIndicator}.
    *
    * @return the dataRightsConfigurationIndicator.
    * @throws IllegalStateException when the member {@link #dataRightsConfigurationIndicator} has already been set.
    */

   public String getDataRightsConfigurationIndicator() {
      if (Objects.isNull(this.dataRightsConfigurationIndicator)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::getDataRightsConfigurationIndicator, the member \"this.dataRightsConfigurationIndicator\" has not yet been set.");
      }

      return this.dataRightsConfigurationIndicator;
   }

   /**
    * Gets the {@link #requiredIndicatorDefinitionIndicatorSet}.
    *
    * @return the requiredIndicatorDefinitionIndicatorSet.
    * @throws IllegalStateException when the member {@link #requiredIndicatorDefinitionIndicatorSet} has already been
    * set.
    */

   public String[] getRequiredIndicatorDefinitionIndicatorSet() {
      if (Objects.isNull(this.requiredIndicatorDefinitionIndicatorSet)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::getRequiredIndicatorDefinitionIndicatorSet, the member \"this.requiredIndicatorDefinitionIndicatorSet\" has not yet been set.");
      }

      return this.requiredIndicatorDefinitionIndicatorSet;
   }

   /**
    * Predicate to determine the validity of the {@link PublishingTemplateDataRights} object. The validity is determined
    * as follows:
    * <ul>
    * <li>The member {@link #dataRightsConfigurationIndicator} must be non-<code>null</code> and non-blank.</li>
    * <li>When set, the array must be non-empty and all the elements of
    * {@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet} must all be valid.</li>
    * <li>When set, the array must be non-empty and all the elements of {@link #requiredIndicatorDefinitionIndicatorSet}
    * must all be valid.</li>
    * <li>When set, the member {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair}
    * must be valid.</li>
    * <li>The following members must be set an valid:</li>
    * <ul>
    * <li>{@link #dataRightsConfigurationIndicator}, and</li>
    * <ul>
    * <li>{@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet}, or</li>
    * <li>{@link #requiredIndicatorDefinitionIndicatorSet}</li>
    * </ul>
    * </ul>
    * </ul>
    *
    * @return <code>true</code>, when the {@link PublishingTemplateDataRights} object is valid; otherwise,
    * <code>false</code>.
    */

   @JsonIgnore
   boolean isValid() {
      //@formatter:off


      if ( Strings.isInvalidOrBlank( this.dataRightsConfigurationIndicator ) ) {
         return false;
      }

      var memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsSet = Objects.nonNull( this.cuiCategoryIndicatorCuiTypeIndicatorPairSet );

      var memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsValid =
         memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsSet
            ?     ( this.cuiCategoryIndicatorCuiTypeIndicatorPairSet.length > 0 )
               && Arrays.stream( this.cuiCategoryIndicatorCuiTypeIndicatorPairSet ).allMatch( CuiCategoryIndicatorCuiTypeIndicatorPair::isValid )
            : false;

      if(     memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsSet
          && !memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsValid ) {
         return false;
      }

      var memberRequiredIndicatorDefinitionIndicatorSetIsSet = Objects.nonNull( this.requiredIndicatorDefinitionIndicatorSet );

      var memberRequiredIndicatorDefinitionIndicatorSetIsValid =
         memberRequiredIndicatorDefinitionIndicatorSetIsSet
            ?    ( this.requiredIndicatorDefinitionIndicatorSet.length > 0 )
              && Arrays.stream( this.requiredIndicatorDefinitionIndicatorSet ).allMatch( Strings::isValidAndNonBlank )
            : false;

      if(     memberRequiredIndicatorDefinitionIndicatorSetIsSet
          && !memberRequiredIndicatorDefinitionIndicatorSetIsValid ) {
         return false;
      }

      if(    !memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsSet
          && !memberRequiredIndicatorDefinitionIndicatorSetIsSet     ) {
         return false;
      }

      var memberCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPairIsSet = Objects.nonNull( this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair );

      var memberCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPairIsValid = this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair.isValid();

      if (     memberCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPairIsSet
           && !memberCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPairIsValid ) {
         return false;
      }

      if(     memberCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPairIsSet
          &&  !memberCuiCategoryIndicatorCuiTypeIndicatorPairSetIsSet ) {
         return false;
      }

      return true;
      //@formatter:on
   }

   /**
    * Sets the member {@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet}.
    *
    * @param cuiCategoryIndicatorCuiTypeIndicatorPairSet the cuiCategoryIndicatorCuiTypeIndicatorPairSet to set.
    * @throws NullPointerException when the parameter <code>cuiCategoryIndicatorCuiTypeIndicatorPairSet</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #cuiCategoryIndicatorCuiTypeIndicatorPairSet} has already
    * been set.
    */

   public void setCuiCategoryIndicatorCuiTypeIndicatorPairSet(CuiCategoryIndicatorCuiTypeIndicatorPair[] cuiCategoryIndicatorCuiTypeIndicatorPairSet) {
      if (Objects.nonNull(this.cuiCategoryIndicatorCuiTypeIndicatorPairSet)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::setCuiCategoryIndicatorCuiTypeIndicatorPairSet, member \"this.cuiCategoryIndicatorCuiTypeIndicatorPairSet\" has already been set.");
      }

      this.cuiCategoryIndicatorCuiTypeIndicatorPairSet = Objects.requireNonNull(
         cuiCategoryIndicatorCuiTypeIndicatorPairSet,
         "PublishingTemplateDataRights::setCuiCategoryIndicatorCuiTypeIndicatorPairSet, parameter \"cuiCategoryIndicatorCuiTypeIndicatorPairSet\" cannot be null.");
   }

   /**
    * Sets the member {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair}.
    *
    * @param cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair the
    * cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair to set.
    * @throws NullPointerException when the parameter
    * <code>cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair</code> is <code>null</code>.
    * @throws IllegalStateException when the member
    * {@link #cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair} has already been set.
    */

   public void setCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair(CuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair) {
      if (Objects.nonNull(this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::setCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair, member \"this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair\" has already been set.");
      }

      this.cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair = Objects.requireNonNull(
         cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair,
         "PublishingTemplateDataRights::setCuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair, parameter \"cuiLimitedDisseminationControlIndicatorSetTrigraphCountryCodeIndicatorSetPair\" cannot be null.");
   }

   /**
    * Sets the member {@link #dataRightsConfigurationIndicator}.
    *
    * @param dataRightsConfigurationIndicator the dataRightsConfigurationIndicator to set.
    * @throws NullPointerException when the parameter <code>dataRightsConfigurationIndicator</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #dataRightsConfigurationIndicator} has already been set.
    */

   public void setDataRightsConfigurationIndicator(String dataRightsConfigurationIndicator) {
      if (Objects.nonNull(this.dataRightsConfigurationIndicator)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::setDataRightsConfigurationIndicator, member \"this.dataRightsConfigurationIndicator\" has already been set.");
      }

      this.dataRightsConfigurationIndicator = Objects.requireNonNull(dataRightsConfigurationIndicator,
         "PublishingTemplateDataRights::setDataRightsConfigurationIndicator, parameter \"dataRightsConfigurationIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #requiredIndicatorDefinitionIndicatorSet}.
    *
    * @param requiredIndicatorDefinitionIndicatorSet the requiredIndicatorDefinitionIndicatorSet to set.
    * @throws NullPointerException when the parameter <code>requiredIndicatorDefinitionIndicatorSet</code> is
    * <code>null</code>.
    * @throws IllegalStateException when the member {@link #requiredIndicatorDefinitionIndicatorSet} has already been
    * set.
    */

   public void setRequiredIndicatorDefinitionIndicatorSet(String[] requiredIndicatorDefinitionIndicatorSet) {
      if (Objects.nonNull(this.requiredIndicatorDefinitionIndicatorSet)) {
         throw new IllegalStateException(
            "PublishingTemplateDataRights::setRequiredIndicatorDefinitionIndicatorSet, member \"this.requiredIndicatorDefinitionIndicatorSet\" has already been set.");
      }

      this.requiredIndicatorDefinitionIndicatorSet = Objects.requireNonNull(requiredIndicatorDefinitionIndicatorSet,
         "PublishingTemplateDataRights::setRequiredIndicatorDefinitionIndicatorSet, parameter \"requiredIndicatorDefinitionIndicatorSet\" cannot be null.");
   }

}

/* EOF */
