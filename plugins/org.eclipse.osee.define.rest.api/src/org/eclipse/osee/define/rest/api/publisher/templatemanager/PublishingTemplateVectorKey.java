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
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A multi-value key used by the Publishing Template Manager for caching Publishing Templates.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateVectorKey implements Comparable<PublishingTemplateVectorKey>, ToMessage {

   /**
    * Saves a list of the key's values.
    */

   private List<PublishingTemplateScalarKey> key;

   /**
    * Creates a new {@link PublishingTemplateVectorKey} for JSON deserialization.
    */

   public PublishingTemplateVectorKey() {
      this.key = null;
   }

   /**
    * Creates a new {@link PublishingTemplateVectorKey} with the provided list of values.
    *
    * @param key a {@link List} of {@link PublishingTemplateScalarKey} objects containing the key's values.
    * @throws NullPointerException when <code>key</code> is <code>null</code>.
    */

   public PublishingTemplateVectorKey(List<PublishingTemplateScalarKey> key) {

      this.key = Objects.requireNonNull(key, "PublishingTemplateVectorKey::new, parameter \"key\" cannot be null.");
   }

   /**
    * The {@link PublishingTemplateVectorKey} implements the {@link Comparable} interface so a collection of the keys
    * can be sorted in alphabetical order. For the sort to be valid it requires that the key values were also sorted in
    * alphabetical order. The values contained in the {@link #key} members of the objects being compared are compared
    * starting with the first value and proceeding to the last. When the first pair of key values are not equal the
    * lexicographical result of that comparison is returned. When the key values are exhausted, if this object has less
    * key values -1 is returned, if this object has more key values 1 is returned, and if both objects have the same
    * number of key values 0 is returned.
    *
    * @param publishingTemplatekey the other {@link PublishingTemplateVectorKey} to be compared.
    * @return the value 0 if the argument <code>other</code> is equal to this {@link PublishingTemplateVectorKey}; a
    * value less than 0 if this {@link PublishingTemplateVectorKey} is lexicographically less than the
    * <code>other</code> argument; and a value greater than 0 if this string is lexicographically greater than the
    * <code>other</code> argument.
    */

   @JsonIgnore
   @Override
   public int compareTo(PublishingTemplateVectorKey other) {
      int length, otherLength, limit, i, rv;

      limit = (length = this.key.size()) > (otherLength = other.key.size()) ? length : otherLength;

      for (i = 0; i < limit; i++) {
         if ((rv = this.key.get(i).compareTo(other.key.get(i))) != 0) {
            return rv;
         }
      }

      return (length == otherLength) ? 0 : ((length < otherLength) ? -1 : 1);
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public boolean equals(Object other) {
      //@formatter:off
      return
            ( other instanceof PublishingTemplateVectorKey )
         && this.key.equals( ((PublishingTemplateVectorKey) other).key );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public int hashCode() {
      return this.key.hashCode();
   }

   /**
    * Gets the list of key values as {@link PublishingTemplateScalarKey} objects.
    *
    * @return the key values.
    * @throws IllegalStateException when the {@link #key} member has not yet been set.
    */

   public List<PublishingTemplateScalarKey> getKey() {

      if (Objects.isNull(this.key)) {
         throw new IllegalStateException("PublishingTemplateVecotrKey::getKey, member \"key\" has not been set.");
      }

      return this.key;
   }

   /**
    * Predicate to the the validity of the {@link PublishingTemplateVectorKey}.
    *
    * @return <code>true</code>, when the {@link #key} is non-<code>null</code>, does not contain any <code>null</code>
    * values, and the values are valid according to {@link PublishingTemplateScalarKey#isValid}; otherwise,
    * <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.key )
         && !this.key.isEmpty()
         && this.key.stream()
               .map( ( key ) -> Objects.isNull( key ) || !key.isValid() )
               .anyMatch( ( v ) -> v );
      //@formatter:on
   }

   /**
    * Sets the key.
    *
    * @param key the value.
    * @throws IllegalStateException when the member {@link #key} has already been set.
    * @throws NullPointerException when the parameter <code>key</code> is <code>null</code>.
    */

   public void setKey(List<PublishingTemplateScalarKey> key) {

      if (Objects.nonNull(this.key)) {
         throw new IllegalStateException("PublishingTemplateVectorKey::setKey, member \"key\" has already been set.");
      }

      this.key = Objects.requireNonNull(key, "PublishingTemplateVectorKey::setKey, parameter \"key\" cannot be null.");
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
         .indent( indent )
         .title( "PublishingTemplatekey" )
         .indentInc()
         .segmentIndexedList( "Key",  this.key   )
         .indentDec()
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
