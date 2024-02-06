/*********************************************************************
 * Copyright (c) 2021 Boeing
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
 * The data structure used to provide the Publishing Template Key Groups for all of the Publishing Templates that are
 * cached by the Publishing Template Manger.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateKeyGroups implements ToMessage {

   /**
    * Saves the list of Publishing Template Key Groups.
    */

   private List<PublishingTemplateKeyGroup> publishingTemplateKeyGroupList;

   /**
    * Creates a new empty {@link PublishingTemplateKeyGroups} for JSON deserialization.
    */

   public PublishingTemplateKeyGroups() {
      this.publishingTemplateKeyGroupList = null;
   }

   /**
    * Creates a new {@link PublishingTemplateKeyGroups} with the provided list of {@link PublishingTemplateKeyGroup}s.
    *
    * @param publishingTemplateKeyGroupList {@link List} of Publishing Template Safe Names.
    * @throws NullPointerException when the parameter <code>safeNames</code> is <code>null</code>.
    */

   public PublishingTemplateKeyGroups(List<PublishingTemplateKeyGroup> publishingTemplateKeyGroupList) {

      Objects.requireNonNull(publishingTemplateKeyGroupList,
         "PublishingTemplateKeyGroups::new, parameter \"publishingTemplateKeyGroupList\" cannot be null.");

      this.publishingTemplateKeyGroupList = publishingTemplateKeyGroupList;
   }

   /**
    * Gets the {@link List} of Publishing Template Key Groups.
    *
    * @return the list of Publishing Template Safe Names.
    * @throws IllegalStateException when an attempt is made to get the Publishing Template Safe Names when the
    * {@link List} of Publishing Template Safe Names has not yet been set.
    */

   public List<PublishingTemplateKeyGroup> getPublishingTemplateKeyGroupList() {

      if (Objects.isNull(this.publishingTemplateKeyGroupList)) {
         throw new IllegalStateException(
            "PublishingTemplateKeyGroups::getPublishingTemplateKeyGroupList, \"publishingTemplateKeyGroupList\" has not been set.");
      }

      return this.publishingTemplateKeyGroupList;
   }

   /**
    * Predicate to test the validity of the {@link PublishingTemplateKeyGroups}.
    *
    * @return <code>true</code>, when:
    * <ul>
    * <li>the list of {@link PublishingTemplateKeyGroup} objects is not <code>null</code>,</li>
    * <li>the list does not contain any <code>null</code> entries, and</li>
    * <li>each {@link PublishingTemplateKeyGroup#isValid};</li>
    * </ul>
    * otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.publishingTemplateKeyGroupList )
         && !this.publishingTemplateKeyGroupList
               .stream()
               .map
                  (
                     ( publishingTemplateKeyGroup ) ->
                           Objects.isNull( publishingTemplateKeyGroup )
                        || !publishingTemplateKeyGroup.isValid()
                  )
               .anyMatch( ( v ) -> v );
      //@formatter:on
   }

   /**
    * Sets the {@link List} of Publishing Template Key Groups.
    *
    * @param publishingTemplateKeyGroupList the list of Publishing Template Key Groups.
    * @throws NullPointerException when the parameter <code>publishingTemplateKeyGroupList</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the List of Publishing Template Key Groups for a
    * {@link PublishingTemplateKeyGroups} that has already been set.
    */

   public void setSafeNames(List<PublishingTemplateKeyGroup> publishingTemplateKeyGroupList) {
      if (Objects.nonNull(this.publishingTemplateKeyGroupList)) {
         throw new IllegalStateException(
            "PublisningTemplateSafeNames::setPublishingTemplateKeyGroupList, member \"publishingTemplateKeyGroupList\" has already been set.");
      }
      Objects.requireNonNull(publishingTemplateKeyGroupList,
         "PublishingTemplateKeyGroups::setPublishingTemplateKeyGroupList, parameter \"publishingTemplateKeyGroup\" cannot be null.");

      this.publishingTemplateKeyGroupList = List.copyOf(publishingTemplateKeyGroupList);
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
         .title( "PublishingTemplateKeyGroups" )
         .indentInc()
         .segmentIndexed( "Safe Names", this.publishingTemplateKeyGroupList )
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
