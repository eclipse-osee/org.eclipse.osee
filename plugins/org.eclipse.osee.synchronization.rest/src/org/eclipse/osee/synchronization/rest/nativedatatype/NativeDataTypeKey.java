/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.synchronization.rest.nativedatatype;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Class used as a map key to lookup items according to the OSEE native data type category. The key contains a
 * {@link NativeDataType} enumeration member to represent the OSEE native data type category. It also contains the
 * native {@link AttributeTypeToken} so that the key can be used to distinguish between native OSEE enumerated data
 * types.
 *
 * @author Loren K. Ashley
 */

public class NativeDataTypeKey {

   /**
    * Saves the {@link NativeDataType} representing OSEE native data type category.
    */

   NativeDataType nativeDataType;

   /**
    * Saves the native {@link AttributeTypeToken} used to distinguish between native OSEE enumerated data types.
    */

   AttributeTypeToken nativeAttributeTypeToken;

   /**
    * Creates a key for non-enumerated ReqIF data types.
    *
    * @param nativeDataType the {@link NativeDataType} enumeration member.
    * @throws NullPointerException when <code>nativeDataType</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>nativeDataType</code> is {@link NativeDataType#ENUMERATED}.
    */

   NativeDataTypeKey(NativeDataType nativeDataType) {

      Objects.requireNonNull(nativeDataType);

      if (nativeDataType.equals(NativeDataType.ENUMERATED)) {
         throw new IllegalArgumentException();
      }

      this.nativeDataType = nativeDataType;
      this.nativeAttributeTypeToken = null;
   }

   /**
    * Creates a key for enumerated OSEE native data types.
    *
    * @param attributeTypeToken the native {@link AttributeTypeToken} describing the OSEE enumeration.
    * @throws NullPointerException when <code>attributeTypeToken</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>attributeTypeToken</code> is not for an enumeration.
    */

   NativeDataTypeKey(AttributeTypeToken attributeTypeToken) {

      Objects.requireNonNull(attributeTypeToken);

      if (!attributeTypeToken.isEnumerated()) {
         throw new IllegalArgumentException();
      }

      this.nativeDataType = NativeDataType.ENUMERATED;
      this.nativeAttributeTypeToken = attributeTypeToken;
   }

   /**
    * Gets the {@link NativeDataType} representing the native OSEE data type category the key is for.
    *
    * @return the {@link NativeDataType}.
    */

   public NativeDataType getNativeDataType() {
      return this.nativeDataType;
   }

   /**
    * Gets the {@link AttributeTypeToken} for the native OSEE enumerated data types.
    *
    * @return when the key represents an enumerated data type, an {@link Optional} containing the
    * {@link AttributeTypeToken}; otherwise an empty {@link Optional}.
    */

   public Optional<AttributeTypeToken> getNativeAttributeTypeToken() {
      return Optional.ofNullable(this.nativeAttributeTypeToken);
   }

   /**
    * Returns a hash code for the key.
    *
    * @implNote For non-enumerated native OSEE data types the hash code of the {@link NativeDataType} enumeration member
    * is returned. For enumerated native OSEE data types the hash code of the {@link AttributeTypeToken} is returned.
    * @return a hash code for the key.
    */

   @Override
   public int hashCode() {
      //@formatter:off
      return
         ( nativeDataType == NativeDataType.ENUMERATED )
            ? this.nativeAttributeTypeToken.getId().hashCode()
            : this.nativeDataType.hashCode();
      //@formatter:on
   }

   /**
    * Predicate to determine if the {@link NativeDataTypeKey} is for an enumerated data type.
    *
    * @return <code>true</code>, when the data type is enumerated; otherwise, <code>false</code>.
    */

   public boolean isEnumerated() {
      return this.nativeDataType.equals(NativeDataType.ENUMERATED);
   }

   /**
    * Predicate to determine if another {@link Object} represents the same native OSEE data type category.
    * {@link NativeDataTypeKey} objects for non-enumerated native OSEE data types are equal when the
    * {@link NativeDataType} members are the same. {@link NativeDataTypeKey} objects for enumerate native OSEE data
    * types are equal when the {@link AttributeTypeToken} members are the same. {@link NativeDataTypeKey} objects for
    * non-enumerated and enumerated native OSEE data types are not equal.
    *
    * @return <code>true</code> when the objects are equal; otherwise, <code>false</code>/
    */

   @Override
   public boolean equals(Object other) {
      //@formatter:off
      return
         ( ( other != null ) && (other instanceof NativeDataTypeKey) && ( this.nativeDataType == ((NativeDataTypeKey)other).nativeDataType ) )
            ? ( this.nativeDataType == NativeDataType.ENUMERATED )
                 ? this.nativeAttributeTypeToken.equals( ((NativeDataTypeKey)other).nativeAttributeTypeToken )
                 : true
            : false;
      //@formatter:on
   }

   /**
    * Returns a name for the key. For non-enumerated data types the key name is the name of the encapsulated
    * {@link NativeDataType} enumeration member. For enumerated data types the key name is the name of the encapsulated
    * {@link NativeDataType} enumeration member followed by a hyphen and the identifier of the encapsulated OSEE
    * {@link AttributeTypeToken} representing the enumeration's definition.
    */

   public String name() {
      //@formatter:off
      return
         this.isEnumerated()
            ? new StringBuilder( 1024 )
                     .append( this.nativeDataType.name() )
                     .append( "-" )
                     .append( this.nativeAttributeTypeToken.getIdString() )
                     .toString()
            : this.nativeDataType.name();
   }

}

/* EOF */
