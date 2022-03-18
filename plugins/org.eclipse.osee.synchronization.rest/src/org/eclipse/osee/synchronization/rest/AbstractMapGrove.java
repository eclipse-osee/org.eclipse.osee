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

package org.eclipse.osee.synchronization.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * This class provides a basic map like implementation of the {@link Grove} interface, to minimize the effort required
 * to implement this interface for specific Synchronization Artifact things.
 *
 * @author Loren K. Ashley
 */

public class AbstractMapGrove implements Grove, ToMessage {

   /**
    * Save the {@link IdentifierType} associated with the {@link GroveThing}s saved in the grove.
    */

   protected final IdentifierType identifierType;

   /**
    * A {@link Map} of {@link GroveThing}s by {@link Identifier}.
    */

   protected final Map<Identifier, GroveThing> map;

   /**
    * When the native things stored in the map implement the {@link Id} interface, this map provides an association
    * between the {@link GroveThing}s and the key of their associated native things.
    */

   protected final Map<Long, GroveThing> nativeKeyMap;

   /**
    * Creates a new empty grove.
    *
    * @param identifierType the {@link IdentifierType} of the {@link GroveThing}s to be saved in the grove.
    * @throws NullPointerException when the parameter <code>identifierType</code> is <code>null</code>.
    */

   protected AbstractMapGrove(IdentifierType identifierType) {
      this.identifierType = Objects.requireNonNull(identifierType);
      this.map = new HashMap<>();
      this.nativeKeyMap = new HashMap<>();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void add(GroveThing groveThing) {
      Objects.requireNonNull(groveThing);
      var groveThingKey = groveThing.getGroveThingKey();

      assert Objects.nonNull(groveThingKey);

      if (this.map.containsKey(groveThingKey)) {
         throw new DuplicateGroveEntry(this, groveThing);
      }

      this.map.put(groveThingKey, groveThing);

      groveThing.getNativeKey().ifPresent(nativeKey -> this.nativeKeyMap.put(nativeKey, groveThing));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Long nativeKey) {
      return this.nativeKeyMap.containsKey(nativeKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void createForeignThings(SynchronizationArtifactBuilder synchronizationArtifactBuilder) {
      if (this.map.size() == 0) {
         return;
      }

      Optional<Consumer<GroveThing>> converterOptional =
         synchronizationArtifactBuilder.getConverter(this.identifierType);

      converterOptional.ifPresent(converter -> this.map.values().forEach(converter::accept));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public GroveThing get(Identifier groveThingKey) {
      return this.map.get(groveThingKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByNativeKey(Long nativeKey) {
      return Optional.ofNullable(this.nativeKeyMap.get(nativeKey));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      return this.identifierType;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> stream() {
      return this.map.values().stream();
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
      outMessage
         .append( indent0 ).append( name ).append( ":" ).append( "\n" );
      //@formatter:on

      this.map.values().forEach(groveThing -> {
         var abstractObjectTypeName = groveThing.getClass().getName();

         //@formatter:off
         outMessage
            .append( indent1 ).append( abstractObjectTypeName ).append( ":" ).append( "\n" )
            .append( indent2 ).append( "Key:          " ).append( groveThing.getGroveThingKey() ).append( "\n" )
            .append( indent2 ).append( "Grove Thing:" ).append( "\n" )
            ;
         //@formatter:on

         groveThing.toMessage(indent + 3, outMessage);

         //@formatter:off
         outMessage
            .append( indent2 ).append( "Native Thing: " ).append( groveThing.getNativeThing() ).append( "\n" )
            ;
         //@formatter:on

      });

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
