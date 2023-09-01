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

package org.eclipse.osee.framework.core.publishing;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.RequiredIndicatorFrequencyIndicatorAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Required statements can be placed on the title page, in the page header, and/or the page footer. Head and footer
 * statements can be required on all pages or only the pages containing data with the required indicator.
 *
 * @author Loren K. Ashley
 */

public enum RequiredIndicatorFrequencyIndicator implements ToMessage {

   /**
    * A footer is required only on pages containing data with the required indicator.
    */

   FOOTER_CONTAINING(0, "FOOTER CONTAINING"),

   /**
    * A footer is required on every page of the document for the required indicator.
    */

   FOOTER_EVERY(1, "FOOTER EVERY"),

   /**
    * A header is required only on pages containing data with the required indicator.
    */

   HEADER_CONTAINING(2, "HEADER CONTAINING"),

   /**
    * A header is required on every page of the document for the required indicator.
    */

   HEADER_EVERY(3, "HEADER EVERY"),

   /**
    * A statement is required on the title page.
    */

   TITLE(4, "TITLE");

   /**
    * Class for the enumeration members of the {@link RequiredIndicatorFrequencyIndicatorEnum}.
    */

   public static class RequiredIndicatorFrequencyIndicatorEnum extends EnumToken {

      /**
       * Creates a new {@link RequiredIndicatorFrequencyEnum} member with the specified <code>ordinal</code> and
       * <code>name</code>.
       *
       * @param ordinal the ordinal value for the enumeration member.
       * @param name the name for the enumeration member.
       */

      public RequiredIndicatorFrequencyIndicatorEnum(int ordinal, String name) {
         super(ordinal, name);
      }

   }

   /**
    * Maps the {@link RequiredIndicatorFrequencyIndicatorAttributeType} {@link RequiredIndicatorFrequencyIndicatorEnum}
    * members to {@link RequiredIndicatorFrequencyIndicator} enumeration members.
    */

   private static final Map<RequiredIndicatorFrequencyIndicatorEnum, RequiredIndicatorFrequencyIndicator> requiredIndicatorFrequencyIndicatorEnumMap;

   /**
    * Maps the display name strings to {@link RequiredIndicatorFrequencyIndicator} enumeration members.
    */

   private static final Map<String, RequiredIndicatorFrequencyIndicator> requiredIndicatorFrequencyIndicatorMap;

   /*
    * Initializes the enumeration maps.
    */

   static {
      //@formatter:off
      requiredIndicatorFrequencyIndicatorMap =
         Arrays.stream( RequiredIndicatorFrequencyIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( RequiredIndicatorFrequencyIndicator::getDisplayName, Function.identity() ) );

      requiredIndicatorFrequencyIndicatorEnumMap =
         Arrays.stream( RequiredIndicatorFrequencyIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( RequiredIndicatorFrequencyIndicator::getEnumToken, Function.identity() ));
      //@formatter:on
   }

   /**
    * Gets the {@link RequiredIndicatorFrequencyIndicator} enumeration member from the file extension string.
    *
    * @param attributeValueString the file extension string.
    * @return when the <code>attributeValueString</code> maps to an enumeration member an {@link Optional} containing
    * the associated {@link RequiredIndicatorFrequencyIndicator} enumeration member; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<RequiredIndicatorFrequencyIndicator> valueOfAttribute(String attributeValueString) {
      return Optional.ofNullable(
         RequiredIndicatorFrequencyIndicator.requiredIndicatorFrequencyIndicatorMap.get(attributeValueString));
   }

   /**
    * Gets the {@link RequiredIndicatorFrequencyIndicator} enumeration member from the
    * {@link RequiredIndicatorFrequencyIndicatorEnum} {@link EnumToken} of the
    * {@link RequiredIndicatorFrequencyIndicatorAttributeType}.
    *
    * @param enumToken the {@link RequiredIndicatorFrequencyIndicatorEnum}.
    * @return when the <code>enumToken</code> maps to an enumeration member an {@link Optional} containing the
    * associated {@link RequiredIndicatorFrequencyIndicator} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<RequiredIndicatorFrequencyIndicator> valueOfEnumToken(
      RequiredIndicatorFrequencyIndicatorEnum enumToken) {
      return Optional.ofNullable(
         RequiredIndicatorFrequencyIndicator.requiredIndicatorFrequencyIndicatorEnumMap.get(enumToken));
   }

   /**
    * The {@link RequiredIndicatorFrequencyIndicatorEnum} for the
    * {@link RequiredIndicatorFrequencyIndicatorAttributeType} that is associated with the
    * {@link RequiredIndicatorFrequencyIndicator} enumeration member.
    */

   private RequiredIndicatorFrequencyIndicatorEnum enumToken;

   /**
    * Creates a new enumeration member and the associated {@link RequiredIndicatorFrequencyIndicatorEnum}.
    *
    * @param enumTokenOrdinal the ordinal to use for the associated {@link RequiredIndicatorFrequencyIndicatorEnum}.
    * @param enumTokenName the name to use for the associated {@link RequiredIndicatorFrequencyIndicatorEnum}.
    */

   private RequiredIndicatorFrequencyIndicator(int enumTokenOrdinal, String enumTokenName) {
      this.enumToken = new RequiredIndicatorFrequencyIndicatorEnum(enumTokenOrdinal, enumTokenName);
   }

   /**
    * Gets the Required Indicator Frequency Indicator display name.
    *
    * @return the display string.
    */

   public String getDisplayName() {
      return this.enumToken.getName();
   }

   /**
    * Gets the {@link RequiredIndicatorFrequencyIndicatorEnum} associated with the enumeration member.
    *
    * @return the associated {@link RequiredIndicatorFrequencyIndicatorEnum}.
    */

   public RequiredIndicatorFrequencyIndicatorEnum getEnumToken() {
      return this.enumToken;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.isNull(message) ? new Message() : message;
      outMessage.segment(this.name(), this.enumToken.getName());
      return outMessage;
   }

   /**
    * Returns a string representation of the enumeration member for debugging. Use the method {@link #getDisplayName} to
    * obtain the display string.
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */