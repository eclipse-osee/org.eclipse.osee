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
import org.eclipse.osee.framework.core.enums.token.CuiLimitedDisseminationControlIndicatorAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An enumeration of the CUI Limited Dissemination Controls. Only designating agencies can apply or approve limited
 * dissemination controls for CUI information. Authorized holders may apply limited dissemination controls as required
 * by or approved by the designating agency.
 *
 * @author Loren K. Ashley
 */

public enum CuiLimitedDisseminationControlIndicator implements ToMessage {

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">No foreign dissemination</a>
    */

   NOFORN(0, "NOFORN"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Federal employees only</a>
    */

   FED_ONLY(1, "FED ONLY"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Federal employees and contractors only</a>
    */

   FEDCON(2, "FEDCON"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">No dissemination to contractors</a>
    */

   NOCON(3, "NOCON"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Dissemination list controlled</a>
    */

   DL_ONLY(4, "DL ONLY"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Releasable by information disclosure
    * official</a>
    */

   RELIDO(5, "RELIDO"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Authorized for release to certain nationals
    * only</a>
    */

   REL_TO(6, "REL TO"),

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Display only</a>
    */

   DISPLAY_ONLY(7, "DISPLAY ONLY");

   /**
    * Class for the enumeration members of the {@link CuiLimitedDisseminationControlIndicatorAttributeType}.
    */

   public static class CuiLimitedDisseminationControlIndicatorEnum extends EnumToken {

      /**
       * Creates a new {@link CuiLimitedDisseminationControlIndicatorEnum} with the specified <code>ordinal</code> and
       * <code>name</code>.
       *
       * @param ordinal the ordinal value for the enumeration member.
       * @param name the name for the enumeration member.
       */

      public CuiLimitedDisseminationControlIndicatorEnum(int ordinal, String name) {
         super(ordinal, name);
      }

   }

   /**
    * Maps the {@link CuiLimitedDisseminationControlIndicatorAttributeType}
    * {@link CuiLimitedDisseminationControlIndicatorEnum} members to {@link CuiLimitedDisseminationControlIndicator}
    * enumeration members.
    */

   private static final Map<CuiLimitedDisseminationControlIndicatorEnum, CuiLimitedDisseminationControlIndicator> cuiLimitedDisseminationControlIndicatorEnumMap;

   /**
    * Maps the file extension strings to {@link CuiLimitedDisseminationControlIndicator} enumeration members.
    */

   private static final Map<String, CuiLimitedDisseminationControlIndicator> cuiLimitedDisseminationControlIndicatorMap;

   /*
    * Initializes the enumeration maps.
    */

   static {
      //@formatter:off
      cuiLimitedDisseminationControlIndicatorMap =
         Arrays.stream( CuiLimitedDisseminationControlIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( CuiLimitedDisseminationControlIndicator::getDisplayName, Function.identity() ) );

      cuiLimitedDisseminationControlIndicatorEnumMap =
         Arrays.stream( CuiLimitedDisseminationControlIndicator.values() )
            .collect( Collectors.toUnmodifiableMap( CuiLimitedDisseminationControlIndicator::getEnumToken, Function.identity() ));
      //@formatter:on
   }

   /**
    * Gets the {@link CuiLimitedDisseminationControlIndicator} enumeration member from the display name string.
    *
    * @param attributeValueString the display name string.
    * @return when the <code>attributeValueString</code> maps to an enumeration member an {@link Optional} containing
    * the associated {@link CuiLimitedDisseminationControlIndicator} enumeration member; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<CuiLimitedDisseminationControlIndicator> valueOfAttribute(String attributeValueString) {
      return Optional.ofNullable(
         CuiLimitedDisseminationControlIndicator.cuiLimitedDisseminationControlIndicatorMap.get(attributeValueString));
   }

   /**
    * Gets the {@link CuiLimitedDisseminationControlIndicator} enumeration member from the
    * {@link CuiLimitedDisseminationControlIndicatorEnum} {@link EnumToken} of the
    * {@link CuiLimitedDisseminationControlIndicatorAttributeType}.
    *
    * @param enumToken the {@link CuiLimitedDisseminationControlIndicatorEnum}.
    * @return when the <code>enumToken</code> maps to an enumeration member an {@link Optional} containing the
    * associated {@link CuiLimitedDisseminationControlIndicator} enumeration member; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<CuiLimitedDisseminationControlIndicator> valueOfEnumToken(
      CuiLimitedDisseminationControlIndicatorEnum enumToken) {
      return Optional.ofNullable(
         CuiLimitedDisseminationControlIndicator.cuiLimitedDisseminationControlIndicatorEnumMap.get(enumToken));
   }

   /**
    * The {@link CuiLimitedDisseminationControlIndicatorEnum} for the
    * {@link CuiLimitedDisseminationControlIndicatorAttributeType} that is associated with the
    * {@link CuiLimitedDisseminationControlIndicator} enumeration member.
    */

   private CuiLimitedDisseminationControlIndicatorEnum enumToken;

   /**
    * Creates a new enumeration member and the associated {@link CuiLimitedDisseminationControlIndicatorEnum}.
    *
    * @param enumTokenOrdinal the ordinal to use for the associated {@link CuiLimitedDisseminationControlIndicatorEnum}.
    * @param enumTokenName the name to use for the associated {@link CuiLimitedDisseminationControlIndicatorEnum}.
    */

   private CuiLimitedDisseminationControlIndicator(int enumTokenOrdinal, String enumTokenName) {
      this.enumToken =
         new CuiLimitedDisseminationControlIndicatorEnum(enumTokenOrdinal, Objects.requireNonNull(enumTokenName));
   }

   /**
    * Gets the display name for the enumeration member.
    *
    * @return the display name.
    */

   public String getDisplayName() {
      return this.enumToken.getName();
   }

   /**
    * Gets the {@link CuiLimitedDisseminationControlIndicatorEnum} associated with the enumeration member.
    *
    * @return the associated {@link CuiLimitedDisseminationControlIndicatorEnum}.
    */

   public CuiLimitedDisseminationControlIndicatorEnum getEnumToken() {
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
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
