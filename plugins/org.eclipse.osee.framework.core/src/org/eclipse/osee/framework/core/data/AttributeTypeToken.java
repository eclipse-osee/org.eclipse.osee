/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import javax.ws.rs.core.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.EnumToken;
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
   static final String TEXT_CALENDAR = "text/calendar";
   static final String TEXT_URI_LIST = "text/uri-list";
   static final String APPLICATION_MSWORD = "application/msword";
   static final String MODEL_OSEE = "model/osee";
   static final String IMAGE = "image/*";

   String getMediaType();

   default NamespaceToken getNamespace() {
      return NamespaceToken.SENTINEL;
   }

   default TaggerTypeToken getTaggerType() {
      return TaggerTypeToken.SENTINEL;
   }

   public static AttributeTypeToken valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
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

   static @NonNull AttributeTypeArtifactId createArtifactId(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeArtifactId(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeArtifactId createArtifactId(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createArtifactId(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeArtifactId createArtifactIdNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createArtifactId(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeBoolean createBoolean(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeBoolean(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeBoolean createBoolean(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createBoolean(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeBoolean createBooleanNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createBoolean(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeBranchId createBranchId(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeBranchId(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeBranchId createBranchId(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createBranchId(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeBranchId createBranchIdNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createBranchId(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeDate createDate(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeDate(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeDate createDate(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createDate(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeDate createDateNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createDate(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeDouble createDouble(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeDouble(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeDouble createDouble(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createDouble(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeDouble createDoubleNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createDouble(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull <T extends EnumToken> AttributeTypeEnum<T> createEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeEnum<T>(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull <T extends EnumToken> AttributeTypeEnum<T> createEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createEnum(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull <T extends EnumToken> AttributeTypeEnum<T> createEnumNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createEnum(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeInputStream createInputStream(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeInputStream(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeInputStream createInputStream(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createInputStream(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createInputStream(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeInteger createInteger(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeInteger(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeInteger createInteger(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createInteger(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeInteger createIntegerNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createInteger(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeLong createLong(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeLong(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeLong createLong(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createLong(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeLong createLongNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createLong(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static @NonNull AttributeTypeString createString(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeString(id, namespace, name, mediaType, description, taggerType);
   }

   static @NonNull AttributeTypeString createString(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createString(id, namespace, name, mediaType, description, determineTaggerType(mediaType));
   }

   static @NonNull AttributeTypeString createStringNoTag(Long id, NamespaceToken namespace, String name, String mediaType, String description) {
      return createString(id, namespace, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   /**
    * return the default tagger for the given mediaType
    */
   static TaggerTypeToken determineTaggerType(String mediaType) {
      switch (mediaType) {
         case "application/msword":
         case MediaType.TEXT_HTML:
            return TaggerTypeToken.XmlTagger;
         default:
            return TaggerTypeToken.PlainTextTagger;
      }
   }
}