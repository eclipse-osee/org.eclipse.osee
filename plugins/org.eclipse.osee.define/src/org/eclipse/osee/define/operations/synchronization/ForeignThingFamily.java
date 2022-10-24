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

package org.eclipse.osee.define.operations.synchronization;

import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Specializations of this abstract class are used by {@link SynchronizationArtifactBuilder} implementations to provide
 * a foreign thing, its identifier, and it's hierarchical parent identifiers to the {@link SynchronizationArtifact}.
 *
 * @author Loren K. Ashley
 */

public abstract class ForeignThingFamily implements ToMessage {

   /**
    * Gets the foreign thing represented by this {@link ForeignThingFamily}.
    *
    * @return the encapsulated foreign thing
    */

   public abstract Object getChild();

   /**
    * Gets an array containing the hierarchical parent foreign thing identifiers and the foreign thing's identifier. The
    * root parent identifier is returned in index 0 and the foreign thing's identifier is returned in the highest index.
    *
    * @return an array of foreign thing {@link String} identifiers.
    */

   public abstract String[] getForeignIdentifiersAsStrings();

   /**
    * Each index of this array contains a member of the {@link IdentifierType} enumeration to indicate what type of
    * foreign thing identifier is contained in the index of the array returned by {@link #getForeignKeys}.
    *
    * @return an array of {@link IdentifierType} enumeration members.
    */

   public abstract IdentifierType[] getIdentifierTypes();

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( name )
         .indentInc()
         .segment( "Child",                this.getChild()                       )
         .segment( "Foreign Keys",         this.getForeignIdentifiersAsStrings() )
         .segment( "Key Identifier Types", this.getIdentifierTypes()             )
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
      return this.toMessage(0, null).toString();
   }

}

/* EOF */