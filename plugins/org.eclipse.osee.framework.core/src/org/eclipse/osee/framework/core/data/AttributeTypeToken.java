/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.Date;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Ryan D. Brooks
 */

public interface AttributeTypeToken extends AttributeTypeId, FullyNamed, HasDescription, NamedId {
   static final AttributeTypeGeneric<?> SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);
   static final String APPLICATION_ZIP = "application/zip";
   static final Date DEFAULT_DATE = new Date(0);
   static final String TEXT_CALENDAR = "text/calendar";
   static final String TEXT_URI_LIST = "text/uri-list";
   static final String APPLICATION_MSWORD = "application/msword";
   static final String MODEL_OSEE = "model/osee";
   static final String IMAGE = "image/*";
   public static final String MISSING_TYPE = "Missing Attribute Type ";

   String getMediaType();

   String getFileExtension();

   Set<DisplayHint> getDisplayHints();

   default NamespaceToken getNamespace() {
      return NamespaceToken.SENTINEL;
   }

   default TaggerTypeToken getTaggerType() {
      return TaggerTypeToken.SENTINEL;
   }

   default boolean isTaggable() {
      return false;
   }

   default boolean isString() {
      return false;
   }

   default boolean isEnumerated() {
      return false;
   }

   default boolean isBoolean() {
      return false;
   }

   default boolean isDate() {
      return false;
   }

   default boolean isInteger() {
      return false;
   }

   default boolean isDouble() {
      return false;
   }

   default boolean isLong() {
      return false;
   }

   default boolean isArtifactId() {
      return false;
   }

   default boolean isBranchId() {
      return false;
   }

   default boolean isInputStream() {
      return false;
   }

   default boolean isJavaObject() {
      return false;
   }

   public static AttributeTypeToken valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   public static AttributeTypeToken valueOf(Long id) {
      return valueOf(id, Named.SENTINEL);
   }

   static AttributeTypeToken valueOf(int id, String name) {
      return valueOf(Long.valueOf(id), name, "");
   }

   static AttributeTypeGeneric<?> valueOf(Long id, String name) {
      return valueOf(id, name, "");
   }

   static @NonNull AttributeTypeGeneric<?> valueOf(Long id, String name, String description) {
      return new AttributeTypeObject(id, NamespaceToken.SENTINEL, name, MediaType.TEXT_PLAIN, description,
         TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeString createString(Long id, NamespaceToken namespace, String name, String mediaType,
      String description, TaggerTypeToken taggerType, String fileExtension) {
      return new AttributeTypeString(id, namespace, name, mediaType, description, taggerType, fileExtension);
   }

   static @NonNull AttributeTypeString createString(Long id, NamespaceToken namespace, String name, String mediaType,
      String description, String fileExtension) {
      return createString(id, namespace, name, mediaType, description, determineTaggerType(mediaType), fileExtension);
   }

   /**
    * return the default tagger for the given mediaType
    */
   static TaggerTypeToken determineTaggerType(String mediaType) {
      switch (mediaType) {
         case "application/msword":
         case MediaType.TEXT_HTML:
            return TaggerTypeToken.XmlTagger;
         case Named.SENTINEL:
            return TaggerTypeToken.SENTINEL;
         default:
            return TaggerTypeToken.PlainTextTagger;
      }
   }

   default boolean isUri() {
      String mediaType = getMediaType();
      if (mediaType.equals(AttributeTypeToken.TEXT_URI_LIST) || mediaType.equals(
         AttributeTypeToken.APPLICATION_MSWORD) || mediaType.equals(AttributeTypeToken.IMAGE) || mediaType.equals(
            AttributeTypeToken.APPLICATION_ZIP) || mediaType.equals(
               MediaType.TEXT_HTML) || mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
         return true;
      }
      return false;
   }

   public default <T extends EnumToken> AttributeTypeEnum<T> toEnum() {
      if (this.isEnumerated()) {
         try {
            return (AttributeTypeEnum<T>) this;
         } catch (Exception e) {
            throw new OseeTypeDoesNotExist("Attribute type [%s] cannot be cast to an enum.", getName());
         }
      }
      throw new OseeTypeDoesNotExist("Attribute type [%s] is not an enum type.", getName());
   }

   default boolean isSingleLine() {
      return getDisplayHints().contains(DisplayHint.SingleLine);
   }

   default boolean isMultiLine() {
      return getDisplayHints().contains(DisplayHint.MultiLine);
   }

   default boolean notEditable() {
      return getDisplayHints().contains(DisplayHint.NoGeneralEdit);
   }

   default boolean notRenderable() {
      return getDisplayHints().contains(DisplayHint.NoGeneralRender);
   }

   default public String getStoreType() {
      if (isEnumerated()) {
         return "Enumeration";
      } else if (isDouble()) {
         return "Double";
      } else if (isInteger()) {
         return "Integer";
      } else if (isLong()) {
         return "Long";
      } else if (isArtifactId()) {
         return "ArtifactId (Long)";
      } else if (isBranchId()) {
         return "BranchId (Long)";
      } else if (isString()) {
         return "String";
      } else if (isBoolean()) {
         return "Boolean";
      } else if (isDate()) {
         return "Date";
      } else if (isInputStream()) {
         return "Input Stream";
      }
      return "Unknown";
   }

}