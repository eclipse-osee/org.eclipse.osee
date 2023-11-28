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

package org.eclipse.osee.define.rest.api.publisher.templatemanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A single value key used by the Publishing Template Manager for caching Publishing Templates.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateScalarKey implements Comparable<PublishingTemplateScalarKey>, ToMessage {

   /**
    * A static map of the valid values for the member {@link #keyType}.
    */

   //@formatter:off
   private static Set<String> keyTypes =
      Arrays.stream( PublishingTemplateKeyType.values() )
         .map( PublishingTemplateKeyType::name )
         .collect( Collectors.toSet() );
   //@formatter:on

   /**
    * Saves the key's string value.
    */

   private String key;

   /**
    * Saves an indicator of the type of key represented by this object.
    */

   private String keyType;

   /**
    * Creates a new {@link PublishingTemplateScalarKey} for JSON deserialization.
    */

   public PublishingTemplateScalarKey() {
      this.key = null;
      this.keyType = null;
   }

   /**
    * Creates a new {@link PublishingTemplateScalarKey} with the provided key and key type.
    *
    * @param key the string value of the key.
    * @param keyType the {@link PublishingTemplateKeyType} of the key.
    * @throws NullPointerException when either of <code>key</code> or <code>keyType</code> are <code>null</code>.
    */

   @JsonIgnore
   public PublishingTemplateScalarKey(String key, PublishingTemplateKeyType keyType) {

      this.key = Objects.requireNonNull(key, "PublishingTemplateScalarKey::new, the parameter \"key\" cannot be null.");
      this.keyType = Objects.requireNonNull(keyType,
         "PublishingTemplateScalarKey::new, the parameter \"keyType\" cannot be null.").name();
   }

   /**
    * Creates a new {@link PublishingTemplateScalarKey} with the provided key and key type.
    *
    * @param key the string value of the key.
    * @param keyType the string name for the key type. This parameter must match the name value of a member of the
    * enumeration {@link PublishingTemplateKeyType}.
    * @throws NullPointerException when either of <code>key</code> or <code>keyType</code> are <code>null</code>.
    */

   public PublishingTemplateScalarKey(String key, String keyType) {

      this.key = Objects.requireNonNull(key, "PublishingTemplateScalarKey::new, the parameter \"key\" cannot be null.");
      this.keyType =
         Objects.requireNonNull(keyType, "PublishingTemplateScalarKey::new, the parameter \"keyType\" cannot be null.");
   }

   /**
    * The {@link PublishingTemplateScalarKey} objects implement the {@link Comparable} interface for sorting. The
    * {@link #key} members are compared and the lexicographical result is returned.
    *
    * @param other the other {@link PublishingTemplateScalarKey} to be compared.
    * @return the value 0 if the argument <code>other</code> is equal to this {@link PublishingTemplaekey}; a value less
    * than 0 if this {@link PublishingTemplateScalarKey} is lexicographically less than the <code>other</code> argument;
    * and a value greater than 0 if this string is lexicographically greater than the <code>other</code> argument.
    */

   @JsonIgnore
   @Override
   public int compareTo(PublishingTemplateScalarKey other) {
      return this.key.compareTo(other.key);
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Keys of different key types are not equal.
    */

   @JsonIgnore
   @Override
   public boolean equals(Object other) {
      //@formatter:off
      return
            ( other instanceof PublishingTemplateScalarKey )
         && this.keyType.equals ( ((PublishingTemplateScalarKey) other).keyType )
         && this.key.equals     ( ((PublishingTemplateScalarKey) other).key     );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Keys of different key types should not be mixed for hashing. The key type is not included in the hash
    * code calculation to reduce the computation.
    */

   @JsonIgnore
   @Override
   public int hashCode() {
      return this.key.hashCode();
   }

   /**
    * Gets the key value.
    *
    * @return the string value of the key.
    * @throws IllegalStateException when the {@link #key} member has not yet been set.
    */

   public String getKey() {

      if (Objects.isNull(this.key)) {
         throw new IllegalStateException("PublishingTemplateScalarKey::getkey, parameter \"key\" has not been set.");
      }

      return this.key;
   }

   /**
    * Gets the key type as a member of the {@Link PublishingTemplateKeyType} enumeration.
    *
    * @return the type of key.
    * @throws IllegalStateException when the {@link #keyType} member has not yet been set.
    * @throws IllegalArgumentException when the {@link keyType} member contains an invalid value.
    */

   @JsonIgnore
   public PublishingTemplateKeyType getKeyTypeAs() {

      if (Objects.isNull(this.keyType) || !PublishingTemplateScalarKey.keyTypes.contains(this.keyType)) {
         throw new IllegalStateException(
            "PublishingTemplateScalarKey::getKeyTypeAs, member \"keyType\" has not been set.");
      }

      return PublishingTemplateKeyType.valueOf(this.keyType);

   }

   /**
    * Gets the key type as a {@link String}.
    *
    * @return the type of key.
    * @throws IllegalStateException when the {@link #keyType} member has not yet been set.
    */

   public String getKeyType() {

      if (Objects.isNull(this.keyType)) {
         throw new IllegalStateException(
            "PublishingTemplateScalarKey::getKeyType, parameter \"keyType\" has not been set.");
      }

      return this.keyType;
   }

   /**
    * Predicate to the the validity of the {@link PublishingTemplateScalarKey}.
    *
    * @return <code>true</code>, when the member {@link #key} is non-<code>null</code>, the member {@link #key} is
    * non-blank, the member {@link #keyType} is non-<code>null</code>, and the member {@link keyType} is a valid value;
    * otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {

      //@formatter:off
      return
            Strings.isValidAndNonBlank( this.key )
         && Objects.nonNull( this.keyType ) && PublishingTemplateScalarKey.keyTypes.contains( this.keyType );
      //@formatter:on
   }

   /**
    * Sets the key value.
    *
    * @param key the key value.
    * @throws IllegalStateException when the member {@link #key} has already been set.
    * @throws NullPointerException when the parameter <code>key</code> is <code>null</code>.
    */

   public void setKey(String key) {

      if (Objects.nonNull(this.key)) {
         throw new IllegalStateException("PublishingTemplateScalarKey::setkey, member \"key\" has already been set.");
      }

      this.key =
         Objects.requireNonNull(key, "PublishingTemplateScalarKey::setkey, member \"key\" has already been set.");
   }

   /**
    * Sets the key type.
    *
    * @param key the key type.
    * @throws IllegalStateException when the member {@link #keyType} has already been set.
    * @throws NullPointerException when the parameter <code>key</code> is <code>null</code>.
    */

   public void setKeyType(String keyType) {

      if (Objects.nonNull(this.keyType)) {
         throw new IllegalStateException("PublishingTemplatekey::setKeyType, member \"keyType\" has already been set.");
      }

      this.keyType =
         Objects.requireNonNull(keyType, "PublishingTemplatekey::setKeyType, parameter \"keyType\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .title( "[ Key Type: " )
         .append( this.keyType )
         .append( ",  " )
         .append( "Key: ")
         .append( this.key )
         .append( " ]" )
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }

}
