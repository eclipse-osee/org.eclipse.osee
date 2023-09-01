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

package org.eclipse.osee.framework.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.enums.token.FileExtensionAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * This enumeration is associated with the {@link FileExtensionAttributeType} and defines the attribute type's
 * enumerated {@link FileExtensionEnum} tokens.
 *
 * @author Loren K. Ashley
 */

public enum FileExtension implements ToMessage {

   //@formatter:off
   CSV  ( 2, "csv"  ),
   JSON ( 3, "json" ),
   TXT  ( 6, "txt"  ),
   XLS  ( 4, "xls"  ),
   XLSX ( 5, "xlsx" ),
   XML  ( 0, "xml"  ),
   ZIP  ( 1, "zip"  );
   //@formatter:on

   /**
    * Class for the enumeration members of the {@link FileExtensionAttributeType}.
    */

   public static class FileExtensionEnum extends EnumToken {

      /**
       * Creates a new {@link FileExtensionEnum} with the specified <code>ordinal</code> and <code>name</code>.
       *
       * @param ordinal the ordinal value for the enumeration member.
       * @param name the name for the enumeration member.
       */

      public FileExtensionEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }

   /**
    * Maps the {@link FileExtensionAttributeType} {@link FileExtensionEnum} members to {@link FileExtension} enumeration
    * members.
    */

   private static final Map<FileExtensionEnum, FileExtension> fileExtensionEnumMap;

   /**
    * Maps the file extension strings to {@link FileExtension} enumeration members.
    */

   private static final Map<String, FileExtension> fileExtensionMap;

   /**
    * The file name and file extension separator.
    */

   private static final Character separator = '.';

   /*
    * Initializes the enumeration maps.
    */

   static {
      //@formatter:off
      fileExtensionMap =
         Arrays.stream( FileExtension.values() )
            .collect( Collectors.toUnmodifiableMap( FileExtension::getFileExtension, Function.identity() ) );

      fileExtensionEnumMap =
         Arrays.stream( FileExtension.values() )
            .collect( Collectors.toUnmodifiableMap( FileExtension::getEnumToken, Function.identity() ));
      //@formatter:on
   }

   /**
    * Gets the {@link FileExtension} enumeration member from the file extension string.
    *
    * @param attributeValueString the file extension string.
    * @return when the <code>attributeValueString</code> maps to an enumeration member an {@link Optional} containing
    * the associated {@link FileExtension} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<FileExtension> valueOfAttribute(String attributeValueString) {
      return Optional.ofNullable(FileExtension.fileExtensionMap.get(attributeValueString));
   }

   /**
    * Gets the {@link FileExtension} enumeration member from the {@link FileExtensionEnum} {@link EnumToken} of the
    * {@link FileExtensionAttributeType}.
    *
    * @param enumToken the {@link FileExtensionEnum}.
    * @return when the <code>enumToken</code> maps to an enumeration member an {@link Optional} containing the
    * associated {@link FileExtension} enumeration member; otherwise, an empty {@link Optional}.
    */

   public static Optional<FileExtension> valueOfEnumToken(FileExtensionEnum enumToken) {
      return Optional.ofNullable(FileExtension.fileExtensionEnumMap.get(enumToken));
   }

   /**
    * The {@link FileExtensionEnum} for the {@link FileExtensionAttributeType} that is associated with the
    * {@link FileExtension} enumeration member.
    */

   private final FileExtensionEnum enumToken;

   /**
    * Creates a new enumeration member and the associated {@link FileExtensionEnum}.
    *
    * @param enumTokenOrdinal the ordinal to use for the associated {@link FileExtensionEnum}.
    * @param enumTokenName the name to use for the associated {@link FileExtensionEnum}.
    */

   private FileExtension(int enumTokenOrdinal, String enumTokenName) {
      this.enumToken = new FileExtensionEnum(enumTokenOrdinal, Objects.requireNonNull(enumTokenName));
   }

   /**
    * Returns a {@link StringBuilder} with <code>input</code>, the file name and extension separator, and the file name
    * extension string appended together.
    *
    * @param input {@link String} to be appended to.
    * @return a new {@link StringBuilder} with <code>input</code>, the separator, and the file name extension string
    * appended.
    * @throws NullPointerException when <code>input</code> is <code>null</code>.
    */

   public StringBuilder append(String input) {
      var fileExtension = this.enumToken.getName();
      //@formatter:off
      return
         new StringBuilder( Objects.requireNonNull(input).length() + 1 + fileExtension.length() )
                .append( input )
                .append( FileExtension.separator )
                .append( fileExtension );
      //@formatter:on
   }

   /**
    * Appends the file name and extension separator and the file name extension string.
    *
    * @param input {@link StringBuilder} to be appended to.
    * @return <code>input</code> {@link StringBuilder} with the separator and the file name extension string appended.
    * @throws NullPointerException when <code>input</code> is <code>null</code>.
    */

   public StringBuilder append(StringBuilder input) {
      Objects.requireNonNull(input).append(FileExtension.separator).append(this.enumToken.getName());
      return input;
   }

   /**
    * Gets the file name extension string.
    *
    * @return the file name extension string.
    */

   public String getFileExtension() {
      return this.enumToken.getName();
   }

   /**
    * Gets the {@link FileExtensionEnum} associated with the enumeration member.
    *
    * @return the associated {@link FileExtensionEnum}.
    */

   public FileExtensionEnum getEnumToken() {
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
    * Returns a string representation of the enumeration member for debugging. Use the method {@link #getFileExtension}
    * to obtain the file extension string.
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
