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
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Required Indicators for CUI categories and/or corporate policy.
 *
 * @author Loren K. Ashley
 */
public enum RequiredIndicator implements ToMessage {

   //@formatter:off
   RESTRICTED_RIGHTS         (  0, "Restricted Rights"          ),
   GOVERNMENT_PURPOSE_RIGHTS (  1, "Government Purpose Rights"  ),
   UNSPECIFIED               (  2, "Unspecified"                ),
   PROPRIETARY               (  3, "Proprietary"                ),
   LIMITED_RIGHTS            (  4, "Limited Rights"             ),
   UNLIMITED_RIGHTS          (  5, "Unlimited Rights"           ),
   EXPORT_CONTROLLED_ITAR    (  6, "Export Controlled ITAR"     );
   //@formatter:on

   /**
    * Class for the enumeration members of the {@link DataRightsClassificationAttributeType}.
    */

   public static class RequiredIndicatorEnum extends EnumToken {

      /**
       * Creates a new {@link RequiredIndicatorEnum} with the specified <code>ordinal</code> and <code>name</code>.
       *
       * @param ordinal the ordinal value for the enumeration member.
       * @param name the name for the enumeration member.
       */

      public RequiredIndicatorEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }

   /**
    * Maps the {@link RequiredIndicatorAttributeType} {@link RequiredIndicatorEnum} members to {@link RequiredIndicator}
    * enumeration members.
    */

   private static final Map<RequiredIndicatorEnum, RequiredIndicator> requiredIndicatorEnumMap;

   /**
    * Maps the display name strings to {@link RequiredIndicator} enumeration members.
    */

   private static final Map<String, RequiredIndicator> requiredIndicatorMap;

   /*
    * Initializes the enumeration maps.
    */

   static {
      //@formatter:off
      requiredIndicatorMap =
         Arrays.stream( RequiredIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( RequiredIndicator::getDisplayName, Function.identity() ) );

      requiredIndicatorEnumMap =
         Arrays.stream( RequiredIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( RequiredIndicator::getEnumToken, Function.identity() ));
      //@formatter:on
   }

   /**
    * Gets the {@link RequiredIndicator} enumeration member from the Required Indicator display name string.
    *
    * @param attributeValueString the file extension string.
    * @return when the <code>attributeValueString</code> maps to an enumeration member an {@link Optional} containing
    * the associated {@link RequiredIndicator} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<RequiredIndicator> valueOfAttribute(String attributeValueString) {
      return Optional.ofNullable(RequiredIndicator.requiredIndicatorMap.get(attributeValueString));
   }

   /**
    * Gets the {@link RequiredIndicator} enumeration member from the {@link RequiredIndicatorEnum} {@link EnumToken} of
    * the {@link RequiredIndicatorAttributeType}.
    *
    * @param enumToken the {@link RequiredIndicatorEnum}.
    * @return when the <code>enumToken</code> maps to an enumeration member an {@link Optional} containing the
    * associated {@link RequiredIndicator} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<RequiredIndicator> valueOfEnumToken(RequiredIndicatorEnum enumToken) {
      return Optional.ofNullable(RequiredIndicator.requiredIndicatorEnumMap.get(enumToken));
   }

   /**
    * The {@link RequiredIndicatorEnum} for the {@link DataRightsClassificationAttributeType} that is associated with
    * the {@link RequiredIndicatorEnum} enumeration member.
    */

   private RequiredIndicatorEnum enumToken;

   /**
    * Creates a new enumeration member and the associated {@link RequiredIndicatorEnum}.
    *
    * @param enumTokenOrdinal the ordinal value for the associated {@link RequiredIndicatorEnum}.
    * @param enumTokenName the name for the associated {@link RequiredIndicatorEnum}.
    */

   private RequiredIndicator(int enumTokenOrdinal, String enumTokenName) {
      this.enumToken = new RequiredIndicatorEnum(enumTokenOrdinal, enumTokenName);
   }

   /**
    * Gets the Required Indicator display name.
    *
    * @return the display string.
    */

   public String getDisplayName() {
      return this.enumToken.getName();
   }

   /**
    * Gets the {@link RequiredIndicatorEnum} associated with the enumeration member.
    *
    * @return the associated {@link RequiredIndicatorEnum}.
    */

   public RequiredIndicatorEnum getEnumToken() {
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
