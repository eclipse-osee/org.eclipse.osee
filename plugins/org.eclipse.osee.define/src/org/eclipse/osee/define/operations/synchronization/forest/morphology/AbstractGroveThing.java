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

package org.eclipse.osee.define.operations.synchronization.forest.morphology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.operations.synchronization.LinkType;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

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

public class AbstractGroveThing implements GroveThing {

   /**
    * Enumeration used to indicate what type of storage is used for linked {@link GroveThing} instances of a particular
    * {@link IdentiferType}.
    */

   public enum LinkRank {

      /**
       * Only one {@link GroveThing} instance is saved.
       */

      SCALAR,

      /**
       * Multiple {@link GroveThing} instances with a unique {@link Identifier} are saved in an unordered collection.
       */

      MAP,

      /**
       * Multiple {@link GroveThing} instances are saved in an ordered collection.
       */

      VECTOR;
   }

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
    * A reference to a foreign hierarchy thing. Used for building the Spec Object hierarchy in the foreign DOM.
    */

   private Object foreignHierarchy;

   /**
    * Saves a the foreign thing associated with this {@link GroveThing}.
    */

   protected Object foreignThing;

   /**
    * A {@link Map} used to save the linked {@link GroveThing} instances for each {@link IdentifierType}.
    */

   protected Map<LinkType, Object> links;

   /**
    * A {@link Map} of the {@link LinkRank} enumeration members indicating the type of {@link Object} stored in the
    * {@link #links} {@link Map} for each {@link IdentifierType}.
    */

   protected Map<LinkType, LinkRank> linkRank;

   /**
    * A {@link Predicate} used to validate a specified link type is valid for the {@link GroveThing}.
    */

   protected Predicate<LinkType> linkTypeValidator;

   /**
    * An array of {@link Function} implementations used to extract the native keys from the native things.
    */

   protected Function<Object, Object>[] nativeKeyFunctions;

   /**
    * Saves the keys for the native things.
    */

   protected Optional<Object[]> nativeKeys;

   /**
    * The number of native things that can be stored by the {@link GroveThing}. This is also the number of native keys
    * that will be provided by {@link GroveThing}s that can provide native keys.
    */

   protected int nativeRank;

   /**
    * Saves the native thing or things associated with this {@link GroveThing}.
    */

   protected Object[] nativeThings;

   /**
    * Saves a {@link Predicate} used to validate an array of native things is the correct size and contains
    * {@link Objects} with the correct classes.
    */

   protected Predicate<Object[]> nativeThingValidator;

   /**
    * Saves the parents of the grove thing. The most senior parent is at the low index.
    */

   protected GroveThing[] parentGroveThings;

   /**
    * Saves a {@link Predicate} used to validate an array of the {@link GroveThing} parents is the correct size and
    * contains {@link GroveThing} instances with the correct {@link IdentifierType}.
    */

   protected Predicate<GroveThing[]> parentsValidator;

   /**
    * Saves the unique identifier for the thing and it's parents. The thing's key is located in the high index and the
    * most senior parent key is at the low index.
    */

   protected Identifier[] primaryKeys;

   /**
    * The number of primary keys provided by the {@link GroveThing}.
    */

   protected int primaryRank;

   /**
    * <code>true</code> indicates the {@link GroveThing} can provide native keys when the native things have been set.
    * <code>false</code> indicates the {@link GroveThing} will not provide native keys.
    */

   protected boolean providesNativeKeys;

   /**
    * Creates a new {@link GroveThing} with the specified properties.
    *
    * @param groveThingKey a unique {@link Identifier} for the {@link GroveThing}.
    * @param primaryRank the number of primary keys to be provided by the {@link GroveThing}.
    * @param nativeRank the number of native things to be associated with this {@link GroveThing}.
    * @param providesNativeKeys set <code>true</code> when the {@link GroveThing} can provide native keys when the
    * native things have been set; otherwise, <code>false</code>.
    * @param linkTypeValidator {@link Predicate} to validate a specified link type is valid for the {@link GroveThing}.
    * @param linkRank a {@link Map} of the {@link LinkType} indicators for each {@link IdentifierType}.
    * @param parentsValidator {@link Predicate} to validate the specified parents are valid.
    * @param nativeThingValidator {@link Predicate} to validate native things are valid.
    * @param nativeKeyFunctions an array of {@link Function} implementations used to extract native keys from native
    * things.
    * @param parents an array of the parent {@link GroveThing}s. This parameter may be a <code>null</code> or empty
    * array.
    */

