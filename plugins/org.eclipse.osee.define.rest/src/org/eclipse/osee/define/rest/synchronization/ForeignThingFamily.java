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

package org.eclipse.osee.define.rest.synchronization;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
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
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( "\n" )
         .append( indent1 ).append( "Child:                " ).append( this.getChild() ).append( "\n" )
         .append( indent1 ).append( "Foreign Keys:         " ).append( Arrays.stream( this.getForeignIdentifiersAsStrings() ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) ).append( "\n" )
         .append( indent1 ).append( "Key Identifier Types: " ).append( Arrays.stream( this.getIdentifierTypes() ).map( IdentifierType::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) ).append( "\n" );
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