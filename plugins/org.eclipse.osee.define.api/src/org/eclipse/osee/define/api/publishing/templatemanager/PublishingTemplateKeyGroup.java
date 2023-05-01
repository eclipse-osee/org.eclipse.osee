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

package org.eclipse.osee.define.api.publishing.templatemanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Comparator;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An entry for the {@link PublishingTemplateKeyGroups} list that contains the following secondary keys for a Publishing
 * Template:
 * <ul>
 * <li>Publishing Template Manager's Publishing Template Identifier</li>
 * <li>Publishing Template Match Criteria</li>
 * <li>Publishing Template Name</li>
 * <li>Publishing Template Safe Name</li>
 * </ul>
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateKeyGroup implements Comparable<PublishingTemplateKeyGroup>, ToMessage {

   /**
    * The Publishing Template Manager's identifier for the Publishing Template associated with the {@link safeName}.
    * This is NOT guaranteed to reflect the OSEE Artifact identifier of the Publishing Template for Artifact Publishing
    * Templates.
    */

   private PublishingTemplateScalarKey identifier;

   /**
    * This key is a list of the Publishing Template's match criteria strings.
    */

   private PublishingTemplateVectorKey matchCriteria;

   /**
    * This key is name of the Publishing Template.
    */

   private PublishingTemplateScalarKey name;

   /**
    * The key is a "safe name" for the Publishing Template.
    */

   private PublishingTemplateScalarKey safeName;

   /**
    * Creates a new {@link PublishingTemplateKeyGroup} for JSON deserialization.
    */

   public PublishingTemplateKeyGroup() {
      this.identifier = null;
      this.matchCriteria = null;
      this.name = null;
      this.safeName = null;
   }

   /**
    * Creates a new {@link PublishingTemplateKeyGroup} with the provided keys.
    *
    * @param identifier the identifier key.
    * @param matchCriteria the match criteria key.
    * @param name the name key.
    * @param safeName the safe name key.
    * @throws NullPointerException when any of the keys are <code>null</code>.
    */

   public PublishingTemplateKeyGroup(PublishingTemplateScalarKey identifier, PublishingTemplateVectorKey matchCriteria, PublishingTemplateScalarKey name, PublishingTemplateScalarKey safeName) {

      if (Objects.isNull(identifier)) {
         throw new NullPointerException("PublishingTemplateKeyGroup::new, parameter \"identifier\" cannot be null.");
      }

      if (Objects.isNull(matchCriteria)) {
         throw new NullPointerException("PublishingTemplateKeyGroup::new, parameter \"matchCriteria\" cannot be null.");
      }

      if (Objects.isNull(name)) {
         throw new NullPointerException("PublishingTemplateKeyGroup::new, parameter \"name\" cannot be null.");
      }

      if (Objects.isNull(safeName)) {
         throw new NullPointerException("PublishingTemplateKeyGroup::new, parameter \"safeName\" cannot be null.");
      }

      this.identifier = identifier;
      this.matchCriteria = matchCriteria;
      this.name = name;
      this.safeName = safeName;
   }

   /**
    * An implementation of the {@link Comparator} interface is provided so that {@link PublishingTemplateKeyGroup}
    * objects may be sorted by the {@link #safeName} for presentation in a GUI. The members {@link #safeName} are
    * compared and the lexicographical result is returned if they are not equal. When the {@link #safeName} members are
    * equal the lexicographical comparison of the {@link #identifier} members is returned.
    *
    * @param other the other {@link PublishingTemplateKeyGroup} for comparison.
    * @return the value 0 if the argument <code>other</code> is equal to this {@link PublishingTemplaeSafeName}; a value
    * less than 0 if this {@link PublishingTemplateKeyGroup} is lexicographically less than the <code>other</code>
    * argument; and a value greater than 0 if this string is lexicographically greater than the <code>other</code>
    * argument.
    */

   @JsonIgnore
   @Override
   public int compareTo(PublishingTemplateKeyGroup other) {
      int rv;
      //@formatter:off
      return
         ( (  rv = this.safeName.compareTo(other.safeName) ) == 0 )
            ? this.identifier.compareTo(other.identifier)
            : rv;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public boolean equals(Object other) {
      //@formatter:off
      return
            ( other instanceof PublishingTemplateKeyGroup )
         && this.identifier.equals    ( ((PublishingTemplateKeyGroup) other).identifier    )
         && this.name.equals          ( ((PublishingTemplateKeyGroup) other).name          )
         && this.safeName.equals      ( ((PublishingTemplateKeyGroup) other).safeName      )
         && this.matchCriteria.equals ( ((PublishingTemplateKeyGroup) other).matchCriteria );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @JsonIgnore
   @Override
   public int hashCode() {
      //@formatter:off
      return
           (  7 * this.identifier.hashCode()    )
         ^ ( 13 * this.name.hashCode()          )
         ^ ( 31 * this.safeName.hashCode()      )
         ^ ( 61 * this.matchCriteria.hashCode() );
      //@formatter:on
   }

   /**
    * Gets the Publishing Template Managers's identifier for the Publishing Template.
    *
    * @return the identifier.
    * @throws IllegalStateException when the {@link #identifier} member has not yet been set.
    */

   public PublishingTemplateScalarKey getIdentifier() {

      if (Objects.isNull(this.identifier)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroup::getIdentifier, member \"identifier\" has not been set.");
      }

      return this.identifier;
   }

   /**
    * Gets the Publishing Template's match criteria.
    *
    * @return the match criteria.
    * @throws IllegalStateException when the {@link #matchCriteria} member has not yet been set.
    */

   public PublishingTemplateVectorKey getMatchCriteria() {

      if (Objects.isNull(this.matchCriteria)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroup::getMatchCriteria, member \"matchCriteria\" has not been set.");
      }

      return this.matchCriteria;
   }

   /**
    * Gets the Publishing Template's name.
    *
    * @return the identifier.
    * @throws IllegalStateException when the {@link #name} member has not yet been set.
    */

   public PublishingTemplateScalarKey getName() {

      if (Objects.isNull(this.name)) {
         throw new IllegalStateException("PublishingTemplateKeyGroup::getName, member \"name\" has not been set.");
      }

      return this.name;
   }

   /**
    * Gets the Publishing Template's safe name.
    *
    * @return the safe name.
    * @throws IllegalStateException when the {@link #safeName} member has not yet been set.
    */

   public PublishingTemplateScalarKey getSafeName() {

      if (Objects.isNull(this.safeName)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroup::getSafeName, member \"safeName\" has not been set.");
      }

      return this.safeName;
   }

   /**
    * Predicate to the the validity of the {@link PublishingTemplateKeyGroup}.
    *
    * @return <code>true</code>, when the members are non-<code>null</code> and valid according to their
    * <code>isValid()</code> methods; otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
               Objects.nonNull( this.identifier    ) && this.identifier.isValid()
            && Objects.nonNull( this.name          ) && this.name.isValid()
            && Objects.nonNull( this.safeName      ) && this.safeName.isValid()
            && Objects.nonNull( this.matchCriteria ) && this.matchCriteria.isValid();
      //@formatter:on
   }

   /**
    * Sets the Publishing Template Manager's identifier for the Publishing Template.
    *
    * @param identifer the identifier key.
    * @throws IllegalStateException when the member {@link #identifier} has already been set.
    * @throws NullPointerException when the parameter <code>identifier</code> is <code>null</code>.
    */

   public void setIdentifier(PublishingTemplateScalarKey identifier) {

      if (Objects.nonNull(this.identifier)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroup::setIdentifier, member \"identifier\" has already been set.");
      }
      if (Objects.isNull(identifier)) {
         throw new NullPointerException(
            "PublishingTemplateKeyGroup::setIdentifier, parameter \"identifier\" cannot be null.");
      }

      this.identifier = identifier;
   }

   /**
    * Sets the match criteria key.
    *
    * @param matchCriteria the match criteria key.
    * @throws IllegalStateException when the member {@link #matchCriteria} has already been set.
    * @throws NullPointerException when the parameter <code>matchCriteria</code> is <code>null</code>.
    */

   public void setMatchCriteria(PublishingTemplateVectorKey matchCriteria) {

      if (Objects.nonNull(this.matchCriteria)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroup::setMatchCriteria, member \"matchCriteria\" has already been set.");
      }
      if (Objects.isNull(matchCriteria)) {
         throw new NullPointerException(
            "PublishingTemplateKeyGroup::setMatchCriteria, parameter \"matchCriteria\" cannot be null.");
      }

      this.matchCriteria = matchCriteria;
   }

   /**
    * Sets the name key.
    *
    * @param name the name key.
    * @throws IllegalStateException when the member {@link #name} has already been set.
    * @throws NullPointerException when the parameter <code>name</code> is <code>null</code>.
    */

   public void setName(PublishingTemplateScalarKey name) {

      if (Objects.nonNull(this.name)) {
         throw new IllegalStateException("PublishingTemplateKeyGroup::setName, member \"name\" has already been set.");
      }
      if (Objects.isNull(name) || !name.isValid()) {
         throw new NullPointerException("PublishingTemplateKeyGroup::setName, parameter \"name\" cannot be null.");
      }

      this.name = name;
   }

   /**
    * Sets the safe name key.
    *
    * @param safeName the safe name key.
    * @throws IllegalStateException when the member {@link #safeName} has already been set.
    * @throws NullPointerException when the parameter <code>safeName</code> is <code>null</code> or blank.
    */

   public void setSafeName(PublishingTemplateScalarKey safeName) {

      if (Objects.nonNull(this.safeName)) {
         throw new IllegalStateException("PublishingTemplateKeyGroup::setSafeName, \"safeName\" has already been set.");
      }
      if (Objects.isNull(safeName) || !safeName.isValid()) {
         throw new NullPointerException(
            "PublishingTemplateKeyGroup::setSafeName, parameter \"safeName\" cannot be null or blank.");
      }

      this.safeName = safeName;
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
         .title( "PublishingTemplateKeyGroup" )
         .indentInc()
         .segment( "Identifier",     this.identifier    )
         .segment( "Name",           this.name          )
         .segment( "Safe Name",      this.safeName      )
         .segment( "Match Criteria", this.matchCriteria )
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

/* EOF */