   //@formatter:off
   public
      AbstractGroveThing
         (
            Identifier                  groveThingKey,
            int                         primaryRank,
            int                         nativeRank,
            boolean                     providesNativeKeys,
            Predicate<LinkType>         linkTypeValidator,
            Map<LinkType,LinkRank>      linkRank,
            Predicate<GroveThing[]>     parentsValidator,
            Predicate<Object[]>         nativeThingValidator,
            Function<Object,Object>[]   nativeKeyFunctions,
            GroveThing...               parents
         ) {
   //@formatter:on

      //@formatter:off

      /*
       * Assert the constructor parameters are sane:
       * * Identifier is non-null
       * * nativeGroveThingRank is within valid range
       * * parents array if non-null and non-empty is a valid size with valid contents
       */

      var reason =
         Objects.isNull( groveThingKey )
            ? "AbstractGroveThing constructor GroveThingKey is null."
            : (    ( primaryRank < AbstractGroveThing.minGroveThingRank )
                || ( primaryRank > AbstractGroveThing.maxGroveThingRank ) )
                 ? "AbstractGroveThing constructor Priamry Rank is out of range."
               : (    ( nativeRank < AbstractGroveThing.minNativeGroveThingRank )
                   || ( nativeRank > AbstractGroveThing.maxNativeGroveThingRank ) )
                    ? "AbstractGroveThing constructor Priamry Rank is out of range."
                    : Objects.isNull( linkRank )
                         ? "AbstractGroveThing constructor Link Rank is null."
                         : Objects.isNull( parentsValidator )
                            ? "AbstractGroveThing constructor Parent Validator is null."
                            : !parentsValidator.test( parents )
                                 ? "AbstractGroveThing constructor parents failed to validate."
                                 : Objects.isNull( nativeThingValidator )
                                      ? "AbstractGroveThing constructor Native Thing Validator is null."
                                      : null;
      //@formatter:on

      if (Objects.nonNull(reason)) {
         throw new GroveThingCreationException(reason, groveThingKey, primaryRank, nativeRank, parents);
      }

      this.primaryRank = primaryRank;
      this.nativeRank = nativeRank;
      this.providesNativeKeys = providesNativeKeys;
      this.linkTypeValidator = linkTypeValidator;
      this.parentsValidator = parentsValidator;
      this.nativeThingValidator = nativeThingValidator;
      this.nativeKeyFunctions = nativeKeyFunctions;
      this.foreignHierarchy = null;
      this.foreignThing = null;
      this.links = new HashMap<>();
      this.linkRank = Objects.requireNonNull(linkRank);
      this.nativeThings = null;
      this.nativeKeys = Optional.empty();
      this.parentGroveThings = Objects.nonNull(parents) && (parents.length > 0) ? parents : null;

      /*
       * Compute the contents of the primary keys array
       */

      if (this.rank() == 1) {
         this.primaryKeys = new Identifier[] {groveThingKey};
      } else {
         this.primaryKeys = new Identifier[this.rank()];

         for (int i = 0; i < this.rank() - 1; i++) {
            this.primaryKeys[i] = parents[i].getIdentifier();
         }

         this.primaryKeys[this.rank() - 1] = groveThingKey;
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object> getForeignHierarchy() {
      return Optional.ofNullable(this.foreignHierarchy);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object> getForeignThing() {
      return Optional.ofNullable(this.foreignThing);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getLinkScalar(LinkType linkType) {
      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && AbstractGroveThing.LinkRank.SCALAR.equals( this.linkRank.get( linkType ) )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) );

      return
         AbstractGroveThing.LinkRank.SCALAR.equals( this.linkRank.get( linkType ) )
            ? Optional.ofNullable((GroveThing)this.links.get(Objects.requireNonNull(linkType)))
            : Optional.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Optional<Collection<GroveThing>> getLinkVector(LinkType linkType) {
      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && (    AbstractGroveThing.LinkRank.VECTOR.equals( this.linkRank.get( linkType ) )
              || AbstractGroveThing.LinkRank.MAP.equals( this.linkRank.get( linkType ) ) )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) );

      switch( this.linkRank.get( linkType ) )
      {
         case MAP:
            return Optional.ofNullable( ((Map<Identifier,GroveThing>) this.links.get(Objects.requireNonNull(linkType))).values() );

         case VECTOR:
            return Optional.ofNullable( ((List<GroveThing>) this.links.get(Objects.requireNonNull(linkType))) );

         default:
            return Optional.empty();
      }
      //@formatter:on
   }

   @Override
   @SuppressWarnings("unchecked")
   public Optional<GroveThing> getLinkVectorElement(LinkType linkType, int index) {
      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && AbstractGroveThing.LinkRank.VECTOR.equals( this.linkRank.get( linkType ) )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) );

