/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public abstract class AttributeTypeGeneric<T> extends NamedIdDescription implements AttributeTypeToken {
   public static final AttributeTypeString SENTINEL =
      AttributeTypeToken.createString(Id.SENTINEL, NamespaceToken.OSEE, Named.SENTINEL, Named.SENTINEL, "", "");

   private final String mediaType;
   private final TaggerTypeToken taggerType;
   private final NamespaceToken namespace;
   private final String fileExtension;
   private final T defaultValue;
   private final Set<OperationTypeToken> operationTypes;
   protected final Set<DisplayHint> displayHints = new HashSet<DisplayHint>();
   private boolean missingAttributeType = false;

   public AttributeTypeGeneric(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, T defaultValue, Set<OperationTypeToken> operationTypes, DisplayHint... hints) {
      super(id, name, description);
      this.namespace = namespace;
      this.mediaType = mediaType;
      this.taggerType = taggerType;
      this.fileExtension = fileExtension;
      this.defaultValue = defaultValue;
      for (DisplayHint hint : hints) {
         this.displayHints.add(hint);
      }
      this.operationTypes = operationTypes != null ? operationTypes : new HashSet<OperationTypeToken>();
   }

   public T getBaseAttributeTypeDefaultValue() {
      return defaultValue;
   }

   @Override
   public String getMediaType() {
      return mediaType;
   }

   @Override
   public String getFileExtension() {
      return fileExtension;
   }

   public T valueFromDouble(double value) {
      return null;
   }

   /**
    * @param storedValue is the raw String stored in the database
    * @return the attribute value in its native Java representation
    */
   @Override
   public abstract T valueFromStorageString(String storedValue);

   public String storageStringFromValue(T value) {
      return value == null ? "" : value.toString();
   }

   /**
    * Validate and coerce an arbitrary value being written to this attribute into this type's native Java
    * representation {@code T}.
    * <p>
    * Because the attribute value write path is generic ({@code setValue(T)}), Java type erasure allows any
    * {@code Object} to reach storage without being checked against what this AttributeType actually represents.
    * Everything is persisted as a String, but the AttributeType is the contract for what that String must be
    * parseable back into. This method enforces that contract at write time so a bad value fails loudly at the
    * source instead of silently corrupting storage or exploding later as a {@code ClassCastException} on read.
    * <p>
    * Rules:
    * <ul>
    * <li>A value already assignable to {@code T} is accepted unchanged.</li>
    * <li>For id-backed types (BranchId/ArtifactId/Long), an {@link Id} or {@link Number} is normalized to the
    * proper {@code T} via its id (so a BranchId, its Long id, or a numeric String all become the same
    * {@code T}).</li>
    * <li>Otherwise the value's {@code toString()} is run through {@link #valueFromStorageString(String)} (the same
    * parse used to load from the database). If it cannot be parsed (e.g. the String "don" into a Long/BranchId
    * attribute), an {@link OseeArgumentException} is thrown naming the attribute and offending value.</li>
    * </ul>
    *
    * @param value the incoming value (any Object the generic write path allowed through)
    * @return the value coerced to this type's native representation {@code T}
    */
   @SuppressWarnings("unchecked")
   public T coerceValue(Object value) {
      if (value == null) {
         throw new OseeArgumentException("Null value written to attribute type [%s]", getName());
      }
      Class<?> nativeClass = defaultValue == null ? null : defaultValue.getClass();
      // Already the correct native type - accept unchanged.
      if (nativeClass != null && nativeClass.isInstance(value)) {
         return (T) value;
      }
      // Only actively validate/coerce the scalar value types whose storage String has a strict parse
      // (Long, BranchId, ArtifactId, Integer, Double, Date, Boolean). String/Enum/Object/InputStream/MapEntry
      // keep their existing write behavior so this guard cannot destabilize them.
      if (!isCoercibleValueType()) {
         return (T) value;
      }
      try {
         if (value instanceof Id) {
            // e.g. a BranchId/ArtifactId passed into a BranchId/ArtifactId/Long attribute - normalize via its id
            return valueFromStorageString(String.valueOf(((Id) value).getId()));
         }
         return valueFromStorageString(value.toString());
      } catch (OseeArgumentException ex) {
         throw ex;
      } catch (RuntimeException ex) {
         throw new OseeArgumentException(ex,
            "Value [%s] of type [%s] is not valid for attribute type [%s] (expected %s)", value,
            value.getClass().getName(), getName(), getStoreType());
      }
   }

   /**
    * @return true for scalar value types whose stored String has a strict parse back to {@code T}
    * ({@code Long}, {@code BranchId}, {@code ArtifactId}, {@code Integer}, {@code Double}, {@code Date},
    * {@code Boolean}). These are the types where a bad value silently corrupts storage today, so they are the
    * ones {@link #coerceValue(Object)} validates.
    */
   private boolean isCoercibleValueType() {
      return isLong() || isBranchId() || isArtifactId() || isInteger() || isDouble() || isDate() || isBoolean();
   }

   /**
    * @param value the attribute value in its native Java representation (converted from the storedValue as needed)
    * @return a user friendly text representation of the attribute value
    */
   public String getDisplayableString(T value) {
      return storageStringFromValue(value);
   }

   @Override
   public TaggerTypeToken getTaggerType() {
      return taggerType;
   }

   @Override
   public NamespaceToken getNamespace() {
      return namespace;
   }

   @Override
   public boolean isTaggable() {
      return taggerType.isValid();
   }

   @Override
   public Set<DisplayHint> getDisplayHints() {
      return displayHints;
   }

   public void addDisplayHint(DisplayHint displayHint) {
      displayHints.add(displayHint);
   }

   @Override
   public Set<OperationTypeToken> getOperationTypes() {
      return operationTypes;
   }

   /**
    * A missing attribute type is a placeholder created by the token service when an id cannot be resolved to a
    * real registered type (see OrcsTokenServiceImpl.getAttributeTypeOrCreate). The flag lets callers detect and
    * report the placeholder without relying on matching the generated name.
    */
   @Override
   public boolean isMissingAttributeType() {
      return missingAttributeType;
   }

   public void setMissingAttributeType(boolean missingAttributeType) {
      this.missingAttributeType = missingAttributeType;
   }

}
