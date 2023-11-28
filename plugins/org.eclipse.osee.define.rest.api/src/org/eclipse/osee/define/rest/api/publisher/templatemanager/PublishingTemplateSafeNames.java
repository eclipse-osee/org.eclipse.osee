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

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to provide a list of Publishing Template Safe Names.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateSafeNames implements ToMessage {

   /**
    * Saves the list of Publishing Template Safe Names.
    */

   private List<String> safeNames;

   /**
    * Creates a new empty {@link PublishingTemplateSafeNames} for JSON deserialization.
    */

   public PublishingTemplateSafeNames() {
      this.safeNames = null;
   }

   /**
    * Creates a new {@link PublishingTemplateSafeNames} with the provided list of Safe Names.
    *
    * @param safeNames {@link List} of Publishing Template Safe Names.
    * @throws NullPointerException when the parameter <code>safeNames</code> is <code>null</code>.
    */

   public PublishingTemplateSafeNames(List<String> safeNames) {
      Objects.requireNonNull(safeNames, "PublishingTemplateSafeNames::new, parameter \"safeNames\" cannot be null.");
      this.safeNames = List.copyOf(safeNames);
   }

   /**
    * Gets the {@link List} of Publishing Template Safe Names.
    *
    * @return the list of Publishing Template Safe Names.
    * @throws IllegalStateException when an attempt is made to get the Publishing Template Safe Names when the
    * {@link List} of Publishing Template Safe Names has not yet been set.
    */

   public List<String> getSafeNames() {
      if (Objects.isNull(this.safeNames)) {
         throw new IllegalStateException("PublishingTemplateSafeNames::getSafeNames, \"safeNames\" has not been set.");
      }
      return this.safeNames;
   }

   /**
    * Predicate to test the validity of the {@link PublishingTemplateSafeNames}.
    *
    * @return <code>true</code>, when the list of Safe Names is not <code>null</code> and the list does not contain any
    * <code>null</code> entries; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
         Objects.nonNull( this.safeNames )
         && !this.safeNames.stream().map( Objects::isNull ).anyMatch( ( v ) -> v );
   }

   /**
    * Sets the {@link List} of Publishing Template Safe Names.
    *
    * @param safeNames the list of Publishing Template Safe Names.
    * @throws NullPointerException when the parameter <code>safeNames</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the List of Publishing Template Safe Names for a
    * {@link PublishingTemplateSafeNames} that has already been set.
    */

   public void setSafeNames(List<String> safeNames) {
      if (Objects.nonNull(this.safeNames)) {
         throw new IllegalStateException(
            "PublisningTemplateSafeNames::setSafeNames, \"safeName\" has already been set.");
      }
      Objects.requireNonNull(safeNames,
         "PublishingTemplateSafeNames::setSafeNames, parameter \"safeNames\" cannot be null.");
      this.safeNames = List.copyOf(safeNames);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PublishingTemplateSafeNames" )
         .indentInc()
         .segmentIndexedList( "Safe Names", this.safeNames )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }

}

/* EOF */