      switch( this.linkRank.get( linkType ) )
      {
         case MAP:
            return Optional.empty();

         case VECTOR:
            return Optional.ofNullable( ((List<GroveThing>) this.links.get(Objects.requireNonNull(linkType))).get( index ) );

         default:
            return Optional.empty();
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public @NonNull Identifier getIdentifier() {
      return this.primaryKeys[this.rank() - 1];
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getParent(int selector) {
      //@formatter:off
      return
         ( selector >= 0 ) && ( selector <= this.rank() - 1 )
            ? Optional.of( this.parentGroveThings[ selector ] )
            : ( selector <= -1 ) && ( selector >= 1 - this.rank() )
                 ? Optional.of( this.parentGroveThings[ this.rank() - 1 + selector ] )
                 : Optional.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object[]> getPrimaryKeys() {
      return Optional.of(this.primaryKeys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Object[]> getNativeKeys() {
      return this.nativeKeys;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object getNativeThing() {
      return this.nativeThings[this.nativeRank() - 1];
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      return this.getIdentifier().getType();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean mayProvideNativeKeys() {
      return this.providesNativeKeys;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean hasNativeKeys() {
      return this.providesNativeKeys && this.nativeKeys.isPresent();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isInGroup(IdentifierTypeGroup identifierTypeGroup) {
      return this.getIdentifier().isInGroup(identifierTypeGroup);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isType(LinkType linkType) {
      //@formatter:off
      return
         ( linkType instanceof IdentifierType )
            ? this.getIdentifier().isType( (IdentifierType) linkType )
            : ( linkType instanceof IdentifierTypeGroup )
                 ? this.getIdentifier().isInGroup( (IdentifierTypeGroup) linkType )
                 : false;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int nativeRank() {
      return this.nativeRank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int rank() {
      return this.primaryRank;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    */

   @Override
   public void setForeignHierarchy(Object foreignHierarchy) {

      if (Objects.nonNull(this.foreignHierarchy)) {
         throw new IllegalStateException(
            "AbstractGroveThing::setForeignHierarchy, attempt to set member \"foreignHierarchy\" when already set.");
      }

      this.foreignHierarchy = Objects.requireNonNull(foreignHierarchy);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    */

   @Override
   public GroveThing setForeignThing(Object foreignThing) {

      if (Objects.nonNull(this.foreignThing)) {
         throw new IllegalStateException(
            "AbstractGroveThing::setForeignThing, attempt to set member \"foreignThing\" when already set.");
      }

      this.foreignThing = Objects.requireNonNull(foreignThing);

      return this;
   }

   /**
    * @param parents
    * @return
    */

   @Override
   public void setLinkScalar(LinkType linkType, GroveThing linkedGroveThing) {

      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && Objects.nonNull( linkedGroveThing )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) )
         && AbstractGroveThing.LinkRank.SCALAR.equals( this.linkRank.get( linkType ) )
         && !this.links.containsKey( linkType );
      //@formatter:on

      if (!Objects.requireNonNull(linkedGroveThing).isType(Objects.requireNonNull(linkType))) {
         throw new UnexpectedGroveThingTypeException(linkedGroveThing, linkType);
      }

      this.links.put(linkType, linkedGroveThing);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("incomplete-switch")
   @Override
   public void setLinkVectorElement(LinkType linkType, GroveThing linkedGroveThing) {

      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && Objects.nonNull( linkedGroveThing )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) )
         && (    AbstractGroveThing.LinkRank.MAP.equals( this.linkRank.get( linkType ) )
              || AbstractGroveThing.LinkRank.VECTOR.equals( this.linkRank.get( linkType ) ) );
      //@formatter:on

      if (!Objects.requireNonNull(linkedGroveThing).isType(Objects.requireNonNull(linkType))) {
         throw new UnexpectedGroveThingTypeException(linkedGroveThing, linkType);
      }

      switch (this.linkRank.get(linkType)) {
         case MAP: {
            @SuppressWarnings("unchecked")
            var map = (Map<Identifier, GroveThing>) this.links.get(Objects.requireNonNull(linkType));

            if (Objects.isNull(map)) {
               map = new HashMap<Identifier, GroveThing>();
               this.links.put(linkType, map);
            }

            map.put(Objects.requireNonNull(linkedGroveThing).getIdentifier(), linkedGroveThing);
            break;
         }

         case VECTOR: {
            @SuppressWarnings("unchecked")
            var list = (List<GroveThing>) this.links.get(Objects.requireNonNull(linkType));

            if (Objects.isNull(list)) {
               list = new ArrayList<GroveThing>();
               this.links.put(linkType, list);
            }

            list.add(Objects.requireNonNull(linkedGroveThing));
            break;
         }
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public GroveThing setNativeThings(Object... nativeThings) {

      //@formatter:off
      assert
            Objects.isNull( this.nativeThings )
         && ( Objects.isNull( this.nativeThingValidator ) || this.nativeThingValidator.test( nativeThings ) );

      this.nativeThings = nativeThings;

      if( !this.providesNativeKeys || Objects.isNull( this.nativeKeyFunctions ) )
      {
         this.nativeKeys = Optional.empty();
         return this;
      }

      var nativeThingKeys = new Object[ this.nativeRank() ];

      for( int i = 0;
               i < this.nativeRank();
               i++ )
      {
         nativeThingKeys[i] = this.nativeKeyFunctions[ i ].apply( this.nativeThings[i] );
      }

      this.nativeKeys = Optional.of( nativeThingKeys );

      return this;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   @SuppressWarnings("unchecked")
   public Stream<GroveThing> streamLinks(LinkType linkType) {
      //@formatter:off
      assert
            Objects.nonNull( linkType )
         && ( Objects.isNull( this.linkTypeValidator ) || this.linkTypeValidator.test( linkType ) )
         && this.links.containsKey( linkType );
      //@formatter:on

      var object = this.links.get(linkType);

      if (Objects.isNull(object)) {
         return Stream.empty();
      }

      switch (this.linkRank.get(linkType)) {
         case SCALAR:
            return Stream.of((GroveThing) this.links.get(linkType));
         case MAP:
            return ((Map<Identifier, GroveThing>) this.links.get(linkType)).values().stream();
         case VECTOR:
            return ((List<GroveThing>) this.links.get(linkType)).stream();
         default:
            return Stream.empty();
      }
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();
      var segmentTitle = new StringBuilder(64);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( name )
         .indentInc()
         .segmentIndexedArray( "Grove Thing Keys", this.primaryKeys )
         ;

      /*
       * Links
       */

      if( Objects.nonNull( this.links ) )
      {
         outMessage
            .segment
               (
                  "Links",
                  this.links.values().stream()
                     .flatMap
                        (
                           ( linkObject ) -> ( linkObject instanceof GroveThing  )
                                                  ? Stream.of( (GroveThing) linkObject )
                                                  : linkObject instanceof List
                                                       ? ((List<GroveThing>) linkObject).stream()
                                                       : ((Map<Identifier,GroveThing>) linkObject).values().stream()
                        )
                     .map( GroveThing::getIdentifier )
                     .map( Identifier::toString )
                     .collect( Collectors.joining( ", ", "[ ", " ]" ) )
               )
            ;
      }

      /*
       * Native Things
       */

      if( Objects.nonNull( this.nativeThings ) )
      {

         outMessage
            .title( "Native Things" )
            .indentInc()
            ;

         for( int i = 0; i < this.nativeRank(); i++ )
         {
            segmentTitle.setLength( 0 );
            segmentTitle.append( "Native Thing[ " ).append( Integer.toString(i) ).append( " ]" );

            if( i >= this.nativeThings.length )
            {
               outMessage.segment( segmentTitle, "(index-out-of-bounds)" );
               continue;
            }

            if( Objects.isNull( this.nativeThings[i] ) )
            {
               outMessage.segment( segmentTitle, "(null)" );
               continue;
            }

            if( this.nativeThings[i] instanceof ToMessage )
            {
               outMessage
                  .title( segmentTitle )
                  .indentInc()
                  .toMessage( (ToMessage) this.nativeThings[i] )
                  .indentDec();
               continue;
            }

            outMessage.segment( segmentTitle, nativeThings[i] );
         }

         outMessage.indentDec();

         this.getNativeKeys().ifPresent( nativeKeys ->
         {
            outMessage
               .title( "Native Thing Keys" )
               .indentInc()
               ;

            for( int i = 0; i < this.nativeRank(); i++ )
            {
               segmentTitle.setLength( 0 );
               segmentTitle.append( "Key[ " ).append( i ).append( " ]" );

               if( i >= this.nativeThings.length )
               {
                  outMessage.segment( segmentTitle, "(index-out-of-bounds)" );
                  continue;
               }

               if( Objects.isNull( this.nativeThings[i] ) )
               {
                  outMessage.segment( segmentTitle, "(null)" );
                  continue;
               }

               if( nativeKeys[i] instanceof ToMessage )
               {
                  outMessage
                     .title( segmentTitle )
                     .indentInc()
                     .toMessage( (ToMessage) nativeKeys[i] )
                     .indentDec()
                     ;
                  continue;
               }

               outMessage.segment( segmentTitle, nativeKeys[i] );
            }
         });
      }

      outMessage
         .indentDec()
         .segment( "Foreign Hierarchy", this.foreignHierarchy )
         .segment( "Foreign Thing",     this.foreignThing     )
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
