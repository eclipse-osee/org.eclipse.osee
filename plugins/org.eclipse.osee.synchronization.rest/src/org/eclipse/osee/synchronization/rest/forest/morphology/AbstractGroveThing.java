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

package org.eclipse.osee.synchronization.rest.forest.morphology;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * This class provides a basic implementation of the {@link GroveThing} interface, to minimize the effort required to
 * implement this interface for specific Synchronization Artifact things.
 * <p>
 * <h2>Primary Key Set:</h2>
 * <p>
 * The primary key set is composed of the {@link Identifier}s obtained from the parent {@link GroveThing}s using the
 * method {@link #getIdentifier} and the {@link Identifier} of this {@link GroveThing}. The index position of a parent
 * key in the key set corresponds to the index position of that parent in the set of parents passed to the constructor.
 * The {@link Identifier} of this {@link GroveThing} goes into the high index position of the primary key set.
 * <p>
 * <h2>Native Key Set:</h2>
 * <p>
 * The native key set is derived from the native OSEE things saved in this container. When all of the native things
 * implement the {@link Id} interface a key for each native thing is extracted using the method {@link Id#getId()}. The
 * position of a native thing's key in the native key set corresponds to the index position of the native thing in the
 * array passed to the method {@link #setNativeThings}. This implementation will not generate a native key set for
 * native things that do not implement the {@link Id} interface.
 *
 * @author Loren K. Ashley
 */

public abstract class AbstractGroveThing implements GroveThing {

   /**
    * The minimum storage rank for the {@link AbstractGroveThing}.
    */

   private static int minGroveThingRank = 1;

   /**
    * The maximum storage rank for {@link AbstratGroveThing}.
    *
    * @implNote This value cannot exceed the maximum rank {@link Store} that is implemented.
    */

   private static int maxGroveThingRank = 3;

   /**
    * The minimum storage rank for native things.
    */

   private static int minNativeGroveThingRank = 1;

   /**
    * The maximum storage rank for native things.
    *
    * @implNote This value cannot exceed the maximum rank {@link Store} that is implemented.
    */

   private static int maxNativeGroveThingRank = 3;

   /**
    * The number of parent references plus one saved for this {@link GroveThing}.
    */

   protected int groveThingRank;

   /**
    * The number of native things saved for the {@link GroveThing}.
    */

   protected int nativeGroveThingRank;

   /**
    * Saves the unique identifier for the thing and it's parents. The thing's key is located in the high index and the
    * most senior parent key is at the low index.
    */

   protected Identifier[] groveThingKeys;

   /**
    * Saves the parents of the grove thing. The most senior parent is at the low index.
    */

   protected GroveThing[] parentGroveThings;

   /**
    * Saves the keys for the native things.
    */

   protected Optional<Object[]> nativeThingKeys;

   /**
    * Saves the native thing or things associated with this {@link GroveThing}.
    */

   protected Object[] nativeThings;

   /**
    * Saves a the foreign thing associated with this {@link GroveThing}.
    */

   protected Object foreignThing;

   /**
    * Creates a new {@link GroveThing} with the assigned {@link Identifier}, native storage rank, and the specified
    * {@link GroveThing} parents.
    *
    * @param groveThingKey a unique {@link Identifier} for the {@link GroveThing}.
    * @param nativeGroveThingRank the number of native things to be associated with this {@link GroveThing}.
    * @param an array of the parent {@link GroveThing}s. This parameter may be a <code>null</code> or empty array.
    */

   public AbstractGroveThing(Identifier groveThingKey, int nativeGroveThingRank, GroveThing... parents) {

      //@formatter:off
      /*
       * Assert the constructor parameters are sane:
       * * Identifier is non-null
       * * nativeGroveThingRank is within valid range
       * * parents array if non-null and non-empty is a valid size with valid contents
       */

      assert
            Objects.nonNull(groveThingKey)
         && ( nativeGroveThingRank >= AbstractGroveThing.minNativeGroveThingRank )
         && ( nativeGroveThingRank <= AbstractGroveThing.maxNativeGroveThingRank )
         && ParameterArray.validateSizeAndType( parents, AbstractGroveThing.minGroveThingRank - 1, AbstractGroveThing.maxGroveThingRank - 1, GroveThing.class );
      //@formatter:on

      this.foreignThing = null;
      this.nativeThings = null;

      /*
       * Save the size of the native things array
       */

      this.nativeGroveThingRank = nativeGroveThingRank;

      /*
       * Save the size of the grove things key array and save the parents array if it was non-null and non-empty
       */

      if (Objects.nonNull(parents) && (parents.length > 0)) {
         this.groveThingRank = parents.length + 1;
         this.parentGroveThings = parents;
      } else {
         this.groveThingRank = 1;
         this.parentGroveThings = null;
      }

      /*
       * Compute the contents of the grove things key array
       */

      if (this.groveThingRank == 1) {
         this.groveThingKeys = new Identifier[] {groveThingKey};
      } else {
         this.groveThingKeys = new Identifier[this.groveThingRank];

         for (int i = 0; i < parents.length; i++) {
            this.groveThingKeys[i] = parents[i].getIdentifier();
         }

         this.groveThingKeys[this.groveThingRank - 1] = groveThingKey;
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getForeignThing() {
      return this.foreignThing;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Identifier getIdentifier() {
      return this.groveThingKeys[this.groveThingRank - 1];
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object[]> getPrimaryKeys() {
      return Optional.ofNullable(this.groveThingKeys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object[]> getNativeKeys() {
      return this.nativeThingKeys;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getNativeThing() {
      return this.nativeThings[this.nativeGroveThingRank - 1];
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int nativeRank() {
      return this.nativeGroveThingRank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int rank() {
      return this.groveThingRank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setForeignThing(Object foreignThing) {
      assert Objects.nonNull(foreignThing);
      assert Objects.isNull(this.foreignThing);

      this.foreignThing = foreignThing;
   }

   public abstract boolean validateNativeThings(Object... nativeThings);

   /**
    * {@inheritDoc}
    */

   @Override
   public GroveThing setNativeThings(Object... nativeThings) {

      assert Objects.isNull(this.nativeThings) && this.validateNativeThings(nativeThings);

      this.nativeThings = nativeThings;

      var nativeThingKeys = new Object[this.nativeThings.length];

      for (int i = 0; i < this.nativeThings.length; i++) {
         if (!(this.nativeThings[i] instanceof Id)) {
            nativeThingKeys = null;
            break;
         }
         nativeThingKeys[i] = ((Id) this.nativeThings[i]).getId();
      }

      this.nativeThingKeys = Optional.ofNullable(nativeThingKeys);

      return this;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);
      var indent2 = IndentedString.indentString(indent + 2);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage.append( indent0 ).append( name ).append( "\n" );

      if( Objects.nonNull(this.groveThingKeys) )
      {
         outMessage.append( indent1 ).append( "Grove Thing Keys: " ).append( Arrays.stream( this.groveThingKeys ).map( Identifier::toString ).collect( Collectors.joining( ", ") ) ).append( "\n" );
      }

      if( Objects.nonNull( this.nativeThings ) )
      {

         outMessage
            .append( indent1 ).append( "Native Things:   " ).append( "\n" )
            ;

         for( int i = 0; i < this.nativeGroveThingRank; i++ )
         {
            outMessage.append( indent2 ).append( "Native Thing[ " ).append( i ).append( " ]: " );

            if( i >= this.nativeThings.length )
            {
               outMessage.append("(index-out-of-bounds)").append( "\n" );
               continue;
            }

            if( Objects.isNull( this.nativeThings[i] ) )
            {
               outMessage.append( "(null)" ).append( "\n" );
               continue;
            }

            if( this.nativeThings[i] instanceof ToMessage )
            {
               outMessage.append( "\n" );
               ((ToMessage) this.nativeThings[i]).toMessage( indent + 3, outMessage );
            }
            else
            {
               outMessage.append( this.nativeThings[i].toString() ).append( "\n" );
            }
         }

         this.getNativeKeys().ifPresent( nativeKeys ->
         {
            outMessage
               .append( indent1 ).append( "Native Thing Keys: " ).append( "\n" )
               ;

            for( int i = 0; i < this.nativeGroveThingRank; i++ )
            {
               outMessage.append( indent2 ).append( "Key[ " ).append( i ).append( " ]: ");

               if( i >= this.nativeThings.length )
               {
                  outMessage.append("(index-out-of-bounds)").append( "\n" );
                  continue;
               }

               if( Objects.isNull( this.nativeThings[i] ) )
               {
                  outMessage.append( "(null)" ).append( "\n" );
                  continue;
               }

               if( nativeKeys[i] instanceof ToMessage )
               {
                  outMessage.append( "\n" );
                  ((ToMessage) nativeKeys[i]).toMessage( indent + 3, outMessage );
               }
               else
               {
                  outMessage.append( nativeKeys[i].toString() ).append( "\n" );
               }
            }
         });
      }

      outMessage
         .append( indent1 ).append( "Foreign Thing:   " ).append( Objects.nonNull( this.foreignThing ) ? this.foreignThing : "(null)" ).append( "\n" )
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
